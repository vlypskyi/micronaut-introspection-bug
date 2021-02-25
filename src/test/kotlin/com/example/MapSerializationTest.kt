package com.example

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.core.annotation.Introspected
import io.micronaut.jackson.modules.BeanIntrospectionModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@Introspected
data class Body(
    @JsonProperty("foo_id")
    val id: Long,

    @get:JsonAnyGetter
    @JsonAnySetter
    val properties: MutableMap<String, Any> = mutableMapOf()
)

@Introspected
data class Wrapper(
    @JsonProperty("body")
    val body: Body
)

class MapSerializationTest {

    @Test
    fun testSerialize() {
        val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val actual = mapper.writeValueAsString(Wrapper(Body(123, mutableMapOf("foo" to "bar"))))
        val expected = """
            {"body":{"foo_id":123,"foo":"bar"}}
        """.trimIndent()
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun testSerializeIntrospected() {
        val mapper = ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(BeanIntrospectionModule())
        val actual = mapper.writeValueAsString(Wrapper(Body(123, mutableMapOf("foo" to "bar"))))
        val expected = """
            {"body":{"foo_id":123,"foo":"bar"}}
        """.trimIndent()
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun testDeserialize() {
        val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        val json = """
            {"body":{"foo_id":123,"foo":"bar"}}
        """.trimIndent()
        val actual: Wrapper = mapper.readValue(json)
        val expected = Wrapper(Body(123,  mutableMapOf("foo" to "bar")))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun testDeserializeIntrospected() {
        val mapper = ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(BeanIntrospectionModule())
        val json = """
            {"body":{"foo_id":123,"foo":"bar"}}
        """.trimIndent()
        val actual: Wrapper = mapper.readValue(json)
        val expected = Wrapper(Body(123, mutableMapOf("foo" to "bar")))
        Assertions.assertEquals(expected, actual)
    }

}
