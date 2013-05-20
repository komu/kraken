/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model

import java.util.PriorityQueue
import net.wanhack.model.common.Actor
import net.wanhack.utils.logger
import java.util.logging.Level

class Clock {
    var time = 0

    private val actors = PriorityQueue<ActorInfo>()

    fun tick(ticks: Int, game: Game) {
        if (log.isLoggable(Level.FINER))
            log.finer("ticking the clock for $ticks ticks")

        tick(game, time + ticks)
    }

    private fun tick(game: Game, maxTime: Int) {
        while (!actors.empty && actors.element().nextTick <= maxTime) {
            val actor = actors.remove()
            time = Math.max(time, actor.nextTick)
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

    fun toString() =
        "Clock [time=$time, objects=$actors]"

    class object {
        private val log = javaClass<Clock>().logger()

        private class ActorInfo(private val actor: Actor, var nextTick: Int): Comparable<ActorInfo> {

            fun tick(game: Game, time: Int): Boolean {
                val rate = actor.act(game)
                if (rate > 0) {
                    nextTick = time + rate
                    return true
                } else
                    return false
            }

            val destroyed: Boolean
                get() = actor.destroyed

            fun toString() =
                "($nextTick: $actor)"

            override fun compareTo(other: ActorInfo) =
                nextTick - other.nextTick
        }
    }
}
