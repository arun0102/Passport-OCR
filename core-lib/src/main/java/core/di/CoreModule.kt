package core.di

import androidx.lifecycle.ViewModelProvider
import core.ui.ViewModelFactory
import dagger.Binds
import dagger.Module


@Module
abstract class CoreModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}