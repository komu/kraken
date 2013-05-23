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

class GameActivity : Activity() {

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

        val game = GameFacade(GameConfiguration(), false, myConsole) { b ->
            Log.d(tag, "game updated: $b")
            gameView.invalidate()
        }

        gameView.game = game

        game.start()
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
