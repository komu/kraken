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

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class GameProxy(val game: Game, val gameRef: DefaultGameRef): InvocationHandler {

    public override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any? {
        if (method.getReturnType() == Void.TYPE)
            gameRef.assertWriteLockedByCurrentThread()

        return method.invoke(game, *((args ?: array<Any?>()) as Array<Any?>))
    }

    class object {
        public fun getProxiedGame(game: Game, gameRef: DefaultGameRef): IGame {
            val interfaces = array<Class<out Any>>(javaClass<IGame>())
            val proxy = GameProxy(game, gameRef)
            return (Proxy.newProxyInstance(javaClass<Game>().getClassLoader(), interfaces, proxy) as IGame)
        }
    }
}
