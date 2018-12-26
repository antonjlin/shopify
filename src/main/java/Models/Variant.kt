package Models

import java.math.BigInteger

class Variant(var id:String,var title:String,var sku:String,var available:Boolean){
    override fun toString(): String {
        return "Variant(id='$id', title='$title', sku='$sku')"
    }
}