package me.vislavy.testimony.listeners

import me.vislavy.testimony.local.database.Database
import me.vislavy.testimony.plugin
import me.vislavy.testimony.menu_system.menu.CaptchaMenu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

@Suppress("UNUSED")
class PlayerListener : Listener {

    private val config = plugin.config
    private val locale = plugin.locale

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val isAccountExists = Database.getAccount(player.name) != null

        if (plugin.config.captcha.enabled && !isAccountExists) {
            CaptchaMenu(player).open()
            return
        }

        if (config.session.enabled && isAccountExists) {
            if (plugin.checkSession(player)) {
                player.sendMessage(locale.prefix + locale.session.valid)
                return
            }

            player.sendMessage(locale.prefix + locale.session.invalid)
        }

        plugin.startAuthenticationProcess(player)
    }

    @EventHandler
    fun onMessage(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (!plugin.isUnauthorized(player)) return
        event.isCancelled = true

        val message = event.message

        Database.getAccount(player.name)?.apply {
            if (!plugin.tryAuth(player, message)) {
                player.sendMessage(locale.prefix + locale.authorization.wrongPassword)
                return
            }

            plugin.stopAuthorizationProcess(player)
            player.sendMessage(locale.prefix + locale.authorization.success)
            return
        }

        if (message.length < config.registration.passwordLength.min || message.length > config.registration.passwordLength.max) {
            player.sendMessage(locale.prefix + locale.registration.wrongPasswordLength)
            return
        }

        plugin.register(player, message)
        plugin.stopAuthorizationProcess(player)
        player.sendMessage(locale.prefix + locale.registration.success)
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (plugin.isUnauthorized(player)) {
            event.isCancelled = true
            Database.getAccount(player.name)?.apply {
                player.sendMessage(locale.prefix + locale.authorization.wrongCommand)
                return
            }

            player.sendMessage(locale.prefix + locale.registration.wrongCommand)
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