package tesseract.data

import noncore.error.FeatureError

class TesseractError(
    message: String = "Tesseract error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)