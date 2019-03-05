package dev.komu.kraken.model.item.armor

import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.creature.Player
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

    override fun equip(player: Player): Boolean {
        val oldArmor = player.replaceArmor(this)
        player.inventory.remove(this)
        if (oldArmor != null) {
            player.message("You were wearing %s.", oldArmor.title)
            player.inventory.add(oldArmor)
        }

        player.message("You are now wearing %s.", title)
        return true
    }
}
