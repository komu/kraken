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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

final class LockProxy implements InvocationHandler {

    private final DefaultGameRef gameRef;

    private LockProxy(DefaultGameRef gameRef) {
        this.gameRef = gameRef;
    }
    
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        // XXX change this to use the annotation
        //if (method.isAnnotationPresent(WriteOperation.class)) {
        if (method.getReturnType() == Void.TYPE) {
            gameRef.scheduleAction(new ActionCallback() {
                public void execute(IGame game) {
                    try {
                        method.invoke(game, args);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return null;
        } else {
            gameRef.executeQuery(new QueryCallback<Exception>() {
                public void execute(IGame game) throws Exception {
                    method.invoke(game, args);
                }
            });
            return null;
        }
    }
    
    public static IGame getProxiedGame(DefaultGameRef gameRef) {
        Class[] interfaces = { IGame.class };
        LockProxy proxy = new LockProxy(gameRef);
        
        return (IGame) Proxy.newProxyInstance(
                Game.class.getClassLoader(), interfaces, proxy);
    }
}
