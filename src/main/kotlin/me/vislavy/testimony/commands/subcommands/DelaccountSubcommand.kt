package me.vislavy.testimony.commands.subcommands

import me.vislavy.testimony.commands.Subcommand
import me.vislavy.testimony.plugin
import me.vislavy.testimony.utils.StringFormatter
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DelaccountSubcommand : Subcommand {

    private val locale = plugin.localeConfig.config
    private val logger = plugin.logger

    override fun execute(sender: CommandSender, label: String, vararg args: String) {
        if (args.size > 1) {
            if (sender is Player) {
                sender.sendMessage(locale.prefix + locale.other.onlyConsole)
                return
            }

            if (plugin.delaccount(args[1])) {
                logger.info(locale.delaccount.success)
                val player = plugin.server.getPlayer(args[1]) ?: return
                plugin.startAuthorizationProcess(player)
            }

            logger.info(StringFormatter.format(locale.delaccount.accountNotExists, args[1]))
            return
        }

        if (sender !is Player) {
            logger.info(locale.delaccount.usage)
            return
        }

        if (!sender.hasPermission("testimony.command.delaccount")) {
            sender.sendMessage(locale.prefix + locale.other.accessDenied)
            return
        }

        plugin.delaccount(sender.name)
        sender.sendMessage(locale.prefix + locale.delaccount.success)
        plugin.startAuthorizationProcess(sender)
    }
}