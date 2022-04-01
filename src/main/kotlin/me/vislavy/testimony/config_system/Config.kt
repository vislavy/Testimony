package me.vislavy.testimony.config_system

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import me.vislavy.testimony.plugin
import me.vislavy.testimony.utils.ColorFormatter
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
open class Config<T : Any>(private val model: KClass<T>) {

    lateinit var config: T
        private set

    fun refresh(fileName: String) {
        val logger = plugin.logger
        logger.info("§6Loading $fileName...")

        val dataFolder = plugin.dataFolder
        if (!dataFolder.exists()) dataFolder.mkdir()
        val configFile = File(dataFolder, fileName)
        if (!configFile.exists()) {
            try {
                Files.copy(
                    plugin.getResource(fileName)!!,
                    configFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
                logger.info("§6The $fileName file was not found. New one created.")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val formattedYaml = StringBuilder().apply {
            configFile.readLines().forEach { line ->
                appendLine(ColorFormatter.format(line))
            }
        }.toString()
        configFile.writeText(formattedYaml)
        config = Yaml.default.decodeFromStream(model.serializer(), configFile.inputStream())
    }
}