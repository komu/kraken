package dev.komu.kraken.model.item.food

enum class Taste(private val s: String, private val like: Boolean) {
    APPLE("apple", true),
    CHICKEN("chicken", true),
    STRAWBERRY("strawberries", true),
    BLUEBERRY("blueberries", true),
    ELDERBERRY("elderberries", true),
    VANILLA("vanilla", true),
    CHEESE("cheese", true),
    STRANGE("strange", false),
    DULL("dull", false);

    override fun toString() = if (like) "like $s" else s
}
