package java.time

@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofMillis(this)", "java.time.Duration"))
fun Long.milliseconds(): Duration = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofMillis(this.toLong())", "java.time.Duration"))
fun Int.milliseconds(): Duration = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofSeconds(this.toLong())", "java.time.Duration"))
fun Int.seconds(): Duration = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofMinutes(this.toLong())", "java.time.Duration"))
fun Int.minutes(): Duration = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofHours(this.toLong())", "java.time.Duration"))
fun Int.hours(): Duration = throw NotImplementedError()
@Deprecated("Use Java 9 time", ReplaceWith("Duration.ofDays(this.toLong())", "java.time.Duration"))
fun Int.days(): Duration = throw NotImplementedError()
