@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package dev.komu.kraken.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.Oracle
import dev.komu.kraken.model.creature.monsters.*

object Creatures : Definitions() {

    inline fun monster(name: String, level: Int, init: CreatureDefinition<Monster>.() -> Unit) =
        creature(name, { Monster(name) }, level, init)

    // Note about probability: default is 100. Defining this attribute
    // with a value of 200 would thus mean that the creature is twice is likely
    // to appear in the level as one with default.

    // (a) Ants

    private fun CreatureDefinition<Monster>.ant() {
        letter = 'a'
        corpsePoisonousness = constant(2)
    }

    val whiteAnt = monster("white ant", level = 4) {
        ant()
        hitPoints = 3..7
        color = Color.WHITE
        naturalWeapon = hit(0, 1..4)
        killExperience = 5
        armorClass = 15
        speed = 4
        weight = 800
    }

    val greyAnt = monster("grey ant", level = 6) {
        ant()
        hitPoints = 4..8
        color = Color.GRAY
        naturalWeapon = hit(0, 2..4)
        killExperience = 8
        armorClass = 13
        speed = 4
        weight = 850
    }

    val redAnt = monster("red ant", level = 7) {
        ant()
        hitPoints = 6..12
        color = Color.RED
        naturalWeapon = hit(0, 2..5)
        killExperience = 9
        armorClass = 13
        speed = 5
        weight = 1100
    }

    val blackAnt = monster("black ant", level = 9) {
        ant()
        hitPoints = 10..16
        color = Color.BLACK
        swarmSize = 3..6
        naturalWeapon = hit(1, 2..6)
        killExperience = 14
        armorClass = 12
        speed = 6
        weight = 1000
    }

    val yellowAnt = monster("yellow ant", level = 12) {
        ant()
        hitPoints = 16..24
        color = Color.YELLOW
        swarmSize = 2..7
        naturalWeapon = hit(0, 4..6)
        killExperience = 22
        armorClass = 12
        speed = 6
        weight = 1300
    }

    val greenAnt = monster("green ant", level = 14) {
        ant()
        hitPoints = 20..35
        color = Color.GREEN
        swarmSize = 4..6
        naturalWeapon = hit(2, 2..7)
        killExperience = 28
        luck = 2
        armorClass = 11
        speed = 7
        weight = 1300
    }

    val blueAnt = monster("blue ant", level = 16) {
        ant()
        hitPoints = 34..48
        color = Color.BLUE
        swarmSize = 5..9
        naturalWeapon = hit(2, 5..12)
        killExperience = 35
        armorClass = 10
        speed = 7
        weight = 1200
    }

    // (A) Angelic Beings

    private fun CreatureDefinition<Monster>.baseAngel() {
        letter = 'A'
        corpsePoisonousness = constant(0)
        color = Color.WHITE
        canUseDoors = true
    }

    val angel = monster("angel", level = 30) {
        baseAngel()
        hitPoints = 250..350
        naturalWeapon = hit(5, 6..16)
        killExperience = 310
        luck = 1
        armorClass = 5
        speed = 7
        weight = 45000
    }

    val archangel = monster("archangel", level = 60) {
        baseAngel()
        hitPoints = 650..850
        naturalWeapon = hit(8, 10..25)
        killExperience = 860
        luck = 2
        armorClass = -5
        speed = 9
        weight = 52000
    }


    // (b) Bats
    private fun CreatureDefinition<Monster>.bat() {
        letter = 'b'
        corpsePoisonousness = constant(1)
    }

    val giantBat = monster("giant bat", level = 1) {
        bat()
        naturalWeapon = hit(0, 1..3)
        hitPoints = 2..5
        color = Color.BROWN
        luck = 0
        killExperience = 2
        armorClass = 15
        speed = 9
        weight = 2300
    }

    val giantWhiteBat = monster("giant white bat", level = 3) {
        bat()
        naturalWeapon = hit(1, 2..4)
        hitPoints = 3..7
        color = Color.WHITE
        luck = 0
        killExperience = 4
        armorClass = 14
        speed = 9
        weight = 2500
    }

