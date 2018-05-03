package net.halawata.stamp.data

import java.io.Serializable
import java.util.*

data class DateRange(
        val from: Date,
        val to: Date,
        val rangeName: String
) : Serializable
