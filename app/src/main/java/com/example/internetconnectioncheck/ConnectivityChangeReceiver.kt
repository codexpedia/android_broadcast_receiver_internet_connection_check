package com.example.internetconnectioncheck

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.widget.Toast
import java.net.URL

class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            checkConnectivity(context)
        }
    }


    private fun checkConnectivity(context: Context) {
        if (!isNetworkInterfaceAvailable(context)) {
            Toast.makeText(context, "You are OFFLINE!", Toast.LENGTH_SHORT).show()
            return
        }

        val handler = Handler()
        Thread(Runnable {
            val isConnected = isAbleToConnect("http://www.google.com", 1000)
            handler.post {
                if (isConnected)
                    Toast.makeText(context, "You are ONLINE!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "You are OFFLINE!", Toast.LENGTH_SHORT).show()
            }
        }).start()

    }

    //This only checks if the network interface is available, doesn't guarantee a particular network service is available, for example, there could be low signal or server downtime
    private fun isNetworkInterfaceAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    //This makes a real connection to an url and checks if you can connect to this url, this needs to be wrapped in a background thread
    private fun isAbleToConnect(url: String, timeout: Int): Boolean {
        try {
            val myUrl = URL(url)
            val connection = myUrl.openConnection()
            connection.connectTimeout = timeout
            connection.connect()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
}
