package me.vislavy.testimony.utils

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordEncrypter {

    private const val seedSize = 8
    private const val secretKeySize = 256
    private const val iterations = 20000

    fun encrypt(passwd: String, seed: String): String {
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val byteSeed = seed.split(":").map { it.toByte() }
        val keySpec = PBEKeySpec(passwd.toCharArray(), byteSeed.toByteArray(), iterations, secretKeySize)
        val encryptedPasswd = secretKeyFactory.generateSecret(keySpec).encoded
        return encryptedPasswd.joinToString(":")
    }

    fun verify(passwd: String, encryptedPasswd: String, seed: String): Boolean {
        val originalEncryptedPasswd = encrypt(passwd, seed)
        return (originalEncryptedPasswd == encryptedPasswd)
    }

    fun generateSeed(): String {
        val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        val seed = ByteArray(seedSize)
        secureRandom.nextBytes(seed)
        return seed.joinToString(":")
    }
}