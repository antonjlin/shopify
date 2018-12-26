package Models

import java.util.*

class Product(){
    var id:Any?=null
    var title:String?=null
    var variants:ArrayList<Variant> = arrayListOf()
    var price:Double?=null
    var tags:ArrayList<String> = arrayListOf()
    var site:String? = null
    var vendor:String? = null
    var handle:String? = null
    var url:String?=null
    var image:String?=null


    fun getFilterString():String{
        var filterString:String = "title=$title||url=$url||price=$price||site=$site||vendor=$vendor||handle=$handle||image=$image||"

        tags.forEach({
            tag -> filterString += "tag=$tag||"
        })

        variants.forEach({
            variant -> filterString += "variant=${variant.title}||"
        })


        return filterString
    }

    override fun toString(): String {
        return "Product(id=$id, title=$title, variants=$variants, price=$price, tags=$tags, site=$site, vendor=$vendor, handle=$handle, url=$url, image=$image)"
    }


}

