package io.swtf.jt.enums

enum class ReadingUnit {
    CUBIC_METER,
    KG,
    KWH;

    companion object {
        fun fromString(string: String): ReadingUnit? {
            return when {
                string.equals(KWH.name, true) -> KWH
                string.equals(CUBIC_METER.name, true) || string.equals("m3", ignoreCase = true) -> CUBIC_METER
                string.equals(KG.name, true) -> KG
                else -> null
            }
        }
    }
}