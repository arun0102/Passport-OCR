package opencv.data

import noncore.error.FeatureError

class OpenCVError(
    message: String = "OpenCv error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)