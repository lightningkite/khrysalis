package com.lightningkite.khrysalis.views.android

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentActivity
import com.lightningkite.khrysalis.observables.StandardEvent
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.subjects.PublishSubject
import java.text.DateFormat
import java.util.*

class TimeButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var minuteInterval: Int = 1

    var format = DateFormat.getTimeInstance(DateFormat.SHORT)

    var date: Date = Date()
        set(value) {
            field = value
            text = format.format(value)
        }

    var onDateEntered = PublishSubject.create<Date>()

    init {
        setOnClickListener {
            context.timeSelectorDialog(date, minuteInterval) {
                date = it
                onDateEntered.onNext(it)
            }
        }
    }
}

fun Context.timeSelectorDialog(start: Date, minuteInterval: Int = 1, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    (this as FragmentActivity)
    TimePickerDialog.newInstance(
        { view, hour, minute, second ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            onResult(cal.time)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    ).apply {
        setTimeInterval(1, minuteInterval)
        show(this@timeSelectorDialog.supportFragmentManager, "Select Time")
    }
}
