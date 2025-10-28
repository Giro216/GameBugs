package com.example.gamebugs.network.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Record", strict = false)
data class MetalCurrency(
    @field:Attribute(name = "Date", required = false)
    var date: String = "",

    @field:Attribute(name = "Code", required = true)
    var code: String = "",

    @field:Element(name = "Buy", required = true)
    var buy: String = "",

    @field:Element(name = "Sell", required = true)
    var sell: String = ""
){
    fun toDouble(): Double {
        return try {
            sell.replace(",", ".").replace(" ", "").toDouble()
        } catch (e: Exception) {
            println("Ошибка преобразования цены: ${e.message}")
            0.0
        }
    }

    fun isGold(): Boolean = code == "1"
}