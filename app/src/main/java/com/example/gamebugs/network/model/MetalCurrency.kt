package com.example.gamebugs.network.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Record", strict = true)
data class MetalCurrency (
    @field:Attribute(name = "Code", required = true)
    val code: String = "",

    //  @Element - это вложенный XML элемент
    @field:Element(name = "Buy", required = true)
    val buy: String = "",


    @field:Element(name = "Sell", required = true)
    val sell: String = ""
){
    fun toDouble():Double{
        return try{
            sell.replace(",", ".").replace(" ", "").toDouble()
        }catch (e: Exception){
            println(e.message)
            0.0
        }
    }

    fun isGold(): Boolean = code == "1"

}

