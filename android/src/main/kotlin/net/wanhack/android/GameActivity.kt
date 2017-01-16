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

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Toast
import net.wanhack.common.Direction
import net.wanhack.common.Directions
import net.wanhack.model.GameConfiguration
import net.wanhack.model.GameFacade
import net.wanhack.model.common.Console
import net.wanhack.model.item.Item
import java.lang.Math.abs
import java.util.*
import java.util.Collections.emptySet
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

class GameActivity : Activity() {

    var game: GameFacade by Delegates.notNull()
    val gameView: GameView by ViewProperty(R.id.gameView)

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)

        setContentView(R.layout.game)
        registerForContextMenu(gameView)

        val gestureDetector = GestureDetector(this, MyGestureListener())
        gameView.setOnTouchListener { v, event ->
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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.game_actions, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.skipTurn   -> game.skipTurn()
            R.id.moveUp     -> game.movePlayerVertically(true)
            R.id.moveDown   -> game.movePlayerVertically(false)
            R.id.equip      -> game.equip()
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

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            e1!!
            e2!!

            var dx = 0
            var dy = 0

            if (abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && abs(e1.x - e2.x) > SWIPE_MIN_DISTANCE)
                dx = if (e1.x < e2.x) 1 else -1

            if (abs(velocityY) > SWIPE_THRESHOLD_VELOCITY && abs(e1.y - e2.y) > SWIPE_MIN_DISTANCE)
                dy = if (e1.y < e2.y) 1 else -1

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

        override fun ask(question: String): Boolean =
            waitForUI { setResult ->
                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle(question)

                builder.setPositiveButton("Yes") { dialog, id ->
                    setResult(true)
                }

                builder.setNegativeButton("No") { dialog, id ->
                    setResult(false)
                }

                builder.setOnCancelListener {
                    setResult(false)
                }

                builder.create()!!.show()
            }

        override fun <T: Item> selectItem(message: String, items: Collection<T>): T? =
            waitForUI { setResult ->
                val itemList = items.toList()
                val titles = Array(itemList.size) { i -> itemList[i].title }

                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle(message)
                builder.setItems(titles) { dialog, which ->
                    setResult(itemList[which])
                }
                builder.setOnCancelListener {
                    setResult(null)
                }
                builder.create()!!.show()
            }

        override fun <T: Item> selectItems(message: String, items: Collection<T>): Set<T> =
            waitForUI { setResult ->
                val itemList = items.toList()
                val titles = Array(itemList.size) { i -> itemList[i].title }

                val selections = HashSet<T>()

                val builder = AlertDialog.Builder(this@GameActivity)

                builder.setTitle(message)
                builder.setMultiChoiceItems(titles, null) { dialog, which, isChecked ->
                    val item = itemList[which]
                    if (isChecked) {
                        selections.add(item)
                    } else {
                        selections.remove(item)
                    }
                }

                builder.setPositiveButton("Ok") { dialog, id ->
                    setResult(selections)
                }

                builder.setNegativeButton("Cancel") { dialog, id ->
                    setResult(emptySet())
                }

                builder.setOnCancelListener {
                    setResult(emptySet())
                }

                builder.create()!!.show()
            }

        override fun selectDirection(): Direction? =
            waitForUI { setResult ->
                val directions = Direction.values()
                val titles = Array(directions.size) { i -> directions[i].shortName }

                val builder = AlertDialog.Builder(this@GameActivity)
                builder.setTitle("Select direction")
                builder.setItems(titles) { dialog, which ->
                    setResult(directions[which])
                }
                builder.setOnCancelListener {
                    setResult(null)
                }
                builder.create()!!.show()
            }

        fun <T> waitForUI(callback: (setResult: (T) -> Unit) -> Unit): T {
            var result: T? = null
            val latch = CountDownLatch(1)

            runOnUiThread {
                callback { r ->
                    result = r
                    latch.countDown()
                }
            }

            latch.await()
            return result as T
        }
    }

    companion object {
        val tag = "wanhack"
    }
}
