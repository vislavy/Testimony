package me.vislavy.testimony.utils

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

object ColorFormatter {

    fun format(value: String): String {
        var formattedValue = value.replace("&", "\u00A7")

        val pattern = Pattern.compile("#[a-fA-F0-9]{6}")
        var matcher = pattern.matcher(value)
        while (matcher.find()) {
            val color = formattedValue.substring(matcher.start(), matcher.end())
            formattedValue = formattedValue.replace(color, ChatColor.of(color).toString())
            matcher = pattern.matcher(formattedValue)
        }

        return formattedValue
    }
}