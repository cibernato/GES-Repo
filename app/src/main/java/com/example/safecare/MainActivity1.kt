package com.example.safecare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var start: Button? = null
    private var stop: Button? = null
    private var addContacts: Button? = null
    var lv: ListView? = null
    var edit: EditText? = null
    var provider: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Before Permission Check", "onCreate: ")

        //SMS and GPS Permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val PERMISSION_ALL = 1
            val PERMISSIONS =
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS)
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            //            return;
        }
        Log.d("After Permission Check", "onCreate:")
        start = findViewById<View>(R.id.start) as Button
        stop = findViewById<View>(R.id.stop) as Button
        lv = findViewById<View>(R.id.contacts) as ListView
        edit = findViewById<View>(R.id.editText) as EditText
        addContacts = findViewById<View>(R.id.add) as Button
        val arrayAdapter: ArrayAdapter<String>
        Toast.makeText(
            applicationContext,
            "Wear Your Helmet and Start Tracking",
            Toast.LENGTH_SHORT
        ).show()
        start!!.setOnClickListener {
            Log.d("Start Button", "Pressed")
            Toast.makeText(
                applicationContext,
                "Safe riding! We track you for safety",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(applicationContext, IService2::class.java)
            startService(intent)
        }
        stop!!.setOnClickListener {
            Log.d("Stop Button", "Pressed")
            val intent = Intent(applicationContext, IService2::class.java)
            stopService(intent)
        }
        val list = ArrayList<String>()
        addContacts!!.setOnClickListener {
            val text = edit!!.text.toString()
            StaticObjects.telefonos.add(text)
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_FINE = 2
    }
}