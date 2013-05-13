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

package net.wanhack.model.creature.monsters

import net.wanhack.model.creature.Monster
import net.wanhack.model.item.Item
import net.wanhack.utils.Probability

class EmmentalElemental(name: String): Monster(name) {

    override fun createCorpse() =
        createItem(javaClass<Item>(), if (Probability.check(50)) "chunk of cheese" else "big chunk of cheese")
}
