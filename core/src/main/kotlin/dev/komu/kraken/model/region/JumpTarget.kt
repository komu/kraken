package dev.komu.kraken.model.region

data class JumpTarget(val region: String, val location: String) {
    val isExit = region == "exit"
}
