package me.vislavy.testimony

import com.github.shynixn.mccoroutine.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AuthenticationTimer(private val player: Player) {

    private var timerJob: Job? = null
    private var particlesJob: Job? = null
    private var bar: BossBar? = null

    private val config = plugin.config
    private val locale = plugin.locale

    fun startRegistration() {
        timerJob = plugin.launch {
            bar = Bukkit.createBossBar(
                locale.timerBar.registration,
                BarColor.YELLOW,
                BarStyle.SOLID
            )
            bar?.isVisible = true
            bar?.addPlayer(player)

            for (second in config.registration.timeout downTo 0) {
                val timerBarProgress = second.toFloat() / config.registration.timeout
                bar?.progress = timerBarProgress.toDouble()

                when (second) {
                    0 -> {
                        bar?.setTitle(locale.timerBar.timeout)

                        delay(1000)
                        player.kickPlayer(locale.prefix + locale.registration.timeout)
                        stop()
                    }
                    in 1..10 -> {
                        bar?.color = BarColor.RED
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F)
                    }
                    else -> Unit
                }

                if (second % config.registration.requestInterval == 0) {
                    player.sendMessage(locale.prefix + locale.registration.request)
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F)
                }

                delay(1000)
            }
        }

        startParticles(player)
    }

    fun startAuthorization() {
        timerJob = plugin.launch {
            bar = Bukkit.createBossBar(
                locale.timerBar.authorization,
                BarColor.YELLOW,
                BarStyle.SOLID
            )
            bar?.isVisible = true
            bar?.addPlayer(player)

            for (second in config.authorization.timeout downTo 0) {
                val timerBarProgress = second.toFloat() / config.authorization.timeout
                bar?.progress = timerBarProgress.toDouble()

                when (second) {
                    0 -> {
                        bar?.setTitle(locale.timerBar.timeout)

                        delay(1000)
                        player.kickPlayer(locale.prefix + locale.authorization.timeout)
                        stop()
                    }
                    in 1..10 -> {
                        bar?.color = BarColor.RED
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F)
                    }
                    else -> Unit
                }

                if (second % config.authorization.requestInterval == 0) {
                    player.sendMessage(locale.prefix + locale.authorization.request)
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 1F, 1F)
                }

                delay(1000)
            }
        }

        startParticles(player)
    }

    private fun startParticles(player: Player) {
        var offset = 0.0
        var particleNumber = 0
        particlesJob = plugin.launch {
            while (true) {
                offset += PI / 16
                particleNumber++

                val location = player.location
                val firstParticleLocation = location.clone().add(cos(offset), sin(offset * 0.5) + 1, sin(offset))
                val secondParticleLocation = location.clone().add(cos(offset + PI), sin(offset * 0.5) + 1, sin(offset + PI))

                val particleColor = when {
                    particleNumber % 2 == 0 -> Color.ORANGE
                    particleNumber % 3 == 0 -> Color.YELLOW
                    else -> Color.RED
                }
                val particleOptions = Particle.DustOptions(particleColor, 1F)

                val world = player.world
                world.spawnParticle(Particle.REDSTONE, firstParticleLocation, 0, particleOptions)
                world.spawnParticle(Particle.REDSTONE, secondParticleLocation, 0, particleOptions)

                delay(10)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        particlesJob?.cancel()
        bar?.isVisible = false
    }
}