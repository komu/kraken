package dev.komu.kraken.model.item.armor

import java.util.*

class Armoring : Iterable<Armor> {
    private val armors = EnumMap<BodyPart, Armor>(BodyPart::class.java)

    override fun iterator(): Iterator<Armor> =
        armors.values.iterator()

    fun removeAllArmors(): Collection<Armor> {
        val result = armors.values.toList()
        armors.clear()
        return result
    }

    val weight: Int
        get() = armors.values.sumBy { it.weight }

    val totalArmorBonus: Int
        get() = armors.values.sumBy { it.armorBonus }

    fun replaceArmor(armor: Armor): Armor? =
        armors.put(armor.bodyPart, armor)

    override fun toString() = armors.values.toString()
}
