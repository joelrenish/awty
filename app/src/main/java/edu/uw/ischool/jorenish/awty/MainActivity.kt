package edu.uw.ischool.jorenish.awty

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
    private var toastRunnable: Runnable? = null

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

        toastRunnable = object : Runnable {
            override fun run() {
                Toast.makeText(this@MainActivity, messageText, Toast.LENGTH_SHORT).show()
                handler.postDelayed(this, intervalValue * 60 * 1000) // Convert minutes to milliseconds
            }
        }
        handler.post(toastRunnable!!)

        startStopButton.text = getString(R.string.stop)
    }

    private fun stopMsgs() {

        toastRunnable?.let { handler.removeCallbacks(it) }

        startStopButton.text = getString(R.string.start)
    }

}