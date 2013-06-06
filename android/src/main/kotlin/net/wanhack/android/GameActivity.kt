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
import android.util.Log
import android.view.ContextMenu
import android.view.View
import android.view.MenuItem
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import net.wanhack.model.common.Directions
import java.util.concurrent.CountDownLatch
import android.app.AlertDialog
import java.util.HashSet
import android.widget.Toast
import kotlin.properties.Delegates

class GameActivity : Activity() {

    var game: GameFacade by Delegates.notNull()
    val gameView: GameView by ViewProperty(R.id.gameView)

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.game)
        registerForContextMenu(gameView)

        val gestureDetector = GestureDetector(this, MyGestureListener())
        gameView.setOnTouchListener { (v, event) ->
            gestureDetector.onTouchEvent(event)
        }

        game = GameFacade(GameConfiguration(), MyConsole()) { b ->
            runOnUiThread {
                gameView.invalidate()
            }
        }

        gameView.game = game

        game.start()
    }

    fun moveTowards(dir: Direction) {
        game.movePlayer(dir)
    }

    public override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        getMenuInflater()!!.inflate(R.menu.game_actions, menu)
    }

    public override fun onContextItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            R.id.skipTurn   -> game.skipTurn()
            R.id.moveUp     -> game.movePlayerVertically(true)
            R.id.moveDown   -> game.movePlayerVertically(false)
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

    inner class MyConsole : Console {

        override fun message(message: String) {
            Log.d(tag, "message: $message")
            runOnUiThread {
                Toast.makeText(this@GameActivity, message, Toast.LENGTH_LONG)!!.show()
            }
        }

        override fun ask(question: String): Boolean {
            Log.d(tag, "ask: $question")

            var result = false
            val latch = CountDownLatch(1)

            runOnUiThread {
                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle(question)

                builder.setPositiveButton("Yes") { (dialog, id) ->
                    result = true
                    latch.countDown()
                }

                builder.setNegativeButton("No") { (dialog, id) ->
                    result = false
                    latch.countDown()
                }

                builder.setOnCancelListener {
                    result = false
                    latch.countDown()
                }

                builder.create()!!.show()
            }

            latch.await()

            return result
        }

        override fun <T: Item> selectItem(message: String, items: Collection<T>): T? {
            val itemList = items.toList()
            var selected: T? = null
            val latch = CountDownLatch(1)

            val titles = Array<String>(itemList.size) { i -> itemList[i].title }

            runOnUiThread {
                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle(message)
                builder.setItems(titles) { (dialog, which) ->
                    selected = itemList[which]
                    latch.countDown()
                }
                builder.setOnCancelListener {
                    selected = null
                    latch.countDown()
                }
                builder.create()!!.show()
            }

            latch.await()

            return selected
        }

        override fun selectItems<T: Item>(message: String, items: Collection<T>): Set<T> {
            val itemList = items.toList()
            val selections = HashSet<T>()
            val latch = CountDownLatch(1)

            val titles = Array<String>(itemList.size) { i -> itemList[i].title }

            runOnUiThread {
                val builder = AlertDialog.Builder(this@GameActivity)

                builder.setTitle(message)
                builder.setMultiChoiceItems(titles, null) { (dialog, which, isChecked) ->
                    val item = itemList[which]
                    if (isChecked) {
                        selections.add(item)
                    } else {
                        selections.remove(item)
                    }
                }

                builder.setPositiveButton("Ok") { (dialog, id) ->
                    latch.countDown()
                }

                builder.setNegativeButton("Cancel") { (dialog, id) ->
                    selections.clear()
                    latch.countDown()
                }

                builder.setOnCancelListener {
                    selections.clear()
                    latch.countDown()
                }

                builder.create()!!.show()
            }

            latch.await()

            return selections
        }

        override fun selectDirection(): Direction? {
            var selected: Direction? = null
            val latch = CountDownLatch(1)

            val directions = Direction.values()
            val titles = Array<String>(directions.size) { i -> directions[i].shortName }

            runOnUiThread {
                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle("Select direction")
                builder.setItems(titles) { (dialog, which) ->
                    selected = directions[which]
                    latch.countDown()
                }
                builder.setOnCancelListener {
                    selected = null
                    latch.countDown()
                }
                builder.create()!!.show()
            }

            latch.await()

            return selected
        }
    }

    class object {
        val tag = "wanhack"
    }
}
