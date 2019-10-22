package org.kollektions.transformations.examples.consumers

import org.kollektions.consumers.always
import org.kollektions.consumers.consume
import org.kollektions.consumers.never
import org.kollektions.consumers.sometimes
import kotlin.test.assertEquals
import kotlin.test.Test

class AlwaysSometimesNever {
    @Test
    fun `condition sometimes met`() {
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 }
        )
        print(actual)
        assertEquals(listOf(false, false, true), actual)
    }
}
