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

import java.awt.Color
import net.wanhack.model.creature.*
import net.wanhack.model.creature.monsters.*
import net.wanhack.model.item.weapon.NaturalWeapon

object Creatures : Definitions() {

    val monster = creature("monster", isAbstract = true, objectClass = javaClass<Monster>()) {
    }

    // Note about probability: default is 100. Defining this attribute
    // with a value of 200 would thus mean that the creature is twice is likely
    // to appear in the level as one with default.

    // (a) Ants

    val ant = creature("ant", parent = monster, isAbstract = true) {
        letter = 'a'
        corpsePoisonousness = exp("2")
    }

    val whiteAnt = creature("white ant", parent = ant, level = 4) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 7)
        color = Color.WHITE
        level = 4
        luck = 0
        killExperience = 5
        armorClass = 15
        tickRate = 140
        weight = 800
    }

    val greyAnt = creature("grey ant", parent = ant, level = 6) {
        
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,4)"))
        hitPoints = randint(4, 8)
        color = Color.GRAY
        level = 6
        luck = 0
        killExperience = 8
        armorClass = 13
        tickRate = 130
        weight = 850
    }

    val redAnt = creature("red ant", parent = ant, level = 7) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,5)"))
        hitPoints = randint(6, 12)
        color = Color.RED
        level = 7
        luck = 0
        killExperience = 9
        armorClass = 13
        tickRate = 100
        weight = 1100
    }

    val blackAnt = creature("black ant", parent = ant, level = 9, swarmSize = exp("randint(3, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(2,6)"))
        hitPoints = randint(10, 16)
        color = Color.BLACK
        level = 9
        luck = 0
        killExperience = 14
        armorClass = 12
        tickRate = 90
        weight = 1000
    }

    val yellowAnt = creature("yellow ant", parent = ant, level = 12, swarmSize = exp("randint(2, 7)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(4,6)"))
        hitPoints = randint(16, 24)
        color = Color.YELLOW
        level = 12
        luck = 0
        killExperience = 22
        armorClass = 12
        tickRate = 90
        weight = 1300
    }

    val greenAnt = creature("green ant", parent = ant, level = 14, swarmSize = exp("randint(4, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,7)"))
        hitPoints = randint(20, 35)
        color = Colors.GREEN
        level = 14
        luck = 2
        killExperience = 28
        armorClass = 11
        tickRate = 80
        weight = 1300
    }

    val blueAnt = creature("blue ant", parent = ant, level = 16, swarmSize = exp("randint(5, 9)")) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(5,12)"))
        hitPoints = randint(34, 48)
        color = Color.BLUE
        level = 16
        luck = 0
        killExperience = 35
        armorClass = 10
        tickRate = 70
        weight = 1200
    }

    // (A) Angelic Beings

    val baseangel = creature("baseangel", parent = monster, isAbstract = true) {
        letter = 'A'
        canUseDoors = true
        corpsePoisonousness = exp("0")
        color = Color.WHITE
    }

    val angel = creature("angel", parent = baseangel, level = 30) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(6,16)"))
        hitPoints = randint(250, 350)
        level = 30
        luck = 1
        killExperience = 310
        armorClass = 5
        tickRate = 80
        weight = 45000
    }

    val archangel = creature("archangel", parent = baseangel, level = 60) {
        naturalWeapon = NaturalWeapon("hit", exp("8"), exp("randint(10,25)"))
        hitPoints = randint(650, 850)
        level = 60
        luck = 2
        killExperience = 860
        armorClass = -5
        tickRate = 50
        weight = 52000
    }


    // (b) Bats

    val bat = creature("bat", parent = monster, isAbstract = true) {
        letter = 'b'
        corpsePoisonousness = exp("1")
    }

    val giantBat = creature("giant bat", parent = bat, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(2, 5)
        color = Colors.BROWN
        level = 1
        luck = 0
        killExperience = 2
        armorClass = 15
        tickRate = 50
        weight = 2300
    }

    val giantWhiteBat = creature("giant white bat", parent = bat, level = 3) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(2,4)"))
        hitPoints = randint(3, 7)
        color = Color.WHITE
        level = 3
        luck = 0
        killExperience = 4
        armorClass = 14
        tickRate = 50
        weight = 2500
    }

    val giantBlackBat = creature("giant black bat", parent = bat, level = 5, swarmSize = exp("randint(2, 4)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,6)"))
        hitPoints = randint(5, 9)
        color = Color.BLACK
        level = 5
        luck = -1
        killExperience = 6
        armorClass = 14
        tickRate = 50
        weight = 2600
    }

    val giantGreenBat = creature("giant green bat", parent = bat, level = 8, swarmSize = exp("randint(3, 5)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,7)"))
        hitPoints = randint(8, 13)
        color = Colors.GREEN
        level = 8
        luck = 2
        killExperience = 9
        armorClass = 14
        tickRate = 50
        weight = 3000
    }

    // "No, a Siamese bat. They're more dangerous."
    val siameseBat = creature("siamese bat", parent = bat, level = 8, swarmSize = exp("randint(3, 5)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,8)"))
        hitPoints = randint(9, 15)
        color = Colors.GREEN
        level = 8
        luck = 2
        killExperience = 13
        armorClass = 13
        tickRate = 50
        weight = 3000
    }

    // (B) Birds

    val bird = creature("bird", parent = monster, isAbstract = true) {
        letter = 'B'
        corpsePoisonousness = exp("0")
    }

    val headlessChicken = creature("headless chicken", parent = bird, level = 1, probability = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,2)"))
        hitPoints = randint(1, 1)
        color = Color.WHITE
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 20
        tickRate = 60
        weight = 1700
    }

    val europeanSwallow = creature("european swallow", parent = bird, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,2)"))
        hitPoints = randint(1, 3)
        color = Color.BLACK
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 18
        tickRate = 40
        weight = 100
    }

    val africanSwallow = creature("african swallow", parent = bird, level = 2, probability = 60) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(2, 4)
        color = Color.BLACK
        level = 2
        luck = 0
        killExperience = 2
        armorClass = 18
        tickRate = 40
        weight = 200
    }

    val crow = creature("crow", parent = bird, level = 4, swarmSize = exp("randint(1, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,4)"))
        hitPoints = randint(3, 7)
        color = Color.BLACK
        level = 4
        luck = -1
        killExperience = 4
        armorClass = 16
        tickRate = 70
        weight = 500
    }

    // (c) Centipedes

    val centipede = creature("centipede", parent = monster, isAbstract = true) {
        letter = 'c'
        corpsePoisonousness = exp("1")
    }

    val giantWhiteCentipede = creature("giant white centipede", parent = centipede, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 5)
        color = Color.WHITE
        level = 1
        luck = 0
        killExperience = 2
        armorClass = 18
        tickRate = 120
        weight = 800
    }

    val giantYellowCentipede = creature("giant yellow centipede", parent = centipede, level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,4)"))
        hitPoints = randint(4, 6)
        color = Color.YELLOW
        level = 2
        luck = 0
        killExperience = 3
        armorClass = 17
        tickRate = 120
        weight = 800
    }

    val giantGreenCentipede = creature("giant green centipede", parent = centipede, level = 4) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(1,6)"))
        hitPoints = randint(6, 10)
        color = Colors.GREEN
        level = 4
        luck = 3
        killExperience = 5
        armorClass = 16
        tickRate = 110
        weight = 900
    }

    val giantRedCentipede = creature("giant red centipede", parent = centipede, level = 6) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,7)"))
        hitPoints = randint(9, 14)
        color = Color.RED
        level = 6
        luck = 0
        killExperience = 7
        armorClass = 16
        tickRate = 100
        weight = 900
    }

    // (C) Canines

    val canine = creature("canine", parent = monster, isAbstract = true) {
        letter = 'C'
        corpsePoisonousness = exp("1")
    }

    val jackal = creature("jackal", parent = canine, level = 1, swarmSize = exp("randint(3, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(1, 5)
        color = Colors.BROWNISH
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 18
        tickRate = 100
        weight = 6000
    }

    val wildDog = creature("wild dog", parent = canine, level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 5)
        color = Colors.BROWN
        level = 2
        luck = 2
        killExperience = 3
        armorClass = 17
        tickRate = 100
        weight = 14000
    }

    val wolf = creature("wolf", parent = canine, level = 11) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,10)"))
        hitPoints = randint(23, 35)
        color = Color.DARK_GRAY
        level = 11
        luck = 0
        killExperience = 10
        armorClass = 15
        tickRate = 100
        weight = 35000
    }


    // (d) Young/Mature Dragons

    val youngDragon = creature("young dragon", parent = monster, isAbstract = true) {
        letter = 'd'
        canUseDoors = true
        corpsePoisonousness = exp("3")
    }

    val youngRedDragon = creature("young red dragon", parent = youngDragon, level = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("3"), exp("randint(3,13)"))
        hitPoints = randint(60, 80)
        color = Color.RED
        level = 10
        luck = 0
        killExperience = 120
        armorClass = 10
        tickRate = 110
        weight = 120000
    }

    val youngBlackDragon = creature("young black dragon", parent = youngDragon, level = 13) {
        naturalWeapon = NaturalWeapon("hit", exp("3"), exp("randint(6,15)"))
        hitPoints = randint(70, 90)
        color = Color.BLACK
        level = 13
        luck = 0
        killExperience = 140
        armorClass = 8
        tickRate = 110
        weight = 145000
    }

    val matureRedDragon = creature("mature red dragon", parent = youngDragon, level = 22) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(6,20)"))
        hitPoints = randint(220, 300)
        color = Color.RED
        level = 22
        luck = 1
        killExperience = 300
        armorClass = 5
        tickRate = 100
        weight = 420000
    }

    val matureBlackDragon = creature("mature black dragon", parent = youngDragon, level = 27) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(5,22)"))
        hitPoints = randint(270, 390)
        color = Color.BLACK
        level = 27
        luck = 0
        killExperience = 340
        armorClass = 3
        tickRate = 100
        weight = 445000
    }

    // (D) Ancient Dragons

    val ancientDragon = creature("ancient dragon", parent = monster, isAbstract = true) {
        letter = 'D'
        canUseDoors = true
        corpsePoisonousness = exp("4")
    }

    val ancientRedDragon = creature("ancient red dragon", parent = ancientDragon, level = 60) {
        naturalWeapon = NaturalWeapon("hit", exp("12"), exp("randint(8,30)"))
        hitPoints = randint(600, 800)
        color = Color.RED
        level = 60
        luck = 2
        killExperience = 5000
        armorClass = -5
        tickRate = 60
        weight = 800000
    }

    val ancientBlackDragon = creature("ancient black dragon", parent = ancientDragon, level = 65) {
        naturalWeapon = NaturalWeapon("hit", exp("12"), exp("randint(10,40)"))
        hitPoints = randint(700, 850)
        color = Color.BLACK
        level = 65
        luck = 2
        killExperience = 6000
        armorClass = -6
        tickRate = 50
        weight = 845000
    }

    // (e) Floating Eyes

    val eye = creature("eye", parent = monster, isAbstract = true) {
        letter = 'e'
        immobile = true
        corpsePoisonousness = exp("1")
    }

    val floatingEye = creature("floating eye", parent = eye, level = 3) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,5)"))
        hitPoints = randint(8, 14)
        color = Color.YELLOW
        level = 3
        luck = 3
        killExperience = 5
        armorClass = 15
        tickRate = 100
        weight = 9000
    }

    // (E) Elementals

    val elemental = creature("elemental", parent = monster, isAbstract = true) {
        letter = 'E'
        canUseDoors = true
        corpsePoisonousness = exp("1")
    }

    val emmentalElemental = creature("emmental elemental", parent = elemental, level = 8, objectClass = javaClass<EmmentalElemental>()) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(1,8)"))
        hitPoints = randint(70, 140)
        color = Color.YELLOW
        level = 8
        luck = -2
        killExperience = 16
        armorClass = 14
        tickRate = 150
        weight = 200000
    }

    val fogElemental = creature("fog elemental", parent = elemental, level = 18) {
        naturalWeapon = NaturalWeapon("hit", exp("7"), exp("randint(3,5)"))
        hitPoints = randint(120, 180)
        color = Color.WHITE
        level = 18
        luck = 0
        killExperience = 125
        armorClass = 7
        tickRate = 100
        weight = 200
    }

    val brunostElemental = creature("brunost elemental", parent = elemental, level = 38, objectClass = javaClass<EmmentalElemental>()) {
        naturalWeapon = NaturalWeapon("hit", exp("10"), exp("randint(1,11)"))
        hitPoints = randint(270, 340)
        color = Color.YELLOW
        level = 38
        luck = -1
        killExperience = 180
        armorClass = 14
        tickRate = 120
        weight = 280000
    }

    // (f) Felines

    val feline = creature("feline", parent = monster, isAbstract = true) {
        letter = 'f'
        corpsePoisonousness = exp("1")
    }

    val crazyCat = creature("crazy cat", parent = feline, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(1, 4)
        color = Color.BLACK
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 18
        tickRate = 100
        weight = 2200
    }

    val lion = creature("lion", parent = feline, level = 12) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,8)"))
        hitPoints = randint(21, 45)
        color = Colors.YELLOWISH
        level = 12
        luck = 1
        killExperience = 11
        armorClass = 14
        tickRate = 100
        weight = 85000
    }


    // (F) Dragon Flies
    // (g) Golems

    val golem = creature("golem", parent = monster, isAbstract = true) {
        letter = 'g'
        canUseDoors = true
        corpsePoisonousness = exp("3")
    }

    val strawGolem = creature("straw golem", parent = golem, level = 5) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,6)"))
        hitPoints = randint(10, 30)
        color = Color.YELLOW
        level = 5
        luck = -2
        canUseDoors = false
        killExperience = 5
        armorClass = 14
        tickRate = 200
        weight = 15000
    }

    val clayGolem = creature("clay golem", parent = golem, level = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,8)"))
        hitPoints = randint(30, 80)
        color = Colors.BROWN
        level = 10
        luck = -1
        canUseDoors = false
        killExperience = 10
        armorClass = 12
        tickRate = 180
        weight = 120000
    }

    val ironGolem = creature("iron golem", parent = golem, level = 15) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(2,9)"))
        hitPoints = randint(100, 150)
        color = Color.GRAY
        level = 15
        luck = -2
        killExperience = 15
        armorClass = 10
        tickRate = 160
        weight = 350000
    }

    val steelGolem = creature("steel golem", parent = golem, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("9"), exp("randint(2,13)"))
        hitPoints = randint(150, 220)
        color = Color.CYAN
        level = 20
        luck = 0
        killExperience = 20
        armorClass = 8
        tickRate = 140
        weight = 420000
    }


    // (G) Ghosts

    val baseghost = creature("baseghost", parent = monster, isAbstract = true) {
        letter = 'G'
        corporeal = false
        omniscient = true
    }

    val spook = creature("spook", parent = baseghost, level = 7) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,6)"))
        hitPoints = randint(8, 13)
        color = Color.WHITE
        level = 7
        luck = 1
        killExperience = 9
        armorClass = 13
        tickRate = 130
        weight = 0
    }

    val ghost = creature("ghost", parent = baseghost, level = 13) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,9)"))
        hitPoints = randint(23, 33)
        color = Colors.WHITEISH
        level = 13
        luck = 1
        killExperience = 19
        armorClass = 13
        tickRate = 110
        weight = 0
    }

    val banshee = creature("banshee", parent = baseghost, level = 19) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(4,6)"))
        hitPoints = randint(33, 53)
        color = Color.CYAN
        level = 19
        luck = 1
        killExperience = 22
        armorClass = 13
        tickRate = 100
        weight = 0
    }

    val bunshee = creature("bunshee", parent = baseghost, level = 23, probability = 30) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(6,13)"))
        hitPoints = randint(53, 73)
        color = Colors.BROWNISH
        level = 23
        luck = 3
        killExperience = 25
        armorClass = 9
        tickRate = 90
        weight = 0
    }


    // (h) Hobbits, Dwarves, Elves
    // (H) Hybrid Monsters

    val hybrid = creature("hybrid", parent = monster, isAbstract = true) {
        letter = 'H'
        color = Color.RED
        corpsePoisonousness = exp("2")
    }

    // (i) Icky Things
    val ickyThing = creature("icky thing", parent = monster, isAbstract = true) {
        letter = 'i'
        corpsePoisonousness = exp("1")
    }

    val whiteIckyThing = creature("white icky thing", parent = ickyThing, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(2, 5)
        color = Color.WHITE
        level = 1
        luck = -2
        killExperience = 2
        armorClass = 14
        tickRate = 100
        weight = 21000
    }

    val redIckyThing = creature("red icky thing", parent = ickyThing, level = 8) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,7)"))
        hitPoints = randint(21, 35)
        color = Color.RED
        level = 8
        luck = 0
        killExperience = 11
        armorClass = 11
        tickRate = 100
        weight = 33500
    }


    // (I) Insects

    val insect = creature("insect", parent = monster, isAbstract = true) {
        letter = 'I'
        corpsePoisonousness = exp("1")
    }

    val giantWasp = creature("giant wasp", parent = insect, level = 3, swarmSize = exp("randint(1, 7)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(1,4)"))
        hitPoints = randint(4, 6)
        color = Color.YELLOW
        level = 3
        luck = 0
        killExperience = 4
        armorClass = 15
        tickRate = 80
        weight = 750
    }

    val inarianMosquito = creature("inarian mosquito", parent = insect, level = 15, swarmSize = exp("randint(4, 14)")) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(3,5)"))
        hitPoints = randint(20, 27)
        color = Color.BLACK
        level = 15
        luck = 2
        killExperience = 17
        armorClass = 10
        tickRate = 80
        weight = 1150
    }


    // (j) Jellies

    val jelly = creature("jelly", parent = monster, isAbstract = true) {
        letter = 'j'
        immobile = true
        corpsePoisonousness = exp("0")
    }

    val whiteJelly = creature("white jelly", parent = jelly, level = 3) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(2,5)"))
        hitPoints = randint(6, 15)
        color = Color.WHITE
        level = 3
        luck = -1
        killExperience = 5
        armorClass = 13
        tickRate = 120
        weight = 7000
    }

    val greenJelly = creature("green jelly", parent = jelly, level = 8) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(3,6)"))
        hitPoints = randint(26, 35)
        color = Colors.GREEN
        level = 3
        luck = 0
        killExperience = 7
        armorClass = 12
        tickRate = 100
        weight = 7500
    }


    // (J) Snakes

    val basesnake = creature("basesnake", parent = monster, isAbstract = true) {
        letter = 'J'
        corpsePoisonousness = exp("3")
    }

    val gardenSnake = creature("garden snake", parent = basesnake, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(1,4)"))
        hitPoints = randint(3, 5)
        color = Colors.GREEN
        level = 1
        luck = -1
        killExperience = 2
        armorClass = 15
        tickRate = 130
        weight = 400
    }

    val rattlesnake = creature("rattlesnake", parent = basesnake, level = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,8)"))
        hitPoints = randint(15, 25)
        color = Colors.BROWNISH
        level = 10
        luck = -1
        killExperience = 9
        armorClass = 13
        tickRate = 100
        weight = 1200
    }

    val kingCobra = creature("king cobra", parent = basesnake, level = 18) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(3,13)"))
        hitPoints = randint(45, 65)
        color = Color.BLACK
        level = 18
        luck = 1
        killExperience = 20
        armorClass = 6
        tickRate = 90
        weight = 3700
    }

    // (k) Kobolds
    val basekobold = creature("basekobold", parent = monster, isAbstract = true) {
        letter = 'k'
        canUseDoors = true
        corpsePoisonousness = exp("3")
    }

    val kobold = creature("kobold", parent = basekobold, level = 3, swarmSize = exp("randint(1, 4)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 7)
        color = Color.RED
        level = 3
        luck = -2
        killExperience = 4
        armorClass = 15
        tickRate = 110
        weight = 35000
    }

    val largeKobold = creature("large kobold", parent = basekobold, level = 14) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(2,7)"))
        hitPoints = randint(40, 65)
        color = Color.BLUE
        level = 14
        luck = -1
        killExperience = 20
        armorClass = 10
        tickRate = 100
        weight = 60000
    }

    // (K) Killer Beetles
    // (l) Lice
    // (L) Liches
    // (m) Molds

    val mold = creature("mold", parent = monster, isAbstract = true) {
        letter = 'm'
        immobile = true
        corpsePoisonousness = exp("3")
    }

    val greenMold = creature("green mold", parent = mold, level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(4, 8)
        color = Colors.GREEN
        level = 2
        luck = 3
        killExperience = 2
        armorClass = 15
        tickRate = 150
        weight = 4000
    }

    val brownMold = creature("brown mold", parent = mold, level = 3) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(2,6)"))
        hitPoints = randint(5, 9)
        color = Colors.BROWN
        level = 3
        luck = 0
        killExperience = 3
        armorClass = 15
        tickRate = 140
        weight = 4500
    }

    val blackMold = creature("black mold", parent = mold, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(3,14)"))
        hitPoints = randint(70, 100)
        color = Color.BLACK
        level = 20
        luck = 2
        killExperience = 25
        armorClass = 6
        tickRate = 100
        weight = 9500
    }

    // (M) Multi-headed Reptiles (Hydras)
    // (n) Nagas
    // (N) (NOT IN USE)
    // (o) Orcs

    val baseorc = creature("baseorc", parent = monster, isAbstract = true) {
        letter = 'o'
        color = Colors.DARK_GREEN
        canUseDoors = true
        corpsePoisonousness = exp("3")
    }

    val orc = creature("orc", parent = baseorc, level = 8, swarmSize = exp("randint(1, 10)")) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(1,7)"))
        hitPoints = randint(10, 20)
        level = 8
        luck = 0
        killExperience = 10
        armorClass = 12
        tickRate = 100
        weight = 65000
    }

    val largeOrc = creature("large orc", parent = baseorc, level = 14, swarmSize = exp("randint(1, 4)")) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(3,10)"))
        hitPoints = randint(30, 50)
        level = 14
        luck = 0
        color = Colors.BROWN
        killExperience = 19
        armorClass = 8
        tickRate = 100
        weight = 85000
    }

    // (O) Ogres

    val baseogre = creature("baseogre", parent = monster, isAbstract = true) {
        letter = 'O'
        color = Colors.BROWN
        canUseDoors = true
        corpsePoisonousness = exp("1")
    }

    val ogre = creature("ogre", parent = baseogre, level = 25, swarmSize = exp("randint(1, 4)")) {
        naturalWeapon = NaturalWeapon("hit", exp("7"), exp("randint(1,10)"))
        hitPoints = randint(150, 250)
        level = 25
        luck = 0
        killExperience = 31
        armorClass = 10
        tickRate = 100
        weight = 135000
    }

    // (p) Persons (humans)

    val person = creature("person", parent = monster, isAbstract = true) {
        letter = 'p'
        canUseDoors = true
        corpsePoisonousness = exp("1")
    }

    val knightOfNi = creature("knight of ni", parent = person, swarmSize = exp("randint(1, 5)"), objectClass = javaClass<KnightOfNi>(), level = 8) {
        hitPoints = randint(20, 30)
        level = 8
        color = Colors.BLACKISH
        luck = 2
        wieldedWeapon = Weapons.scimitar.create()
        killExperience = 25
        armorClass = 10
        tickRate = 100
        weight = 75000
    }

    val poorKnightOfQueenMargareta = creature("poor knight of Queen Margareta", parent = person, level = 15) {
        hitPoints = randint(70, 120)
        level = 15
        color = Color.RED
        luck = 0
        wieldedWeapon = Weapons.dagger.create()
        killExperience = 18
        armorClass = 14
        tickRate = 90
        weight = 85000

        inventoryItems.add(Items.oldRags.create())
    }

    // (P) Giants

    val giant = creature("giant", parent = monster, isAbstract = true) {
        letter = 'P'
        color = Colors.BROWN
        canUseDoors = true
        corpsePoisonousness = exp("1")
    }

    val hillGiant = creature("hill giant", parent = giant, level = 31) {
        naturalWeapon = NaturalWeapon("hit", exp("10"), exp("randint(2,9)"))
        hitPoints = randint(200, 300)
        level = 31
        luck = 0
        killExperience = 40
        armorClass = 6
        tickRate = 100
        weight = 170000
    }

    // (q) Quadrupeds
    // (Q) Quylthulgs
    // (r) Rodents

    val rodent = creature("rodent", parent = monster, isAbstract = true) {
        letter = 'r'
        color = Color.BLACK
        corpsePoisonousness = exp("1")
    }

    val caveRat = creature("cave rat", parent = rodent, level = 1, swarmSize = exp("randint(1, 8)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(1, 3)
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 16
        tickRate = 90
        weight = 500
    }

    // (R) Reptiles and Amphibians

    val baselizard = creature("baselizard", parent = monster, isAbstract = true) {
        letter = 'R'
        color = Colors.GREEN
        corpsePoisonousness = exp("0")
    }

    val newt = creature("newt", parent = baselizard, level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(1,3)"))
        hitPoints = randint(1, 3)
        color = Color.YELLOW
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 16
        tickRate = 120
        weight = 300
    }

    val lizard = creature("lizard", parent = baselizard, level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(2, 4)
        color = Colors.GREEN
        level = 2
        luck = 2
        killExperience = 2
        armorClass = 16
        tickRate = 100
        weight = 670
    }

    val crocodile = creature("crocodile", parent = baselizard, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("7"), exp("randint(4,7)"))
        hitPoints = randint(50, 80)
        color = Colors.DARK_GREEN
        level = 20
        luck = 0
        killExperience = 15
        armorClass = 16
        tickRate = 100
        weight = 200000
    }

    val crazyFrog = creature("crazy frog", parent = baselizard, level = 15) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(2,7)"))
        hitPoints = randint(50, 70)
        color = Color.BLACK
        level = 15
        luck = 0
        killExperience = 12
        armorClass = 14
        tickRate = 100
        weight = 6400
    }

    // (s) Skeletons
    // (S) Spiders

    val basespider = creature("basespider", parent = monster, isAbstract = true) {
        letter = 'S'
        color = Color.BLACK
        corpsePoisonousness = exp("2")
    }

    val caveSpider = creature("cave spider", parent = basespider, level = 1, probability = 60) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 6)
        level = 1
        luck = 0
        killExperience = 3
        armorClass = 16
        tickRate = 110
        weight = 1300
    }

    val veryPoisonousSpider = creature("very poisonous spider", parent = basespider, probability = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(2,5)"))
        hitPoints = randint(3, 6)
        level = 1
        luck = 2
        killExperience = 3
        armorClass = 16
        tickRate = 110
        weight = 800
        corpsePoisonousness = exp("randint(20, 40)")
    }

    // (t) Ticks
    // (T) Trolls

    val basetroll = creature("basetroll", parent = monster, isAbstract = true) {
        letter = 'T'
        color = Colors.DARK_GREEN
        canUseDoors = true
        corpsePoisonousness = exp("2")
    }

    val troll = creature("troll", parent = basetroll, level = 15) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(3,12)"))
        hitPoints = randint(150, 200)
        level = 15
        luck = -2
        killExperience = 26
        armorClass = 14
        tickRate = 110
        weight = 110000
    }
    // (u) Minor Demons

    val minorDemon = creature("minor demon", parent = monster, isAbstract = true) {
        letter = 'u'
        color = Color.RED
        corpsePoisonousness = exp("3")
    }

    val imp = creature("imp", parent = minorDemon, level = 15) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(1,7)"))
        hitPoints = randint(60, 90)
        level = 15
        luck = 2
        color = Colors.GREEN
        killExperience = 21
        armorClass = 12
        tickRate = 80
        weight = 59000
    }


    // (U) Major Demons

    val majorDemon = creature("major demon", parent = monster, isAbstract = true) {
        letter = 'U'
        color = Color.RED
        canUseDoors = true
        corpsePoisonousness = exp("5")
    }

    val hornedDemon = creature("horned demon", parent = majorDemon, level = 35) {
        naturalWeapon = NaturalWeapon("hit", exp("12"), exp("randint(4,17)"))
        hitPoints = randint(260, 330)
        level = 35
        luck = 2
        color = Color.RED
        killExperience = 280
        armorClass = 6
        tickRate = 80
        weight = 132000
    }


    // (v) Vortexes
    // (V) Vampires

    val basevampire = creature("basevampire", parent = monster, isAbstract = true) {
        letter = 'z'
        color = Color.BLACK
        canUseDoors = true
        corpsePoisonousness = exp("5")
    }

    val vampire = creature("vampire", parent = basevampire, level = 25) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(3,10)"))
        hitPoints = randint(50, 90)
        level = 25
        luck = 2
        killExperience = 24
        armorClass = 6
        tickRate = 100
        weight = 63000
    }

    // (w) Worms and Worm Masses
    // (W) Wraiths

    val basewraith = creature("basewraith", objectClass = javaClass<Wraith>(), parent = monster, isAbstract = true) {
        letter = 'W'
        color = Color.BLACK
        canUseDoors = true
    }

    val bogWraith = creature("bog wraith", parent = basewraith, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,6)"))
        hitPoints = randint(40, 60)
        level = 20
        luck = 0
        killExperience = 23
        armorClass = 7
        tickRate = 100
        weight = 15000
    }

    val forestWraith = creature("forest wraith", parent = basewraith, level = 25) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(2,8)"))
        hitPoints = randint(60, 90)
        level = 25
        luck = 2
        color = Colors.GREEN
        killExperience = 29
        armorClass = 6
        tickRate = 90
        weight = 15000
    }

    // (x) (NOT IN USE)
    // (X) Xorn, Xaren etc.
    // (y) Yeeks
    // (Y) Yetis

    // (z) Zombies
    val basezombie = creature("basezombie", parent = monster, isAbstract = true) {
        letter = 'z'
        color = Color.BLACK
        corpsePoisonousness = exp("3")
    }

    val koboldZombie = creature("kobold zombie", parent = basezombie, level = 8) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(1,6)"))
        hitPoints = randint(20, 40)
        level = 8
        luck = 0
        killExperience = 11
        armorClass = 10
        tickRate = 100
        weight = 55000
    }

    val orcZombie = creature("orc zombie", parent = basezombie, level = 14) {
        naturalWeapon = NaturalWeapon("hit", exp("3"), exp("randint(2,8)"))
        hitPoints = randint(35, 55)
        level = 14
        luck = 0
        color = Colors.DARK_GREEN
        killExperience = 16
        armorClass = 10
        tickRate = 100
        weight = 85000
    }

    val trollZombie = creature("troll zombie", parent = basezombie, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(4,10)"))
        hitPoints = randint(100, 170)
        level = 20
        luck = 1
        color = Colors.GREEN
        canUseDoors = true
        killExperience = 31
        armorClass = 7
        tickRate = 100
        weight = 155000
    }

    // (Z) Zephyr Hounds


    // Special creatures

    // Special creatures
    val blackKnight = creature("The Black Knight", objectClass = javaClass<BlackKnight>(), probability = 0, level = 30) {
        hitPoints = randint(500, 600)
        color = Color.BLACK
        level = 30
        hitBonus = 20
        weight = 80000
        luck = 2
        canUseDoors = true
        killExperience = 4000
        armorClass = -6
        tickRate = 60
        wieldedWeapon = Weapons.blackSword.create()
    }

    val bugsBunny = creature("Bugs Bunny", objectClass = javaClass<BugsBunny>(), probability = 0, level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("10"), exp("randint(4,10)"))
        hitPoints = randint(100, 200)
        color = Color.WHITE
        level = 20
        luck = 2
        killExperience = 450
        armorClass = 0
        tickRate = 50
        weight = 4500
    }

    val oracle = creature("The Oracle", objectClass = javaClass<Oracle>(), probability = 0) {
    }

    val surstromming = creature("the Surstromming", parent = hybrid, level = 50, probability = 0) {
        naturalWeapon = NaturalWeapon("hit", exp("15"), exp("randint(6,20)"))
        hitPoints = randint(800, 850)
        color = Color.BLACK
        level = 50
        luck = 1
        canUseDoors = true
        killExperience = 5500
        armorClass = -6
        tickRate = 50
        weight = 123000
        corpsePoisonousness = exp("10")
    }

    val melog = creature("the Melog", parent = golem, level = 30, probability = 0) {
        naturalWeapon = NaturalWeapon("hit", exp("8"), exp("randint(3,15)"))
        hitPoints = 550
        color = Color.BLUE
        level = 30
        luck = 1
        canUseDoors = true
        killExperience = 2500
        armorClass = -6
        tickRate = 100
        weight = 400000
    }
}
