package me.vislavy.testimony.listeners

import me.vislavy.testimony.plugin
import me.vislavy.testimony.menu_system.Menu
import me.vislavy.testimony.menu_system.menu.CaptchaMenu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

@Suppress("UNUSED")
class MenuListener : Listener {

    private val locale = plugin.locale

    @EventHandler
    fun onItemClick(event: InventoryClickEvent) {
        val inventoryHolder = event.inventory.holder
        if (inventoryHolder is Menu) {
            if (event.currentItem == null) return

            event.isCancelled = true
            inventoryHolder.onItemClick(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val inventoryHolder = event.inventory.holder
        if (inventoryHolder is CaptchaMenu) {
            if (inventoryHolder.isPassed) return

            val player = event.player as Player
            player.kickPlayer(locale.prefix + locale.captcha.captchaNotPassed)
        }
    }
}