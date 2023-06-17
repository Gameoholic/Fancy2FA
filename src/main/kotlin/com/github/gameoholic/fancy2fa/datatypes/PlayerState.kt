package com.github.gameoholic.fancy2fa.datatypes

import org.bukkit.inventory.Inventory

//menuInventory is the currently opened inventory, used in InventoryCloseListener
data class PlayerState(val type: PlayerStateType, val data: Any? = null, val menuInventory: Inventory? = null)