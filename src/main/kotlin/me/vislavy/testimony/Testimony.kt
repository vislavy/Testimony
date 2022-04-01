package me.vislavy.testimony

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import me.vislavy.testimony.config_system.configs.LocaleConfig
import me.vislavy.testimony.config_system.configs.PluginConfig
import me.vislavy.testimony.config_system.data.ConfigModel
import me.vislavy.testimony.config_system.data.LocaleModel
import me.vislavy.testimony.listeners.MenuListener
import me.vislavy.testimony.listeners.PlayerListener
import me.vislavy.testimony.local.data.Account
import me.vislavy.testimony.local.database.Database
import me.vislavy.testimony.utils.PasswordEncrypter
import me.vislavy.testimony.utils.StringFormatter
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

val plugin = JavaPlugin.getPlugin(Testimony::class.java)

class Testimony : SuspendingJavaPlugin() {

    lateinit var config: ConfigModel
        private set

    lateinit var locale: LocaleModel
        private set

    private val unauthorizedPlayers = mutableListOf<Player>()
    private val timers = mutableMapOf<String, AuthenticationTimer>()

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        registerConfigs()
        registerListeners()

        Database.openConnection()

        logger.info("§6$logo")
    }

    override suspend fun onDisableAsync() {
        super.onDisableAsync()

        Database.closeConnection()
    }

    fun checkSession(player: Player): Boolean {
        val account = Database.getAccount(player.name) ?: return false
        if (player.address!!.hostString != account.ip) return false

        val currentDate = Date()
        val timeDifferentInSeconds = (currentDate.time - account.lastSeen) / 1000
        if (timeDifferentInSeconds >= config.session.timeout) return false

        return true
    }

    fun startAuthenticationProcess(player: Player) {
        val timer = AuthenticationTimer(player)
        timers[player.name] = timer

        unauthorizedPlayers.add(player)

        val account = Database.getAccount(player.name)
        if (account == null) {
            timer.startRegistration()
            return
        }

        timer.startAuthorization()
    }

    fun stopAuthorizationProcess(player: Player) {
        unauthorizedPlayers.remove(player)

        timers[player.name]?.stop()
        timers.remove(player.name)
        showWelcomeMessage(player)
    }

    fun isUnauthorized(player: Player) = unauthorizedPlayers.contains(player)

    fun register(player: Player, password: String) {
        val seed = PasswordEncrypter.generateSeed()
        val encryptedPassword = PasswordEncrypter.encrypt(password, seed)
        val account = Account(
            nickname = player.name,
            ip = player.address!!.hostString,
            lastSeen = Date().time,
            encryptedPassword = encryptedPassword,
            seed = seed
        )
        Database.addAccount(account)
    }

    fun tryAuth(player: Player, password: String): Boolean {
        val account = Database.getAccount(player.name) ?: return false
        val encryptedPassword = PasswordEncrypter.encrypt(password, account.seed)
        if (encryptedPassword != account.encryptedPassword) return false

        val updatedAccount = account.copy(ip = player.address!!.hostString, lastSeen = Date().time)
        Database.updateAccount(player.name, updatedAccount)
        return true
    }

    private fun registerConfigs() {
        val pluginConfig = PluginConfig()
        pluginConfig.refresh()
        config = pluginConfig.config

        val localeConfig = LocaleConfig()
        localeConfig.refresh()
        locale = localeConfig.config
    }

    private fun registerListeners() {
        with(server.pluginManager) {
            registerSuspendingEvents(PlayerListener(), plugin)
            registerSuspendingEvents(MenuListener(), plugin)
        }
    }

    private fun showWelcomeMessage(player: Player) {
        player.sendTitle(
            StringFormatter.format(locale.welcomeMessage.title, player.name),
            locale.welcomeMessage.subtitle,
            10,
            60,
            10
        )
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F)
    }

//    private fun registerCommands() {
//        getCommand("register")?.setSuspendingExecutor(RegisterCommand())
//        getCommand("login")?.setSuspendingExecutor(LoginCommand())
//    }

    companion object {
        private val logo = """
        
        ████████╗███████╗░██████╗████████╗██╗███╗░░░███╗░█████╗░███╗░░██╗██╗░░░██╗
        ╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝██║████╗░████║██╔══██╗████╗░██║╚██╗░██╔╝
        ░░░██║░░░█████╗░░╚█████╗░░░░██║░░░██║██╔████╔██║██║░░██║██╔██╗██║░╚████╔╝░
        ░░░██║░░░██╔══╝░░░╚═══██╗░░░██║░░░██║██║╚██╔╝██║██║░░██║██║╚████║░░╚██╔╝░░
        ░░░██║░░░███████╗██████╔╝░░░██║░░░██║██║░╚═╝░██║╚█████╔╝██║░╚███║░░░██║░░░
        ░░░╚═╝░░░╚══════╝╚═════╝░░░░╚═╝░░░╚═╝╚═╝░░░░░╚═╝░╚════╝░╚═╝░░╚══╝░░░╚═╝░░░
                                        ▄█░░░░░█▀█
                                        ░█░░▄░░█▄█
    """.trimIndent()
    }
}