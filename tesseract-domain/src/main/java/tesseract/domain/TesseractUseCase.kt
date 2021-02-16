package tesseract.domain

import io.reactivex.Single
import non_core.lib.Result
import java.io.File

interface TesseractUseCase {
    fun getTesseractScanning(imageFile: File): Single<Result<String>>
}
