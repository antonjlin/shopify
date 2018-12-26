package Models

import java.util.*

data class SiteConfig(
    val name:String,
    val myshopifyUrl:String,
    val domain:String,
    val apiKey:String,
    var delay:Int=3000,
    var timeout:Int=3000,
    val currency:String="USD",
    val currencyConversionRate:Double=1.0,
    val currencySumbol:String="$"
)