package Requests

import org.apache.http.Header
import org.json.JSONObject

class HTTPResponse(var statusCode:Int?=null,var body:String?=null,var headers:Array<Header>?=null){

    fun getJson():JSONObject{
        return JSONObject(this.body)
    }

}