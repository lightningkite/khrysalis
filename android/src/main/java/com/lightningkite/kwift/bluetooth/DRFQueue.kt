package com.lightningkite.kwift.bluetooth

import com.lightningkite.kwift.async.DelayedResultFunction
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class DRFQueue {

    private val busy = AtomicBoolean(false)
    private val queue = ConcurrentLinkedQueue<DelayedResultFunction<*>>()
    fun enqueue(debugMessage: String, action: DelayedResultFunction<*>){
        println("enqueue at size ${queue.size} - $debugMessage")
        queue.add(action)
        tryAction()
    }

    fun tryAction() {
        if (busy.compareAndSet(false, true)) {
            val nextAction: DelayedResultFunction<*>? = queue.poll()
            println("Starting BLE task. Queue size: ${queue.size}")
            nextAction?.invoke {
                println("BLE task complete.")
                busy.set(false)
                tryAction()
            } ?: run {
                busy.set(false)
            }
        } else {
            println("BLE busy, waiting...")
        }
    }
}
