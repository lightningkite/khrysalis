package com.lightningkite.kwift.views.android

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import com.lightningkite.kwift.observables.shared.StandardEvent
import java.text.DateFormat
import java.util.*

class DateButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatButton(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var format = DateFormat.getDateInstance(DateFormat.SHORT)

    var date: Date = Date()
        set(value) {
            field = value
            text = format.format(value)
        }

    var onDateEntered = StandardEvent<Date>()

    init {
        setOnClickListener {
            context.dateSelectorDialog(date) {
                date = it
                onDateEntered.invokeAll(it)
            }
        }
    }
}
fun Context.dateSelectorDialog(start: Date, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onResult(cal.time)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}
