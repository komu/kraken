@file:Suppress("unused")
package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.weapon.WeaponClass
import dev.komu.kraken.utils.exp.*

object Weapons : Definitions() {

    // Blades

    private inline fun blade(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.SWORD) {
            letter = '†'
            damage = 1 * d3
            letter = '۶'
            color = Color.DARK_GRAY
            init()
        }

    val dagger = blade("a dagger") {
        level = 2
        weight = 100
    }
    
    val knife = blade("a knife") {
        level = 1
        damage = 1 * d2
    }

    val rapier = blade("a rapier") {
        level = 3
        toHit = 1
        damage = 1 * d5
        weight = 1100
    }

    val shortSword = blade("a short sword") {
        level = 5
        toHit = 1
        damage = 1 * d6
        weight = 1200
    }

    val scimitar = blade("a scimitar") {
        level = 8
        probability = 80
        toHit = 2
        damage = d6 + 1
        weight = 2100
    }

    val longSword = blade("a long sword") {
        level = 10
        probability = 60
        toHit = 2
        damage = d6 + 2
        weight = 2500
    }

    val mithrilSword = blade("a mithril sword") {
        level = 20
        probability = 10
        toHit = 5
        damage = 2 * d8 + 4
        weight = 1000
        color = Color.WHITE
    }

    val twoHandedSword = blade("a two-handed sword") {
        level = 16
        probability = 50
        toHit = 3
        damage = d8 + 3
        weight = 4200
    }

    // Axes

    private inline fun axe(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.AXE) {
            damage = 1 * d3
            letter = '۶'
            color = Color.DARK_GRAY
            init()
        }

    val hatchet = axe("a hatchet") {
        level = 6
        toHit = 0
        damage = d4 + 2
        weight = 1800
    }

    val concreteAxe = axe("an axe") {
        level = 10
        probability = 80
        toHit = 1
        damage = d6 + 2
        weight = 2700
    }

    val battleAxe = axe("a battle axe") {
        level = 14
        probability = 60
        toHit = 1
        damage = 2 * d3 + 4
        weight = 4500
    }

    val twoHandedAxe = axe("a two-handed axe") {
        level = 18
        probability = 50
        toHit = 3
        damage = d9 + 4
        weight = 7200
    }

    // Blunt weapons

    private inline fun blunt(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.BLUNT) {
            damage = 1 * d3
            letter = '/'
            color = Color.BROWN
            init()
        }

    val club = blunt("a club") {
        level = 2
        damage = 1 * d5
        weight = 2100
    }

    val mace = blunt("a mace") {
        level = 6
        toHit = 1
        damage = d5 + 2
        weight = 2000
        color = Color.BLACK
    }

    val flail = blunt("a flail") {
        level = 9
        probability = 80
        toHit = 2
        damage = d7 + 2
        weight = 3800
    }

    val warHammer = blunt("a war hammer") {
        level = 12
        probability = 60
        toHit = 2
        damage = 2 * d4 + 3
        weight = 4000
        color = Color.GRAY
    }

    val spoon = blunt("spoon") {
        level = 1
        probability = 30
        toHit = -1
        damage = constant(1)
        weight = 20
    }

    // Pointed weapons

    private inline fun pointed(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.SPEAR) {
            letter = '\\'
            color = Color.BROWN
            init()
        }

    val spear = pointed("a spear") {
        level = 5
        toHit = 2
        damage = 1 * d7
        weight = 2700
    }

    val halberd = pointed("a halberd") {
        level = 15
        probability = 50
        toHit = 2
        damage = 2 * d6 + 3
        weight = 3500
        color = Color.GRAY
    }

    // Projectiles

    private inline fun projectile(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.PROJECTILE) {
            damage = 1 * d3
            letter = '}'
            color = Color.BROWN
            init()
        }

    val javelin = projectile("a javelin") {
        level = 11
        probability = 50
        toHit = 1
        damage = d12 + 2
        weight = 2700
    }

    val arrow = projectile("an arrow") {
        level = 1
        probability = 0
        damage = 2 * d4
        weight = 120
        color = Color.BROWNISH
    }

    val bolt = projectile("a bolt") {
        level = 1
        probability = 0
        damage = 2 * d4 + 1
        weight = 160
        color = Color.BROWNISH
    }

    val dart = projectile("a dart") {
        level = 1
        probability = 0
        damage = 1 * d5
        weight = 60
        color = Color.RED
    }

    // Missile launchers

    private inline fun launcher(name: String, init: WeaponDefinition.() -> Unit) =
        weapon(name, WeaponClass.LAUNCHER) {
            damage = 1 * d3
            letter = '{'
            color = Color.BROWN
            init()
        }

    val shortBow = launcher("a short bow") {
        level = 6
        probability = 0
        weight = 2000
    }

    val lightCrossbow = launcher("a light crossbow") {
        level = 11
        probability = 0
        weight = 4500
    }

    // Unique weapons

    val blackSword = blade("the Black Sword") {
        level = 50
        probability = 0
        maximumInstances = 1
        toHit = 5
        damage = 4 * d5 + 5
        weight = 3000
        color = Color.BLACK
    }

    val aluminiumPole = blunt("Aluminium Pole") {
        level = 50
        probability = 0
        maximumInstances = 1
        toHit = 2
        damage = 4 * d5 + 1
        weight = 1000
        color = Color.ALUMINIUM
    }
}
