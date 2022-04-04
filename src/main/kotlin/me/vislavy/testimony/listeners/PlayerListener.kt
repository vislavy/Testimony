package me.vislavy.testimony.listeners

import me.vislavy.testimony.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

@Suppress("UNUSED")
class PlayerListener : Listener {

    private val config = plugin.pluginConfig.config
    private val locale = plugin.localeConfig.config

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (config.session.enabled && plugin.isAccountExists(player.name)) {
            if (plugin.checkSession(player)) {
                player.sendMessage(locale.prefix + locale.session.valid)
                return
            }

            player.sendMessage(locale.prefix + locale.session.invalid)
        }

        plugin.startAuthorizationProcess(player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (plugin.isUnauthorized(player)) {
            plugin.stopAuthorizationProcess(player)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPasswordEntry(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (!plugin.isUnauthorized(player)) return

        event.isCancelled = true
        val password = event.message.trim()

        if (plugin.isAccountExists(player.name)) {
            if (!plugin.tryAuth(player, password)) {
                player.sendMessage(locale.prefix + locale.authorization.wrongPassword)
                return
            }

            plugin.stopAuthorizationProcess(player)
            player.sendMessage(locale.prefix + locale.authorization.success)
            return
        }

        if (password.length < config.registration.passwordLength.min || password.length > config.registration.passwordLength.max) {
            player.sendMessage(locale.prefix + locale.registration.wrongPasswordLength)
            return
        }

        plugin.stopAuthorizationProcess(player)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onCasualPasswordEntry(event: AsyncPlayerChatEvent) {
        val player = event.player
        val password = event.message.trim()
        if (plugin.tryAuth(player, password)) {
            event.isCancelled = true
            player.sendMessage(locale.prefix + locale.other.casualPasswordEntry)
        }
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (plugin.isUnauthorized(player)) {
            event.isCancelled = true
            when (plugin.isAccountExists(player.name)) {
                true -> player.sendMessage(locale.prefix + locale.authorization.wrongCommand)
                else -> player.sendMessage(locale.prefix + locale.registration.wrongCommand)
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if (plugin.isUnauthorized(player)) {
            val fromPosition = event.from
            val toPosition = event.to ?: return
            toPosition.x = fromPosition.x
            toPosition.y = fromPosition.y
            toPosition.z = fromPosition.z
            event.setTo(toPosition)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (plugin.isUnauthorized(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return

        val player = event.entity as Player
        if (plugin.isUnauthorized(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player) return

        val player = event.damager as Player
        if (plugin.isUnauthorized(player)) {
            event.isCancelled = true
        }
    }
}