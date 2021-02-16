package opencv.data.di

import dagger.Binds
import dagger.Module
import opencv.data.OpenCVUseCaseImpl
import opencv.domain.OpenCVUseCase

@Module
interface OpenCVModule {
    @Binds
    fun bindOpenCVUseCase(openCVUseCaseImpl: OpenCVUseCaseImpl): OpenCVUseCase
}