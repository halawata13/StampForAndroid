package net.halawata.stamp.data

import java.io.Serializable
import java.util.*

data class Location(
        val id: String,
        val latitude: Double,
        val longitude: Double,
        val createDate: Date,
        val memo: String
) : Serializable
