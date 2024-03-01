package edu.uw.ischool.jorenish.awty

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import java.util.*

class MessageService : Service() {

    private var timer: Timer? = null
    private var isServiceRunning = false
    private var interval: Long = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("message") && intent.hasExtra("phoneNumber") && intent.hasExtra("interval")) {
            val message = intent.getStringExtra("message")
            val phoneNumber = intent.getStringExtra("phoneNumber")
            interval = intent.getLongExtra("interval", 1)

            startService(message, phoneNumber, interval)
        } else {
            stopSelf()
        }
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private fun startService(message: String?, phoneNumber: String?, interval: Long) {
        if (!isServiceRunning) {
            this.interval = interval
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val toastMessage = "$phoneNumber: $message"
                    showToast(toastMessage)
                }
            }, 0, interval)
            isServiceRunning = true
        }
    }

    private fun stopService() {
        if (isServiceRunning) {
            timer?.cancel()
            isServiceRunning = false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