    val giantBlackBat = monster("giant black bat", level = 5) {
        bat()
        swarmSize = 2..4
        naturalWeapon = hit(0, 2..6)
        hitPoints = 5..9
        color = Color.BLACK
        luck = -1
        killExperience = 6
        armorClass = 14
        speed = 9
        weight = 2600
    }

    val giantGreenBat = monster("giant green bat", level = 8) {
        bat()
        naturalWeapon = hit(1, 3..7)
        swarmSize = 3..5
        hitPoints = 8..13
        color = Color.GREEN
        luck = 2
        killExperience = 9
        armorClass = 14
        speed = 9
        weight = 3000
    }

    // "No, a Siamese bat. They're more dangerous."
    val siameseBat = monster("siamese bat", level = 8) {
        bat()
        swarmSize = 3..5
        naturalWeapon = hit(1, 3..8)
        hitPoints = 9..15
        color = Color.GREEN
        luck = 2
        killExperience = 13
        armorClass = 13
        speed = 9
        weight = 3000
    }

    // (B) Birds

    private fun CreatureDefinition<Monster>.bird() {
        letter = 'B'
        corpsePoisonousness = constant(0)
    }

    val headlessChicken = monster("headless chicken", level = 1) {
        bird()
        probability = 20
        naturalWeapon = hit(0, 1..2)
        hitPoints = 1..1
        color = Color.WHITE
        luck = 0
        killExperience = 1
        armorClass = 20
        speed = 8
        weight = 1700
    }

    val europeanSwallow = monster("european swallow", level = 1) {
        bird()
        naturalWeapon = hit(0, 1..2)
        hitPoints = 1..3
        color = Color.BLACK
        luck = 0
        killExperience = 1
        armorClass = 18
        speed = 10
        weight = 100
    }

    val africanSwallow = monster("african swallow", level = 2) {
        bird()
        probability = 60
        naturalWeapon = hit(0, 1..3)
        hitPoints = 2..4
        color = Color.BLACK
        luck = 0
        killExperience = 2
        armorClass = 18
        speed = 10
        weight = 200
    }

    val crow = monster("crow", level = 4) {
        bird()
        swarmSize = 1..6
        naturalWeapon = hit(0, 2..4)
        hitPoints = 3..7
        color = Color.BLACK
        luck = -1
        killExperience = 4
        armorClass = 16
        speed = 7
        weight = 500
    }

    // (c) Centipedes

    private fun CreatureDefinition<Monster>.centipede() {
        letter = 'c'
        corpsePoisonousness = constant(1)
    }

    val giantWhiteCentipede = monster("giant white centipede", level = 1) {
        centipede()
        naturalWeapon = hit(0, 1..4)
        hitPoints = 3..5
        color = Color.WHITE
        luck = 0
        killExperience = 2
        armorClass = 18
        speed = 4
        weight = 800
    }

    val giantYellowCentipede = monster("giant yellow centipede", level = 2) {
        centipede()
        naturalWeapon = hit(0, 2..4)
        hitPoints = 4..6
        color = Color.YELLOW
        luck = 0
        killExperience = 3
        armorClass = 17
        speed = 4
        weight = 800
    }

    val giantGreenCentipede = monster("giant green centipede", level = 4) {
        centipede()
        naturalWeapon = hit(1, 1..6)
        hitPoints = 6..10
        color = Color.GREEN
        luck = 3
        killExperience = 5
        armorClass = 16
        speed = 5
        weight = 900
    }

    val giantRedCentipede = monster("giant red centipede", level = 6) {
        centipede()
        naturalWeapon = hit(0, 2..7)
        hitPoints = 9..14
        color = Color.RED
        luck = 0
        killExperience = 7
        armorClass = 16
        speed = 5
        weight = 900
    }

    // (C) Canines


    private fun CreatureDefinition<Monster>.canine() {
        letter = 'C'
        corpsePoisonousness = constant(1)
    }

    val jackal = monster("jackal", level = 1) {
        canine()
        swarmSize = 3..6
        naturalWeapon = hit(0, 1..3)
        hitPoints = 1..5
        color = Color.BROWNISH
        luck = 0
        killExperience = 1
        armorClass = 18
        speed = 5
        weight = 6000
    }

