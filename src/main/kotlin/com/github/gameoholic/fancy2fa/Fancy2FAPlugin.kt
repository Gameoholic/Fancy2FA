package com.github.gameoholic.fancy2fa

import org.bukkit.plugin.java.JavaPlugin

class Fancy2FAPlugin : JavaPlugin() {

        override fun onEnable() {
                Fancy2FA.onEnable(this)
        }
}