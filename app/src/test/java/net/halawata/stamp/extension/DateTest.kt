package net.halawata.stamp.extension

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class DateTest {

    @Test
    fun at0() {
        val date = Date()
        val current = Calendar.getInstance().apply {
            time = date
        }
        val at0 = Calendar.getInstance().apply {
            time = date.at0()
        }

        assertEquals(current.get(Calendar.YEAR), at0.get(Calendar.YEAR))
        assertEquals(current.get(Calendar.MONTH), at0.get(Calendar.MONTH))
        assertEquals(current.get(Calendar.DAY_OF_MONTH), at0.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, at0.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, at0.get(Calendar.MINUTE))
        assertEquals(0, at0.get(Calendar.SECOND))
    }
}
