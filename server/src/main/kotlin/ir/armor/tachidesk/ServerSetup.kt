package ir.armor.tachidesk

import eu.kanade.tachiyomi.App
import ir.armor.tachidesk.database.makeDataBaseTables
import ir.armor.tachidesk.util.systemTray
import net.harawata.appdirs.AppDirsFactory
import org.kodein.di.DI
import org.kodein.di.conf.global
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer
import xyz.nulldev.ts.config.ConfigKodeinModule
import xyz.nulldev.ts.config.GlobalConfigManager
import java.io.File

object applicationDirs {
    val dataRoot = AppDirsFactory.getInstance().getUserDataDir("Tachidesk", null, null)!!
    val extensionsRoot = "$dataRoot/extensions"
    val thumbnailsRoot = "$dataRoot/thumbnails"
    val mangaRoot = "$dataRoot/manga"
}

val serverConfig: ServerConfig by lazy { GlobalConfigManager.module() }

val systemTray by lazy { systemTray() }

val androidCompat by lazy { AndroidCompat() }

fun serverSetup() {
    // register server config
    GlobalConfigManager.registerModule(
        ServerConfig.register(GlobalConfigManager.config)
    )

    // make dirs we need
    listOf(
        applicationDirs.dataRoot,
        applicationDirs.extensionsRoot,
        "${applicationDirs.extensionsRoot}/icon",
        applicationDirs.thumbnailsRoot
    ).forEach {
        File(it).mkdirs()
    }

    makeDataBaseTables()

    // create system tray
    systemTray

    // Load config API
    DI.global.addImport(ConfigKodeinModule().create())
    // Load Android compatibility dependencies
    AndroidCompatInitializer().init()
    // start app
    androidCompat.startApp(App())

    // socks proxy settings
    System.getProperties()["proxySet"] = serverConfig.socksProxy.toString()
    System.getProperties()["socksProxyHost"] = serverConfig.socksProxyHost
    System.getProperties()["socksProxyPort"] = serverConfig.socksProxyPort
}
