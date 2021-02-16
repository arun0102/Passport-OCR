package firebase_ml.data

import noncore.error.FeatureError

class FirebaseMLError(
    message: String = "Firebase ML error",
    throwable: Throwable? = null
) : FeatureError(message, throwable)