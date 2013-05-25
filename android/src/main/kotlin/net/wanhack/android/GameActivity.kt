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
import java.util.concurrent.Executors

class GameActivity : Activity() {

    var game: GameFacade? = null
    val gameView: GameView
        get() = findViewById(R.id.gameView) as GameView
    val gameExecutor = Executors.newSingleThreadExecutor()

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
            runOnUiThread(Runnable {
                gameView.invalidate()
            })
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
        when (item!!.getItemId()) {
            R.id.skipTurn   -> gameAction { it.skipTurn() }
            R.id.moveUp     -> gameAction { it.movePlayerVertically(true) }
            R.id.moveDown   -> gameAction { it.movePlayerVertically(false) }
            R.id.wield      -> gameAction { it.wield() }
            R.id.wear       -> gameAction { it.wear() }
            R.id.rest       -> gameAction { it.rest(-1) }
            R.id.talk       -> gameAction { it.talk() }
            R.id.open_door  -> gameAction { it.openDoor() }
            R.id.close_door -> gameAction { it.closeDoor() }
            R.id.pickup     -> gameAction { it.pickup() }
            R.id.drop       -> gameAction { it.drop() }
            R.id.eat        -> gameAction { it.eat() }
            R.id.fling      -> gameAction { it.fling() }
            R.id.search     -> gameAction { it.search() }
            else            -> return super<Activity>.onContextItemSelected(item)
        }
        return true
    }
    
    fun gameAction(action: (GameFacade) -> Unit) {
        gameExecutor.execute(Runnable {
            action(game!!)
        })
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

    inner class MyConsole : Console {

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
