@file:Suppress("unused")

package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.LightSource
import dev.komu.kraken.model.item.armor.BodyPart.*
import dev.komu.kraken.model.item.food.ColdCoffee
import dev.komu.kraken.model.item.food.CyanideCapsule
import dev.komu.kraken.model.item.food.Food
import dev.komu.kraken.model.item.food.WaferThinMint

@Suppress("unused")
object Items : Definitions() {

    // MISC ITEMS

    // Rocks

    private fun rock(name: String, postInit: ItemDefinition<Item>.() -> Unit) = item(name, { Item(name) }) {
        letter = '*'
        postInit()
    }

    val rock = rock("a rock") {
        weight = 200
    }

    val verySmallRock = rock("a very small rock") {
        probability = 10
        weight = 0
    }

    val boulder = rock("a boulder") {
        probability = 5
        weight = 80000
    }

    // Light sources

    val torch = item("a torch", ::LightSource) {
        level = 1
        probability = 75
        color = Color.BROWN
        weight = 300
    }

    val lantern = item("a lantern", ::LightSource) {
        level = 10
        probability = 10
        color = Color.LIGHT_BROWN
        weight = 600
    }

    val norwegianLantern = item("a Norwegian lantern", ::LightSource) {
        level = 25
        probability = 15
        maximumInstances = 5
        color = Color.RED
        weight = 820
    }

    // EDIBLES

    val foodRation = food("food ration") {
        effectiveness = 1000
        weight = 500
    }

    val chunkOfCheese = food("chunk of cheese") {
        effectiveness = 600
        color = Color.YELLOW
        weight = 350
    }

    val bigChunkOfCheese = food("big chunk of cheese") {
        effectiveness = 1200
        color = Color.YELLOW
        weight = 1100
    }

    val bun = food("bun") {
        effectiveness = 80
        probability = 60
        color = Color.BROWNISH
        weight = 60
    }

    val fridayBun = food("Friday bun") {
        probability = 30
        color = Color.BROWNISH
        weight = 120
        effectiveness = 400
        healingEffect = 5
    }

    val wraithEssence = food("wraith essence") {
        probability = 0

        color = Color.WHITE
        weight = 4
        effectiveness = 100
    }

    val waferThinMint = item("wafer-thin mint", ::WaferThinMint) {
        probability = 20
    }

    val cyanideCapsule = item("a cyanide capsule", ::CyanideCapsule) {
        probability = 0
    }

    val potionOfColdCoffee = item("potion of cold coffee", ::ColdCoffee) {
        probability = 10
    }

    // POTIONS

    val healingPotion = food("healing potion", ::Food) {
        probability = 25
        color = Color.LIGHT_BLUE
        weight = 250
        letter = '!'
        effectiveness = 10
        healingEffect = 10
    }

    // SCROLLS

    private fun ItemDefinition<*>.scroll() {
        color = Color.WHITE
        letter = '?'
    }

    // WANDS

    private fun ItemDefinition<*>.wand() {
        color = Color.BROWN
        letter = '-'
    }

    // STAVES

    private fun ItemDefinition<*>.staff() {
        color = Color.BROWN
        letter = '_'
    }

    // RINGS

    private fun ItemDefinition<*>.ring() {
        color = Color.BROWN
        letter = '='
    }

    // AMULETS

    // ARMORS

    // Light armors

    private inline fun armor(name: String, light: Boolean, postInit: ArmorDefinition.() -> Unit) =
        armor(name) {
            letter = if (light) ']' else '['

            postInit()
        }

    val oldRags = armor("old rags", light = true) {
        level = 1
        probability = 30
        weight = 500
        color = Color.RED
        bodyPart = TORSO
        armorBonus = 0
    }

    val clothes = armor("clothes", light = true) {
        level = 1
        weight = 500
        color = Color.BLUE
        bodyPart = TORSO
        armorBonus = 1
    }

    val softLeatherArmor = armor("a soft leather armor", light = true) {
        level = 4
        weight = 2300
        bodyPart = TORSO
        armorBonus = 2
    }

