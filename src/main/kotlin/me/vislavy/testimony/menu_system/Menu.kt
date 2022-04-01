package me.vislavy.testimony.menu_system

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

abstract class Menu(private val player: Player) : InventoryHolder {

    private lateinit var inventory: Inventory

    override fun getInventory() = inventory

    abstract fun getTitle(): String

    abstract fun getSlotCount(): Int

    abstract fun setMenuItems()

    abstract fun onItemClick(event: InventoryClickEvent)

    fun open() {
        inventory = Bukkit.createInventory(this, getSlotCount(), getTitle())
        setMenuItems()
        player.openInventory(inventory)
    }

    fun createItem(
        material: Material,
        displayName: String? = null,
        amount: Int = 0,
        vararg lore: String
    ): ItemStack {

        val item = ItemStack(material)
        val itemMeta = item.itemMeta
        itemMeta?.setDisplayName(displayName)
        itemMeta?.lore = lore.toList()
        item.itemMeta = itemMeta
        item.amount = amount
        return item
    }
}