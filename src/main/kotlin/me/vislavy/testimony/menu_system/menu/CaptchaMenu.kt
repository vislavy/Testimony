package me.vislavy.testimony.menu_system.menu

import me.vislavy.testimony.plugin
import me.vislavy.testimony.utils.StringFormatter
import me.vislavy.testimony.menu_system.Menu
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CaptchaMenu(private val player: Player) : Menu(player) {

    var isPassed = false
        private set

    private val desiredVariant = getRandomItem()
    private val locale = plugin.locale

    override fun getTitle() = StringFormatter.format(locale.captcha.title, desiredVariant.amount)

    override fun getSlotCount() = 9

    override fun setMenuItems() {
        val desiredItemIndex = (0 until 3).random()
        val elimination = mutableListOf(desiredVariant.amount)
        for (index in 0 until 3) {
            val slotIndex = 2 + 2 * index

            if (index == desiredItemIndex) {
                inventory.setItem(slotIndex, desiredVariant)
                continue
            }

            val otherVariant = getRandomItem(elimination)
            elimination.add(otherVariant.amount)
            inventory.setItem(slotIndex, otherVariant)
        }
    }

    override fun onItemClick(event: InventoryClickEvent) {
        if (event.currentItem?.amount == desiredVariant.amount) {
            isPassed = true

            player.playSound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1F, 1F)
            event.whoClicked.closeInventory()

            plugin.startAuthenticationProcess(player)
            return
        }

        player.kickPlayer(locale.prefix + locale.captcha.captchaNotPassed)
    }

    private fun getRandomItem(elimination: List<Int> = emptyList()) : ItemStack {
        val randomRange = (1..64).toMutableList()
        randomRange.removeAll(elimination)
        val randomValue = randomRange.random()
        return createItem(Material.MOJANG_BANNER_PATTERN, randomValue.toString(), randomValue)
    }
}