    val hardLeatherArmor = armor("a hard leather armor", light = true) {
        level = 4
        probability = 70
        weight = 3000
        bodyPart = TORSO
        armorBonus = 3
    }

    val studdedLeatherArmor = armor("a studded leather armor", light = true) {
        level = 9
        probability = 60
        weight = 3500
        bodyPart = TORSO
        armorBonus = 4
    }

    // Heavy armors

    val ringMail = armor("a ring mail", light = false) {
        level = 15
        probability = 40
        weight = 4600
        bodyPart = TORSO
        armorBonus = 5
    }

    val chainMail = armor("a chain mail", light = false) {
        level = 30
        probability = 30
        weight = 6000
        bodyPart = TORSO
        armorBonus = 6
    }

    val plateMail = armor("a plate mail", light = false) {
        level = 50
        probability = 20
        weight = 12000
        bodyPart = TORSO
        armorBonus = 7
    }

    // Helmets

    val leatherCap = armor("a leather cap", light = true) {
        level = 1
        weight = 200
        bodyPart = HEAD
        armorBonus = 1
    }

    val metalCap = armor("a metal cap", light = false) {
        level = 5
        weight = 400
        bodyPart = HEAD
        armorBonus = 2
    }

    val chainCoif = armor("a chain coif", light = false) {
        level = 10
        weight = 2100
        bodyPart = HEAD
        armorBonus = 3
    }

    val ironHelmet = armor("an iron helmet", light = false) {
        level = 20
        probability = 60
        weight = 3500
        bodyPart = HEAD
        armorBonus = 4
    }

    val steelHelmet = armor("a steel helmet", light = false) {
        level = 30
        probability = 25
        weight = 3300
        bodyPart = HEAD
        armorBonus = 5
    }

    // Shields

    val smallWoodenShield = armor("a small wooden shield", light = true) {
        level = 1
        weight = 700
        bodyPart = SHIELD
        armorBonus = 1
    }

    val smallMetalShield = armor("a small metal shield", light = true) {
        level = 4
        weight = 1500
        bodyPart = SHIELD
        armorBonus = 2
    }

    val largeWoodenShield = armor("a large wooden shield", light = true) {
        level = 13
        probability = 80
        weight = 3200
        bodyPart = SHIELD
        armorBonus = 3
    }

    val largeMetalShield = armor("a large metal shield", light = false) {
        level = 18
        probability = 40
        weight = 4500
        bodyPart = SHIELD
        armorBonus = 4
    }


    // Gloves, gauntlets

    val leatherGloves = armor("a pair of leather gloves", light = true) {
        level = 3
        weight = 200
        bodyPart = HANDS
        armorBonus = 1
    }

    val gauntlets = armor("a pair of gauntlets", light = true) {
        level = 9
        probability = 60
        weight = 450
        bodyPart = HANDS
        armorBonus = 2
    }


    // Boots, shoes

    val leatherShoes = armor("a pair of leather shoes", light = true) {
        level = 1
        weight = 380
        bodyPart = FEET
        armorBonus = 1
    }

    val leatherBoots = armor("a pair of leather boots", light = true) {
        level = 8
        weight = 1200
        bodyPart = FEET
        armorBonus = 2
    }

    val steelBoots = armor("a pair of steel boots", light = false) {
        level = 23
        probability = 80
        weight = 2700
        bodyPart = FEET
        armorBonus = 3
    }

    // Unique armors

    // ARTIFACT ITEMS

    val grailShapedLantern = item("the Grail-shaped lantern", ::LightSource) {
        level = 30
        probability = 0
        maximumInstances = 5
        weight = 800
    }

    // TODO: class for digger items - pick-axe, pick, shovel at least
    val unordinaryShovel = item("the Unordinary Shovel", ::Item) {
        level = 0
        probability = 0
        maximumInstances = 1
        color = Color.WHITE
        letter = 'ٱ'
        weight = 2500
    }
}
