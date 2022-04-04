package me.vislavy.testimony.commands

import com.github.shynixn.mccoroutine.SuspendingCommandExecutor
import me.vislavy.testimony.commands.subcommands.DelaccountSubcommand
import me.vislavy.testimony.commands.subcommands.ReloadSubcommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class TestimonyCommand : SuspendingCommandExecutor {

    override suspend fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) return false

        when (args[0].lowercase()) {
            "reload" -> ReloadSubcommand().execute(sender, label, *args)
            "delaccount" -> DelaccountSubcommand().execute(sender, label, *args)
            else -> return false
        }

        return true
    }
}