package com.lightningkite.kwift.actual

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

@SuppressLint("StaticFieldLeak")
object HttpClient {

    lateinit var appContext: Context

    var immediateMode = false
    inline fun runResult(crossinline action: () -> Unit) {
        if (immediateMode) {
            action()
        } else {
            Handler(Looper.getMainLooper()).post {
                action()
            }
        }
    }

    inline fun Call.go(callback: Callback) {
        if (immediateMode) {
            try {
                val result = execute()
                callback.onResponse(this, result)
            } catch (e: IOException) {
                callback.onFailure(this, e)
            }
        } else {
            enqueue(callback)
        }
    }

    const val GET = "GET"
    const val POST = "POST"
    const val PUT = "PUT"
    const val PATCH = "PATCH"
    const val DELETE = "DELETE"

    val client = OkHttpClient.Builder().build()
    val mapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(SimpleModule(
            "EnumFix",
            Version.unknownVersion(),
            mapOf(
                TimeAlone::class.java to object : StdDeserializer<TimeAlone>(
                    TimeAlone::class.java){
                    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TimeAlone? {
                        if(p.currentToken == JsonToken.VALUE_NULL) return null
                        return TimeAlone.iso(p.text)
                    }
                },
                DateAlone::class.java to object : StdDeserializer<DateAlone>(
                    DateAlone::class.java){
                    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DateAlone? {
                        if(p.currentToken == JsonToken.VALUE_NULL) return null
                        return DateAlone.iso(p.text)
                    }
                }
            ),
            listOf(
                object : StdSerializer<Enum<*>>(Enum::class.java) {
                    override fun serialize(value: Enum<*>?, gen: JsonGenerator, provider: SerializerProvider?) {
                        if (value == null) {
                            gen.writeNull()
                        } else {
                            gen.writeString(value.name.toLowerCase())
                        }
                    }
                },
                object : StdSerializer<TimeAlone>(TimeAlone::class.java) {
                    override fun serialize(value: TimeAlone?, gen: JsonGenerator, provider: SerializerProvider?) {
                        if (value == null) {
                            gen.writeNull()
                        } else {
                            gen.writeString(value.iso())
                        }
                    }
                },
                object : StdSerializer<DateAlone>(DateAlone::class.java) {
                    override fun serialize(value: DateAlone?, gen: JsonGenerator, provider: SerializerProvider?) {
                        if (value == null) {
                            gen.writeNull()
                        } else {
                            gen.writeString(value.iso())
                        }
                    }
                }
            )
        ))
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .setDateFormat(StdDateFormat().withLenient(true))

    inline fun <reified T : Any> call(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: Any? = null,
        crossinline onResult: @escaping() (code: Int, result: T?, error: String?) -> Unit
    ) {
        Log.i("HttpClient", "Sending $method request to $url with headers $headers")
        val request = Request.Builder()
            .url(url)
            .method(method, body?.let {
                val sending = mapper.writeValueAsString(it)
                Log.i("HttpClient", "with body $sending")
                RequestBody.create(MediaType.parse("application/json"), sending)
            })
            .headers(Headers.of(headers))
            .addHeader("Accept-Language", Locale.getDefault().language)
            .build()

        client.newCall(request).go(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", "Failure: ${e.message}")
                runResult {
                    onResult.invoke(0, null, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                runResult {
                    val code = response.code()
                    if (code / 100 == 2) {
                        try {
                            val read =
                                mapper.readValue<T>(raw, object : TypeReference<T>() {})
                            onResult.invoke(code, read, null)
                        } catch (e: Exception) {
                            Log.e("HttpClient", "Failure to parse: ${e.message}")
                            onResult.invoke(code, null, e.message)
                        }
                    } else {
                        onResult.invoke(code, null, raw ?: "")
                    }
                }
            }
        })
    }

    inline fun callWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: Any? = null,
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        Log.i("HttpClient", "Sending $method request to $url with headers $headers")
        val request = Request.Builder()
            .url(url)
            .method(method, body?.let {
                val sending = mapper.writeValueAsString(it)
                Log.i("HttpClient", "with body $sending")
                RequestBody.create(MediaType.parse("application/json"), sending)
            })
            .headers(Headers.of(headers))
            .addHeader("Accept-Language", Locale.getDefault().language)
            .build()

        client.newCall(request).go(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", "Failure: ${e.message}")
                runResult {
                    onResult.invoke(0, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                runResult {
                    val code = response.code()
                    if (code / 100 == 2) {
                        onResult.invoke(response.code(), null)
                    } else {
                        onResult.invoke(code, raw ?: "")
                    }
                }
            }
        })
    }

    inline fun uploadImageReferenceWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        imageReference: ImageReference,
        maxSize: Long = 10_000_000,
        additionalFields: Map<String, String> = mapOf(),
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        uploadImageWithoutResult(
            url = url,
            method = method,
            headers = headers,
            fieldName = fieldName,
            image = MediaStore.Images.Media.getBitmap(appContext.contentResolver, imageReference),
            maxSize = maxSize,
            additionalFields = additionalFields,
            onResult = onResult
        )
    }

    inline fun uploadImageWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        image: ImageData,
        maxSize: Long = 10_000_000,
        additionalFields: Map<String, String> = mapOf(),
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        var qualityToTry = 100
        var data = ByteArrayOutputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
            it.toByteArray()
        }
        while(data.size > maxSize) {
            qualityToTry -= 5
            data = ByteArrayOutputStream().use {
                image.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
                it.toByteArray()
            }
        }
        Log.i("HttpClient", "Sending $method request to $url with headers $headers and image at quality level $qualityToTry")
        val request = Request.Builder()
            .url(url)
            .method(
                method,
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        fieldName,
                        "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), data)
                    )
                    .let { it ->
                        var result = it
                        for((key, value) in additionalFields){
                            result = result.addFormDataPart(key, value)
                        }
                        result
                    }
                    .build()
            )
            .headers(Headers.of(headers))
            .build()

        client.newCall(request).go(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runResult {
                    onResult.invoke(0, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                runResult {
                    val code = response.code()
                    if (code / 100 == 2) {
                        onResult.invoke(response.code(), null)
                    } else {
                        onResult.invoke(code, raw ?: "")
                    }
                }
            }
        })
    }
}

