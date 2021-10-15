package com.lightningkite.butterfly.net

import com.fasterxml.jackson.annotation.JsonProperty
import com.lightningkite.butterfly.fromJsonString
import com.lightningkite.butterfly.toJsonString
import org.junit.Assert
import org.junit.Test

class CodableTest {
    data class TestModel(
        @get:JsonProperty("is_active")  val isActive: Boolean = false,
        @get:JsonProperty("is_staff") val isStaff: Boolean = false,
        val baseline: String = "Hello"
    )

//    @Test fun serialize(){
//        val model = TestModel(
//            isActive = true,
//            isStaff = true,
//            baseline = "Hi Low"
//        )
//        println(model.toJsonString())
//        Assert.assertEquals(model, model.toJsonString().fromJsonString<TestModel>())
//    }
}