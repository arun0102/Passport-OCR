package firebase_ml.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import firebase_ml.data.utils.CompressImage
import firebase_ml.domain.FirebaseMLUseCase
import io.reactivex.Single
import non_core.lib.Result
import opencv.domain.OpenCVUseCase
import templates.domain.GetTemplateDataUseCase
import java.io.File
import java.io.IOException
import javax.inject.Inject

const val LINE_THRESHOLD = 20

class FirebaseUseCaseImpl @Inject constructor(
    private val context: Context,
    private val openCVUseCase: OpenCVUseCase,
    private val templateDataUseCase: GetTemplateDataUseCase
) : FirebaseMLUseCase {
    override fun getFirebaseMLScanning(
        imageFile: File,
        selectedTemplate: String
    ): Single<Result<String>> {

//        val compressedImage = CompressImage.getCompressedImageFile(imageFile)
        val firebaseVisionImage = imageFromPath(context = context, file = imageFile)

        val textDetector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        return Single.create<Result<String>> { emitter ->
            firebaseVisionImage?.let {
                textDetector.processImage(firebaseVisionImage).addOnCompleteListener {
                    Log.d(
                        "FirebaseUseCaseImpl", "Extracted Text : " +
                                "${it.result?.text}"
                    )
                    emitter.onSuccess(
                        Result.withValue(
                            templateDataUseCase.getTemplateMap(
                                it.result?.textBlocks?.getFirebaseText() ?: "",
                                selectedTemplate
                            )
                        )
                    )
                }
            }
        }
    }

    private fun MutableList<FirebaseVisionText.TextBlock>.getFirebaseText(): String {
        val dataMap = mutableMapOf<BlockData, String>()
        for (block in this) {
            val keysItr = dataMap.keys.iterator()
            var isFound = false
            val blockTopY = block.cornerPoints!![0].y
            val blockBottomY = block.cornerPoints!![3].y
            val blockHeight = blockBottomY - blockTopY
            while (keysItr.hasNext()) {
                val mapKey = keysItr.next()
                if ((mapKey.topY - (mapKey.height / 3) < blockTopY) && (mapKey.bottomY + (mapKey.height / 3) > blockBottomY)) {
                    isFound = true
                    dataMap[mapKey] += " " + block.text
                    break
                }
            }
            if (!isFound) {
                dataMap[BlockData(blockTopY, blockBottomY, blockHeight)] = block.text
            }
        }
        return dataMap.toStringData()
    }

    data class BlockData(val topY: Int, val bottomY: Int, val height: Int)

    private fun Map<BlockData, String>.toStringData(): String {
        return this.values.joinToString("\n")
    }

    private fun imageFromPath(context: Context, file: File): FirebaseVisionImage? {
        var image: FirebaseVisionImage? = null
        try {
            // To get openCv image path
//            val result = openCVUseCase.getOpenCVImagePath(file)
            image = FirebaseVisionImage.fromFilePath(context, Uri.fromFile(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}
