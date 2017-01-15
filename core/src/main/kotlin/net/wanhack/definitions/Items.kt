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

package net.wanhack.definitions

import net.wanhack.model.item.*
import net.wanhack.model.item.armor.BodyPart.*
import net.wanhack.model.item.food.*
import net.wanhack.model.item.armor.Armor
import net.wanhack.model.common.Color

@Suppress("unused")
object Items : Definitions() {

    // MISC ITEMS

    // Rocks

    fun rock(name: String, probability: Int? = null) = item(name, probability=probability) {
        Item(name).init {
            letter = '*'
        }
    }

    val rock = rock("a rock").init {
        weight = 200
    }

    val verySmallRock = rock("a very small rock", probability = 10).init {
        weight = 0
    }

    val boulder = rock("a boulder", probability = 5).init {
        weight = 80000
    }

    // Light sources

    val torch = item("a torch", level = 1, probability = 75) {
        LightSource("a torch").init {
            color = Color.BROWN
            weight = 300
        }
    }

    val lantern = item("a lantern", level = 10, probability = 10) {
        LightSource("a lantern").init {
            color = Color.LIGHT_BROWN
            weight = 600
        }
    }

    val norwegianLantern = item("a Norwegian lantern", level = 25, probability = 15, maximumInstances = 5) {
        LightSource("a Norwegian lantern").init {
            color = Color.RED
            weight = 820
        }
    }

    // EDIBLES

    fun food(name: String, probability: Int? = null, init: Food.() -> Unit) = item(name, probability=probability) {
        Food(name).init { init() }
    }

    val foodRation = food("food ration") {
        effectiveness = 1000
        weight = 500
    }

    val chunkOfCheese = food("chunk of cheese") {
        color = Color.YELLOW
        effectiveness = 600
        weight = 350
    }

    val bigChunkOfCheese = food("big chunk of cheese") {
        color = Color.YELLOW
        effectiveness = 1200
        weight = 1100
    }

    val bun = food("bun", probability = 60) {
        letter = '%'
        color = Color.BROWNISH
        effectiveness = 80
        weight = 60
    }

    val fridayBun = item("Friday bun", probability = 30) {
        HealingEdible("Friday bun").init {
            color = Color.BROWNISH
            effectiveness = 400
            healingEffect = 5
            weight = 120
        }
    }

