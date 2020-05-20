package com.lightningkite.khrysalis.net

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
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
import com.lightningkite.khrysalis.Image
import com.lightningkite.khrysalis.PlatformSpecific
import com.lightningkite.khrysalis.bytes.Data
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.loadImage
import com.lightningkite.khrysalis.time.TimeAlone
import com.lightningkite.khrysalis.time.DateAlone
import com.lightningkite.khrysalis.time.iso8601
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

@SuppressLint("StaticFieldLeak")
object HttpClient {

    private var _appContext: Context? = null
    @PlatformSpecific
    var appContext: Context
        get() = _appContext!!
        set(value){
            _appContext = value
            ioScheduler =  Schedulers.io()
            responseScheduler =  AndroidSchedulers.mainThread()
        }

    var ioScheduler: Scheduler? = null
    var responseScheduler: Scheduler? = null
    fun <T> Single<T>.threadCorrectly(): Single<T> {
        var current = this
        if(ioScheduler != null){
            current = current.subscribeOn(ioScheduler)
        }
        if(responseScheduler != null){
            current = current.observeOn(responseScheduler)
        }
        return current
    }
    fun <T> Observable<T>.threadCorrectly(): Observable<T> {
        var current = this
        if(ioScheduler != null){
            current = current.subscribeOn(ioScheduler)
        }
        if(responseScheduler != null){
            current = current.observeOn(responseScheduler)
        }
        return current
    }

    const val GET = "GET"
    const val POST = "POST"
    const val PUT = "PUT"
    const val PATCH = "PATCH"
    const val DELETE = "DELETE"

