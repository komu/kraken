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

import net.wanhack.model.common.Color
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Monster
import net.wanhack.model.creature.Oracle
import net.wanhack.model.creature.monsters.*
import net.wanhack.model.item.weapon.NaturalWeapon
import net.wanhack.utils.exp.Expression

@Suppress("unused")
object Creatures : Definitions() {

    fun monster(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
            creature(name, level = level, swarmSize = swarmSize, probability = probability) {
                val monster = Monster(name)
                monster.init()
                monster
            }

    // Note about probability: default is 100. Defining this attribute
    // with a value of 200 would thus mean that the creature is twice is likely
    // to appear in the level as one with default.

    // (a) Ants

    fun ant(name: String, level: Int, swarmSize: Expression? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize) {
                letter = 'a'
                corpsePoisonousness = exp("2")
                init()
            }

    val whiteAnt = ant("white ant", level = 4) {
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

    val greyAnt = ant("grey ant", level = 6) {
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

    val redAnt = ant("red ant", level = 7) {
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

    val blackAnt = ant("black ant", level = 9, swarmSize = exp("randint(3, 6)")) {
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

    val yellowAnt = ant("yellow ant", level = 12, swarmSize = exp("randint(2, 7)")) {
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

    val greenAnt = ant("green ant", level = 14, swarmSize = exp("randint(4, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,7)"))
        hitPoints = randint(20, 35)
        color = Color.GREEN
        level = 14
        luck = 2
        killExperience = 28
        armorClass = 11
        tickRate = 80
        weight = 1300
    }

    val blueAnt = ant("blue ant", level = 16, swarmSize = exp("randint(5, 9)")) {
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

    fun baseAngel(name: String, level: Int, swarmSize: Expression? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize) {
                letter = 'A'
                canUseDoors = true
                corpsePoisonousness = exp("0")
                color = Color.WHITE
                init()
            }

    val angel = baseAngel("angel", level = 30) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(6,16)"))
        hitPoints = randint(250, 350)
        level = 30
        luck = 1
        killExperience = 310
        armorClass = 5
        tickRate = 80
        weight = 45000
    }

    val archangel = baseAngel("archangel", level = 60) {
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
    fun bat(name: String, level: Int, swarmSize: Expression? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize) {
                letter = 'b'
                corpsePoisonousness = exp("1")
                init()
            }

    val giantBat = bat("giant bat", level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(2, 5)
        color = Color.BROWN
        level = 1
        luck = 0
        killExperience = 2
        armorClass = 15
        tickRate = 50
        weight = 2300
    }

    val giantWhiteBat = bat("giant white bat", level = 3) {
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

    val giantBlackBat = bat("giant black bat", level = 5, swarmSize = exp("randint(2, 4)")) {
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

    val giantGreenBat = bat("giant green bat", level = 8, swarmSize = exp("randint(3, 5)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,7)"))
        hitPoints = randint(8, 13)
        color = Color.GREEN
        level = 8
        luck = 2
        killExperience = 9
        armorClass = 14
        tickRate = 50
        weight = 3000
    }

    // "No, a Siamese bat. They're more dangerous."
    val siameseBat = bat("siamese bat", level = 8, swarmSize = exp("randint(3, 5)")) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,8)"))
        hitPoints = randint(9, 15)
        color = Color.GREEN
        level = 8
        luck = 2
        killExperience = 13
        armorClass = 13
        tickRate = 50
        weight = 3000
    }

    // (B) Birds

    fun bird(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize, probability = probability) {
                letter = 'B'
                corpsePoisonousness = exp("0")
                init()
            }

    val headlessChicken = bird("headless chicken", level = 1, probability = 20) {
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

    val europeanSwallow = bird("european swallow", level = 1) {
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

    val africanSwallow = bird("african swallow", level = 2, probability = 60) {
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

    val crow = bird("crow", level = 4, swarmSize = exp("randint(1, 6)")) {
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

    fun centipede(name: String, level: Int, swarmSize: Expression? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize) {
                letter = 'c'
                corpsePoisonousness = exp("1")
                init()
            }

    val giantWhiteCentipede = centipede("giant white centipede", level = 1) {
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

    val giantYellowCentipede = centipede("giant yellow centipede", level = 2) {
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

    val giantGreenCentipede = centipede("giant green centipede", level = 4) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(1,6)"))
        hitPoints = randint(6, 10)
        color = Color.GREEN
        level = 4
        luck = 3
        killExperience = 5
        armorClass = 16
        tickRate = 110
        weight = 900
    }

    val giantRedCentipede = centipede("giant red centipede", level = 6) {
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


    fun canine(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize, probability = probability) {
                letter = 'C'
                corpsePoisonousness = exp("1")
                init()
            }

    val jackal = canine("jackal", level = 1, swarmSize = exp("randint(3, 6)")) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,3)"))
        hitPoints = randint(1, 5)
        color = Color.BROWNISH
        level = 1
        luck = 0
        killExperience = 1
        armorClass = 18
        tickRate = 100
        weight = 6000
    }

    val wildDog = canine("wild dog", level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 5)
        color = Color.BROWN
        level = 2
        luck = 2
        killExperience = 3
        armorClass = 17
        tickRate = 100
        weight = 14000
    }

    val wolf = canine("wolf", level = 11) {
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

    fun youngDragon(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize, probability = probability) {
                letter = 'd'
                canUseDoors = true
                corpsePoisonousness = exp("3")
                init()
            }

    val youngRedDragon = youngDragon("young red dragon", level = 10) {
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

    val youngBlackDragon = youngDragon("young black dragon", level = 13) {
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

    val matureRedDragon = youngDragon("mature red dragon", level = 22) {
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

    val matureBlackDragon = youngDragon("mature black dragon", level = 27) {
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

    fun ancientDragon(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
            monster(name, level = level, swarmSize = swarmSize, probability = probability) {
                letter = 'D'
                canUseDoors = true
                corpsePoisonousness = exp("4")
                init()
            }

    val ancientRedDragon = ancientDragon("ancient red dragon", level = 60) {
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

    val ancientBlackDragon = ancientDragon("ancient black dragon", level = 65) {
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

    fun eye(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'e'
            immobile = true
            corpsePoisonousness = exp("1")
            init()
        }

    val floatingEye = eye("floating eye", level = 3) {
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

    fun <T: Creature> elemental(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, create: (String) -> T, init: T.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            val e = create(name)
            e.letter = 'E'
            e.canUseDoors = true
            e.corpsePoisonousness = exp("1")
            e.init()
        }

    val emmentalElemental = elemental("emmental elemental", level = 8, create = ::EmmentalElemental) {
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

    val fogElemental = elemental("fog elemental", level = 18, create = ::Monster) {
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

    val brunostElemental = elemental("brunost elemental", level = 38, create = ::EmmentalElemental) {
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

    fun feline(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'f'
            corpsePoisonousness = exp("1")
            init()
        }

    val crazyCat = feline("crazy cat", level = 1) {
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

    val lion = feline("lion", level = 12) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,8)"))
        hitPoints = randint(21, 45)
        color = Color.YELLOWISH
        level = 12
        luck = 1
        killExperience = 11
        armorClass = 14
        tickRate = 100
        weight = 85000
    }


    // (F) Dragon Flies
    // (g) Golems

    fun golem(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'g'
            canUseDoors = true
            corpsePoisonousness = exp("3")
            init()
        }

    val strawGolem = golem("straw golem", level = 5) {
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

    val clayGolem = golem("clay golem", level = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,8)"))
        hitPoints = randint(30, 80)
        color = Color.BROWN
        level = 10
        luck = -1
        canUseDoors = false
        killExperience = 10
        armorClass = 12
        tickRate = 180
        weight = 120000
    }

    val ironGolem = golem("iron golem", level = 15) {
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

    val steelGolem = golem("steel golem", level = 20) {
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

    fun baseGhost(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'G'
            corporeal = false
            omniscient = true
            init()
        }

    val spook = baseGhost("spook", level = 7) {
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

    val ghost = baseGhost("ghost", level = 13) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(2,9)"))
        hitPoints = randint(23, 33)
        color = Color.WHITEISH
        level = 13
        luck = 1
        killExperience = 19
        armorClass = 13
        tickRate = 110
        weight = 0
    }

    val banshee = baseGhost("banshee", level = 19) {
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

    val bunshee = baseGhost("bunshee", level = 23, probability = 30) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(6,13)"))
        hitPoints = randint(53, 73)
        color = Color.BROWNISH
        level = 23
        luck = 3
        killExperience = 25
        armorClass = 9
        tickRate = 90
        weight = 0
    }


    // (h) Hobbits, Dwarves, Elves
    // (H) Hybrid Monsters

    fun hybrid(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'H'
            color = Color.RED
            corpsePoisonousness = exp("2")
            init()
        }

    // (i) Icky Things
    fun ickyThing(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'i'
            corpsePoisonousness = exp("1")
            init()
        }

    val whiteIckyThing = ickyThing("white icky thing", level = 1) {
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

    val redIckyThing = ickyThing("red icky thing", level = 8) {
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

    fun insect(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'I'
            corpsePoisonousness = exp("1")
            init()
        }

    val giantWasp = insect("giant wasp", level = 3, swarmSize = exp("randint(1, 7)")) {
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

    val inarianMosquito = insect("inarian mosquito", level = 15, swarmSize = exp("randint(4, 14)")) {
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

    fun jelly(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'j'
            immobile = true
            corpsePoisonousness = exp("0")
            init()
        }

    val whiteJelly = jelly("white jelly", level = 3) {
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

    val greenJelly = jelly("green jelly", level = 8) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(3,6)"))
        hitPoints = randint(26, 35)
        color = Color.GREEN
        level = 3
        luck = 0
        killExperience = 7
        armorClass = 12
        tickRate = 100
        weight = 7500
    }


    // (J) Snakes

    fun baseSnake(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'J'
            corpsePoisonousness = exp("3")
            init()
        }

    val gardenSnake = baseSnake("garden snake", level = 1) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(1,4)"))
        hitPoints = randint(3, 5)
        color = Color.GREEN
        level = 1
        luck = -1
        killExperience = 2
        armorClass = 15
        tickRate = 130
        weight = 400
    }

    val rattlesnake = baseSnake("rattlesnake", level = 10) {
        naturalWeapon = NaturalWeapon("hit", exp("1"), exp("randint(3,8)"))
        hitPoints = randint(15, 25)
        color = Color.BROWNISH
        level = 10
        luck = -1
        killExperience = 9
        armorClass = 13
        tickRate = 100
        weight = 1200
    }

    val kingCobra = baseSnake("king cobra", level = 18) {
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
    fun baseKobold(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'k'
            canUseDoors = true
            corpsePoisonousness = exp("3")
            init()
        }

    val kobold = baseKobold("kobold", level = 3, swarmSize = exp("randint(1, 4)")) {
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

    val largeKobold = baseKobold("large kobold", level = 14) {
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

    fun mold(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'm'
            immobile = true
            corpsePoisonousness = exp("3")
            init()
        }

    val greenMold = mold("green mold", level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(4, 8)
        color = Color.GREEN
        level = 2
        luck = 3
        killExperience = 2
        armorClass = 15
        tickRate = 150
        weight = 4000
    }

    val brownMold = mold("brown mold", level = 3) {
        naturalWeapon = NaturalWeapon("hit", exp("-1"), exp("randint(2,6)"))
        hitPoints = randint(5, 9)
        color = Color.BROWN
        level = 3
        luck = 0
        killExperience = 3
        armorClass = 15
        tickRate = 140
        weight = 4500
    }

    val blackMold = mold("black mold", level = 20) {
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

    fun baseOrc(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'o'
            color = Color.DARK_GREEN
            canUseDoors = true
            corpsePoisonousness = exp("3")
            init()
        }

    val orc = baseOrc("orc", level = 8, swarmSize = exp("randint(1, 10)")) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(1,7)"))
        hitPoints = randint(10, 20)
        level = 8
        luck = 0
        killExperience = 10
        armorClass = 12
        tickRate = 100
        weight = 65000
    }

    val largeOrc = baseOrc("large orc", level = 14, swarmSize = exp("randint(1, 4)")) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(3,10)"))
        hitPoints = randint(30, 50)
        level = 14
        luck = 0
        color = Color.BROWN
        killExperience = 19
        armorClass = 8
        tickRate = 100
        weight = 85000
    }

    // (O) Ogres

    fun baseOgre(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'O'
            color = Color.BROWN
            canUseDoors = true
            corpsePoisonousness = exp("1")
            init()
        }

    val ogre = baseOgre("ogre", level = 25, swarmSize = exp("randint(1, 4)")) {
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

    fun <T: Creature> person(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, create: (String) -> T, init: T.() -> Unit) =
        creature(name, level = level, swarmSize = swarmSize, probability = probability) {
            val p = create(name)
            p.letter = 'p'
            p.canUseDoors = true
            p.corpsePoisonousness = exp("1")
            p.init()
            p
        }

    val knightOfNi = person("knight of ni", swarmSize = exp("randint(1, 5)"), create = { KnightOfNi(it) }, level = 8) {
        hitPoints = randint(20, 30)
        level = 8
        color = Color.BLACKISH
        luck = 2
        wieldedWeapon = Weapons.scimitar.create()
        killExperience = 25
        armorClass = 10
        tickRate = 100
        weight = 75000
    }

    val poorKnightOfQueenMargareta = person("poor knight of Queen Margareta", create = { Monster(it) }, level = 15) {
        hitPoints = randint(70, 120)
        level = 15
        color = Color.RED
        luck = 0
        wieldedWeapon = Weapons.dagger.create()
        killExperience = 18
        armorClass = 14
        tickRate = 90
        weight = 85000

        inventory.add(Items.oldRags.create())
    }

    // (P) Giants

    fun giant(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'P'
            color = Color.BROWN
            canUseDoors = true
            corpsePoisonousness = exp("1")
            init()
        }

    val hillGiant = giant("hill giant", level = 31) {
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

    fun rodent(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'r'
            color = Color.BLACK
            corpsePoisonousness = exp("1")
            init()
        }

    val caveRat = rodent("cave rat", level = 1, swarmSize = exp("randint(1, 8)")) {
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

    fun baseLizard(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'R'
            color = Color.GREEN
            corpsePoisonousness = exp("0")
            init()
        }

    val newt = baseLizard("newt", level = 1) {
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

    val lizard = baseLizard("lizard", level = 2) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(2, 4)
        color = Color.GREEN
        level = 2
        luck = 2
        killExperience = 2
        armorClass = 16
        tickRate = 100
        weight = 670
    }

    val crocodile = baseLizard("crocodile", level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("7"), exp("randint(4,7)"))
        hitPoints = randint(50, 80)
        color = Color.DARK_GREEN
        level = 20
        luck = 0
        killExperience = 15
        armorClass = 16
        tickRate = 100
        weight = 200000
    }

    val crazyFrog = baseLizard("crazy frog", level = 15) {
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

    fun baseSpider(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'S'
            color = Color.BLACK
            corpsePoisonousness = exp("2")
            init()
        }

    val caveSpider = baseSpider("cave spider", level = 1, probability = 60) {
        naturalWeapon = NaturalWeapon("hit", exp("0"), exp("randint(1,4)"))
        hitPoints = randint(3, 6)
        level = 1
        luck = 0
        killExperience = 3
        armorClass = 16
        tickRate = 110
        weight = 1300
    }

    val veryPoisonousSpider = baseSpider("very poisonous spider", level = 1, probability = 10) {
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

    fun baseTroll(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'T'
            color = Color.DARK_GREEN
            canUseDoors = true
            corpsePoisonousness = exp("2")
            init()
        }

    val troll = baseTroll("troll", level = 15) {
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

    fun minorDemon(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'u'
            color = Color.RED
            corpsePoisonousness = exp("3")
            init()
        }

    val imp = minorDemon("imp", level = 15) {
        naturalWeapon = NaturalWeapon("hit", exp("5"), exp("randint(1,7)"))
        hitPoints = randint(60, 90)
        level = 15
        luck = 2
        color = Color.GREEN
        killExperience = 21
        armorClass = 12
        tickRate = 80
        weight = 59000
    }


    // (U) Major Demons

    fun majorDemon(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'U'
            color = Color.RED
            canUseDoors = true
            corpsePoisonousness = exp("5")
            init()
        }

    val hornedDemon = majorDemon("horned demon", level = 35) {
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

    fun baseVampire(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'z'
            color = Color.BLACK
            canUseDoors = true
            corpsePoisonousness = exp("5")
            init()
        }

    val vampire = baseVampire("vampire", level = 25) {
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

    fun baseWraith(name: String, level: Int, init: Wraith.() -> Unit) = creature(name, level = level) {
        Wraith(name).init { init() }
    }

    val bogWraith = baseWraith("bog wraith", level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(2,6)"))
        hitPoints = randint(40, 60)
        level = 20
        luck = 0
        killExperience = 23
        armorClass = 7
        tickRate = 100
        weight = 15000
    }

    val forestWraith = baseWraith("forest wraith", level = 25) {
        naturalWeapon = NaturalWeapon("hit", exp("4"), exp("randint(2,8)"))
        hitPoints = randint(60, 90)
        level = 25
        luck = 2
        color = Color.GREEN
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
    fun baseZombie(name: String, level: Int, swarmSize: Expression? = null, probability: Int? = null, init: Monster.() -> Unit) =
        monster(name, level = level, swarmSize = swarmSize, probability = probability) {
            letter = 'z'
            color = Color.BLACK
            corpsePoisonousness = exp("3")
            init()
        }

    val koboldZombie = baseZombie("kobold zombie", level = 8) {
        naturalWeapon = NaturalWeapon("hit", exp("2"), exp("randint(1,6)"))
        hitPoints = randint(20, 40)
        level = 8
        luck = 0
        killExperience = 11
        armorClass = 10
        tickRate = 100
        weight = 55000
    }

    val orcZombie = baseZombie("orc zombie", level = 14) {
        naturalWeapon = NaturalWeapon("hit", exp("3"), exp("randint(2,8)"))
        hitPoints = randint(35, 55)
        level = 14
        luck = 0
        color = Color.DARK_GREEN
        killExperience = 16
        armorClass = 10
        tickRate = 100
        weight = 85000
    }

    val trollZombie = baseZombie("troll zombie", level = 20) {
        naturalWeapon = NaturalWeapon("hit", exp("6"), exp("randint(4,10)"))
        hitPoints = randint(100, 170)
        level = 20
        luck = 1
        color = Color.GREEN
        canUseDoors = true
        killExperience = 31
        armorClass = 7
        tickRate = 100
        weight = 155000
    }

    // (Z) Zephyr Hounds


    // Special creatures

    // Special creatures
    val blackKnight = creature("The Black Knight", probability = 0, level = 30) { BlackKnight() }

    val bugsBunny = creature("Bugs Bunny", probability = 0, level = 20) { BugsBunny() }

    val oracle = creature("The Oracle", level = 1000, probability = 0) { Oracle() }

    val surstromming = hybrid("the Surstromming", level = 50, probability = 0) {
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

    val melog = golem("the Melog", level = 30, probability = 0) {
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
