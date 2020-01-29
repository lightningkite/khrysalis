package com.lightningkite.khrysalis.views.geometry

@Suppress("EnumEntryName")
enum class Align {
    start, center, end, fill
}

data class AlignPair(val horizontal: Align, val vertical: Align) {

    companion object{
        val center = AlignPair(
            horizontal = Align.center,
            vertical = Align.center
        )
        val fill = AlignPair(
            horizontal = Align.fill,
            vertical = Align.fill
        )

        val topLeft = AlignPair(
            horizontal = Align.start,
            vertical = Align.start
        )
        val topCenter = AlignPair(
            horizontal = Align.center,
            vertical = Align.start
        )
        val topFill = AlignPair(
            horizontal = Align.fill,
            vertical = Align.start
        )
        val topRight = AlignPair(
            horizontal = Align.end,
            vertical = Align.start
        )
        val centerLeft = AlignPair(
            horizontal = Align.start,
            vertical = Align.center
        )
        val centerCenter = AlignPair(
            horizontal = Align.center,
            vertical = Align.center
        )
        val centerFill = AlignPair(
            horizontal = Align.fill,
            vertical = Align.center
        )
        val centerRight = AlignPair(
            horizontal = Align.end,
            vertical = Align.center
        )
        val fillLeft = AlignPair(
            horizontal = Align.start,
            vertical = Align.fill
        )
        val fillCenter = AlignPair(
            horizontal = Align.center,
            vertical = Align.fill
        )
        val fillFill = AlignPair(
            horizontal = Align.fill,
            vertical = Align.fill
        )
        val fillRight = AlignPair(
            horizontal = Align.end,
            vertical = Align.fill
        )
        val bottomLeft = AlignPair(
            horizontal = Align.start,
            vertical = Align.end
        )
        val bottomCenter = AlignPair(
            horizontal = Align.center,
            vertical = Align.end
        )
        val bottomFill = AlignPair(
            horizontal = Align.fill,
            vertical = Align.end
        )
        val bottomRight = AlignPair(
            horizontal = Align.end,
            vertical = Align.end
        )
    }

}
