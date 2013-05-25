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

package net.wanhack.android

import java.lang.Math.abs
import android.app.Activity
import android.os.Bundle
import net.wanhack.model.GameFacade
import net.wanhack.model.GameConfiguration
import net.wanhack.model.common.Console
import net.wanhack.model.item.Item
import net.wanhack.model.common.Direction
import net.wanhack.definitions.*
import android.util.Log
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.ServiceProvider
import net.wanhack.service.region.RegionLoader
import android.view.ContextMenu
import android.view.View
import android.view.MenuItem
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import net.wanhack.model.common.Directions

class GameActivity : Activity() {

    var game: GameFacade? = null
    val gameView: GameView
        get() = findViewById(R.id.gameView) as GameView

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        val objectFactory = ObjectFactory()
        objectFactory.addDefinitions(Weapons)
        objectFactory.addDefinitions(Items)
        objectFactory.addDefinitions(Creatures)

        val myConsole = MyConsole()
        ServiceProvider.console = myConsole
        ServiceProvider.objectFactory = objectFactory
        ServiceProvider.regionLoader = RegionLoader(objectFactory)

        setContentView(R.layout.game)
        registerForContextMenu(gameView)

        val gestureDetector = GestureDetector(this, MyGestureListener())
        gameView.setOnTouchListener(View.OnTouchListener { (v, event) ->
            gestureDetector.onTouchEvent(event)
        })

        game = GameFacade(GameConfiguration(), false, myConsole) { b ->
            Log.d(tag, "game updated: $b")
            gameView.invalidate()
        }

        gameView.game = game!!

        game!!.start()
    }

    fun moveTowards(dir: Direction) {
        game!!.movePlayer(dir)
    }

    public override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        getMenuInflater()!!.inflate(R.menu.game_actions, menu)
    }

    public override fun onContextItemSelected(item: MenuItem?): Boolean {
        val game = this.game!!
        when (item!!.getItemId()) {
            R.id.wield      -> game.wield()
            R.id.wear       -> game.wear()
            R.id.rest       -> game.rest(-1)
            R.id.talk       -> game.talk()
            R.id.open_door  -> game.openDoor()
            R.id.close_door -> game.closeDoor()
            R.id.pickup     -> game.pickup()
            R.id.drop       -> game.drop()
            R.id.eat        -> game.eat()
            R.id.fling      -> game.fling()
            R.id.search     -> game.search()
            R.id.skipTurn   -> game.skipTurn()
            else            -> return super<Activity>.onContextItemSelected(item)
        }
        return true
    }

    inner class MyGestureListener : SimpleOnGestureListener() {

        val SWIPE_MIN_DISTANCE = 120
        val SWIPE_MAX_OFF_PATH = 250
        val SWIPE_THRESHOLD_VELOCITY = 200

        public override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            e1!!
            e2!!

            var dx = 0
            var dy = 0

            if (abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && abs(e1.getX() - e2.getX()) > SWIPE_MIN_DISTANCE)
                dx = if (e1.getX() < e2.getX()) 1 else -1

            if (abs(velocityY) > SWIPE_THRESHOLD_VELOCITY && abs(e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE)
                dy = if (e1.getY() < e2.getY()) 1 else -1

            val direction = Directions.forDeltas(dx, dy)
            if (direction != null)
                moveTowards(direction)
            return false
        }
    }

    class MyConsole : Console {

        override fun message(message: String) {
            Log.d(tag, "message: $message")
        }

        override fun ask(question: String): Boolean {
            Log.d(tag, "ask: $question")
            return false
        }

        override fun <T: Item> selectItem(itemType: Class<T>, message: String, items: Collection<T>): T? {
            Log.d(tag, "selectItem: $message")
            return null
        }
        override fun selectItems(message: String, items: Collection<Item>): Set<Item> {
            Log.d(tag, "selectItems: $message")
            return setOf()
        }

        override fun selectDirection(): Direction? {
            Log.d(tag, "selectDirection")
            return null
        }
    }

    class object {
        val tag = "wanhack"
    }
}
