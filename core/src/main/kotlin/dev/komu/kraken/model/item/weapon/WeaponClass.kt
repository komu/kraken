package dev.komu.kraken.model.item.weapon

import dev.komu.kraken.model.skill.Proficiency
import dev.komu.kraken.model.skill.Proficiency.BASIC
import dev.komu.kraken.model.skill.Proficiency.UNSKILLED

enum class WeaponClass(val defaultProficiency: Proficiency) {
    NOT_WEAPON(UNSKILLED),
    NATURAL(BASIC),
    KNIFE(UNSKILLED),
    SWORD(UNSKILLED),
    AXE(UNSKILLED),
    BLUNT(UNSKILLED),
    JAVELIN(UNSKILLED),
    SPEAR(UNSKILLED),
    LAUNCHER(UNSKILLED),
    PROJECTILE(UNSKILLED);

    override fun toString() =
        when(this) {
            BLUNT       -> "blunt weapon"
            LAUNCHER    -> "missile launcher"
            else        -> name.toLowerCase()
        }

    val skillLevelIncreaseMessage: String
        get() =
            if (this == NATURAL)
                "You feel more proficient in martial arts."
            else
                "You feel more proficient in using ${toString()}s."
}
