package dev.komu.kraken.content

import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.service.config.ObjectFactory
import dev.komu.kraken.utils.isFestivus
import java.time.DayOfWeek
import java.time.LocalDate

fun preparePlayer(player: Player) {
    player.wieldedWeapon = Weapons.dagger.create()
    player.inventory.add(Items.foodRation.create())
    player.inventory.add(Items.cyanideCapsule.create())

    val today = LocalDate.now()
    if (today.isFestivus) {
        player.message("Happy Festivus!")
        player.strength += 10
        player.luck = 2
        player.inventory.add(Weapons.aluminiumPole.create())
    }

    if (today.dayOfWeek == DayOfWeek.FRIDAY) {
        player.message("It is Friday, good luck!")
        player.luck = 1
    }
}

fun buildObjectFactory() =
    ObjectFactory().apply {
        addDefinitions(Weapons)
        addDefinitions(Items)
        addDefinitions(Creatures)
    }
