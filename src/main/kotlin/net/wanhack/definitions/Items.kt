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
import java.awt.Color

object Items : Definitions() {

    // MISC ITEMS

    // Rocks

    val rock = item("a rock", objectClass = javaClass<Item>()) {
        weight = 200
    }

    val verySmallRock = item("a very small rock", parent = rock, probability = 10) {
        weight = 0
    }

    val boulder = item("a boulder", parent = rock, probability = 5) {
        weight = 80000
    }

    // Light sources

    val lightSource = item("light source", objectClass = javaClass<LightSource>(), isAbstract = true) {
        color = Color.YELLOW
        letter = '~'
    }

    val torch = item("a torch", parent = lightSource, level = 1, probability = 75) {
        color = Colors.BROWN
        weight = 300
    }

    val lantern = item("a lantern", parent = lightSource, level = 10, probability = 10) {
        color = Colors.LIGHT_BROWN
        weight = 600
    }

    val norwegianLantern = item("a Norwegian lantern", parent = lightSource, level = 25, probability = 15, maximumInstances = 5) {
        color = Color.RED
        weight = 820
    }

    // EDIBLES

    val foodRation = item("food ration", objectClass = javaClass<Food>()) {
        letter = '%'
        effectiveness = 1000
        weight = 500
    }

    val chunkOfCheese = item("chunk of cheese", objectClass = javaClass<Food>()) {
        letter = '%'
        color = Color.YELLOW
        effectiveness = 600
        weight = 350
    }

    val bigChunkOfCheese = item("big chunk of cheese", parent = chunkOfCheese) {
        effectiveness = 1200
        weight = 1100
    }

    val bun = item("bun", objectClass = javaClass<Food>(), probability = 60) {
        letter = '%'
        color = Colors.BROWNISH
        effectiveness = 80
        weight = 60
    }

    val fridayBun = item("Friday bun", objectClass = javaClass<HealingEdible>(), probability = 30) {
        letter = '%'
        color = Colors.BROWNISH
        effectiveness = 400
        healingEffect = 5
        weight = 120
    }

    val wraithEssence = item("wraith essence", objectClass = javaClass<HealingEdible>(), probability = 0) {
        letter = '%'
        color = Color.WHITE
        effectiveness = 100
        weight = 4
    }

    val waferThinMint = item("wafer-thin mint", objectClass = javaClass<WaferThinMint>(), probability = 20) {
    }

    val cyanideCapsule = item("a cyanide capsule", objectClass = javaClass<CyanideCapsule>(), probability = 0) {
    }

    // POTIONS

    val potion = item("potion", objectClass = javaClass<Item>(), isAbstract = true) {
        color = Colors.BROWN
        letter = '!'
    }

    val healingPotion = item<HealingEdible>("healing potion", objectClass = javaClass<HealingEdible>(), parent = potion, probability = 25) {
        color = Colors.LIGHT_BLUE
        effectiveness = 10
        healingEffect = 10
        weight = 250
        letter = '!'
    }

    // SCROLLS

    val scroll = item("scroll", objectClass = javaClass<Item>(), isAbstract = true) {
        color = Color.WHITE
        letter = '?'
    }


    // WANDS  

    val wand = item("wand", objectClass = javaClass<Item>(), isAbstract = true) {
        color = Colors.BROWN
        letter = '-'
    }

    // STAVES 

    val staff = item("staff", objectClass = javaClass<Item>(), isAbstract = true) {
        color = Colors.BROWN
        letter = '_'
    }


    // RINGS  

    val ring = item("ring", objectClass = javaClass<Item>(), isAbstract = true) {
        color = Colors.BROWN
        letter = '='
    }

    // AMULETS

    // ARMORS

    // Light armors

    val lightArmor = item("light armor", objectClass = javaClass<armor.Armor>(), isAbstract = true) {
        letter = ']'
        color = Colors.BROWN
        bodyPart = TORSO
    }

    val oldRags = item("old rags", parent = lightArmor, level = 1, probability = 30) {
        weight = 500
        color = Color.RED
        bodyPart = TORSO
        armorBonus = 0
    }

    val clothes = item("clothes", parent = lightArmor, level = 1) {
        weight = 500
        color = Color.BLUE
        bodyPart = TORSO
        armorBonus = 1
    }

    val softLeatherArmor = item("a soft leather armor", parent = lightArmor, level = 4) {
        weight = 2300
        bodyPart = TORSO
        armorBonus = 2
    }

