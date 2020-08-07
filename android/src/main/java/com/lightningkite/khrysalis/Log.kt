package com.lightningkite.khrysalis

interface LogInterface {
    fun v(label: String, value: String)
    fun d(label: String, value: String)
    fun i(label: String, value: String)
    fun w(label: String, value: String)
    fun e(label: String, value: String)
    fun wtf(label: String, value: String)
}
object AndroidLog: LogInterface {
    override fun v(label: String, value: String) { android.util.Log.d(label, value) }
    override fun d(label: String, value: String) { android.util.Log.d(label, value) }
    override fun i(label: String, value: String) { android.util.Log.d(label, value) }
    override fun w(label: String, value: String) { android.util.Log.d(label, value) }
    override fun e(label: String, value: String) { android.util.Log.d(label, value) }
    override fun wtf(label: String, value: String) { android.util.Log.wtf(label, value) }
}
object SystemOutLog: LogInterface {
    override fun v(label: String, value: String) { println("--Verbose-- $label: $value") }
    override fun d(label: String, value: String) { println("--Debug  -- $label: $value") }
    override fun i(label: String, value: String) { println("--Info   -- $label: $value") }
    override fun w(label: String, value: String) { println("--Warning-- $label: $value") }
    override fun e(label: String, value: String) { println("--Error  -- $label: $value") }
    override fun wtf(label: String, value: String) { println("--WTF    -- $label: $value") }
}
var Log: LogInterface = AndroidLog