package me.vislavy.testimony.commands

import org.bukkit.command.CommandSender

interface Subcommand {

    fun execute(sender: CommandSender, label: String, vararg args: String)
}