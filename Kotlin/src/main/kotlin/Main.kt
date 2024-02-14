

import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable
import okhttp3.OkHttpClient

interface GdaxService {
    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>
    @Send
    fun sendSubscribe(number:Int)
    @Receive
    fun observeTicker(): Flowable<Int>
}
fun main(args: Array<String>) {
    println("Connect (y/n)?")

    var answer = readln()

    if (answer == "y") {
        var subscribe_message = 0
        val okHttpClient= OkHttpClient()
        val moshi = Moshi.Builder().build()

        val scarletInstance = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory("ws://localhost:11337/"))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .build()

        val gdaxService = scarletInstance.create<GdaxService>()
        gdaxService.observeWebSocketEvent()
            .filter { it is WebSocket.Event.OnConnectionOpened<*> }
            .subscribe {
                println("Pick a number to send:")
                subscribe_message = Integer.parseInt(readln())
                gdaxService.sendSubscribe(subscribe_message)
                println("Sent:" + subscribe_message)
            }

        gdaxService.observeTicker()
            .subscribe { ticker ->
                println("Received ${ticker}")
                println("Send another number? (y/n)")
                answer = readln()
                if (answer == "y") {
                    println("Pick a number to send:")
                    subscribe_message = Integer.parseInt(readln())
                    gdaxService.sendSubscribe(subscribe_message)
                    println("Sent:" + subscribe_message)
                }
            }
    }
    else {
        println("Bye then")
    }





    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html
}