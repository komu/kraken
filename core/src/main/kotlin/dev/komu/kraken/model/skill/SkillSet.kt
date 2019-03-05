package dev.komu.kraken.model.skill

import dev.komu.kraken.model.common.MessageTarget
import dev.komu.kraken.model.item.weapon.WeaponClass
import java.util.*

class SkillSet {
    private val weaponSkills = EnumMap<WeaponClass, SkillLevel>(WeaponClass::class.java)

    fun getWeaponProficiency(weaponClass: WeaponClass) =
        weaponSkills[weaponClass]?.proficiency ?: weaponClass.defaultProficiency

    fun exerciseSkill(weaponClass: WeaponClass, target: MessageTarget) {
        if (weaponClass == WeaponClass.NOT_WEAPON)
            return

        val level = weaponSkills.getOrPut(weaponClass) {
            SkillLevel(weaponClass.defaultProficiency)
        }

        level.training++
        val nextLevel = level.proficiency.next
        if (nextLevel != null && level.training >= nextLevel.trainingToReachThisLevel) {
            level.proficiency = nextLevel
            target.message(weaponClass.skillLevelIncreaseMessage)
        }
    }

    fun setWeaponProficiency(weaponClass: WeaponClass, proficiency: Proficiency) {
        val level = weaponSkills.getOrPut(weaponClass) {
            SkillLevel(proficiency)
        }

        level.proficiency = proficiency
        level.training = proficiency.trainingToReachThisLevel
    }

    override fun toString() = "[weaponSkills=$weaponSkills]"

    class SkillLevel(var proficiency: Proficiency) {
        var training = 0

        override fun toString() = "($proficiency/$training)"
    }
}
