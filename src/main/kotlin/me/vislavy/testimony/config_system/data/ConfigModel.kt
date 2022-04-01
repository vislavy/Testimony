package me.vislavy.testimony.config_system.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigModel(
    val captcha: CaptchaConfig,
    val registration: RegistrationConfig,
    val authorization: AuthenticationConfig,
    val session: SessionConfig
)

@Serializable
data class CaptchaConfig(
    val enabled: Boolean
)

@Serializable
data class RegistrationConfig(
    val timeout: Int,
    @SerialName("request_interval")
    val requestInterval: Int,
    @SerialName("password_length")
    val passwordLength: PasswordLengthConfig,
)

@Serializable
data class PasswordLengthConfig(
    val min: Int,
    val max: Int
)

@Serializable
data class AuthenticationConfig(
    val timeout: Int,
    @SerialName("request_interval")
    val requestInterval: Int,
)

@Serializable
data class SessionConfig(
    val enabled: Boolean,
    val timeout: Int
)