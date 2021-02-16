package arun.pkg.passportdemo.di

import android.content.Context
import arun.pkg.passportdemo.PassportApplication
import arun.pkg.passportdemo.main.di.MainFragmentModule
import core.di.CoreModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import firebase_ml.data.di.FirebaseMLModule
import firebase_ml.data.di.TesseractModule
import opencv.data.di.OpenCVModule
import template.data.di.TemplatesModule
import javax.inject.Singleton

/**
 * Dagger App component declaration
 */
@Component(
    modules = [AndroidSupportInjectionModule::class,
        MainFragmentModule::class,
        CoreModule::class,
        FirebaseMLModule::class,
        OpenCVModule::class,
        TemplatesModule::class,
        TesseractModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<PassportApplication> {

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun setContext(context: Context): Builder
    }
}