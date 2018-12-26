package Monitors

import Models.*
import Requests.RequestMethod
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Thread.sleep


class ProductListings(val siteConfig: SiteConfig,val interval:Int=1000): Monitor() {

    val productListingsEP = "https://${siteConfig.domain}/api/product_listings.json?limit=250"
    val productsJsonEP = "https://${siteConfig.domain}/products.json?limit=250"
    var currentProducts:ArrayList<Product> = arrayListOf()
    val proxyList = ProxyList("proxies.txt")

    init{
        running.set(true)
    }

    fun scrapeProducts(prodJson: Boolean = false, page: Int = 1):ArrayList<Product> {
        var endpoint:String? = null;
        if(prodJson) endpoint = "$productsJsonEP?page=$page" else endpoint = productListingsEP
        val t1 = System.currentTimeMillis()

        val r = Requests.performRequest(
                endpoint,
                method=RequestMethod.GET,
                httpAuthString = siteConfig.apiKey + ":" + siteConfig.apiKey,
                contentType = "application/json",
                proxy=proxyList.getNext()
        )

        if(r.statusCode != 200){
            Thread.sleep(200)
            return scrapeProducts(prodJson,page)
        }

        val t2 = System.currentTimeMillis()
        val rawJsonResponse = r.getJson()
        var jsonProducts:JSONArray? = null;
        if(prodJson) jsonProducts = rawJsonResponse.getJSONArray("products") else jsonProducts  = rawJsonResponse.getJSONArray("product_listings")
        val numProducts: Int = jsonProducts.length()
        val products = arrayListOf<Product>()
        for (i in 0 until numProducts) {
            val jsonProduct: JSONObject = jsonProducts.getJSONObject(i)
            val product = mapProduct(jsonProduct);
            products.add(product)
        }
        println("Total process time: ${System.currentTimeMillis() - t2}ms")
        println("Total scrape time: ${System.currentTimeMillis() - t1}ms")
        return products
    }

    fun mapProduct(prodJson: JSONObject): Product {
        val p = Product()
        val handle = prodJson.getString("handle")

        try{
            p.id = prodJson.getLong("id").toString()
        }catch(e:JSONException){
            p.id = prodJson.getLong("product_id").toString()
        }
        p.handle = handle
        p.title = prodJson.getString("title")
        p.site = this.siteConfig.domain
        p.url = "https://${this.siteConfig.domain}/products/${handle}"
        val jsonVariants = prodJson.getJSONArray("variants")
        val numVariants = jsonVariants.length()
        val variants = arrayListOf<Variant>()

        jsonVariants.forEach({jsonVariant ->
            variants.add(mapVariant(jsonVariant as JSONObject))
        })
        p.variants = variants
        var jsonTags:List<Any>? = null
        try {
            jsonTags = prodJson.getJSONArray("tags").toList()

        }catch(e:Exception){
            jsonTags = prodJson.getString("tags").split(",")
        }
        val tags = arrayListOf<String>()
        jsonTags!!.forEach({tag ->
            tags.add(tag as String)
        })

        p.tags = tags
        return p
    }

    fun mapVariant(variantJSON:JSONObject): Variant {
        val v = Variant(id = variantJSON.getLong("id").toString(), title = variantJSON.getString("title"), sku = variantJSON.getString("sku"), available = variantJSON.getBoolean("available"))
        return v
    }


    fun findProductByID(targetProduct: Product, currentProducts:ArrayList<Product>): Product?{
            currentProducts.forEach({currentProd ->
                if(currentProd.id == targetProduct.id) return currentProd
            })
        return null
    }

    fun initScrape(){
        this.currentProducts = scrapeProducts(prodJson = true)
    }

    fun compareProducts(newProducts:ArrayList<Product>){
        newProducts.forEach({ newProduct ->
            val foundProduct = findProductByID(newProduct, currentProducts)
            if (foundProduct == null) {
                println("New product: ${newProduct.title}")
                currentProducts.add(newProduct)
            }else{
                //product is found
                if(hasRestockedVariants(newProduct,foundProduct)){
                    println("Restock: ${newProduct.title}")
                    //replace product
                    currentProducts[currentProducts.indexOf(foundProduct)] = newProduct
                }

            }

        })
    }

    fun hasRestockedVariants(newProduct: Product, oldProduct: Product):Boolean{
        for(newVar in newProduct.variants!!){
            if(newVar.available){

                var isRestocked:Boolean? = null
                for(oldVar in oldProduct.variants!!){
                    if(newVar.id == oldVar.id){
                        if(newVar.available && !oldVar.available){
//                            println("New variant went in stock")
                            isRestocked =  true
                            break
                        }else{
//                            println("Found matching variant: ${oldVar.id} ${newVar.id} , not restocked.")
                            // found variant but was not restocked
                            isRestocked = false
                            break
                        }
                    }
                }
                if(isRestocked == null){
//                    print("New variant found: ${newVar!!.id}")
                    return true
                }else if(isRestocked){
                    return true
                }

            }
        }
        return false
    }

    override fun run(){
        initScrape()
        var startTime = System.currentTimeMillis()
        var scrapeTime:Long? = null
        var sleepTime:Long? = null
        while(this.running.get()){
            startTime = System.currentTimeMillis()
            compareProducts(scrapeProducts(prodJson = true))
            scrapeTime = System.currentTimeMillis() - startTime
            sleepTime = if(interval -scrapeTime > 0) interval -scrapeTime else 0
            println("Scrape took ${scrapeTime}ms, sleeping ${sleepTime}ms")
            sleep(sleepTime)
        }
    }
}


