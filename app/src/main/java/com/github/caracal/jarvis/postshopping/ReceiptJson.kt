package com.github.caracal.jarvis.postshopping

import org.json.JSONArray
import org.json.JSONObject

/** Serializes this [ReceiptData] to the JSON representation published to cloud sync. */
fun ReceiptData.toJson(): String {
    val itemsArray = JSONArray()
    for (item in items) {
        val itemJson = JSONObject()
        itemJson.put("name", item.name)
        itemJson.put("price", item.price)
        itemJson.put("quantity", item.quantity)
        itemJson.put("unitPrice", item.unitPrice)
        itemsArray.put(itemJson)
    }

    val json = JSONObject()
    json.put("shopName", shopName)
    json.put("date", date.toString())
    json.put("items", itemsArray)
    json.put("subtotal", subtotal)
    json.put("tax", tax)
    json.put("total", total)
    json.put("subtotalZeroRated", subtotalZeroRated)
    json.put("subtotalStandardRated", subtotalStandardRated)
    return json.toString()
}
