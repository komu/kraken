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

package net.wanhack.test.files

import net.wanhack.model.creature.Creature
import net.wanhack.model.item.Item
import net.wanhack.service.config.ObjectFactory
import org.junit.Test as test

class DefinitionsTest() {

    test fun loadDefinitions() {
        val obj = ObjectFactory()
        obj.parse("/items/items.xml", javaClass<Item>(), "item")
        obj.parse("/items/weapons.xml", javaClass<Item>(), "item")
        obj.parse("/creatures/creatures.xml", javaClass<Creature>(), "creature")

        for (def in obj.getAvailableDefinitionsForClass(javaClass<Creature>()))
            def.createObject()

        for (def in obj.getAvailableDefinitionsForClass(javaClass<Item>()))
            def.createObject()
    }
}
