package opencv.data

import android.content.Context
import android.util.Log
import opencv.domain.OpenCVUseCase
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import javax.inject.Inject

class OpenCVUseCaseImpl @Inject constructor(
    val context: Context
) : OpenCVUseCase {
    private val thresholdMin: Double = 125.0 // Threshold 80 to 105 is Ok
    private val thresholdMax: Double = 255.0 // Always 255

    init {
        if (!OpenCVLoader.initDebug()) {
            Log.w("TAG", "Unable to load OpenCV")
        } else {
            Log.w("TAG", "OpenCV loaded")
        }
    }

    override fun getOpenCVImagePath(imageFile: File): String {
        return imageProcessing(imageFile.absolutePath)
    }

    private fun imageProcessing(inFile: String? = ""): String {
        //Load this image in grayscale
        val img = Imgcodecs.imread(inFile)
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2GRAY, 3)

        //Invert the colors (because objects are represented as white pixels, and the background is represented by black pixels)
        Core.bitwise_not(img, img)

        val element =
            Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 2.0), Point(1.0, 1.0))
        Imgproc.dilate(img, img, element)
        //We can now perform our erosion, we must declare our rectangle-shaped structuring element and call the erode function
        Imgproc.erode(img, img, element)
        //Binarize it
        //Use adaptive threshold if necessary
        //Imgproc.adaptiveThreshold(img, img, thresholdMax.toDouble(), ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 40);
        Imgproc.threshold(img, img, thresholdMin, thresholdMax, Imgproc.THRESH_BINARY)
//
//        val rotatedRectAngle = imageRotateRectAngle(img)
//
//        val size = Size(img.width().toDouble(), img.height().toDouble())
//
//        val result = deskew(img, size, rotatedRectAngle)
//
//        Imgproc.resize(result, result, size, 2.0, 2.0, Imgproc.INTER_CUBIC)

        val openCvFileName = inFile!!.replace(".jpg", "_open_cv.jpg")
        return saveImageInSDCard(img, openCvFileName)
    }

    private fun imageRotateRectAngle(img: Mat): Double {
        //Find all white pixels
        val wLocMat = Mat.zeros(img.size(), img.type())
        Core.findNonZero(img, wLocMat)

        //Create an empty Mat and pass it to the function
        val matOfPoint = MatOfPoint(wLocMat)

        //Translate MatOfPoint to MatOfPoint2f in order to user at a next step
        val mat2f = MatOfPoint2f()
        matOfPoint.convertTo(mat2f, CvType.CV_32FC2)

        //Get rotated rect of white pixels
        val rotatedRect = Imgproc.minAreaRect(mat2f)

        if (rotatedRect.angle < -45) {
            rotatedRect.angle = rotatedRect.angle + 90.0f
        } else {
            rotatedRect.angle
        }
        return rotatedRect.angle
    }

    private fun saveImageInSDCard(result: Mat?, outputPath: String): String {
        val bool: Boolean = Imgcodecs.imwrite(outputPath, result)
        if (bool) {
            Log.d("TAG", "OpenCV SUCCESS writing image to external storage: $outputPath")
        } else {
            Log.d("TAG", "OpenCV Fail writing image to external storage: $outputPath")
        }
        return outputPath
    }

    private fun deskew(src: Mat, size: Size, angle: Double): Mat? {
        val center = Point((src.width() / 2).toDouble(), (src.height() / 2).toDouble())
        val rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0)
        //1.0 means 100 % scale
        Imgproc.warpAffine(
            src, src, rotImage, size,
//            Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS
            Imgproc.INTER_CUBIC + Imgproc.CV_WARP_FILL_OUTLIERS
        )
        return src
    }
}
