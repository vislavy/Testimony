package me.vislavy.testimony.config_system.configs

import me.vislavy.testimony.config_system.Config
import me.vislavy.testimony.config_system.data.LocaleModel

class LocaleConfig : Config<LocaleModel>(LocaleModel::class) {

    fun refresh() {
        super.refresh(FileName)
    }

    companion object {
        private const val FileName = "locale.yml"
    }
}