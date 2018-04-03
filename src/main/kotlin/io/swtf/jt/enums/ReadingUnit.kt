package io.swtf.jt.enums

enum class ReadingUnit {
    METER_CUBE,
    KWH;

   companion object {
       fun fromString(string: String): ReadingUnit? {
           return when{
               string.equals(KWH.name, true) -> KWH
               string.equals(METER_CUBE.name, true) || string.equals("m3", ignoreCase = true)-> METER_CUBE
               else -> null
           }
       }
   }
}