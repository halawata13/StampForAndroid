package net.halawata.stamp.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.at0(): Date {
    val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
    return formatter.parse(formatter.format(this))
}
