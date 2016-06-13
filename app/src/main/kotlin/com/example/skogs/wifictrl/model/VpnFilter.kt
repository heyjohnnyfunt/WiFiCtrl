package com.example.skogs.wifictrl.model

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Handler
import android.os.Message
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import com.example.skogs.wifictrl.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

/**
 * Created by skogs on 11.06.2016.
 */
class VpnFilter : VpnService(), Handler.Callback {


    private val TAG = "Ctrl+WiFi"//getString(R.string.app_name);

    private var mHandler: Handler? = null
    private var mThread: Thread? = null
    internal var builder = Builder()

    private var mInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = Handler()
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread!!.interrupt()
        }

        // Start a new session by creating a new thread.
        mThread = Thread(Runnable {
            Log.i(TAG, "******************* Running vpnService")

            try {
                runVpnConnection()
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e(TAG, "Got " + e.toString());
            } finally {
                try {
                    mInterface!!.close()
                } catch (e: Exception) {
                    // ignore
                }

                mInterface = null

                mHandler!!.sendEmptyMessage(R.string.disconnected)
                Log.i(TAG, "Exiting")
            }
        }, TAG)

        mThread!!.start()
        return START_STICKY
    }

    override fun onDestroy() {
        if (mThread != null) {
            mThread!!.interrupt()
        }
        super.onDestroy();
    }

    @Throws(Exception::class)
    private fun configure() {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null) {
            Log.i(TAG, "Using the previous interface")
            return
        }

        // Configure a builder while parsing the parameters.
        builder.setSession(TAG)//.setMtu(1500)
        builder.addAddress("10.0.0.1", 32).addRoute("0.0.0.0", 0)
        try {
            mInterface!!.close()
        } catch (e: Exception) {
            // ignore
        }

        mInterface = builder.establish()
    }

    @Throws(Exception::class)
    private fun runVpnConnection(): Boolean {

        configure()

        val `in` = FileInputStream(mInterface!!.fileDescriptor)

        // Packets received need to be written to this output stream.
        val out = FileOutputStream(mInterface!!.fileDescriptor)

        // The UDP channel can be used to pass/get ip package to/from server
        val tunnel = DatagramChannel.open()

        // Protect the tunnel before connecting to avoid loopback.
        if (!protect(tunnel.socket())) {
            throw IllegalStateException("Cannot protect the tunnel");
        }
        // Connect to the server, localhost is used for demonstration only.
        tunnel.connect(InetSocketAddress("10.0.0.1", 55555))
        // For simplicity, we use the same thread for both reading and
        // writing. Here we put the tunnel into non-blocking mode.
        tunnel.configureBlocking(false)

        // Allocate the buffer for a single packet.
        val packet = ByteBuffer.allocate(32767)

        // Protect this socket, so package send by it will not be feedback to the vpn service.
        protect(tunnel.socket())

        // We use a timer to determine the status of the tunnel. It
        // works on both sides. A positive value means sending, and
        // any other means receiving. We start with receiving.
        var timer = 0

        // We keep forwarding packets till something goes wrong.
        while (true) {
            // Assume that we did not make any progress in this iteration.
            var idle = true

            // Read the outgoing packet from the input stream.
            var length = `in`.read(packet.array())

            if (length > 0) {

                Log.i(TAG, "************new packet 1")
//                System.exit(-1)
                /*while (packet.hasRemaining()) {
                    Log.i(TAG, "" + packet.get())
                }*/
                // Write the outgoing packet to the tunnel.
                packet.limit(length)
                tunnel.write(packet);
                packet.clear()
                // There might be more outgoing packets.
                idle = false
                // If we were receiving, switch to sending.
                if (timer < 1) {
                    timer = 1
                }

            }
            length = tunnel.read(packet)

            if (length > 0) {

                Log.i(TAG, "************new packet 2")
                // Ignore control messages, which start with zero.
                if (packet.get(0).toInt() !== 0) {
                    // Write the incoming packet to the output stream.
                    out.write(packet.array(), 0, length)
                }
                packet.clear()
                // There might be more incoming packets.
                idle = false
                // If we were sending, switch to receiving.
                if (timer > 0) {
                    timer = 0
                }
            }
            // If we are idle or waiting for the network, sleep for a
            // fraction of time to avoid busy looping.
            if (idle) {
                Thread.sleep(100)
                // Increase the timer. This is inaccurate but good enough,
                // since everything is operated in non-blocking mode.
                timer += if (timer > 0) 100 else -100
                // We are receiving for a long time but not sending.
                if (timer < -15000) {
                    // Send empty control messages.
                    packet.put(0.toByte()).limit(1)
                    for (i in 0..2) {
                        packet.position(0)
                        tunnel.write(packet)
                    }
                    packet.clear()
                    // Switch to sending.
                    timer = 1
                }
                // We are sending for a long time but not receiving.
                if (timer > 20000) {
                    throw IllegalStateException("Timed out")
                }
            }
            Thread.sleep(50)
        }
    }

    override fun handleMessage(message: Message?): Boolean {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show()
        }
        return true
    }

    fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    Log.i(TAG, "****** INET ADDRESS ******")
                    Log.i(TAG, "address: " + inetAddress.getHostAddress())
                    Log.i(TAG, "hostname: " + inetAddress.getHostName())
                    Log.i(TAG, "address.toString(): " + inetAddress.getHostAddress().toString())
                    if (!inetAddress.isLoopbackAddress()) {
                        //IPAddresses.setText(inetAddress.getHostAddress().toString());
                        Log.i(TAG, "IS NOT LOOPBACK ADDRESS: " + inetAddress.getHostAddress().toString())
                        return inetAddress.getHostAddress().toString()
                    } else {
                        Log.i(TAG, "It is a loopback address")
                    }
                }
            }
        } catch (ex: SocketException) {
            val LOG_TAG: String? = null
            Log.e(LOG_TAG, ex.toString())
        }

        return null
    }


    /*private var mThread: Thread? = null
    private var mInterface: ParcelFileDescriptor? = null
    // Configure a builder for the interface.
    internal var builder = Builder()
    private val TAG = "Ctrl+Wifi Service"

    private var mServerAddress: String? = null
    private var mServerPort: String? = null
    private val mConfigureIntent: PendingIntent? = null
    private var mHandler: Handler? = null
    private var mParameters: String? = null

    override fun onDestroy() {
        if (mThread != null) {
            mThread!!.interrupt()
        }
        super.onDestroy()
    }*/

    /*override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = Handler()
        }

        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            mThread!!.interrupt()
        }

        // Extract information from the intent.
        val prefix = packageName
        mServerAddress = intent.getStringExtra(prefix + ".ADDRESS")
        mServerPort = intent.getStringExtra(prefix + ".PORT")
        // Start a new session by creating a new thread.
        mThread = Thread(this, TAG)

        mThread!!.start()
        return START_STICKY
    }

    fun handleMessage(message: Message?): Boolean {
        if (message != null) {
            Toast.makeText(this, message!!.what, Toast.LENGTH_SHORT).show()
        }
        return true
    }

    @Synchronized fun run() {
        try {
            Log.i(TAG, "Starting")
            // If anything needs to be obtained using the network, get it now.
            // This greatly reduces the complexity of seamless handover, which
            // tries to recreate the tunnel without shutting down everything.
            // In this demo, all we need to know is the server address.
            val server = InetSocketAddress(
                    mServerAddress, Integer.parseInt(mServerPort))
            // We try to create the tunnel for several times. The better way
            // is to work with ConnectivityManager, such as trying only when
            // the network is avaiable. Here we just use a counter to keep
            // things simple.
            var attempt = 0
            while (attempt < 10) {
                mHandler!!.sendEmptyMessage(R.string.connecting)
                // Reset the counter if we were connected.
                if (run(server)) {
                    attempt = 0
                }
                // Sleep for a while. This also checks if we got interrupted.
                Thread.sleep(3000)
                ++attempt
            }
            Log.i(TAG, "Giving up")
        } catch (e: Exception) {
            Log.e(TAG, "Got " + e.toString())
        } finally {
            try {
                mInterface!!.close()
            } catch (e: Exception) {
                // ignore
            }

            mInterface = null
            mParameters = null
            mHandler!!.sendEmptyMessage(R.string.disconnected)
            Log.i(TAG, "Exiting")
        }
    }

    @Throws(Exception::class)
    private fun run(server: InetSocketAddress): Boolean {
        var tunnel: DatagramChannel? = null
        var connected = false
        try {
            // Create a DatagramChannel as the VPN tunnel.
            tunnel = DatagramChannel.open()
            // Protect the tunnel before connecting to avoid loopback.
            if (!protect(tunnel!!.socket())) {
                throw IllegalStateException("Cannot protect the tunnel")
            }
            // Connect to the server.
            tunnel.connect(server)
            // For simplicity, we use the same thread for both reading and
            // writing. Here we put the tunnel into non-blocking mode.
            tunnel.configureBlocking(false)
            // Authenticate and configure the virtual network interface.
//            handshake(tunnel)
            // Now we are connected. Set the flag and show the message.
            connected = true
            mHandler!!.sendEmptyMessage(R.string.connected)
            // Packets to be sent are queued in this input stream.
            val `in` = FileInputStream(mInterface!!.fileDescriptor)
            // Packets received need to be written to this output stream.
            val out = FileOutputStream(mInterface!!.fileDescriptor)
            // Allocate the buffer for a single packet.
            val packet = ByteBuffer.allocate(32767)
            // We use a timer to determine the status of the tunnel. It
            // works on both sides. A positive value means sending, and
            // any other means receiving. We start with receiving.
            var timer = 0
            // We keep forwarding packets till something goes wrong.
            while (true) {
                // Assume that we did not make any progress in this iteration.
                var idle = true
                // Read the outgoing packet from the input stream.
                var length = `in`.read(packet.array())
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    packet.limit(length)
                    tunnel.write(packet)
                    packet.clear()
                    // There might be more outgoing packets.
                    idle = false
                    // If we were receiving, switch to sending.
                    if (timer < 1) {
                        timer = 1
                    }
                }
                // Read the incoming packet from the tunnel.
                length = tunnel.read(packet)
                if (length > 0) {
                    // Ignore control messages, which start with zero.
                    if (packet.get(0) !== 0.toByte()) {
                        // Write the incoming packet to the output stream.
                        out.write(packet.array(), 0, length)
                    }
                    packet.clear()
                    // There might be more incoming packets.
                    idle = false
                    // If we were sending, switch to receiving.
                    if (timer > 0) {
                        timer = 0
                    }
                }
                // If we are idle or waiting for the network, sleep for a
                // fraction of time to avoid busy looping.
                if (idle) {
                    Thread.sleep(100)
                    // Increase the timer. This is inaccurate but good enough,
                    // since everything is operated in non-blocking mode.
                    timer += if (timer > 0) 100 else -100
                    // We are receiving for a long time but not sending.
                    if (timer < -15000) {
                        // Send empty control messages.
                        packet.put(0.toByte()).limit(1)
                        for (i in 0..2) {
                            packet.position(0)
                            tunnel.write(packet)
                        }
                        packet.clear()
                        // Switch to sending.
                        timer = 1
                    }
                    // We are sending for a long time but not receiving.
                    if (timer > 20000) {
                        throw IllegalStateException("Timed out")
                    }
                }
            }
        } catch (e: InterruptedException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Got " + e.toString())
        } finally {
            try {
                tunnel!!.close()
            } catch (e: Exception) {
                // ignore
            }

        }
        return connected
    }

    @Throws(Exception::class)
    private fun configure(parameters: String) {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null && parameters == mParameters) {
            Log.i(TAG, "Using the previous interface")
            return
        }
        // Configure a builder while parsing the parameters.
        val builder = Builder()
        for (parameter in parameters.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val fields = parameter.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            try {
                when (fields[0][0]) {
                    'm' -> builder.setMtu(java.lang.Short.parseShort(fields[1]).toInt())
                    'a' -> builder.addAddress(fields[1], Integer.parseInt(fields[2]))
                    'r' -> builder.addRoute(fields[1], Integer.parseInt(fields[2]))
                    'd' -> builder.addDnsServer(fields[1])
                    's' -> builder.addSearchDomain(fields[1])
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Bad parameter: " + parameter)
            }

        }
        // Close the old interface since the parameters have been changed.
        try {
            mInterface!!.close()
        } catch (e: Exception) {
            // ignore
        }

        // Create a new interface using the builder and save the parameters.
        mInterface = builder.setSession(mServerAddress).setConfigureIntent(mConfigureIntent).establish()
        mParameters = parameters
        Log.i(TAG, "New interface: " + parameters)
    }*/

    // Services interface
    /*override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = Handler()
        }
        // Stop the previous session by interrupting the thread.
        if (mThread != null) {
            (mThread as Thread).interrupt();
        }

        // Start a new session by creating a new thread.
        mThread = Thread(Runnable {
            try {
                // Configure the TUN and get the interface.
//                mInterface = builder.setSession(TAG).addAddress("192.168.1.2", 24).addRoute("0.0.0.0", 0).establish()

                configure()

                // Packets to be sent are queued in this input stream.
                val `in` = FileInputStream(mInterface!!.fileDescriptor)

                // Packets received need to be written to this output stream.
                val out = FileOutputStream(mInterface!!.fileDescriptor)

                // The UDP channel can be used to pass/get ip package to/from server
                val tunnel = DatagramChannel.open()

                // Allocate the buffer for a single packet.
                val packet = ByteBuffer.allocate(32767)

                // Connect to the server, localhost is used for demonstration only.
                tunnel.connect(InetSocketAddress("127.0.0.1", 8087))

                // Protect this socket, so package send by it will not be feedback to the vpn service.
                protect(tunnel.socket())

                // We use a timer to determine the status of the tunnel. It
                // works on both sides. A positive value means sending, and
                // any other means receiving. We start with receiving.
                var timer = 0

                // Use a loop to pass packets.
                while (true) {

                    var idle = true

                    // Read the outgoing packet from the input stream.
                    var length = `in`.read(packet.array())
                    Log.i(TAG, "************ packet length = $length")

                    if (length > 0) {
                        Log.i(TAG, "************new packet")
                        // Write the outgoing packet to the tunnel.
                        packet.limit(length)
                        tunnel.write(packet)
                        packet.clear()
                        // There might be more outgoing packets.
                        idle = false
                        // If we were receiving, switch to sending.
                        if (timer < 1) {
                            timer = 1
                        }
                    }
                    // Read the incoming packet from the tunnel.
                    length = tunnel.read(packet)
                    if (length > 0) {
                        // Ignore control messages, which start with zero.
                        if (packet.get(0) !== 0.toByte()) {
                            // Write the incoming packet to the output stream.
                            out.write(packet.array(), 0, length)
                        }
                        packet.clear()
                        // There might be more incoming packets.
                        idle = false
                        // If we were sending, switch to receiving.
                        if (timer > 0) {
                            timer = 0
                        }
                    }
                    // If we are idle or waiting for the network, sleep for a
                    // fraction of time to avoid busy looping.
                    if (idle) {
                        Thread.sleep(100)
                        // Increase the timer. This is inaccurate but good enough,
                        // since everything is operated in non-blocking mode.
                        timer += if (timer > 0) 100 else -100
                        // We are receiving for a long time but not sending.
                        if (timer < -15000) {
                            // Send empty control messages.
                            packet.put(0.toByte()).limit(1)
                            for (i in 0..2) {
                                packet.position(0)
                                tunnel.write(packet)
                            }
                            packet.clear()
                            // Switch to sending.
                            timer = 1
                        }
                        // We are sending for a long time but not receiving.
                        if (timer > 20000) {
                            throw IllegalStateException("Timed out")
                        }
                    }
                    Thread.sleep(100)
                }

            } catch (e: Exception) {
                // Catch any exception
                e.printStackTrace()
            } finally {
                try {
                    if (mInterface != null) {
                        mInterface!!.close()
                        mInterface = null
                    }
                } catch (e: Exception) {

                }

            }
        }, TAG)

        //start the service
        mThread!!.start()
        return START_STICKY
    }*/
}