package Utils

import jdk.nashorn.internal.objects.NativeDate.getTime
import java.sql.Timestamp
import java.util.Date


class Logger(val prefix:String){

}

fun log(text:String){
    val date = Date()
    val time = date.time
    val ts = Timestamp(time)
    println("[$ts] $text")
}

fun getTS():String{
    val date = Date()
    val time = date.time
    val ts = Timestamp(time)
    return ts.toString()
}