package com.github.caracal.jarvis

import android.app.Application
import com.github.caracal.jarvis.shopping.data.SharedPrefsShoppingRepository
import com.github.caracal.jarvis.shopping.data.ShoppingRepository

/**
 * Application-wide composition root.
 *
 * Hosts the single, application-scoped [ShoppingRepository] instance so that ViewModels are
 * constructed with an injected dependency rather than each Fragment/Factory instantiating the
 * data source itself. This project's current Android Gradle Plugin version does not yet support
 * the Hilt Gradle plugin (see gradle/libs.versions.toml notes); this manual composition root
 * follows the same dependency-injection intent — a single source of truth for wiring
 * dependencies, no direct data-source construction in Fragments/Activities — until Hilt can be
 * adopted.
 */
class JarvisApplication : Application() {

    /** Single, application-scoped repository for all shopping data access. */
    val shoppingRepository: ShoppingRepository by lazy { SharedPrefsShoppingRepository(this) }
}
