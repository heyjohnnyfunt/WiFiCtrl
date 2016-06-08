package com.example.skogs.wifictrl.model

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


/**
 * Created by skogs on 08.06.2016.
 */
class SocketModel {

    companion object {
        private val PORT = "8888"
        private val IP = "192.168.137.1"

        private var mSocket: Socket? = null;

        fun connect() {

            try {
                mSocket = IO.socket("http://$IP/$PORT");
            } catch (e: URISyntaxException) {

            }

            mSocket?.connect();

        }

    }


}