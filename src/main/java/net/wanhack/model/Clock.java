/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.model;

import java.io.Serializable;
import java.util.PriorityQueue;

import net.wanhack.model.common.Actor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The game clock. The clock keeps track of all {@link Actor} objects
 * and sends them tick events at appropriate times. 
 */
public final class Clock implements Serializable {

    /** The current tick */
    private int time = 0;
    
    /** The objects to tick */
    private final PriorityQueue<ActorInfo> actors =
        new PriorityQueue<ActorInfo>();
    
    private static final long serialVersionUID = 0;
    
    private static final Log log = LogFactory.getLog(Clock.class);
    
    /**
     * Go forward in time 
     * 
     * @param ticks amount of ticks to go forward
     * @param game to pass to ticked objects
     */
    public void tick(int ticks, Game game) {
        if (log.isTraceEnabled()) {
            log.trace("ticking the clock for " + ticks + " ticks");
        }
        
        assert ticks > 0 : "non-positive ticks: " + ticks;

        tick(game, time + ticks);
    }
    
    private void tick(Game game, int maxTime) {
        while (!actors.isEmpty() && actors.peek().nextTick <= maxTime) {
            ActorInfo actor = actors.poll();
            
            time = Math.max(time, actor.nextTick);
            
            if (!actor.isDestroyed()) {
                boolean reschedule = actor.tick(game, time);
                if (reschedule) {
                    actors.add(actor);
                }
            }
        }
        
        time = maxTime;
    }

    /**
     * Returns the time that has elapsed in the game, in ticks.
     */
    public int getTime() {
        return time;
    }
    
    public void clear() {
        actors.clear();
    }
    
    public void schedule(int ticks, Actor actor) {
        assert actor != null : "null actor";
        assert ticks >= 0 : "negative ticks: " + ticks;
        
        actors.add(new ActorInfo(actor, time + ticks));
    }
    
    @Override
    public String toString() {
        return "Clock [time=" + time + ", objects=" + actors + "]";
    }
    
    private static class ActorInfo implements Comparable<ActorInfo>, Serializable {
        private final Actor actor;
        private int nextTick;
        private static final long serialVersionUID = 0;
        
        public ActorInfo(Actor actor, int nextTick) {
            this.actor = actor;
            this.nextTick = nextTick;
        }
        
        public boolean tick(Game game, int time) {
            int rate = actor.act(game);
            if (rate > 0) {
                nextTick = time + rate;
                return true;
            } else {
                return false;
            }
        }
        
        public boolean isDestroyed() {
            return actor.isDestroyed();
        }
        
        @Override
        public String toString() {
            return "(" + nextTick + ": " + actor + ")";
        }
        
        public int compareTo(ActorInfo o) {
            return nextTick - o.nextTick;
        }
    }
}
