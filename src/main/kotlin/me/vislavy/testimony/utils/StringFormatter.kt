package me.vislavy.testimony.utils

object StringFormatter {

    fun format(value: String, vararg args: Any): String {
        var formattedValue = value
        args.forEachIndexed { index, arg ->
            formattedValue = formattedValue.replace("{$index}", arg.toString())
        }

        return formattedValue
    }
}