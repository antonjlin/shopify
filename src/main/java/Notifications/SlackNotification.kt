package Notifications

import Enums.FilterLevel
import Enums.NotificationType
import Models.Product
import Models.ShopifyVariant
import Models.Variant
import Utils.getTS
import org.json.JSONArray
import org.json.JSONObject

class SlackNotification(val product: Product,val filterLevel: FilterLevel,val notificationType:NotificationType){

    var json = JSONObject()
    var mainAttachment = JSONObject()
    var variantFields = arrayListOf<JSONObject>()

    init{
        variantFields

    }


    fun mapToJson(){
        mainAttachment.put("fallback",product.title)
        mainAttachment.put("color","#3AA3E3")


        var atcFieldString = ""
        var qtFieldString = ""
        var atcLink = ""
        product.variants!!.forEach({variant ->
            atcLink = "${product.site}/cart/${variant.id}:1"
            atcFieldString += "<${atcLink}|${variant.title}>\n"
            qtFieldString += "<https://www.calicos.io/dashboard?qt=${atcLink}|${variant.title}>\n"

        })

        val atcField = JSONObject()
        val qtField = JSONObject()

        atcField.put("title","ATC")
        atcField.put("value",atcFieldString)
        atcField.put("short",true)


        qtField.put("title","QT")
        qtField.put("value",qtFieldString)
        qtField.put("short",true)


        val fields = JSONArray()
        fields.put(atcField)
        fields.put(qtField)

        mainAttachment.put("fields",fields)
        mainAttachment.put("title",product.title)
        mainAttachment.put("title_link",product.url)
        mainAttachment.put("author_name",product.site)
        mainAttachment.put("author_link",product.site)
        mainAttachment.put("footer",getTS())
        mainAttachment.put("ts",System.currentTimeMillis()/1000)
        mainAttachment.put("thumb_url",product.image)


        val attachments = JSONArray()
        attachments.put(mainAttachment)

        json.put("attachments",attachments)



        println(json.toString())


        val r = khttp.post("https://hooks.slack.com/services/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",json = json)

        println(r.text)

    }

}

fun main(args: Array<String>) {

    val p = Product()
    p.site = "https://kith.com"
    p.title="yeezy boost 350 frozen yellow"
    p.url = "https://kith.com/collections/all"
    p.image = "https://image-cdn.hypb.st/https%3A%2F%2Fhypebeast.com%2Fwp-content%2Fblogs.dir%2F6%2Ffiles%2F2018%2F12%2Fadidas-yeezy-boost-350-v2-semi-frozen-yellow-restock-store-list-1.jpg?q=75&w=800&cbr=1&fit=max"
    p.variants = arrayListOf(
            Variant(id="1243",title="US 7.5",sku="sdfsdf",available = false),
            Variant(id="1243",title="US 8",sku="sdfsdf",available = false),
            Variant(id="1243",title="US 8.5",sku="sdfsdf",available = false),
            Variant(id="1243",title="US 9",sku="sdfsdf",available = false),
            Variant(id="1243",title="US 9.5",sku="sdfsdf",available = false))
    val s = SlackNotification(p,FilterLevel.unfiltered,NotificationType.discount)
    s.mapToJson()
}