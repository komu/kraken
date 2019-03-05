package dev.komu.kraken.utils

import java.time.LocalDate
import java.time.Month

val LocalDate.isFestivus: Boolean
    get() = month == Month.DECEMBER && dayOfMonth == 23
