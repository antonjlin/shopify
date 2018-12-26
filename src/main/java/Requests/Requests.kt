package Requests

import java.util.*
import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.util.EntityUtils
import org.apache.http.client.config.RequestConfig
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.auth.AuthScope
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.*
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.apache.http.entity.StringEntity
import org.json.JSONObject


fun performRequest(url:String,method:RequestMethod,jsonBody:JSONObject?=null, httpAuthString: String?=null,proxy:String?=null,contentType:String? = null, headers:List<Header> = listOf()):HTTPResponse{
    val credsProvider = BasicCredentialsProvider()

    val config = RequestConfig.custom()

    if(proxy != null){
        val proxyArray = proxy.split(":")
        val ip = proxyArray.get(0)
        val port = proxyArray.get(1)
        val proxyUser = proxyArray.get(2)
        val proxyPass = proxyArray.get(3)
        val proxyObject = HttpHost(ip, port.toInt())
        config.setProxy(proxyObject)


        credsProvider.setCredentials(
                AuthScope(ip, port.toInt()),
                UsernamePasswordCredentials(proxyUser, proxyPass)
        )
    }

    val builtConfig = config.build()

    val httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build()

    val httpResponse:HTTPResponse = HTTPResponse()

    try {
        var request:HttpRequestBase? = null
        when(method) {

            RequestMethod.GET -> request = HttpGet(url)
            RequestMethod.PUT -> request = HttpPut(url)
            RequestMethod.HEAD -> request = HttpHead(url)
            RequestMethod.PATCH-> request = HttpPatch(url)
            RequestMethod.DELETE -> request = HttpDelete(url)
            RequestMethod.OPTIONS -> request = HttpOptions(url)
            RequestMethod.POST -> {
                request = HttpPost(url)
                if(jsonBody!=null){
                    request.entity= StringEntity(jsonBody.toString(), APPLICATION_JSON);
                }
            }
            else ->{
                throw(Exception("HTTP Method not specified"))
            }
        }

        request.config = builtConfig



        if(httpAuthString != null){
            val splitString = httpAuthString.split(":")
            val user = splitString.get(0)
            val pass = splitString.get(1)
            val encodedAuth = String(Base64.getEncoder().encode(httpAuthString.toByteArray()))
            request.setHeader("Authorization", "Basic " + encodedAuth);

        }

        headers.forEach({header ->
            request.setHeader(header)
        })


        if(contentType != null){
            request.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        }

        val response = httpclient.execute(request)
        try {
            httpResponse.statusCode = response.statusLine.statusCode

            if(httpResponse.statusCode != 200){
                println(response.statusLine)
            }
            httpResponse.body = EntityUtils.toString(response.entity)
            httpResponse.headers = response.allHeaders
        } finally {
            response.close()
        }
    } finally {
        httpclient.close()
    }
    return httpResponse
}


fun main(args: Array<String>) {
    val j = performRequest(
            url="https://jeffsmith.myshopify.com/wallets/checkouts.json",
            method=RequestMethod.POST,
            jsonBody = JSONObject(),
            httpAuthString = "b31cc5137a6a9ed57fa486f5f74c3dab:b31cc5137a6a9ed57fa486f5f74c3dab",
            proxy="38.83.74.94:3128:solemobb_gmail_com:UHrCzoqYcZ")

    println(j.body)
    println(Arrays.toString(j.headers))
    println(j.statusCode)
}


