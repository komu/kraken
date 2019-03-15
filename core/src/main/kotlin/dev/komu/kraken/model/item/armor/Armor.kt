package dev.komu.kraken.model.item.armor

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.item.Equipable

open class Armor(name: String): Equipable(name) {

    var armorBonus = 1
    var bodyPart = BodyPart.TORSO

    init {
        color = Color.BROWN
        letter = ']'
    }

    override val description: String
        get() = "ac: $armorBonus; ${super.description}"

    override fun equip(creature: Creature): Boolean {
        val oldArmor = creature.armoring.replaceArmor(this)
        creature.inventory.remove(this)
        if (oldArmor != null) {
            creature.message("You were wearing %s.", oldArmor.title)
            creature.inventory.add(oldArmor)
        }

        creature.message("You are now wearing %s.", title)
        return true
    }
}
