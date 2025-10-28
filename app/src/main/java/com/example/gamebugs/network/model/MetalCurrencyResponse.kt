package com.example.gamebugs.network.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Metall", strict = false)
data class MetalCurrencyResponse(
    @param:Attribute(name = "FromDate", required = false)
    @field:Attribute(name = "FromDate", required = false)
    var fromDate: String = "",

    @param:Attribute(name = "ToDate", required = false)
    @field:Attribute(name = "ToDate", required = false)
    var toDate: String = "",

    @param:Attribute(name = "name", required = false)
    @field:Attribute(name = "name", required = false)
    var name: String = "",

    @param:ElementList(inline = true, entry = "Record", required = false)
    @field:ElementList(inline = true, entry = "Record", required = false)
    var records: List<MetalCurrency> = emptyList()
)