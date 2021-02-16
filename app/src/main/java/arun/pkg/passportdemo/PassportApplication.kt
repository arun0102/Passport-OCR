package arun.pkg.passportdemo

import arun.pkg.passportdemo.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Application class
 */
class PassportApplication : DaggerApplication() {

    //Dagger lib initialization
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .setContext(this)
            .build()
    }
}