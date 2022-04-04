package me.vislavy.testimony.config_system.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocaleModel(
    val prefix: String,
    @SerialName("welcome_message")
    val welcomeMessage: WelcomeMessageLocale,
    val captcha: CaptchaLocale,
    @SerialName("timer_bar")
    val timerBar: TimerBarLocale,
    val registration: RegistrationLocale,
    val authorization: AuthorizationLocale,
    val session: SessionLocale,
    val delaccount: DelaccountLocale,
    val other: OtherLocale
)

@Serializable
data class WelcomeMessageLocale(
    val title: String,
    val subtitle: String
)

@Serializable
data class CaptchaLocale(
    val title: String,
    @SerialName("captcha_not_passed")
    val captchaNotPassed: String
)

@Serializable
data class TimerBarLocale(
    val registration: String,
    val authorization: String,
    val timeout: String
)

@Serializable
data class RegistrationLocale(
    val request: String,
    val success: String,
    @SerialName("wrong_password_length")
    val wrongPasswordLength: String,
    @SerialName("wrong_command")
    val wrongCommand: String,
    val timeout: String
)

@Serializable
data class AuthorizationLocale(
    val request: String,
    val success: String,
    @SerialName("wrong_command")
    val wrongCommand: String,
    @SerialName("wrong_password")
    val wrongPassword: String,
    val timeout: String
)

@Serializable
data class SessionLocale(
    val valid: String,
    val invalid: String
)

@Serializable
data class DelaccountLocale(
    val usage: String,
    val success: String,
    @SerialName("account_not_exists")
    val accountNotExists: String
)

@Serializable
data class OtherLocale(
    @SerialName("access_denied")
    val accessDenied: String,
    @SerialName("only_console")
    val onlyConsole: String,
    val reload: String,
    @SerialName("casual_password_entry")
    val casualPasswordEntry: String
)