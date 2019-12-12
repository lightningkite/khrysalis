package com.lightningkite.kwift.views.android

import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.lightningkite.kwift.observables.shared.StandardEvent
import java.text.DateFormat
import java.util.*

class TimeButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var format = DateFormat.getTimeInstance(DateFormat.SHORT)

    var date: Date = Date()
        set(value) {
            field = value
            text = format.format(value)
        }

    var onDateEntered = StandardEvent<Date>()

    init {
        setOnClickListener {
            context.timeSelectorDialog(date) {
                date = it
                onDateEntered.invokeAll(it)
            }
        }
    }
}

fun Context.timeSelectorDialog(start: Date, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    IntervalTimePickerDialog(
        this,
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false,
        TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            onResult(cal.time)
        }
    ).show()
}