    val wildDog = monster("wild dog", level = 2) {
        canine()
        naturalWeapon = hit(0, 1..4)
        hitPoints = 3..5
        color = Color.BROWN
        luck = 2
        killExperience = 3
        armorClass = 17
        speed = 5
        weight = 14000
    }

    val wolf = monster("wolf", level = 11) {
        canine()
        naturalWeapon = hit(2, 2..10)
        hitPoints = 23..35
        color = Color.DARK_GRAY
        luck = 0
        killExperience = 10
        armorClass = 15
        speed = 5
        weight = 35000
    }


    // (d) Young/Mature Dragons

    private fun CreatureDefinition<Monster>.youngDragon() {
        letter = 'd'
        canUseDoors = true
        corpsePoisonousness = constant(3)
    }

    val youngRedDragon = monster("young red dragon", level = 10) {
        youngDragon()
        naturalWeapon = hit(3, 3..13)
        hitPoints = 60..80
        color = Color.RED
        luck = 0
        killExperience = 120
        armorClass = 10
        speed = 5
        weight = 120000
    }

    val youngBlackDragon = monster("young black dragon", level = 13) {
        youngDragon()
        naturalWeapon = hit(3, 6..15)
        hitPoints = 70..90
        color = Color.BLACK
        luck = 0
        killExperience = 140
        armorClass = 8
        speed = 5
        weight = 145000
    }

    val matureRedDragon = monster("mature red dragon", level = 22) {
        youngDragon()
        naturalWeapon = hit(6, 6..20)
        hitPoints = 220..300
        color = Color.RED
        luck = 1
        killExperience = 300
        armorClass = 5
        speed = 5
        weight = 420000
    }

    val matureBlackDragon = monster("mature black dragon", level = 27) {
        youngDragon()
        naturalWeapon = hit(6, 5..22)
        hitPoints = 270..390
        color = Color.BLACK
        luck = 0
        killExperience = 340
        armorClass = 3
        speed = 5
        weight = 445000
    }

    // (D) Ancient Dragons

    private fun CreatureDefinition<Monster>.ancientDragon() {
        letter = 'D'
        canUseDoors = true
        corpsePoisonousness = constant(4)
    }

    val ancientRedDragon = monster("ancient red dragon", level = 60) {
        ancientDragon()
        naturalWeapon = hit(12, 8..30)
        hitPoints = 600..800
        color = Color.RED
        luck = 2
        killExperience = 5000
        armorClass = -5
        speed = 8
        weight = 800000
    }

    val ancientBlackDragon = monster("ancient black dragon", level = 65) {
        ancientDragon()
        naturalWeapon = hit(12, 10..40)
        hitPoints = 700..850
        color = Color.BLACK
        luck = 2
        killExperience = 6000
        armorClass = -6
        speed = 9
        weight = 845000
    }

    // (e) Floating Eyes

    private fun CreatureDefinition<Monster>.eye() {
        letter = 'e'
        immobile = true
        corpsePoisonousness = constant(1)
    }

    val floatingEye = monster("floating eye", level = 3) {
        eye()
        naturalWeapon = hit(0, 1..5)
        hitPoints = 8..14
        color = Color.YELLOW
        luck = 3
        killExperience = 5
        armorClass = 15
        speed = 5
        weight = 9000
    }

    // (E) Elementals

    private fun CreatureDefinition<Monster>.elemental() {
        letter = 'E'
        canUseDoors = true
        corpsePoisonousness = constant(1)
    }

    val emmentalElemental =
        creature("emmental elemental", level = 8, create = { EmmentalElemental("emmental elemental") }) {
            elemental()
            naturalWeapon = hit(4, 1..8)
            hitPoints = 70..140
            color = Color.YELLOW
            luck = -2
            killExperience = 16
            armorClass = 14
            speed = 3
            weight = 200000
        }

