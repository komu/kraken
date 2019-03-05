/*
 * Copyright 2013 The Releasers of Kraken
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

package dev.komu.kraken.desktop.extensions

import java.awt.event.ActionEvent
import javax.swing.*

operator fun ActionMap.set(name: Any, action: Action?) =
    put(name, action)

operator fun InputMap.set(keyStroke: KeyStroke, actionMapKey: Any?) =
    put(keyStroke, actionMapKey)

operator fun InputMap.set(keyStroke: String, actionMapKey: Any?) =
    put(KeyStroke.getKeyStroke(keyStroke), actionMapKey)

fun makeAction(name: String = "", mnemonic: Int? = null, code: () -> Unit): Action = object : AbstractAction(name) {
    init {
        if (mnemonic != null)
            putValue(Action.MNEMONIC_KEY, mnemonic)
    }
    override fun actionPerformed(e: ActionEvent?) = code()
}
