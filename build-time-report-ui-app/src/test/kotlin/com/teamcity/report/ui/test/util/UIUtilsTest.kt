package com.teamcity.report.ui.test.util

import com.teamcity.report.ui.util.durationRepresentation
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Dmitry Zhuravlev
 *         Date:  02.11.2017
 */
class UIUtilsTest {
    @Test
    fun durationRepresentation() {
        assertEquals("10 ms ", durationRepresentation(10L))
        assertEquals("1 sec ", durationRepresentation(1_000L))
        assertEquals("1 min 1 sec ", durationRepresentation(61_000L))
        assertEquals("1 hour 1 min 1 sec ", durationRepresentation(3_661_000L))
    }
}