    val fogElemental =
        monster("fog elemental", level = 18) {
            elemental()
            naturalWeapon = hit(7, 3..5)
            hitPoints = 120..180
            color = Color.WHITE
            luck = 0
            killExperience = 125
            armorClass = 7
            speed = 5
            weight = 200
        }

    val brunostElemental =
        creature("brunost elemental", level = 38, create = { EmmentalElemental("brunost elemental") }) {
            elemental()
            naturalWeapon = hit(10, 1..11)
            hitPoints = 270..340
            color = Color.YELLOW
            luck = -1
            killExperience = 180
            armorClass = 14
            speed = 4
            weight = 280000
        }

    // (f) Felines

    private fun CreatureDefinition<Monster>.feline() {
        letter = 'f'
        corpsePoisonousness = constant(1)
    }

    val crazyCat = monster("crazy cat", level = 1) {
        feline()
        naturalWeapon = hit(0, 1..3)
        hitPoints = 1..4
        color = Color.BLACK
        luck = 0
        killExperience = 1
        armorClass = 18
        speed = 5
        weight = 2200
    }

    val lion = monster("lion", level = 12) {
        feline()
        naturalWeapon = hit(0, 1..8)
        hitPoints = 21..45
        color = Color.YELLOWISH
        luck = 1
        killExperience = 11
        armorClass = 14
        speed = 5
        weight = 85000
    }


    // (F) Dragon Flies
    // (g) Golems

    private fun CreatureDefinition<Monster>.golem() {
        letter = 'g'
        canUseDoors = true
        corpsePoisonousness = constant(3)
    }

    val strawGolem = monster("straw golem", level = 5) {
        golem()
        naturalWeapon = hit(0, 2..6)
        hitPoints = 10..30
        color = Color.YELLOW
        luck = -2
        canUseDoors = false
        killExperience = 5
        armorClass = 14
        speed = 2
        weight = 15000
    }

    val clayGolem = monster("clay golem", level = 10) {
        golem()
        naturalWeapon = hit(0, 1..8)
        hitPoints = 30..80
        color = Color.BROWN
        luck = -1
        canUseDoors = false
        killExperience = 10
        armorClass = 12
        speed = 3
        weight = 120000
    }

    val ironGolem = monster("iron golem", level = 15) {
        golem()
        naturalWeapon = hit(5, 2..9)
        hitPoints = 100..150
        color = Color.GRAY
        luck = -2
        killExperience = 15
        armorClass = 10
        speed = 3
        weight = 350000
    }

    val steelGolem = monster("steel golem", level = 20) {
        golem()
        naturalWeapon = hit(9, 2..13)
        hitPoints = 150..220
        color = Color.CYAN
        luck = 0
        killExperience = 20
        armorClass = 8
        speed = 4
        weight = 420000
    }


    // (G) Ghosts

    private fun CreatureDefinition<Monster>.baseGhost() {
        letter = 'G'
        corporeal = false
        omniscient = true
    }

    val spook = monster("spook", level = 7) {
        baseGhost()
        naturalWeapon = hit(0, 1..6)
        hitPoints = 8..13
        color = Color.WHITE
        luck = 1
        killExperience = 9
        armorClass = 13
        speed = 4
        weight = 0
    }

    val ghost = monster("ghost", level = 13) {
        baseGhost()
        naturalWeapon = hit(0, 2..9)
        hitPoints = 23..33
        color = Color.WHITEISH
        killExperience = 19
        armorClass = 13
        speed = 5
        weight = 0
    }

    val banshee = monster("banshee", level = 19) {
        baseGhost()
        naturalWeapon = hit(4, 4..6)
        hitPoints = 33..53
        color = Color.CYAN
        luck = 1
        killExperience = 22
        armorClass = 13
        speed = 5
        weight = 0
    }

    val bunshee = monster("bunshee", level = 23) {
        baseGhost()
        probability = 30
        naturalWeapon = hit(5, 6..13)
        hitPoints = 53..73
        color = Color.BROWNISH
        luck = 3
        killExperience = 25
        armorClass = 9
        speed = 6
        weight = 0
    }


    // (h) Hobbits, Dwarves, Elves
    // (H) Hybrid Monsters

