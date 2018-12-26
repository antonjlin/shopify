package Models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ShopifyJsonResponse {

    var Products: List<ShopifyProduct>? = null
    override fun toString(): String {
        return "ShopifyJsonResponse(Products=$Products)"
    }


}