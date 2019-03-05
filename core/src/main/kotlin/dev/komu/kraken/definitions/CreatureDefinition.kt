package dev.komu.kraken.definitions

import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.utils.exp.Expression

class CreatureDefinition<out T : Creature>(val name: String, override val level: Int, val createCreature: () -> T) : ObjectDefinition<T>() {

    var swarmSize = Expression.constant(1)

    var instantiable = true

    fun createSwarm(): Collection<T> {
        val swarm = mutableListOf<T>()

        repeat(swarmSize.evaluate()) {
            swarm.add(create())
        }

        return swarm
    }

    override fun create(): T = createCreature()

    override fun toString() = "CreatureDefinition [name=$name]"
}
