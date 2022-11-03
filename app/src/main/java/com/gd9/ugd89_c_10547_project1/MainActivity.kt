package com.gd9.ugd89_c_10547_project1

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.Exception

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private var camInfo= Camera.CameraInfo.CAMERA_FACING_BACK
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager

    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            //
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0f) {
                    if (camInfo == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        camInfo = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    } else {
                        camInfo = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    if (mCameraView != null) {
                        mCamera?.stopPreview();
                    }
                    mCamera?.release();
                    try {
                        mCamera = Camera.open(camInfo)
                    } catch (e: Exception) {
                        Log.d("Error", "Failed to get Camera" + e.message)
                    }
                    if (mCamera != null) {
                        mCameraView = CameraView(applicationContext, mCamera!!)
                        val camView = findViewById<View>(R.id.FLCamera) as FrameLayout
                        camView.addView(mCameraView)

                    }
                }
            }
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

                // Sides = Tilting phone left(10) and right(-10)
                val sides = event.values[0]

                // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
                val upDown = event.values[1]

                // Changes the colour of the square if it's completely flat
                if (upDown.toInt() != 0 && sides.toInt() != 0) {
                    sendNotification()
                }
            }
        }
    }

    private fun setUpSensorStuff() {
        // Create the sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Specify the sensor you want to listen to
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Checks for the sensor we have registered
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //Log.d("Main", "onSensorChanged: sides ${event.values[0]} front/back ${event.values[1]} ")

            // Sides = Tilting phone left(10) and right(-10)
            val sides = event.values[0]

            // Up/Down = Tilting phone up(10), flat (0), upside-down(-10)
            val upDown = event.values[1]


            // Changes the colour of the square if it's completely flat
            if (upDown.toInt() > 2 || sides.toInt() > 2){sendNotification()}
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Modul89_A_10547"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Modul89_C_10547")
            .setContentText("Hape Anda Goyang~~")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        setUpSensorStuff()
        createNotificationChannel()
        setContentView(R.layout.activity_main)
        try {
            mCamera = Camera.open(0)
        }catch (e: Exception){
            Log.d("Error", "Failed to get camera" + e.message)
        }
        if (mCamera!=null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }

        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // on below line we are registering
            // our sensor with sensor manager
            sensorManager.registerListener(proximitySensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        @SuppressLint("MissingInflatedId", "LocalSuppress")
        val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view: View? -> System.exit(0)}
    }
}