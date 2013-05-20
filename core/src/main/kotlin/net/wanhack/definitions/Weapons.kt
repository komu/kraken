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

import net.wanhack.model.item.weapon.*
import java.awt.Color

object Weapons : Definitions() {

    // Blades

    val blade = item("blade", isAbstract=true, objectClass=javaClass<Sword>()) {
        damage =  exp("1d3")
        letter = '†'
        color = Color.DARK_GRAY
    }

    val dagger = item("a dagger", blade, level=2) {
        weight = 100
    }

    val knife = item("a knife", blade, level=1) {
        damage = exp("1d2")
    }

    val rapier = item("a rapier", blade, level=3) {
        toHit = exp("1")
        damage = exp("1d5")
        weight = 1100
    }

    val shortSword = item("a short sword", blade, level=5) {
        toHit = exp("1")
        damage = exp("1d6")
        weight = 1200
    }

    val scimitar = item("a scimitar", blade, level=8, probability=80) {
        toHit = exp("2")
        damage = exp("1d6+1")
        weight = 2100
    }

    val longSword = item("a long sword", blade, level=10, probability=60) {
        toHit = exp("2")
        damage = exp("1d6+2")
        weight = 2500
    }

    val mithrilSword = item("a mithril sword", blade, level=20, probability=10) {
        toHit = exp("5")
        damage = exp("2d8+4")
        weight = 1000
        color = Color.WHITE
    }

    val twoHandedSword = item("a two-handed sword", blade, level=16, probability=50) {
        toHit = exp("3")
        damage = exp("1d8+3")
        weight = 4200
    }

    // Axes

    val axe = item("axe", isAbstract=true, objectClass=javaClass<net.wanhack.model.item.weapon.Sword>()) {
        damage = exp("1d3")
        letter = '۶'
        color = Color.DARK_GRAY
    }
    
    val hatchet = item("a hatchet", parent=axe, level=6) {
        toHit = exp("0")
        damage = exp("1d4+2")
        weight = 1800
    }
    
    val concreteAxe = item("an axe", parent=axe, level=10, probability=80) {
        toHit = exp("1")
        damage = exp("1d6+2")
        weight = 2700
    }
    
    val battleAxe = item("a battle axe", parent=axe, level=14, probability=60) {
        toHit = exp("1")
        damage = exp("2d3+4")
        weight = 4500
    }

    val twoHandedAxe = item("a two-handed axe", parent=blade, level=18, probability=50) {
        toHit = exp("3")
        damage = exp("1d9+4")
        weight = 7200
    }
    
    // Blunt weapons
    
    val blunt = item("blunt", isAbstract=true, objectClass=javaClass<BluntWeapon>()) {
        damage = exp("1d3")
        letter = '/'
        color = Colors.BROWN
    }
    
    val club = item("a club", parent=blunt, level=2) {
        toHit = exp("0")
        damage = exp("1d5")
        weight = 2100
    }
    
    val mace = item("a mace", parent=blunt, level=6) {
        toHit = exp("1")
        damage = exp("1d5+2")
        weight = 2000
        color = Color.BLACK
    }
    
    val flail = item("a flail", parent=blunt, level=9, probability=80) {
        toHit = exp("2")
        damage = exp("1d7+2")
        weight = 3800
    }
    
    val warHammer = item("a war hammer", parent=blunt, level=12, probability=60) {
        toHit = exp("2")
        damage = exp("2d4+3")
        weight = 4000
        color = Color.GRAY
    }
    
    val spoon = item("spoon", parent=blunt, level=1, probability=30) {
        toHit = exp("-1")
        damage = exp("1")
        weight = 20
    }
    
    // Pointed weapons
    
    val pointed = item("pointed", isAbstract=true, objectClass=javaClass<PointedWeapon>()) {
        letter = '\\'
        color = Colors.BROWN
    }
    
    val spear = item("a spear", parent=pointed, level=5) {
        toHit = exp("2")
        damage = exp("1d7")
        weight = 2700
    }
    
    val halberd = item("a halberd", parent=pointed, level=15, probability=50) {
        toHit = exp("2")
        damage = exp("2d6+3")
        weight = 3500
        color = Color.GRAY
    }
    
    // Projectiles
    
    val projectile = item("projectile", isAbstract=true, objectClass=javaClass<Projectile>()) {
        damage = exp("1d3")
        letter = '}'
        color = Colors.BROWN
    }
    
    val javelin = item("a javelin", parent=projectile, level=11, probability=50) {
        toHit = exp("1")
        damage = exp("1d12+2")
        weight = 2700
    }
    
    val arrow = item("an arrow", parent=projectile, level=1, probability=0) {
        damage = exp("2d4")
        weight = 120
        color = Colors.BROWNISH
    }
    
    val bolt = item("a bolt", parent=projectile, level=1, probability=0) {
        damage = exp("2d4+1randint(4, 9)")
        weight = 160
        color = Colors.BROWNISH
    }
    
    val dart = item("a dart", parent=projectile, level=1, probability=0) {
        damage = exp("1d5")
        weight = 60
        color = Color.RED
    }
    
    // Missile launchers
    
    val launcher = item("launcher", isAbstract=true, objectClass=javaClass<MissileLauncher>()) {
        damage = exp("randint(1, 3)")
        letter = '{'
        color = Colors.BROWN
    }
    
    val shortBow = item("a short bow", parent=launcher, level=6, probability=0) {
         weight = 2000
    }
    
    val lightCrossbow = item("a light crossbow", parent=launcher, level=11, probability=0) {
        weight = 4500
    }
    
    // Unique weapons
    
    val blackSword = item("the Black Sword", parent=blade, level=50, probability=0, maximumInstances=1) {
        toHit = exp("5")
        damage = exp("4d5+5")
        weight = 3000
        color = Color.BLACK
    }
    
    val aluminiumPole = item("Aluminium Pole", parent=blunt, level=50, probability=0, maximumInstances=1) {
        toHit = exp("2")
        damage = exp("4d5+1")
        weight = 1000
        color = Colors.ALUMINIUM
    }
}