    private fun CreatureDefinition<Monster>.hybrid() {
        letter = 'H'
        color = Color.RED
        corpsePoisonousness = constant(2)
    }

    // (i) Icky Things
    private fun CreatureDefinition<Monster>.ickyThing() {
        letter = 'i'
        corpsePoisonousness = constant(1)
    }

    val whiteIckyThing = monster("white icky thing", level = 1) {
        ickyThing()
        naturalWeapon = hit(0, 1..3)
        hitPoints = 2..5
        color = Color.WHITE
        luck = -2
        killExperience = 2
        armorClass = 14
        speed = 5
        weight = 21000
    }
    
    val redIckyThing = monster("red icky thing", level = 8) {
        ickyThing()
        naturalWeapon = hit(2, 2..7)
        hitPoints = 21..35
        color = Color.RED
        luck = 0
        killExperience = 11
        armorClass = 11
        speed = 5
        weight = 33500
    }


    // (I) Insects

    private fun CreatureDefinition<Monster>.insect() {
        letter = 'I'
        corpsePoisonousness = constant(1)
    }

    val giantWasp = monster("giant wasp", level = 3) {
        insect()
        swarmSize = 1..7
        naturalWeapon = hit(1, 1..4)
        hitPoints = 4..6
        color = Color.YELLOW
        luck = 0
        killExperience = 4
        armorClass = 15
        speed = 7
        weight = 750
    }

    val inarianMosquito = monster("inarian mosquito", level = 15) {
        insect()
        swarmSize = 4..14
        naturalWeapon = hit(5, 3..5)
        hitPoints = 20..27
        color = Color.BLACK
        luck = 2
        killExperience = 17
        armorClass = 10
        speed = 7
        weight = 1150
    }


    // (j) Jellies

    private fun CreatureDefinition<Monster>.jelly() {
        letter = 'j'
        immobile = true
        corpsePoisonousness = constant(0)
    }

    val whiteJelly = monster("white jelly", level = 3) {
        jelly()
        naturalWeapon = hit(-1, 2..5)
        hitPoints = 6..15
        color = Color.WHITE
        luck = -1
        killExperience = 5
        armorClass = 13
        speed = 4
        weight = 7000
    }

    val greenJelly = monster("green jelly", level = 8) {
        jelly()
        naturalWeapon = hit(-1, 3..6)
        hitPoints = 26..35
        color = Color.GREEN
        luck = 0
        killExperience = 7
        armorClass = 12
        speed = 5
        weight = 7500
    }


    // (J) Snakes

    private fun CreatureDefinition<Monster>.baseSnake() {
        letter = 'J'
        corpsePoisonousness = constant(3)
    }

    val gardenSnake = monster("garden snake", level = 1) {
        baseSnake()
        naturalWeapon = hit(-1, 1..4)
        hitPoints = 3..5
        color = Color.GREEN
        luck = -1
        killExperience = 2
        armorClass = 15
        speed = 4
        weight = 400
    }

    val rattlesnake = monster("rattlesnake", level = 10) {
        baseSnake()
        naturalWeapon = hit(1, 3..8)
        hitPoints = 15..25
        color = Color.BROWNISH
        luck = -1
        killExperience = 9
        armorClass = 13
        speed = 5
        weight = 1200
    }

    val kingCobra = monster("king cobra", level = 18) {
        baseSnake()
        naturalWeapon = hit(4, 3..13)
        hitPoints = 45..65
        color = Color.BLACK
        luck = 1
        killExperience = 20
        armorClass = 6
        speed = 6
        weight = 3700
    }

    // (k) Kobolds
    private fun CreatureDefinition<Monster>.baseKobold() {
        letter = 'k'
        canUseDoors = true
        corpsePoisonousness = constant(3)
    }

    val kobold = monster("kobold", level = 3) {
        baseKobold()
        swarmSize = 1..4
        naturalWeapon = hit(0, 1..4)
        hitPoints = 3..7
        color = Color.RED
        luck = -2
        killExperience = 4
        armorClass = 15
        speed = 5
        weight = 35000
    }

