package com.example.skogs.wifictrl.model

import android.content.Context
import android.content.Intent
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL
import java.util.*


/**
 * Created by skogs on 08.06.2016.
 */
/*
class SocketModel private constructor(private var context: Context?) {
    val socket: SocketIO

    init {
        this.socket = chatServerSocket
    }

    companion object {

        private var instance: SocketModel? = null
        private val SERVER_ADDRESS = "http://192.168.137.1:8888"

        operator fun get(context: Context): SocketModel {
            if (instance == null) {
                instance = getSync(context)
            }
            instance!!.context = context
            return instance!!
        }

        @Synchronized fun getSync(context: Context): SocketModel {
            if (instance == null) {
                instance = SocketModel(context)
            }
            return instance!!
        }
    }

    private val chatServerSocket: IO?
        get() {
            try {
                val socket = IO.socket(SERVER_ADDRESS, object : IO.Options() {

                    fun onDisconnect() {
                        println("disconnected")
                    }

                    fun onConnect() {
                        println("connected")
                    }

                    fun on(event: String, ioAcknowledge: IOAcknowledge, vararg objects: Any) {
                        if (event == "chatMessage") {
                            val json = objects[0] as JSONObject
                            val chatMessage = ChatMessage(json)

                            val intent = Intent()
                            intent.setAction("newChatMessage")
                            intent.putExtra("chatMessage", chatMessage)
                            context!!.sendBroadcast(intent)
                        }
                    }

                    fun onError(e: SocketIOException) {
                        e.printStackTrace()
                    }
                })
                return socket
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
            }

            return null
        }

}*/
class SocketModel {

    companion object {

        private val PORT = "8888"
        private val IP = "192.168.137.1"

        private var mSocket: Socket? = null;

        fun connect() {

            try {
                mSocket = IO.socket("http://$IP:$PORT");
            } catch (e: URISyntaxException) {
                println("ERROR IN SOCKET")
            }

//            mSocket?.on("echo back", onNewMessage);
            try{
                mSocket?.connect()
            } catch (e: URISyntaxException){
                println("Not connected!!!")
            }

        }

        fun sendData(){
            Log.d("TAG","Send echo")
            mSocket?.emit("echo", "hello");
            mSocket?.on("echo back", onNewMessage);
        }

        private val onNewMessage = Emitter.Listener {
            Log.d("TAG", "ECHO RECEEEEIVED!!!");
        };
    }

}