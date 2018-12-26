package Models

import java.io.File
import java.io.InputStream
import java.lang.reflect.Proxy

class ProxyList(file:String){
    var proxyList = arrayListOf<String>()
    var currentIndex = 0
    var size = proxyList.size

    init{
        val classLoader = javaClass.classLoader
        val inputStream: InputStream = File(classLoader.getResource(file)!!.file).inputStream()
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines({ lines ->
            lines.forEach({
                proxyList.add(it)
            })
        })

        size = proxyList.size
        println("Imported ${proxyList.size} proxies.")
    }

    fun getNext():String{
        val proxy = proxyList.get(currentIndex % size)
        currentIndex += 1
        return proxy
    }
}
