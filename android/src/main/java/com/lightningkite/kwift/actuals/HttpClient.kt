package com.lightningkite.kwift.actuals

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
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

    const val GET = "GET"
    const val POST = "POST"
    const val PUT = "PUT"
    const val PATCH = "PATCH"
    const val DELETE = "DELETE"

    val client = OkHttpClient.Builder().build()
    val mapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(SimpleModule("EnumFix", Version.unknownVersion(), listOf(
            object : StdSerializer<Enum<*>>(Enum::class.java) {
                override fun serialize(value: Enum<*>?, gen: JsonGenerator, provider: SerializerProvider?) {
                    gen.writeString(value?.name?.toLowerCase() ?: "")
                }
            }
        )))
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", "Failure: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(0, null, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                Handler(Looper.getMainLooper()).post {
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

    inline fun <reified T : Any> uploadImageWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        image: ImageData,
        crossinline onResult: @escaping() (code: Int, result: T?, error: String?) -> Unit
    ) {
        Log.i("HttpClient", "Sending $method request to $url with headers $headers and image")
        val data = ByteArrayOutputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 90, it)
            it.toByteArray()
        }
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
                    .build()
            )
            .headers(Headers.of(headers))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(0, null, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                Handler(Looper.getMainLooper()).post {
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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", "Failure: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(0, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                Handler(Looper.getMainLooper()).post {
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

    inline fun uploadImageWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        image: ImageData,
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        Log.i("HttpClient", "Sending $method request to $url with headers $headers and image")
        val data = ByteArrayOutputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 90, it)
            it.toByteArray()
        }
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
                    .build()
            )
            .headers(Headers.of(headers))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(0, e.message ?: "")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val raw = response.body()!!.string()
                Log.i("HttpClient", "Response ${response.code()}: $raw")
                Handler(Looper.getMainLooper()).post {
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