    val largeKobold = monster("large kobold", level = 14) {
        baseKobold()
        naturalWeapon = hit(4, 2..7)
        hitPoints = 40..65
        color = Color.BLUE
        luck = -1
        killExperience = 20
        armorClass = 10
        speed = 5
        weight = 60000
    }

    // (K) Killer Beetles
    // (l) Lice
    // (L) Liches
    // (m) Molds

    private fun CreatureDefinition<Monster>.mold() {
        letter = 'm'
        immobile = true
        corpsePoisonousness = constant(3)
    }

    val greenMold = monster("green mold", level = 2) {
        mold()
        naturalWeapon = hit(0, 1..4)
        hitPoints = 4..8
        color = Color.GREEN
        luck = 3
        killExperience = 2
        armorClass = 15
        speed = 3
        weight = 4000
    }

    val brownMold = monster("brown mold", level = 3) {
        mold()
        naturalWeapon = hit(-1, 2..6)
        hitPoints = 5..9
        color = Color.BROWN
        luck = 0
        killExperience = 3
        armorClass = 15
        speed = 4
        weight = 4500
    }

    val blackMold = monster("black mold", level = 20) {
        mold()
        naturalWeapon = hit(6, 3..14)
        hitPoints = 70..100
        color = Color.BLACK
        luck = 2
        killExperience = 25
        armorClass = 6
        speed = 5
        weight = 9500
    }

    // (M) Multi-headed Reptiles (Hydras)
    // (n) Nagas
    // (N) (NOT IN USE)
    // (o) Orcs

    private fun CreatureDefinition<Monster>.baseOrc() {
        letter = 'o'
        color = Color.DARK_GREEN
        canUseDoors = true
        corpsePoisonousness = constant(3)
    }

    val orc = monster("orc", level = 8) {
        baseOrc()
        swarmSize = 1..10
        naturalWeapon = hit(2, 1..7)
        hitPoints = 10..20
        luck = 0
        killExperience = 10
        armorClass = 12
        speed = 5
        weight = 65000
    }

    val largeOrc = monster("large orc", level = 14) {
        baseOrc()
        swarmSize = 1..4
        naturalWeapon = hit(6, 3..10)
        hitPoints = 30..50
        luck = 0
        color = Color.BROWN
        killExperience = 19
        armorClass = 8
        speed = 5
        weight = 85000
    }

    // (O) Ogres

    private fun CreatureDefinition<Monster>.baseOgre() {
        letter = 'O'
        color = Color.BROWN
        canUseDoors = true
        corpsePoisonousness = constant(1)
    }

    val ogre = monster("ogre", level = 25) {
        baseOgre()
        swarmSize = 1..4
        naturalWeapon = hit(7, 1..10)
        hitPoints = 150..250
        luck = 0
        killExperience = 31
        armorClass = 10
        speed = 5
        weight = 135000
    }

    // (p) Persons (humans)

    private fun CreatureDefinition<Monster>.person() {
        letter = 'p'
        canUseDoors = true
        corpsePoisonousness = constant(1)
    }

    val knightOfNi = creature("knight of ni", level = 8, create = ::KnightOfNi) {
        person()
        swarmSize = 1..5
        hitPoints = 20..30
        color = Color.BLACKISH
        luck = 2
        killExperience = 25
        armorClass = 10
        speed = 5
        weight = 75000
        init {
            wieldedWeapon = Weapons.scimitar.create()
        }
    }

    val poorKnightOfQueenMargareta = monster("poor knight of Queen Margareta", level = 15) {
        person()
        hitPoints = 70..120
        color = Color.RED
        luck = 0
        killExperience = 18
        armorClass = 14
        speed = 6
        weight = 85000

        init {
            wieldedWeapon = Weapons.dagger.create()
            inventory.add(Items.oldRags.create())
        }
    }

    // (P) Giants

    private fun CreatureDefinition<Monster>.giant() {
        letter = 'P'
        color = Color.BROWN
        canUseDoors = true
        corpsePoisonousness = constant(1)
    }

