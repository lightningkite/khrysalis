import java.text.DateFormatSymbols

object TimeNames {
    private val symbols = DateFormatSymbols()
    val shortMonthNames: List<String> = symbols.shortMonths.toList().dropLast(1)
    val monthNames: List<String> = symbols.months.toList().dropLast(1)
    val shortWeekdayNames: List<String> = symbols.shortWeekdays.toList().drop(1)
    val weekdayNames: List<String> = symbols.weekdays.toList().drop(1)
}

fun main(){
    println(TimeNames.shortMonthNames)
    println(TimeNames.monthNames)
    println(TimeNames.shortWeekdayNames)
    println(TimeNames.weekdayNames)
}
