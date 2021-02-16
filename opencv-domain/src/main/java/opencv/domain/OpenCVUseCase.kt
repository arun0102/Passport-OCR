package opencv.domain

import java.io.File

interface OpenCVUseCase {
    fun getOpenCVImagePath(imageFile: File): String
}