    val hardLeatherArmor = item("a hard leather armor", parent = lightArmor, level = 4, probability = 70) {
        weight = 3000
        bodyPart = TORSO
        armorBonus = 3
    }

    val studdedLeatherArmor = item("a studded leather armor", parent = lightArmor, level = 9, probability = 60) {
        weight = 3500
        bodyPart = TORSO
        armorBonus = 4
    }

    // Heavy armors

    val heavyArmor = item("heavy armor", objectClass = javaClass<armor.Armor>(), isAbstract = true) {
        letter = '['
        color = Colors.BROWN
        bodyPart = TORSO
    }

    val ringMail = item("a ring mail", parent = heavyArmor, level = 15, probability = 40) {
        weight = 4600
        bodyPart = TORSO
        armorBonus = 5
    }

    val chainMail = item("a chain mail", parent = heavyArmor, level = 30, probability = 30) {
        weight = 6000
        bodyPart = TORSO
        armorBonus = 6
    }

    val plateMail = item("a plate mail", parent = heavyArmor, level = 50, probability = 20) {
        weight = 12000
        bodyPart = TORSO
        armorBonus = 7
    }

    // Helmets

    val leatherCap = item("a leather cap", parent = lightArmor, level = 1) {
        weight = 200
        bodyPart = HEAD
        armorBonus = 1
    }

    val metalCap = item("a metal cap", parent = heavyArmor, level = 5) {
        weight = 400
        bodyPart = HEAD
        armorBonus = 2
    }

    val chainCoif = item("a chain coif", parent = heavyArmor, level = 10) {
        weight = 2100
        bodyPart = HEAD
        armorBonus = 3
    }

    val ironHelmet = item("an iron helmet", parent = heavyArmor, level = 20, probability = 60) {
        weight = 3500
        bodyPart = HEAD
        armorBonus = 4
    }

    val steelHelmet = item("a steel helmet", parent = heavyArmor, level = 30, probability = 25) {
        weight = 3300
        bodyPart = HEAD
        armorBonus = 5
    }

    // Shields

    val smallWoodenShield = item("a small wooden shield", parent = lightArmor, level = 1) {
        weight = 700
        bodyPart = SHIELD
        armorBonus = 1
    }

    val smallMetalShield = item("a small metal shield", parent = lightArmor, level = 4) {
        weight = 1500
        bodyPart = SHIELD
        armorBonus = 2
    }

    val largeWoodenShield = item("a large wooden shield", parent = heavyArmor, level = 13, probability = 80) {
        weight = 3200
        bodyPart = SHIELD
        armorBonus = 3
    }

    val largeMetalShield = item("a large metal shield", parent = heavyArmor, level = 18, probability = 40) {
        weight = 4500
        bodyPart = SHIELD
        armorBonus = 4
    }


    // Gloves, gauntlets

    val leatherGloves = item("a pair of leather gloves", parent = lightArmor, level = 3) {
        weight = 200
        bodyPart = HANDS
        armorBonus = 1
    }

    val gauntlets = item("a pair of gauntlets", parent = heavyArmor, level = 9, probability = 60) {
        weight = 450
        bodyPart = HANDS
        armorBonus = 2
    }


    // Boots, shoes

    val leatherShoes = item("a pair of leather shoes", parent = lightArmor, level = 1) {
        weight = 380
        bodyPart = FEET
        armorBonus = 1
    }

    val leatherBoots = item("a pair of leather boots", parent = lightArmor, level = 8) {
        weight = 1200
        bodyPart = FEET
        armorBonus = 2
    }

    val steelBoots = item("a pair of steel boots", parent = heavyArmor, level = 23, probability = 80) {
        weight = 2700
        bodyPart = FEET
        armorBonus = 3
    }

    // Unique armors

    // ARTIFACT ITEMS

    val grailShapedLantern = item("the Grail-shaped lantern", parent = lightSource, level = 30, probability = 0, maximumInstances = 5) {
        color = Color.YELLOW
        weight = 800
    }

    // TODO: class for digger items - pick-axe, pick, shovel at least
    val unordinaryShovel = item("the Unordinary Shovel", objectClass = javaClass<Item>(), level = 0, probability = 0, maximumInstances = 1) {
        color = Color.WHITE
        letter = 'ٱ'
        weight = 2500
    }
}
