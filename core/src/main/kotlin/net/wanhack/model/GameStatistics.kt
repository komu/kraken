package net.wanhack.model

import net.wanhack.model.creature.Player

class GameStatistics(player: Player, val time: Int) {
    val playerName = player.name
    val strength = player.strength
    val charisma = player.charisma
    val hitPoints = player.hitPoints
    val maximumHitPoints = player.maximumHitPoints
    val regionName = player.region.name
    val armorClass = player.armorClass
    val level = player.level
    val experience = player.experience
    val hungerLevel = player.hungerLevel
}
