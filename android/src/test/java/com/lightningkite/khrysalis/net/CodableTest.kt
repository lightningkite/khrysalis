package com.lightningkite.khrysalis.net

import com.fasterxml.jackson.annotation.JsonProperty
import com.lightningkite.khrysalis.fromJsonString
import com.lightningkite.khrysalis.toJsonString
import org.junit.Assert
import org.junit.Test

class CodableTest {
    data class TestModel(
        val isActive: Boolean = false,
        @get:JsonProperty("is_staff") val isStaff: Boolean = false,
        val baseline: String = "Hello"
    )

    @Test fun serialize(){
        val model = TestModel(
            isActive = true,
            isStaff = true,
            baseline = "Hi Low"
        )
        println(model.toJsonString())
        Assert.assertEquals(model, model.toJsonString().fromJsonString<TestModel>())
    }
}