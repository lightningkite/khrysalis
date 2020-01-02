package com.lightningkite.kwift.observables.actual

import androidx.annotation.ColorRes
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEvent
import com.lightningkite.kwift.actual.HttpClient
import com.lightningkite.kwift.actual.dateAlone
import com.lightningkite.kwift.actual.dateFrom
import com.lightningkite.kwift.actual.weak
import com.lightningkite.kwift.observables.shared.*
import com.lightningkite.kwift.shared.DateAlone
import com.lightningkite.kwift.shared.TimeAlone
import java.util.*
import kotlin.math.absoluteValue

fun WeekViewEvent(id: Long, title: String, start: Date, end: Date, @ColorRes colorRes: Int): WeekViewEvent {
    val event = WeekViewEvent(
        id,
        title,
        Calendar.getInstance().apply { time = start },
        Calendar.getInstance().apply { time = end })
    event.color = HttpClient.appContext.resources.getColor(colorRes)
    return event
}

val WeekViewEvent.startDate: Date get() = this.startTime.time
val WeekViewEvent.endDate: Date get() = this.endTime.time

private val WVObs = WeakHashMap<WeekView, ReferenceObservableProperty<DateAlone>>()
val WeekView.firstVisibleDayObservable: MutableObservableProperty<DateAlone>
    get() {
        val obs = WVObs.getOrPut(this) {
            val weakSelf by weak(this)
            val changeEvent = StandardEvent<DateAlone>()
            setScrollListener { newFirstVisibleDay, _ ->
                println("Scrolled! $newFirstVisibleDay")
                changeEvent.invokeAll(newFirstVisibleDay.time.dateAlone)
            }
            ReferenceObservableProperty(
                get = { weakSelf?.firstVisibleDay?.time?.dateAlone ?: DateAlone.now() },
                set = {
                    this.goToDate(Calendar.getInstance().apply {
                        time = dateFrom(it, TimeAlone.noon)
                    })
                },
                onChange = changeEvent
            )
        }
        return obs
    }

fun WeekView.bindLoading(
    data: ObservableProperty<(start: Date, end: Date) -> List<WeekViewEvent>>,
    onEventClick: (WeekViewEvent) -> Unit,
    onEmptyClick: (Date) -> Unit
) {
    data.addAndRunWeak(this) { self, load ->
        this.setMonthChangeListener { newYear, newMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(newYear, newMonth - 1, 1, 0, 0, 0)
            val start = Date(calendar.timeInMillis)
            calendar.add(Calendar.MONTH, 1)
            val end = Date(calendar.timeInMillis)
            load(start, end)
        }
        this.notifyDatasetChanged()
    }
    this.setOnEventClickListener { event, eventRect -> onEventClick(event) }
    this.setEmptyViewClickListener { onEmptyClick(it.time) }
}

fun WeekView.bind(
    data: ObservableProperty<List<WeekViewEvent>>,
    onRangeRequest: (start: Date, end: Date) -> Unit,
    onEventClick: (WeekViewEvent) -> Unit,
    onEmptyClick: (Date) -> Unit
) {
    bindLoading(
        data = data.map { data ->
            return@map { start: Date, end: Date ->
                onRangeRequest(start, end)
                val firstIndex = data.binarySearchBy(start.time) { it.startTime.timeInMillis }.absoluteValue.minus(1)
                    .coerceAtLeast(0)
                val lastIndex =
                    data.binarySearchBy(end.time) { it.endTime.timeInMillis }.absoluteValue.coerceAtMost(data.size)
                if (firstIndex < lastIndex) {
                    data.subList(firstIndex, lastIndex).toList()
                } else {
                    listOf()
                }
            }
        },
        onEventClick = onEventClick,
        onEmptyClick = onEmptyClick
    )
}

var WeekView.visibleDays: Int
    get() = numberOfVisibleDays
    set(value){
        val fix = firstVisibleDay
        numberOfVisibleDays = value
        if(fix != null) {
            goToDate(fix)
        }
    }

//fun WeekView.bind(
//    data: (month: DateAlone) -> ObservableProperty<List<WeekViewEvent>>,
//    onEventClick: (WeekViewEvent) -> Unit,
//    onEmptyClick: (Date) -> Unit
//) {
//    this.setMonthChangeListener { newYear, newMonth ->
//        val month = DateAlone(newYear, newMonth, 1)
//        val obs = data(month)
//        obs.addAndRunWeak(this) { self, value ->
//            self.notifyDatasetChanged()
//        }
//        obs.value
//    }
//    this.setOnEventClickListener { event, eventRect -> onEventClick(event) }
//    this.setEmptyViewClickListener { onEmptyClick(it.time) }
//}
