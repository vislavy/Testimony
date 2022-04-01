package me.vislavy.testimony.utils

import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.filter.AbstractFilter

class LoggerFilter : AbstractFilter() {

    private val commandBlacklist = listOf(
        "/l",
        "/reg",
        "/changepassword",
        "/deleteaccount"
    )

    override fun filter(event: LogEvent?): Filter.Result {
        val message = event?.message ?: return Filter.Result.NEUTRAL
        message.formattedMessage?.let {
            commandBlacklist.forEach { command ->
                if (it.contains(command)) return Filter.Result.DENY
            }
        }

        return Filter.Result.NEUTRAL
    }
}