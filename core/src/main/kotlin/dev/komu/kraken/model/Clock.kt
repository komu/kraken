package dev.komu.kraken.model

import java.util.*

class Clock {
    var time = 0

    private val tasks = PriorityQueue<ScheduleTask>()

    fun tick() {
        while (!tasks.isEmpty() && tasks.element().nextTick <= time) {
            tasks.remove().callback()
        }

        time += 1
    }

    fun clear() {
        tasks.clear()
    }

    fun scheduleOnce(turns: Int, callback: () -> Unit) {
        tasks.add(ScheduleTask(time + turns, callback))
    }

    fun schedulePeriodic(turns: Int, callback: () -> Unit) {
        scheduleOnce(turns) {
            callback()
            schedulePeriodic(turns, callback)
        }
    }

    override fun toString() =
        "Clock [time=$time, objects=$tasks]"

    private class ScheduleTask(var nextTick: Int, val callback: () -> Unit) : Comparable<ScheduleTask> {
        override fun compareTo(other: ScheduleTask) =
            nextTick - other.nextTick
    }
}