    @PlatformSpecific
    val client = OkHttpClient.Builder().build()
    @PlatformSpecific
    val mapper = ObjectMapper()
        .registerModule(KotlinModule())
        .registerModule(SimpleModule(
            "EnumFix",
            Version.unknownVersion(),
            mapOf(
                TimeAlone::class.java to object : StdDeserializer<TimeAlone>(
                    TimeAlone::class.java
                ) {
                    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TimeAlone? {
                        if (p.currentToken == JsonToken.VALUE_NULL) return null
                        return TimeAlone.iso(p.text)
                    }
                },
                DateAlone::class.java to object : StdDeserializer<DateAlone>(
                    DateAlone::class.java
                ) {
                    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DateAlone? {
                        if (p.currentToken == JsonToken.VALUE_NULL) return null
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
                            gen.writeString(value.iso8601())
                        }
                    }
                },
                object : StdSerializer<DateAlone>(DateAlone::class.java) {
                    override fun serialize(value: DateAlone?, gen: JsonGenerator, provider: SerializerProvider?) {
                        if (value == null) {
                            gen.writeNull()
                        } else {
                            gen.writeString(value.iso8601())
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

    fun call(
        url: String,
        method: String = HttpClient.GET,
        headers: Map<String, String> = mapOf(),
        body: HttpBody? = null
    ): Single<HttpResponse> {
        val request = Request.Builder()
            .url(url)
            .method(method, body)
            .headers(Headers.of(headers))
            .addHeader("Accept-Language", Locale.getDefault().language)
            .build()
        return Single.create<HttpResponse> { emitter ->
            try {
                println("Sending $method request to $url with headers $headers")
                val response = client.newCall(request).execute()
                println("Response from $method request to $url with headers $headers: ${response.code()}")
                emitter.onSuccess(response)
            } catch(e:Exception) {
                emitter.onError(e)
            }
        }.threadCorrectly()
    }

    fun webSocket(
        url: String
    ): Observable<ConnectedWebSocket> {
        return Observable.using<ConnectedWebSocket, ConnectedWebSocket>(
            {
                val out = ConnectedWebSocket(url)
                out.underlyingSocket = client.newWebSocket(
                    Request.Builder()
                        .url(url.replace("http", "ws"))
                        .addHeader("Accept-Language", Locale.getDefault().language)
                        .build(),
                    out
                )
                out
            },
            { it.ownConnection },
            { it.onComplete() }
        )
    }






    //YONDER LIES OLD CODE
    //Don't use these anymore.  Rx is better.




    var immediateMode = false

    @PlatformSpecific
    inline fun runResult(crossinline action: () -> Unit) {
        if (immediateMode) {
            action()
        } else {
            Handler(Looper.getMainLooper()).post {
                action()
            }
        }
    }

    @PlatformSpecific
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

    @Deprecated("Use Rx Style instead")
    inline fun <reified T : Any> call(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: Any? = null,
        crossinline onResult: @escaping() (code: Int, result: T?, error: String?) -> Unit
    ) {
        println("Sending $method request to $url with headers $headers")
        val request = Request.Builder()
            .url(url)
            .method(method, body?.let {
                val sending = mapper.writeValueAsString(it)
                println("with body $sending")
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
                println("Response ${response.code()}: $raw")
                runResult {
                    val code = response.code()
                    if (code / 100 == 2) {
                        try {
                            val read =
                                mapper.readValue<T>(
                                    raw,
                                    object : TypeReference<T>() {})
                            onResult.invoke(code, read, null)
                        } catch (e: Exception) {
                            Log.e("HttpClient", "Failure to parse: ${e.message}")
                            e.printStackTrace()
                            onResult.invoke(code, null, e.message)
                        }
                    } else {
                        onResult.invoke(code, null, raw ?: "")
                    }
                }
            }
        })
    }

    @Deprecated("Use Rx Style instead")
    inline fun callRaw(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: Any? = null,
        crossinline onResult: @escaping() (code: Int, result: String?, error: String?) -> Unit
    ) {
        println("Sending $method request to $url with headers $headers")
        val request = Request.Builder()
            .url(url)
            .method(method, body?.let {
                val sending = mapper.writeValueAsString(it)
                println("with body $sending")
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
                println("Response ${response.code()}: $raw")
                runResult {
                    val code = response.code()
                    if (code / 100 == 2) {
                        onResult.invoke(code, raw, null)
                    } else {
                        onResult.invoke(code, null, raw ?: "")
                    }
                }
            }
        })
    }

    @Deprecated("Use Rx Style instead")
    inline fun callWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: Any? = null,
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        println("Sending $method request to $url with headers $headers")
        val request = Request.Builder()
            .url(url)
            .method(method, body?.let {
                val sending = mapper.writeValueAsString(it)
                println("with body $sending")
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
                println("Response ${response.code()}: $raw")
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

    @Deprecated("Use Rx Style instead")
    inline fun uploadImageWithoutResult(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        image: Image,
        maxSize: Long = 10_000_000,
        additionalFields: Map<String, String> = mapOf(),
        crossinline onResult: @escaping() (code: Int, error: String?) -> Unit
    ) {
        loadImage(image) { rawImage ->
            if (rawImage == null) {
                onResult(0, "Failed to read image.")
                return@loadImage
            }
            var qualityToTry = 100
            var data = ByteArrayOutputStream().use {
                rawImage.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
                it.toByteArray()
            }
            while (data.size > maxSize) {
                qualityToTry -= 5
                data = ByteArrayOutputStream().use {
                    rawImage.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
                    it.toByteArray()
                }
            }
            Log.i(
                "HttpClient",
                "Sending $method request to $url with headers $headers and image at quality level $qualityToTry"
            )
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
                            for ((key, value) in additionalFields) {
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
                    println("Response ${response.code()}: $raw")
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


    @Deprecated("Use Rx Style instead")
    inline fun <reified T : Any> uploadImage(
        url: String,
        method: String,
        headers: Map<String, String>,
        fieldName: String,
        image: Image,
        maxSize: Long = 10_000_000,
        additionalFields: Map<String, String> = mapOf(),
        crossinline onResult: @escaping() (code: Int, result: T?, error: String?) -> Unit
    ) {
        loadImage(image) { rawImage ->
            if (rawImage == null) {
                onResult(0, null, "Failed to read image.")
                return@loadImage
            }
            var qualityToTry = 100
            var data = ByteArrayOutputStream().use {
                rawImage.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
                it.toByteArray()
            }
            while (data.size > maxSize) {
                qualityToTry -= 5
                data = ByteArrayOutputStream().use {
                    rawImage.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
                    it.toByteArray()
                }
            }
            Log.i(
                "HttpClient",
                "Sending $method request to $url with headers $headers and image at quality level $qualityToTry"
            )
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
                            for ((key, value) in additionalFields) {
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
                        onResult.invoke(0, null, e.message ?: "")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val raw = response.body()!!.string()
                    println("Response ${response.code()}: $raw")
                    runResult {
                        val code = response.code()
                        if (code / 100 == 2) {
                            try {
                                val read =
                                    mapper.readValue<T>(
                                        raw,
                                        object : TypeReference<T>() {})
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
    }
}

