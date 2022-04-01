package me.vislavy.testimony.config_system.configs

import me.vislavy.testimony.config_system.Config
import me.vislavy.testimony.config_system.data.ConfigModel

class PluginConfig : Config<ConfigModel>(ConfigModel::class) {

    fun refresh() {
        super.refresh(FileName)
    }

    companion object {
        private const val FileName = "config.yml"
    }
}