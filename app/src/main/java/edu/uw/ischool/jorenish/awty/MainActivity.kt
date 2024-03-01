package edu.uw.ischool.jorenish.awty

import android.Manifest
import android.telephony.SmsManager
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var message: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var mins: EditText
    private lateinit var startStopButton: Button
    private var handler: Handler = Handler()
    private var smsRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        message = findViewById(R.id.editTextMessage)
        phoneNumber = findViewById(R.id.editTextPhoneNumber)
        mins = findViewById(R.id.editTextMins)
        startStopButton = findViewById(R.id.startStopButton)

        startStopButton.setOnClickListener {
            if (startStopButton.text == getString(R.string.start)) {
                startMsgs()
            } else {
                stopMsgs()
            }
        }
    }

    private fun startMsgs() {
        val messageText = message.text.toString()
        val phoneNumberText = phoneNumber.text.toString()
        val intervalText = mins.text.toString()

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please input a message to text", Toast.LENGTH_SHORT).show()
            return
        }
        if (phoneNumberText.isEmpty()) {
            Toast.makeText(this, "Please input a phone number", Toast.LENGTH_SHORT).show()
            return
        }
        if (intervalText.isEmpty()) {
            Toast.makeText(this, "Please input how many minutes between texts", Toast.LENGTH_SHORT).show()
            return
        }

        val intervalValue = intervalText.toLongOrNull()
        if (intervalValue == null || intervalValue <= 0) {
            Toast.makeText(this, "Please enter a valid delay between texts", Toast.LENGTH_SHORT).show()
            return
        }

        smsRunnable = object : Runnable {
            override fun run() {
                sendSMS(phoneNumberText, messageText)
                handler.postDelayed(this, intervalValue * 60 * 1000) // Convert minutes to milliseconds
            }
        }
        handler.post(smsRunnable!!)
        startStopButton.text = getString(R.string.stop)
    }

    private fun stopMsgs() {
        smsRunnable?.let { handler.removeCallbacks(it) }
        startStopButton.text = getString(R.string.start)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "Message sent to $phoneNumber", Toast.LENGTH_SHORT).show()
            } else {
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS), PERMISSION_REQUEST_SEND_SMS)
            }
        } else {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "Message sent to $phoneNumber", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS permission granted. You can now send SMS messages.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "SMS permission denied. Message not sent.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_SEND_SMS = 123
    }

}