package com.example.gamebugs.network.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Metall", strict = false)
class MetalCurrencyResponse(
    @field:Attribute(name = "Date", required = false)
    val date: String = "",
    @field:ElementList(inline = true, entry = "Record", required = false)
    val records: List<MetalCurrency> = emptyList()
)