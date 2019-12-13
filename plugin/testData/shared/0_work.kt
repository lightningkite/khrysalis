package com.hammerprice.lk

import com.hammerprice.lk.model.Advertisement
import com.hammerprice.lk.model.Lot
import java.util.*
import kotlin.math.max
import kotlin.math.min

class AdPlacer(ads: List<Advertisement>, val spacing: Int = 4) {
    val specialAds: Map<String, Advertisement> = ads.asSequence().flatMap { it.lots.asSequence().map { lot -> lot to it } }.associate { it }

    //Weighted shuffle
    val otherAds: List<Advertisement> = run {
        val random = Random()
        val remainingAds = ads.toMutableList()
        remainingAds.removeAll { it.lots.isNotEmpty() }
        var totalWeight = remainingAds.sumBy { it.priority }
        if (totalWeight == 0) {
            return@run remainingAds
        }
        val output = ArrayList<Advertisement>(ads.size)
        while (remainingAds.isNotEmpty()) {
            var selection = random.nextInt(totalWeight)
            val selectedIndex = remainingAds.indexOfFirst { item ->
                selection -= item.priority
                selection < 0
            }
            val item = remainingAds.removeAt(selectedIndex)
            totalWeight -= item.priority
            output.add(item)
            if (totalWeight == 0) {
                return@run output
            }
        }
        output
    }

    fun <T : Any> place(sourceList: List<T>): List<Any> {
        if (otherAds.isEmpty()) return sourceList
        val list = ArrayList<Any>()
        var adIndex = 0
        var index = 0
        outer@ while (index < sourceList.size - spacing) {
            //Check for special lots
            if (sourceList.getOrNull(index) is Lot) {
                //Add first X ads
                for (iter in 0 until spacing) {
                    val item = sourceList[index]
                    val specialAd = (item as? Lot)?.id?.let { specialAds[it] }
                    //If we find a special ad to insert, add it and break out and we'll continue from there.
                    if (specialAd != null) {
                        list.add(specialAd)
                        list.add(item)
                        index += 1
                        continue@outer
                    } else {
                        list.add(item)
                        index += 1
                    }
                }
                //Check ahead for any more special ads
                for (checkAheadIndex in 0 until min(spacing - 1, sourceList.lastIndex - index)) {
                    val item = sourceList[index + checkAheadIndex]
                    val specialAd = (item as? Lot)?.id?.let { specialAds[it] }
                    if (specialAd != null) {
                        //We found an add at checkAheadIndex;
                        //add all of the lots inbetween
                        for (i in 0 until checkAheadIndex) {
                            list.add(sourceList[index + i])
                        }
                        //insert an the ad and its lot
                        list.add(specialAd)
                        list.add(item)
                        //Break out and continue from here
                        index += checkAheadIndex + 1
                        continue@outer
                    }
                }
                list.add(otherAds[(adIndex++) % otherAds.size])
            } else {
                //Add first X ads
                for (iter in 0 until spacing) {
                    val item = sourceList[index]
                    list.add(item)
                    index += 1
                }
                //Place an ad
                list.add(otherAds[(adIndex++) % otherAds.size])
            }
        }
        for (i in index..sourceList.lastIndex) {
            list.add(sourceList[i])
        }
        return list
    }
}
