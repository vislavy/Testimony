package me.vislavy.testimony.commands.subcommands

import me.vislavy.testimony.commands.Subcommand
import me.vislavy.testimony.plugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ReloadSubcommand : Subcommand {

    private val locale = plugin.localeConfig.config
    private val logger = plugin.logger

    override fun execute(sender: CommandSender, label: String, vararg args: String) {
        if (!sender.hasPermission("testimony.command.reload")) {
            sender.sendMessage(locale.prefix + locale.other.accessDenied)
            return
        }

        plugin.pluginConfig.refresh()
        plugin.localeConfig.refresh()

        when (sender is Player) {
            true -> sender.sendMessage(locale.prefix + locale.other.reload)
            false -> logger.info(locale.other.reload)
        }
    }
}