package Helpers

import Enums.FilterLevel
import Models.Product
import java.util.*


class Filter(var hypeKeywords:ArrayList<String>,var personalKeywords:ArrayList<String>){
    init {
        println("Initialized ${hypeKeywords.size} hype keywords and ${personalKeywords.size} personal keywords")
    }

    fun getFilterLevel(product: Product): FilterLevel {

        for(keywordString in hypeKeywords){
            if(filterOne(product,keywordString)) {
                return FilterLevel.hype;
            }
        }

        for(keywordString in personalKeywords){
            if(filterOne(product,keywordString)) {
                return FilterLevel.personal;
            }
        }

        return FilterLevel.unfiltered
    }

    fun filterOne(product: Product, keywordString:String):Boolean{
        val keywordArray = keywordString.toLowerCase().split(",")
        val posKws = ArrayList<String>()
        val negKws = ArrayList<String>()

        keywordArray.forEach({keyword ->
            when(keyword.startsWith("+")){
                true -> posKws.add(keyword.removePrefix("+"))
                false -> negKws.add(keyword.removePrefix("-"))
            }
        })

        for(kw in negKws){
            if(product.getFilterString().toLowerCase().contains(kw)){
                return false
            }
        }

        for(kw in posKws){
            if(!product.getFilterString().toLowerCase().contains(kw)){
                return false
            }
        }
        return true
    }


}




fun main(args: Array<String>) {
    val f = Filter(arrayListOf("+yeezy,+boost,+frozen,-yellow"), arrayListOf("+yeezy,+boost,-frozen"))
    val p = Product()
    p.title = "YEEZY BOOST 350 V2 (FROZEN YELLOW)"
    println(f.getFilterLevel(p))
}