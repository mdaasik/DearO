package com.carworkz.dearo

import com.carworkz.dearo.utils.Utility
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private var email = "singh.kush100@gmail.com"

    @Test
    fun testEmailValidator() {
        assertEquals(Utility.isEmailValid(email), true)
    }
}
