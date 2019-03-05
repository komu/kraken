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

package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.LightSource
import dev.komu.kraken.model.item.armor.Armor
import dev.komu.kraken.model.item.armor.BodyPart.*
import dev.komu.kraken.model.item.food.*

@Suppress("unused")
object Items : dev.komu.kraken.definitions.Definitions() {

    // MISC ITEMS

    // Rocks

    fun rock(name: String, probability: Int? = null) = item(name, probability=probability) {
        Item(name).apply {
            letter = '*'
        }
    }

    val rock = dev.komu.kraken.definitions.Items.rock("a rock").init {
        weight = 200
    }

    val verySmallRock = dev.komu.kraken.definitions.Items.rock("a very small rock", probability = 10).init {
        weight = 0
    }

    val boulder = dev.komu.kraken.definitions.Items.rock("a boulder", probability = 5).init {
        weight = 80000
    }

    // Light sources

    val torch = item("a torch", level = 1, probability = 75) {
        LightSource("a torch").apply {
            color = Color.BROWN
            weight = 300
        }
    }

    val lantern = item("a lantern", level = 10, probability = 10) {
        LightSource("a lantern").apply {
            color = Color.LIGHT_BROWN
            weight = 600
        }
    }

    val norwegianLantern = item("a Norwegian lantern", level = 25, probability = 15, maximumInstances = 5) {
        LightSource("a Norwegian lantern").apply {
            color = Color.RED
            weight = 820
        }
    }

    // EDIBLES

    fun food(name: String, probability: Int? = null, init: Food.() -> Unit) = item(name, probability=probability) {
        Food(name).apply { init() }
    }

    val foodRation = dev.komu.kraken.definitions.Items.food("food ration") {
        effectiveness = 1000
        weight = 500
    }

    val chunkOfCheese = dev.komu.kraken.definitions.Items.food("chunk of cheese") {
        color = Color.YELLOW
        effectiveness = 600
        weight = 350
    }

    val bigChunkOfCheese = dev.komu.kraken.definitions.Items.food("big chunk of cheese") {
        color = Color.YELLOW
        effectiveness = 1200
        weight = 1100
    }

    val bun = dev.komu.kraken.definitions.Items.food("bun", probability = 60) {
        letter = '%'
        color = Color.BROWNISH
        effectiveness = 80
        weight = 60
    }

    val fridayBun = item("Friday bun", probability = 30) {
        HealingEdible("Friday bun").apply {
            color = Color.BROWNISH
            effectiveness = 400
            healingEffect = 5
            weight = 120
        }
    }

    val wraithEssence = item("wraith essence", probability = 0) {
        HealingEdible("wraith essence").apply {
            color = Color.WHITE
            effectiveness = 100
            weight = 4
        }
    }

    val waferThinMint = item("wafer-thin mint", probability = 20) { WaferThinMint() }

    val cyanideCapsule = item("a cyanide capsule", probability = 0) { CyanideCapsule() }

    val potionOfColdCoffee = item("potion of cold coffee", probability = 10) { ColdCoffee() }

    // POTIONS

    val healingPotion = item("healing potion", probability = 25) {
        HealingEdible("healing potion").apply {
            color = Color.LIGHT_BLUE
            effectiveness = 10
            healingEffect = 10
            weight = 250
            letter = '!'
        }
    }

    // SCROLLS

    fun scroll(name: String) = item(name) {
        Item(name).apply {
            color = Color.WHITE
            letter = '?'
        }
    }

    // WANDS  

    fun wand(name: String) = item(name) {
        Item(name).apply {
            color = Color.BROWN
            letter = '-'
        }
    }

    // STAVES 

    fun staff(name: String) = item(name) {
        Item(name).apply {
            color = Color.BROWN
            letter = '_'
        }
    }

    // RINGS  

    fun ring(name: String) = item(name) {
        Item(name).apply {
            color = Color.BROWN
            letter = '='
        }
    }

    // AMULETS

    // ARMORS

    // Light armors

    fun lightArmor(name: String, level: Int, probability: Int? = null, maximumInstances: Int? = null, init: Armor.() -> Unit) =
        item(name, level = level, probability = probability, maximumInstances = maximumInstances) {
            Armor(name).apply {
                letter = ']'
                init()
            }
        }

    fun heavyArmor(name: String, level: Int, probability: Int? = null, maximumInstances: Int? = null, init: Armor.() -> Unit) =
        item(name, level = level, probability = probability, maximumInstances = maximumInstances) {
            Armor(name).apply {
                letter = '['
                init()
            }
        }

    val oldRags = dev.komu.kraken.definitions.Items.lightArmor("old rags", level = 1, probability = 30) {
        weight = 500
        color = Color.RED
        bodyPart = TORSO
        armorBonus = 0
    }

    val clothes = dev.komu.kraken.definitions.Items.lightArmor("clothes", level = 1) {
        weight = 500
        color = Color.BLUE
        bodyPart = TORSO
        armorBonus = 1
    }

