package firebase_ml.domain

import io.reactivex.Single
import java.io.File
import non_core.lib.Result

interface FirebaseMLUseCase {
    fun getFirebaseMLScanning(imageFile: File, selectedTemplate: String): Single<Result<String>>
}
