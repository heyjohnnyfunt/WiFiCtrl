package com.example.skogs.wifictrl.fragment

import android.app.Fragment
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.skogs.wifictrl.Constants
import com.example.skogs.wifictrl.R
import com.example.skogs.wifictrl.WifiActivity
import com.example.skogs.wifictrl.model.Database
import com.example.skogs.wifictrl.model.WifiStation
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import org.json.JSONException
import org.json.JSONObject
import java.security.SecureRandom

/**
 * Details view for a Wi-Fi base station.
 *
 * @author Mike Gouline
 */
open class WifiDetailFragment : Fragment() {

    companion object {

        private val PORT = "8888"

        private var randomBytes: ByteArray ? = null
        private var randomByteString: String ? = null
        private var chosenWifi: WifiStation? = null
        private var mSocket: Socket? = null;
        private var isCurrentWifi: Boolean = false

        private var connectBtn: Button? = null
        private var errorText: TextView? = null

        fun newInstance(item: WifiStation): WifiDetailFragment {
            chosenWifi = item;
            return WifiDetailFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wifi_detail, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disconnectSocket()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        connectBtn = view?.findViewById(R.id.connect_btn) as Button
        errorText = view?.findViewById(R.id.error_text) as TextView
        val ssidTextView = view?.findViewById(R.id.wlan_ssid) as TextView
        val authTextView = view?.findViewById(R.id.wlan_auth) as TextView
        val passEditText = view?.findViewById(R.id.wlan_pass) as EditText

        // check if chosen network is current
        val connectedWifi = arguments.getCharArray(Constants.connctedWifiArg) ?: null
        val c = connectedWifi?.joinToString("")
        if (c == chosenWifi?.ssid) {
            isCurrentWifi = true
        } else {
            isCurrentWifi = false
        }

        // if current network is chosen one, change button text
        if (isCurrentWifi)
            connectBtn?.text = getString(R.string.authenticate)
        else
            connectBtn?.text = getString(R.string.action_connect_wifi)


        ssidTextView.text = chosenWifi?.ssid
        authTextView.text = chosenWifi?.capabilities

        val security = WifiStation.getSecurity(chosenWifi!!);

        if (security !== -1) {
            view?.findViewById(R.id.password_input)?.visibility = View.VISIBLE
        }

        connectBtn?.setOnClickListener {

            if (isCurrentWifi) {
                authAlert();
            } else {
                val builder = AlertDialog.Builder(activity);
                builder.setMessage("Connect?")
                        .setPositiveButton("YES") {

                            dialog, whichButton ->

                            if (connect(chosenWifi, passEditText.text.toString(), security)) {
                                arguments.putCharArray(Constants.connctedWifiArg, chosenWifi!!.ssid!!.toCharArray());
                                connectBtn?.text = getString(R.string.authenticate)
                                errorText?.visibility = View.INVISIBLE
                                isCurrentWifi = true
                                authAlert();

                            } else {
                                errorText?.text = "Invalid password"
                                errorText?.visibility = View.VISIBLE
                                errorText?.setTextColor(resources.getColor(R.color.colorAccent));
                                isCurrentWifi = false
                            }
                        }
                        .setNegativeButton("NO") {
                            dialog, whichButton ->
                        }

                val alert = builder.create();
                alert.show();
            }
        }
    }

    private fun unsuccessfulKeyCheckAlert() {
        val builder = AlertDialog.Builder(activity);
        builder.setMessage("Received key is not valid. Try to receive it again?")
                .setPositiveButton("YES") {
                    dialog, whichButton ->
                    emitCheckKeyRequest()
                }
                .setNegativeButton("NO") {
                    dialog, whichButton ->
                    disconnectSocket()
                }
        val alert = builder.create();
        alert.show();
    }

    private fun authAlert() {
        val builder = AlertDialog.Builder(activity);
        builder.setTitle("AP Authorization");
        builder.setItems(arrayOf("Derive Key", "Check key", "Simply connect", "Disconnect", "Cancel"),
                {
                    dialog, whichButton ->
                    when (whichButton) {
                        0 -> emitDeriveKeyRequest()
                        1 -> emitCheckKeyRequest()
                        2 -> confirmAlert("Connected successfully without authentication");
                        3 -> {
                            val activity = activity
                            if (activity is WifiActivity) {
                                disconnectSocket()
                                activity.disconnect()
                                confirmAlert("Successfully disconnected");
                                connectBtn?.text = getString(R.string.action_connect_wifi)
                                isCurrentWifi = false
                                arguments.putCharArray(Constants.connctedWifiArg, null);
                            }
                        }
                    }
                });
        val alert = builder.create();
        alert.show();
    }

    private fun confirmAlert(msg: String) {
        val builder = AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setPositiveButton("ОК") {
                    dialog, whichButton ->
                }
        val alert = builder.create();
        alert.show();
    }

    private fun reconnectAlert() {
        val builder = AlertDialog.Builder(activity);
        builder.setMessage("Try to connect again?")
                .setPositiveButton("YES") {
                    dialog, whichButton ->
                    connectSocket()
                }
                .setNegativeButton("NO") {
                    dialog, whichButton ->
                    disconnectSocket()
                }
        val alert = builder.create();
        alert.show();
    }

    fun connect(chosenWifi: WifiStation?, pass: String, type: Int): Boolean {

        if (chosenWifi == null)
            return false

        var result = false
        val conf = WifiConfiguration()

        conf.SSID = "\"" + chosenWifi.ssid + "\"";

        when (type) {
            -1 -> {
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
        //WEP
            0 -> {
                conf.wepKeys[0] = "\"$pass\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            }
        //WPA-PSK
            1 -> {
                conf.preSharedKey = "\"$pass\"";
            }
        //WPA-EAP
            2 -> {

            }
        }

        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager;
        wifiManager.addNetwork(conf);

        val list: List<WifiConfiguration> = wifiManager.configuredNetworks

        list.forEach {
            if (it.SSID != null && it.SSID.equals("\"${chosenWifi.ssid}\"")) {
                wifiManager.disconnect();
                arguments.putCharArray(Constants.connctedWifiArg, null);
                wifiManager.enableNetwork(it.networkId, true);
                result = wifiManager.reconnect();
            }
        }
        return result
    }

    private fun getGateway(): String? {
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return intToIp(wifiManager.dhcpInfo.gateway)
    }

    private fun intToIp(i: Int): String {
        return ((i and 0xFF).toString() + "." +
                ((i shr 8) and 0xFF) + "." +
                ((i shr 16) and 0xFF) + "." +
                ((i shr 24) and 0xFF)) ;
    }

    fun emitDeriveKeyRequest() {
        connectSocket();
        mSocket?.emit("deriveKey")
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun emitCheckKeyRequest() {
        connectSocket();
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes);

        val sb = StringBuffer()
        for (b in randomBytes) {
            sb.append(String.format("%02X", b));
        }
//        randomBytes = BigInteger(130, SecureRandom()).toByteArray()

        randomByteString = sb.toString()
        println("******************* random bytes = " + randomByteString)
//        println("******************* random bytes = " + Hex.encodeHex(randomBytes))

        mSocket?.emit("checkKey", randomByteString)
    }

    fun connectSocket() {

        val IP = getGateway()

        mSocket = IO.socket("http://$IP:$PORT");

        if (!mSocket!!.connected()) {

            mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket?.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
//            mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket?.on("checkKeyResponse", onKeyCheck);
            mSocket?.on("deriveKeyResponse", onNewKeyDerivation);
            mSocket?.connect()
        };
    }

    fun disconnectSocket() {
        mSocket?.disconnect();

        mSocket?.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket?.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
//        mSocket?.off(Socket.EVENT_DISCONNECT, onDisconnect);

        mSocket?.off("checkKeyResponse", onKeyCheck);
        mSocket?.off("deriveKeyResponse", onNewKeyDerivation);
    }

    fun checkKey(sign: String): Boolean {

        val publicKey = Database.get(activity, chosenWifi!!)

        println("********************** sign = " + sign)
        println("********************** randomByteString = " + randomByteString!!)
        val result: Boolean;
        try {
            val sgr = EdDSAEngine()
            val spec = EdDSANamedCurveTable.getByName("ed25519-sha-512")
            val pubKey = EdDSAPublicKeySpec(hexStringToByteArray(publicKey!!), spec)
            val vk = EdDSAPublicKey(pubKey)
            sgr.initVerify(vk)
            sgr.update(hexStringToByteArray(randomByteString!!))
            result = sgr.verify(hexStringToByteArray(sign))
        } catch (e: Exception) {
            result = false
            println(e)
        }

        if (result) return true

        return false;
    }


    private val onKeyCheck = Emitter.Listener { args ->
        activity.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val receivedString: String
            try {
                receivedString = data.getString("encodedString")
                val result = checkKey(receivedString)
                if (result) {
                    confirmAlert("Received string: $receivedString.\nKey checking successful. Traffic unblocked")
                } else {
                    unsuccessfulKeyCheckAlert()
                }

            } catch (e: JSONException) {
                return@Runnable
            }
        })
    };

    private val onNewKeyDerivation = Emitter.Listener { args ->
        activity.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val key: String
            try {
                key = data.getString("key")
                Database.set(activity, chosenWifi!!, key)
                confirmAlert("Key successfully installed. Check settings tab.")
            } catch (e: JSONException) {
                return@Runnable
            }
        })
    };

    private val onConnectTimeout = Emitter.Listener {
        activity.runOnUiThread({
            Toast.makeText(activity.applicationContext, R.string.error_timeout, Toast.LENGTH_LONG).show()
            disconnectSocket()
        })
    };

    private val onConnectError = Emitter.Listener {
        activity.runOnUiThread({
            Toast.makeText(activity.applicationContext, R.string.error_connect, Toast.LENGTH_LONG).show()
//            reconnectAlert()
            disconnectSocket()
        })
    }

    private val onDisconnect = Emitter.Listener {
        activity.runOnUiThread({
            confirmAlert("Disconnected")
        })
    }


}