    val softLeatherArmor = dev.komu.kraken.definitions.Items.lightArmor("a soft leather armor", level = 4) {
        weight = 2300
        bodyPart = TORSO
        armorBonus = 2
    }

    val hardLeatherArmor =
        dev.komu.kraken.definitions.Items.lightArmor("a hard leather armor", level = 4, probability = 70) {
            weight = 3000
            bodyPart = TORSO
            armorBonus = 3
        }

    val studdedLeatherArmor =
        dev.komu.kraken.definitions.Items.lightArmor("a studded leather armor", level = 9, probability = 60) {
            weight = 3500
            bodyPart = TORSO
            armorBonus = 4
        }

    // Heavy armors

    val ringMail = dev.komu.kraken.definitions.Items.heavyArmor("a ring mail", level = 15, probability = 40) {
        weight = 4600
        bodyPart = TORSO
        armorBonus = 5
    }

    val chainMail = dev.komu.kraken.definitions.Items.heavyArmor("a chain mail", level = 30, probability = 30) {
        weight = 6000
        bodyPart = TORSO
        armorBonus = 6
    }

    val plateMail = dev.komu.kraken.definitions.Items.heavyArmor("a plate mail", level = 50, probability = 20) {
        weight = 12000
        bodyPart = TORSO
        armorBonus = 7
    }

    // Helmets

    val leatherCap = dev.komu.kraken.definitions.Items.lightArmor("a leather cap", level = 1) {
        weight = 200
        bodyPart = HEAD
        armorBonus = 1
    }

    val metalCap = dev.komu.kraken.definitions.Items.heavyArmor("a metal cap", level = 5) {
        weight = 400
        bodyPart = HEAD
        armorBonus = 2
    }

    val chainCoif = dev.komu.kraken.definitions.Items.heavyArmor("a chain coif", level = 10) {
        weight = 2100
        bodyPart = HEAD
        armorBonus = 3
    }

    val ironHelmet = dev.komu.kraken.definitions.Items.heavyArmor("an iron helmet", level = 20, probability = 60) {
        weight = 3500
        bodyPart = HEAD
        armorBonus = 4
    }

    val steelHelmet = dev.komu.kraken.definitions.Items.heavyArmor("a steel helmet", level = 30, probability = 25) {
        weight = 3300
        bodyPart = HEAD
        armorBonus = 5
    }

    // Shields

    val smallWoodenShield = dev.komu.kraken.definitions.Items.lightArmor("a small wooden shield", level = 1) {
        weight = 700
        bodyPart = SHIELD
        armorBonus = 1
    }

    val smallMetalShield = dev.komu.kraken.definitions.Items.lightArmor("a small metal shield", level = 4) {
        weight = 1500
        bodyPart = SHIELD
        armorBonus = 2
    }

    val largeWoodenShield =
        dev.komu.kraken.definitions.Items.lightArmor("a large wooden shield", level = 13, probability = 80) {
            weight = 3200
            bodyPart = SHIELD
            armorBonus = 3
        }

    val largeMetalShield =
        dev.komu.kraken.definitions.Items.heavyArmor("a large metal shield", level = 18, probability = 40) {
            weight = 4500
            bodyPart = SHIELD
            armorBonus = 4
        }


    // Gloves, gauntlets

    val leatherGloves = dev.komu.kraken.definitions.Items.lightArmor("a pair of leather gloves", level = 3) {
        weight = 200
        bodyPart = HANDS
        armorBonus = 1
    }

    val gauntlets = dev.komu.kraken.definitions.Items.lightArmor("a pair of gauntlets", level = 9, probability = 60) {
        weight = 450
        bodyPart = HANDS
        armorBonus = 2
    }


    // Boots, shoes

    val leatherShoes = dev.komu.kraken.definitions.Items.lightArmor("a pair of leather shoes", level = 1) {
        weight = 380
        bodyPart = FEET
        armorBonus = 1
    }

    val leatherBoots = dev.komu.kraken.definitions.Items.lightArmor("a pair of leather boots", level = 8) {
        weight = 1200
        bodyPart = FEET
        armorBonus = 2
    }

    val steelBoots =
        dev.komu.kraken.definitions.Items.heavyArmor("a pair of steel boots", level = 23, probability = 80) {
            weight = 2700
            bodyPart = FEET
            armorBonus = 3
        }

    // Unique armors

    // ARTIFACT ITEMS

    val grailShapedLantern = item("the Grail-shaped lantern", level = 30, probability = 0, maximumInstances = 5) {
        LightSource("the Grail-shaped lantern").apply {
            weight = 800
        }
    }

    // TODO: class for digger items - pick-axe, pick, shovel at least
    val unordinaryShovel = item("the Unordinary Shovel", level = 0, probability = 0, maximumInstances = 1) {
        Item("the Unordinary Shovel").apply {
            color = Color.WHITE
            letter = 'ٱ'
            weight = 2500
        }
    }
}
