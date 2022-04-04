package me.vislavy.testimony

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.github.shynixn.mccoroutine.setSuspendingExecutor
import me.vislavy.testimony.commands.TestimonyCommand
import me.vislavy.testimony.config_system.configs.LocaleConfig
import me.vislavy.testimony.config_system.configs.PluginConfig
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

    lateinit var pluginConfig: PluginConfig
        private set
    lateinit var localeConfig: LocaleConfig
        private set

    private val unauthorizedPlayers = mutableListOf<Player>()
    private val timers = mutableMapOf<String, AuthenticationTimer>()

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        registerConfigs()
        registerListeners()
        registerCommands()

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
        return timeDifferentInSeconds <= pluginConfig.config.session.timeout
    }

    fun startAuthorizationProcess(player: Player) {
        val timer = AuthenticationTimer(player)
        timers[player.name] = timer

        unauthorizedPlayers.add(player)

        when (isAccountExists(player.name)) {
            true -> timer.startAuthorization()
            false -> timer.startRegistration()
        }
    }

    fun stopAuthorizationProcess(player: Player) {
        unauthorizedPlayers.remove(player)

        timers[player.name]?.stop()
        timers.remove(player.name)
        showWelcomeMessage(player)
    }

    fun isUnauthorized(player: Player) = unauthorizedPlayers.contains(player)

    fun isAccountExists(nickname: String) = Database.getAccount(nickname) != null

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

    fun delaccount(nickname: String): Boolean {
        if (isAccountExists(nickname))  {
            Database.deleteAccount(nickname)
            return true
        }

        return false
    }

    private fun registerConfigs() {
        pluginConfig = PluginConfig()
        pluginConfig.refresh()

        localeConfig = LocaleConfig()
        localeConfig.refresh()
    }

    private fun registerListeners() {
        with(server.pluginManager) {
            registerSuspendingEvents(PlayerListener(), plugin)
            registerSuspendingEvents(MenuListener(), plugin)
        }
    }

    private fun showWelcomeMessage(player: Player) {
        player.sendTitle(
            StringFormatter.format(localeConfig.config.welcomeMessage.title, player.name),
            localeConfig.config.welcomeMessage.subtitle,
            10,
            60,
            10
        )
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F)
    }

    private fun registerCommands() {
        getCommand("testimony")?.setSuspendingExecutor(TestimonyCommand())
    }

    companion object {
        private val logo = """
        
        ████████╗███████╗░██████╗████████╗██╗███╗░░░███╗░█████╗░███╗░░██╗██╗░░░██╗
        ╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝██║████╗░████║██╔══██╗████╗░██║╚██╗░██╔╝
        ░░░██║░░░█████╗░░╚█████╗░░░░██║░░░██║██╔████╔██║██║░░██║██╔██╗██║░╚████╔╝░
        ░░░██║░░░██╔══╝░░░╚═══██╗░░░██║░░░██║██║╚██╔╝██║██║░░██║██║╚████║░░╚██╔╝░░
        ░░░██║░░░███████╗██████╔╝░░░██║░░░██║██║░╚═╝░██║╚█████╔╝██║░╚███║░░░██║░░░
        ░░░╚═╝░░░╚══════╝╚═════╝░░░░╚═╝░░░╚═╝╚═╝░░░░░╚═╝░╚════╝░╚═╝░░╚══╝░░░╚═╝░░░
    """.trimIndent()
    }
}