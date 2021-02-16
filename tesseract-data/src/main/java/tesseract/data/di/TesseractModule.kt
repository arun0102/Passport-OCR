package firebase_ml.data.di

import dagger.Binds
import dagger.Module
import tesseract.data.TesseractUseCaseImpl
import tesseract.domain.TesseractUseCase

@Module
interface TesseractModule {
    @Binds
    fun bindTesseractUseCase(tesseractUseCaseImpl: TesseractUseCaseImpl): TesseractUseCase
}