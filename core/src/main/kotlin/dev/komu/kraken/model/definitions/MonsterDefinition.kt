package dev.komu.kraken.model.definitions

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Drops
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.MonsterState
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.exp.Expression
import dev.komu.kraken.utils.randomInt

class MonsterDefinition(val name: String) : ObjectDefinition<Monster>() {

    override var level = 1
    var letter: Char? = null
    var color: Color? = null
    var hitPoints: ClosedRange<Int>? = null
    var canUseDoors: Boolean? = null
    var corpsePoisonousness: Expression? = null
    var immobile: Boolean? = null
    var naturalWeapon: NaturalWeapon? = null
    var killExperience: Int? = null
    var luck: Int? = null
    var armorClass: Int? = null
    var speed: Int? = null
    var weight: Int? = null
    var hitBonus: Int? = null
    var corporeal: Boolean? = null
    var omniscient: Boolean? = null
    var wieldedWeapon: WeaponDefinition? = null
    val inventory = mutableListOf<ItemDefinition<*>>()
    var state: (() -> MonsterState)? = null
    var drops: Drops? = null

    var swarmSize: ClosedRange<Int> = 1..1

    var instantiable = true

    fun createSwarm(): Collection<Monster> =
        List(randomInt(swarmSize)) { create() }

    override fun create(): Monster = Monster(name).also {
        if (color != null)
            it.color = color!!

        if (hitPoints != null) {
            val hp = randomInt(hitPoints!!)
            it.maximumHitPoints = hp
            it.hitPoints = hp
        }

        if (canUseDoors != null)
            it.canUseDoors = canUseDoors!!

        if (corpsePoisonousness != null)
            it.corpsePoisonousness = corpsePoisonousness!!

        if (letter != null)
            it.letter = letter!!

        if (naturalWeapon != null)
            it.naturalWeapon = naturalWeapon!!

        if (state != null)
            it.state = state!!()

        if (killExperience != null)
            it.killExperience = killExperience!!

        if (luck != null)
            it.luck = luck!!

        if (hitBonus != null)
            it.hitBonus = hitBonus!!

        if (armorClass != null)
            it.armorClass = armorClass!!

        if (speed != null)
            it.baseSpeed = speed!!

        if (weight != null)
            it.weight = weight!!

        if (immobile != null)
            it.immobile = immobile!!

        if (corporeal != null)
            it.corporeal = corporeal!!

        if (omniscient != null)
            it.omniscient = omniscient!!

        if (wieldedWeapon != null)
            it.wieldedWeapon = wieldedWeapon!!.create()

        if (drops != null)
            it.drops = drops!!

        for (item in inventory)
            it.inventory.add(item.create())
    }

    override fun toString() = "CreatureDefinition [name=$name]"
}