    val hillGiant = monster("hill giant", level = 31) {
        giant()
        naturalWeapon = hit(10, 2..9)
        hitPoints = 200..300
        luck = 0
        killExperience = 40
        armorClass = 6
        speed = 5
        weight = 170000
    }

    // (q) Quadrupeds
    // (Q) Quylthulgs
    // (r) Rodents

    private fun CreatureDefinition<Monster>.rodent() {
        letter = 'r'
        color = Color.BLACK
    }

    val caveRat = monster("cave rat", level = 1) {
        rodent()
        swarmSize = 1..8
        naturalWeapon = hit(0, 1..4)
        hitPoints = 1..3
        luck = 0
        killExperience = 1
        armorClass = 16
        speed = 6
        weight = 500
    }

    // (R) Reptiles and Amphibians

    private fun CreatureDefinition<Monster>.baseLizard() {
        letter = 'R'
        color = Color.GREEN
        corpsePoisonousness = constant(0)
    }

    val newt = monster("newt", level = 1) {
        baseLizard()
        naturalWeapon = hit(-1, 1..3)
        hitPoints = 1..3
        color = Color.YELLOW
        luck = 0
        killExperience = 1
        armorClass = 16
        speed = 4
        weight = 300
    }

    val lizard = monster("lizard", level = 2) {
        baseLizard()
        naturalWeapon = hit(0, 1..4)
        hitPoints = 2..4
        color = Color.GREEN
        luck = 2
        killExperience = 2
        armorClass = 16
        speed = 5
        weight = 670
    }

    val crocodile = monster("crocodile", level = 20) {
        baseLizard()
        naturalWeapon = hit(7, 4..7)
        hitPoints = 50..80
        color = Color.DARK_GREEN
        luck = 0
        killExperience = 15
        armorClass = 16
        speed = 5
        weight = 200000
    }

    val crazyFrog = monster("crazy frog", level = 15) {
        baseLizard()
        naturalWeapon = hit(5, 2..7)
        hitPoints = 50..70
        color = Color.BLACK
        luck = 0
        killExperience = 12
        armorClass = 14
        speed = 5
        weight = 6400
    }

    // (s) Skeletons
    // (S) Spiders

    private fun CreatureDefinition<Monster>.baseSpider() {
        letter = 'S'
        color = Color.BLACK
        corpsePoisonousness = constant(2)
    }

    val caveSpider = monster("cave spider", level = 1) {
        baseSpider()
        probability = 60
        naturalWeapon = hit(0, 1..4)
        hitPoints = 3..6
        luck = 0
        killExperience = 3
        armorClass = 16
        speed = 5
        weight = 1300
    }

    val veryPoisonousSpider = monster("very poisonous spider", level = 1) {
        baseSpider()
        probability = 10
        naturalWeapon = hit(1, 2..5)
        hitPoints = 3..6
        luck = 2
        killExperience = 3
        armorClass = 16
        speed = 5
        weight = 800
        corpsePoisonousness = random(20..40)
    }

    // (t) Ticks
    // (T) Trolls

    private fun CreatureDefinition<Monster>.baseTroll() {
        letter = 'T'
        color = Color.DARK_GREEN
        canUseDoors = true
        corpsePoisonousness = constant(2)
    }

    val troll = monster("troll", level = 15) {
        baseTroll()
        naturalWeapon = hit(4, 3..12)
        hitPoints = 150..200
        luck = -2
        killExperience = 26
        armorClass = 14
        speed = 5
        weight = 110000
    }

    // (u) Minor Demons

    private fun CreatureDefinition<Monster>.minorDemon() {
        letter = 'u'
        color = Color.RED
        corpsePoisonousness = constant(3)
    }

    val imp = monster("imp", level = 15) {
        minorDemon()
        naturalWeapon = hit(5, 1..7)
        hitPoints = 60..90
        luck = 2
        color = Color.GREEN
        killExperience = 21
        armorClass = 12
        speed = 7
        weight = 59000
    }


    // (U) Major Demons

    private fun CreatureDefinition<Monster>.majorDemon() {
        letter = 'U'
        color = Color.RED
        canUseDoors = true
        corpsePoisonousness = constant(5)
    }

