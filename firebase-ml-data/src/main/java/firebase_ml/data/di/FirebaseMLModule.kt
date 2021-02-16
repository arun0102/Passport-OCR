package firebase_ml.data.di

import dagger.Binds
import dagger.Module
import firebase_ml.data.FirebaseUseCaseImpl
import firebase_ml.domain.FirebaseMLUseCase

@Module
interface FirebaseMLModule {
    @Binds
    fun bindFirebaseMLUseCase(firebaseUseCaseImpl: FirebaseUseCaseImpl): FirebaseMLUseCase
}