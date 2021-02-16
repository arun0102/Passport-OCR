package tesseract.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import io.reactivex.Single
import non_core.lib.Result
import tesseract.domain.TesseractUseCase
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class TesseractUseCaseImpl @Inject constructor(
    val context: Context
) : TesseractUseCase {
    private val TESS_DATA = "/tessdata"
    private lateinit var tessBaseAPI: TessBaseAPI
    override fun getTesseractScanning(imageFile: File): Single<Result<String>> {
        prepareTessData()
        return startOCR(imageFile.absolutePath)
    }

    private fun startOCR(imagePath: String): Single<Result<String>> {
        return Single.create<Result<String>> { emitter ->
            var result: String
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                options.inSampleSize = 1
                val bitmap = BitmapFactory.decodeFile(imagePath, options)
                result = this.getText(bitmap)
                Log.e("TesseractUseCaseImpl", "Tesseract scanned result : $result")
                emitter.onSuccess(Result.withValue(result))
            } catch (e: Exception) {
                Log.e("TesseractUseCaseImpl", e.message)
            }
        }.onErrorReturn {
            Result.withError(TesseractError("Tesseract unknown error", it))
        }
    }

    private fun getText(bitmap: Bitmap): String {
        try {
            tessBaseAPI = TessBaseAPI()
            tessBaseAPI.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK
        } catch (e: Exception) {
            Log.e("TesseractUseCaseImpl", e.message)
        }

        val dataPath = context.getExternalFilesDir("/")!!.path + "/"
        tessBaseAPI.init(dataPath, "eng")
        tessBaseAPI.setImage(bitmap)
        var retStr = "No result"
        try {
            retStr = tessBaseAPI.utF8Text
        } catch (e: Exception) {
            Log.e("TesseractUseCaseImpl", e.message)
        }

        tessBaseAPI.end()
        return retStr
    }

    private fun prepareTessData() {
        try {
            val dir = context.getExternalFilesDir(TESS_DATA) ?: File(TESS_DATA)
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    return
                }
            }
            val fileList = context.assets.list("") ?: emptyArray()
            for (fileName in fileList) {
                val pathToDataFile = "$dir/$fileName"
                if (!File(pathToDataFile).exists()) {
                    val input = context.assets.open(fileName)
                    val out = FileOutputStream(pathToDataFile)
                    val buff = ByteArray(1024)
                    var len = input.read(buff)
                    while (-1 != len) {
                        out.write(buff, 0, len)
                        len = input.read(buff)
                    }
                    input.close()
                    out.close()
                }
            }
        } catch (e: Exception) {
            Log.e("TesseractUseCaseImpl", e.message)
        }

    }
}
