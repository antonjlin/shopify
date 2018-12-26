package Models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class ShopifyVariant {
    var id: String? = null
    var available: Boolean? = null
    var title: String? = null
}