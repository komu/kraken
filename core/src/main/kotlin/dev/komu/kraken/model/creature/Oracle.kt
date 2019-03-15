package dev.komu.kraken.model.creature

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.common.Color
import dev.komu.kraken.model.item.weapon.NaturalWeapon
import dev.komu.kraken.utils.exp.Expression
import dev.komu.kraken.utils.randomElement

class Oracle(name: String) : Monster(name) {

    init {
        hitPoints = 10000
        letter = '@'
        color = Color.WHITE
        baseSpeed = 500000
        state = OracleState
        naturalWeapon = NaturalWeapon("curse", 20, Expression.constant(0))
    }

    override fun talk(target: Creature) {
        target.say(this, wisdoms.randomElement())
    }

    private object OracleState : MonsterState {
        override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> =
            null to this

        override val isFriendly: Boolean
            get() = true
    }

    companion object {
        private val wisdoms = listOf(
            "Beauty is in the eye of beholder.",
            "They say that no ordinary shovel can dig into Exceptionally Hard Rock.",
            "Only the light of Graal can defeat the ultimate darkness.",
            "They say that Festivus is the day to start your adventure.",
            "They say that The Founders are there on Friday.",
            "The deaf are not afraid of the Ni.",
            "All names have a meaning.",
            "How small a rock needs to be in order to float on water?",
            "Beholders tend to feel uneasy around spoons.",
            "Chain smoking might be good for you.",
            "The Siamese bats are more dangerous.",
            "There is no fork.",
            "Enlarge your shield.",
            "They say a lot of things.",
            "Don't believe everything you are told.",
            "This sentence is a lie.",
            "This sentence is true, but can't be proved.",
            "What's the value of x in the following sequence? 1, 2, 720!, x, ...",
            "How many roads must a man walk down?",
            "Attributes are the first step on the road that leads to the Dark Side."
        )
    }
}