    val hornedDemon = monster("horned demon", level = 35) {
        majorDemon()
        naturalWeapon = hit(12, 4..17)
        hitPoints = 260..330
        luck = 2
        color = Color.RED
        killExperience = 280
        armorClass = 6
        speed = 7
        weight = 132000
    }


    // (v) Vortexes
    // (V) Vampires

    private fun CreatureDefinition<Monster>.baseVampire() {
        letter = 'z'
        color = Color.BLACK
        canUseDoors = true
        corpsePoisonousness = constant(5)
    }

    val vampire = monster("vampire", level = 25) {
        baseVampire()
        naturalWeapon = hit(4, 3..10)
        hitPoints = 50..90
        luck = 2
        killExperience = 24
        armorClass = 6
        speed = 5
        weight = 63000
    }

    // (w) Worms and Worm Masses
    // (W) Wraiths

    private fun CreatureDefinition<Monster>.baseWraith() {
        letter = 'W'
        color = Color.BLACK
        canUseDoors = true
    }

    val bogWraith = creature("bog wraith", level = 20, create = { Wraith("bog wraith") }) {
        baseWraith()
        naturalWeapon = hit(2, 2..6)
        hitPoints = 40..60
        luck = 0
        killExperience = 23
        armorClass = 7
        speed = 5
        weight = 15000
    }

    val forestWraith = creature("forest wraith", level = 25, create = { Wraith("bog wraith") }) {
        baseWraith()
        naturalWeapon = hit(4, 2..8)
        hitPoints = 60..90
        luck = 2
        color = Color.GREEN
        killExperience = 29
        armorClass = 6
        speed = 6
        weight = 15000
    }

    // (x) (NOT IN USE)
    // (X) Xorn, Xaren etc.
    // (y) Yeeks
    // (Y) Yetis

    // (z) Zombies
    private fun CreatureDefinition<Monster>.baseZombie() {
        letter = 'z'
        color = Color.BLACK
        corpsePoisonousness = constant(3)
    }

    val koboldZombie = monster("kobold zombie", level = 8) {
        baseZombie()
        naturalWeapon = hit(2, 1..6)
        hitPoints = 20..40
        luck = 0
        killExperience = 11
        armorClass = 10
        speed = 5
        weight = 55000
    }

    val orcZombie = monster("orc zombie", level = 14) {
        baseZombie()
        naturalWeapon = hit(3, 2..8)
        hitPoints = 35..55
        luck = 0
        color = Color.DARK_GREEN
        killExperience = 16
        armorClass = 10
        speed = 5
        weight = 85000
    }

    val trollZombie = monster("troll zombie", level = 20) {
        baseZombie()
        naturalWeapon = hit(6, 4..10)
        hitPoints = 100..170
        luck = 1
        color = Color.GREEN
        canUseDoors = true
        killExperience = 31
        armorClass = 7
        speed = 5
        weight = 155000
    }

    // (Z) Zephyr Hounds


    // Special creatures

    // Special creatures
    val blackKnight = creature("The Black Knight", ::BlackKnight, level = 30) {
        probability = 0
    }

    val bugsBunny = creature("Bugs Bunny", ::BugsBunny, level = 20) {
        probability = 0
    }

    val oracle = creature("The Oracle", ::Oracle, level = 1000) {
        probability = 0
    }

    val surstromming = monster("the Surstromming", level = 50) {
        hybrid()
        probability = 0
        naturalWeapon = hit(15, 6..20)
        hitPoints = 800..850
        color = Color.BLACK
        luck = 1
        canUseDoors = true
        killExperience = 5500
        armorClass = -6
        speed = 9
        weight = 123000
        corpsePoisonousness = constant(10)
    }

    val melog = monster("the Melog", level = 30) {
        golem()
        probability = 0
        naturalWeapon = hit(8, 3..15)
        hitPoints = 550..550
        color = Color.BLUE
        luck = 1
        canUseDoors = true
        killExperience = 2500
        armorClass = -6
        speed = 5
        weight = 400000
    }
}
