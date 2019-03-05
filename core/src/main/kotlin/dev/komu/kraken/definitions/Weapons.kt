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
import dev.komu.kraken.model.item.weapon.*

@Suppress("unused")
object Weapons : dev.komu.kraken.definitions.Definitions() {

    // Blades

    fun blade(name: String, level: Int? = null, probability: Int? = null, maximumInstances: Int? = null, init: (Sword.() -> Unit)) =
        item(name, level=level, probability=probability, maximumInstances=maximumInstances) {
            val s = Sword(name)
            s.init()
            s
        }

    val dagger = blade("a dagger", level=2) {
        weight = 100
    }

    val knife = blade("a knife", level=1) {
        damage = exp("1d2")
    }

    val rapier = blade("a rapier", level=3) {
        toHit = exp("1")
        damage = exp("1d5")
        weight = 1100
    }

    val shortSword = blade("a short sword", level=5) {
        toHit = exp("1")
        damage = exp("1d6")
        weight = 1200
    }

    val scimitar = blade("a scimitar", level=8, probability=80) {
        toHit = exp("2")
        damage = exp("1d6+1")
        weight = 2100
    }

    val longSword = blade("a long sword", level=10, probability=60) {
        toHit = exp("2")
        damage = exp("1d6+2")
        weight = 2500
    }

    val mithrilSword = blade("a mithril sword", level=20, probability=10) {
        toHit = exp("5")
        damage = exp("2d8+4")
        weight = 1000
        color = Color.WHITE
    }

    val twoHandedSword = blade("a two-handed sword", level=16, probability=50) {
        toHit = exp("3")
        damage = exp("1d8+3")
        weight = 4200
    }

    // Axes

    fun axe(name: String, level: Int? = null, probability: Int? = null, init: (Sword.() -> Unit)) =
            item(name, level=level, probability=probability) {
                val s = Sword(name)
                s.damage = exp("1d3")
                s.letter = '۶'
                s.color = Color.DARK_GRAY
                s.init()
                s
            }

    val hatchet = axe("a hatchet", level=6) {
        toHit = exp("0")
        damage = exp("1d4+2")
        weight = 1800
    }
    
    val concreteAxe = axe("an axe", level=10, probability=80) {
        toHit = exp("1")
        damage = exp("1d6+2")
        weight = 2700
    }
    
    val battleAxe = axe("a battle axe", level=14, probability=60) {
        toHit = exp("1")
        damage = exp("2d3+4")
        weight = 4500
    }

    val twoHandedAxe = axe("a two-handed axe", level=18, probability=50) {
        toHit = exp("3")
        damage = exp("1d9+4")
        weight = 7200
    }
    
    // Blunt weapons

    fun blunt(name: String, level: Int? = null, probability: Int? = null, maximumInstances: Int? = null, init: (BluntWeapon.() -> Unit)) =
        item(name, level=level, probability=probability, maximumInstances=maximumInstances) {
            val w = BluntWeapon(name)
            w.damage = exp("1d3")
            w.letter = '/'
            w.color = Color.BROWN
            w.init()
            w
        }

    val club = blunt("a club", level=2) {
        toHit = exp("0")
        damage = exp("1d5")
        weight = 2100
    }
    
    val mace = blunt("a mace", level=6) {
        toHit = exp("1")
        damage = exp("1d5+2")
        weight = 2000
        color = Color.BLACK
    }
    
    val flail = blunt("a flail", level=9, probability=80) {
        toHit = exp("2")
        damage = exp("1d7+2")
        weight = 3800
    }
    
    val warHammer = blunt("a war hammer", level=12, probability=60) {
        toHit = exp("2")
        damage = exp("2d4+3")
        weight = 4000
        color = Color.GRAY
    }
    
    val spoon = blunt("spoon", level=1, probability=30) {
        toHit = exp("-1")
        damage = exp("1")
        weight = 20
    }
    
    // Pointed weapons

    fun pointed(name: String, level: Int? = null, probability: Int? = null, maximumInstances: Int? = null, init: (PointedWeapon.() -> Unit)) =
            item(name, level=level, probability=probability, maximumInstances=maximumInstances) {
                val w = PointedWeapon(name)
                w.letter = '\\'
                w.color = Color.BROWN
                w.init()
                w
            }

    val spear = pointed("a spear", level=5) {
        toHit = exp("2")
        damage = exp("1d7")
        weight = 2700
    }
    
    val halberd = pointed("a halberd", level=15, probability=50) {
        toHit = exp("2")
        damage = exp("2d6+3")
        weight = 3500
        color = Color.GRAY
    }
    
    // Projectiles

    fun projectile(name: String, level: Int? = null, probability: Int? = null, maximumInstances: Int? = null, init: (Projectile.() -> Unit)) =
        item(name, level=level, probability=probability, maximumInstances=maximumInstances) {
            val w = Projectile(name)
            w.damage = exp("1d3")
            w.letter = '}'
            w.color = Color.BROWN
            w.init()
            w
        }

    val javelin = projectile("a javelin", level=11, probability=50) {
        toHit = exp("1")
        damage = exp("1d12+2")
        weight = 2700
    }
    
    val arrow = projectile("an arrow", level=1, probability=0) {
        damage = exp("2d4")
        weight = 120
        color = Color.BROWNISH
    }
    
    val bolt = projectile("a bolt", level=1, probability=0) {
        damage = exp("2d4+1randint(4, 9)")
        weight = 160
        color = Color.BROWNISH
    }
    
    val dart = projectile("a dart", level=1, probability=0) {
        damage = exp("1d5")
        weight = 60
        color = Color.RED
    }
    
    // Missile launchers

    fun launcher(name: String, level: Int? = null, probability: Int? = null, maximumInstances: Int? = null, init: (MissileLauncher.() -> Unit)) =
        item(name, level=level, probability=probability, maximumInstances=maximumInstances) {
            val w = MissileLauncher(name)
            w.damage = exp("randint(1, 3)")
            w.letter = '{'
            w.color = Color.BROWN
            w.init()
            w
        }

    val shortBow = launcher("a short bow", level=6, probability=0) {
         weight = 2000
    }
    
    val lightCrossbow = launcher("a light crossbow", level=11, probability=0) {
        weight = 4500
    }
    
    // Unique weapons
    
    val blackSword = blade("the Black Sword", level=50, probability=0, maximumInstances=1) {
        toHit = exp("5")
        damage = exp("4d5+5")
        weight = 3000
        color = Color.BLACK
    }
    
    val aluminiumPole = blunt("Aluminium Pole", level=50, probability=0, maximumInstances=1) {
        toHit = exp("2")
        damage = exp("4d5+1")
        weight = 1000
        color = Color.ALUMINIUM
    }
}
