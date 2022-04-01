package me.vislavy.testimony.local.data

data class Account(
    val nickname: String,
    val ip: String,
    val lastSeen: Long,
    val encryptedPassword: String,
    val seed: String
)
