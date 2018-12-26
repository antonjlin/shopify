package Models


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ShopifyProduct {
    var title: String? = null
    var available: Boolean? = null
    var variants: List<ShopifyVariant>? = null
    var description: String? = null
    var featured_image: String? = null
    var price: Int? = null



    override fun toString(): String {
        return "ShopifyProduct(title=$title, available=$available, variants=$variants, description=$description, featured_image=$featured_image, price=$price)"
    }


}