package dev.komu.kraken.model

import dev.komu.kraken.model.common.Actor
import dev.komu.kraken.utils.logger
import java.lang.Math.max
import java.util.*

class Clock {
    var time = 0

    private val actors = PriorityQueue<ActorInfo>()

    fun tick(ticks: Int, game: Game) {
        log.finer("ticking the clock for $ticks ticks")

        tick(game, time + ticks)
    }

    private fun tick(game: Game, maxTime: Int) {
        while (!actors.isEmpty() && actors.element().nextTick <= maxTime) {
            val actor = actors.remove()
            time = max(time, actor.nextTick)
            if (!actor.destroyed) {
                val reschedule = actor.tick(game, time)
                if (reschedule)
                    actors.add(actor)
            }
        }

        time = maxTime
    }

    fun clear() {
        actors.clear()
    }

    fun schedule(ticks: Int, actor: Actor) {
        actors.add(ActorInfo(actor, time + ticks))
    }

    override fun toString() =
        "Clock [time=$time, objects=$actors]"

    companion object {
        private val log = Clock::class.java.logger()

        private class ActorInfo(private val actor: Actor, var nextTick: Int): Comparable<ActorInfo> {

            fun tick(game: Game, time: Int): Boolean {
                val rate = actor.act(game)
                return if (rate > 0) {
                    nextTick = time + rate
                    true
                } else
                    false
            }

            val destroyed: Boolean
                get() = actor.destroyed

            override fun toString() =
                "($nextTick: $actor)"

            override fun compareTo(other: ActorInfo) =
                nextTick - other.nextTick
        }
    }
}
