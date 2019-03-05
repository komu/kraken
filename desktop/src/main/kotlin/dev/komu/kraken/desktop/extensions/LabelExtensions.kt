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

import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.SwingConstants.LEADING
import javax.swing.border.Border

fun label(text: String? = null, icon: Icon? = null, horizontalAlignment: Int = LEADING, callback: JLabel.() -> Unit): JLabel {
    val label = JLabel(text, icon, horizontalAlignment)
    label.callback()
    return label
}

var JLabel.icon: Icon?
    get() = getIcon()
    set(icon) = setIcon(icon)

var JLabel.horizontalAlignment: Int
    get() = getHorizontalAlignment()
    set(alignment) = setHorizontalAlignment(alignment)

var JLabel.border: Border?
    get() = getBorder()
    set(border) = setBorder(border)
