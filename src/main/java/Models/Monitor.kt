package Models

import java.util.concurrent.atomic.AtomicBoolean

open class Monitor():Runnable{
    val running:AtomicBoolean = AtomicBoolean(false);
    private var t: Thread? = null

    override fun run(){}

    fun start () {
        if (t == null) {
            t = Thread(this)
            running.set(true)
            t!!.start()

        }
    }

    fun stop() {
        running.set(false)
        t!!.interrupt()
    }
}