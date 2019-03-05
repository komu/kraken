package dev.komu.kraken.desktop.extensions

import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.SwingConstants.LEADING

fun label(text: String? = null, icon: Icon? = null, horizontalAlignment: Int = LEADING, callback: JLabel.() -> Unit): JLabel {
    val label = JLabel(text, icon, horizontalAlignment)
    label.callback()
    return label
}
