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
