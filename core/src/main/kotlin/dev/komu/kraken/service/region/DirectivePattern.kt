package dev.komu.kraken.service.region

class DirectivePattern(pattern: String) {

    private val pattern = Regex(pattern.replace("[int]", "(\\d+)").replace("[str]", "\"([^\"]+)\"").replace(Regex(" +"), "\\\\s+"))

    fun getTokens(str: String): Array<String>? =
        pattern.matchEntire(str)?.groupValues?.drop(1)?.toTypedArray()
}