    val wraithEssence = item("wraith essence", probability = 0) {
        HealingEdible("wraith essence").init {
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
        HealingEdible("healing potion").init {
            color = Color.LIGHT_BLUE
            effectiveness = 10
            healingEffect = 10
            weight = 250
            letter = '!'
        }
    }

    // SCROLLS

    fun scroll(name: String) = item(name) {
        Item(name).init {
            color = Color.WHITE
            letter = '?'
        }
    }

    // WANDS  

    fun wand(name: String) = item(name) {
        Item(name).init {
            color = Color.BROWN
            letter = '-'
        }
    }

    // STAVES 

    fun staff(name: String) = item(name) {
        Item(name).init {
            color = Color.BROWN
            letter = '_'
        }
    }

    // RINGS  

    fun ring(name: String) = item(name) {
        Item(name).init {
            color = Color.BROWN
            letter = '='
        }
    }

    // AMULETS

    // ARMORS

    // Light armors

    fun lightArmor(name: String, level: Int, probability: Int? = null, maximumInstances: Int? = null, init: Armor.() -> Unit) =
        item(name, level = level, probability = probability, maximumInstances = maximumInstances) {
            Armor(name).init {
                letter = ']'
                init()
            }
        }

    fun heavyArmor(name: String, level: Int, probability: Int? = null, maximumInstances: Int? = null, init: Armor.() -> Unit) =
        item(name, level = level, probability = probability, maximumInstances = maximumInstances) {
            Armor(name).init {
                letter = '['
                init()
            }
        }

    val oldRags = lightArmor("old rags", level = 1, probability = 30) {
        weight = 500
        color = Color.RED
        bodyPart = TORSO
        armorBonus = 0
    }

    val clothes = lightArmor("clothes", level = 1) {
        weight = 500
        color = Color.BLUE
        bodyPart = TORSO
        armorBonus = 1
    }

    val softLeatherArmor = lightArmor("a soft leather armor", level = 4) {
        weight = 2300
        bodyPart = TORSO
        armorBonus = 2
    }

    val hardLeatherArmor = lightArmor("a hard leather armor", level = 4, probability = 70) {
        weight = 3000
        bodyPart = TORSO
        armorBonus = 3
    }

    val studdedLeatherArmor = lightArmor("a studded leather armor", level = 9, probability = 60) {
        weight = 3500
        bodyPart = TORSO
        armorBonus = 4
    }

    // Heavy armors

    val ringMail = heavyArmor("a ring mail", level = 15, probability = 40) {
        weight = 4600
        bodyPart = TORSO
        armorBonus = 5
    }

    val chainMail = heavyArmor("a chain mail", level = 30, probability = 30) {
        weight = 6000
        bodyPart = TORSO
        armorBonus = 6
    }

    val plateMail = heavyArmor("a plate mail", level = 50, probability = 20) {
        weight = 12000
        bodyPart = TORSO
        armorBonus = 7
    }

    // Helmets

    val leatherCap = lightArmor("a leather cap", level = 1) {
        weight = 200
        bodyPart = HEAD
        armorBonus = 1
    }

    val metalCap = heavyArmor("a metal cap", level = 5) {
        weight = 400
        bodyPart = HEAD
        armorBonus = 2
    }

    val chainCoif = heavyArmor("a chain coif", level = 10) {
        weight = 2100
        bodyPart = HEAD
        armorBonus = 3
    }

    val ironHelmet = heavyArmor("an iron helmet", level = 20, probability = 60) {
        weight = 3500
        bodyPart = HEAD
        armorBonus = 4
    }

    val steelHelmet = heavyArmor("a steel helmet", level = 30, probability = 25) {
        weight = 3300
        bodyPart = HEAD
        armorBonus = 5
    }

    // Shields

    val smallWoodenShield = lightArmor("a small wooden shield", level = 1) {
        weight = 700
        bodyPart = SHIELD
        armorBonus = 1
    }

    val smallMetalShield = lightArmor("a small metal shield", level = 4) {
        weight = 1500
        bodyPart = SHIELD
        armorBonus = 2
    }

    val largeWoodenShield = lightArmor("a large wooden shield", level = 13, probability = 80) {
        weight = 3200
        bodyPart = SHIELD
        armorBonus = 3
    }

    val largeMetalShield = heavyArmor("a large metal shield", level = 18, probability = 40) {
        weight = 4500
        bodyPart = SHIELD
        armorBonus = 4
    }


    // Gloves, gauntlets

    val leatherGloves = lightArmor("a pair of leather gloves", level = 3) {
        weight = 200
        bodyPart = HANDS
        armorBonus = 1
    }

    val gauntlets = lightArmor("a pair of gauntlets", level = 9, probability = 60) {
        weight = 450
        bodyPart = HANDS
        armorBonus = 2
    }


    // Boots, shoes

    val leatherShoes = lightArmor("a pair of leather shoes", level = 1) {
        weight = 380
        bodyPart = FEET
        armorBonus = 1
    }

    val leatherBoots = lightArmor("a pair of leather boots", level = 8) {
        weight = 1200
        bodyPart = FEET
        armorBonus = 2
    }

    val steelBoots = heavyArmor("a pair of steel boots", level = 23, probability = 80) {
        weight = 2700
        bodyPart = FEET
        armorBonus = 3
    }

    // Unique armors

    // ARTIFACT ITEMS

    val grailShapedLantern = item("the Grail-shaped lantern", level = 30, probability = 0, maximumInstances = 5) {
        LightSource("the Grail-shaped lantern").init {
            weight = 800
        }
    }

    // TODO: class for digger items - pick-axe, pick, shovel at least
    val unordinaryShovel = item("the Unordinary Shovel", level = 0, probability = 0, maximumInstances = 1) {
        Item("the Unordinary Shovel").init {
            color = Color.WHITE
            letter = 'ٱ'
            weight = 2500
        }
    }
}
