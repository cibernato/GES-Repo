package com.example.safecare

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.*

class IService2 : Service(), SensorEventListener {
    var handler = Handler(Looper.getMainLooper())
    private val mPeriodicEventHandler = Handler()
    private val PERIODIC_EVENT_TIMEOUT = 3000
    private val fuseTimer = Timer()
    private var sendCount = 0
    private var sentRecently = 'N'

    //Three Sensor Fusion - Variables:
    // angular speeds from gyro
    private val gyro = FloatArray(3)

    // rotation matrix from gyro data
    private var gyroMatrix = FloatArray(9)

    // orientation angles from gyro matrix
    private val gyroOrientation = FloatArray(3)

    // magnetic field vector
    private val magnet = FloatArray(3)

    // accelerometer vector
    private val accel = FloatArray(3)

    // orientation angles from accel and magnet
    private val accMagOrientation: FloatArray = FloatArray(3)

    // final orientation angles from sensor fusion
    private val fusedOrientation = FloatArray(3)

    // accelerometer and magnetometer based rotation matrix
    private val rotationMatrix = FloatArray(9)
    private var timestamp = 0f
    private var initState = true

    //Sensor Variables:
    private var senSensorManager: SensorManager? = null
    private val senProximity: Sensor? = null
    private val mSensorEvent: SensorEvent? = null

    //GPS
    var latitude = 0.0
    var longitude = 0.0
    var locationManager: LocationManager? = null
    var locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude
            longitude = location.longitude
            Log.d("latitude changed", "" + latitude)
            Log.d("longitude changed", "" + longitude)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //SMS Variables
    var smsManager = SmsManager.getDefault()
    var phoneNum: String? = ""
    var textMsg: String? = null
    private var prevNumber: String? = null
    private val x: Float? = null
    private val y: Float? = null
    private val z: Float? = null
    private val doPeriodicTask = Runnable {
        Log.d("Delay", "Delay Ended**********")
        Log.d("Updating flag", "run: ")
        sentRecently = 'N'
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("Initialing Service", "OnCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPeriodicEventHandler.removeCallbacks(doPeriodicTask)
        Log.d("Stopping Service", "OnDestroy")
        senSensorManager!!.unregisterListener(this)
        sendCount = 0
        Toast.makeText(this, "Stopped Tracking", Toast.LENGTH_SHORT).show()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            locationManager!!.removeUpdates(locationListener)
        }
    }

    override fun onStartCommand(intent: Intent, flag: Int, startId: Int): Int {
        Log.d("Starting work", "OnStart")


        // Acquire a reference to the system Location Manager
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        // Define a listener that responds to location updates


        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            handler.post {
                Toast.makeText(
                    this@IService2.applicationContext,
                    "No GPS Permission!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            500,
            0f,
            locationListener
        )
        val locationProvider = LocationManager.NETWORK_PROVIDER
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            handler.post {
                Toast.makeText(
                    this@IService2.applicationContext,
                    "No GPS Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        latitude = locationManager!!.getLastKnownLocation(locationProvider)?.latitude ?: 0.0
        longitude = locationManager!!.getLastKnownLocation(locationProvider)?.longitude ?: 0.0
        Log.d("latitude", "" + latitude)
        Log.d("longitude", "" + longitude)
        onTaskRemoved(intent)
        senSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val senAccelerometer = senSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        initListeners()
        fuseTimer.scheduleAtFixedRate(
            CalculateFusedOrientationTask(),
            1000, TIME_CONSTANT.toLong()
        )
        return START_STICKY
    }

    fun initListeners() {
        senSensorManager!!.registerListener(
            this,
            senSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        senSensorManager!!.registerListener(
            this,
            senSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        senSensorManager!!.registerListener(
            this,
            senSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val mySensor = sensorEvent.sensor
        when (sensorEvent.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // copy new accelerometer data into accel array
                // then calculate new orientation
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3)
                calculateAccMagOrientation()
            }
            Sensor.TYPE_GYROSCOPE ->                 // process gyro data
                gyroFunction(sensorEvent)
            Sensor.TYPE_MAGNETIC_FIELD ->                 // copy new magnetometer data into magnet array
                System.arraycopy(sensorEvent.values, 0, magnet, 0, 3)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
        //      Log.d("MY_APP", sensor.toString() + "-" + i);
    }

    fun calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation)
        }
    }

    private fun getRotationVectorFromGyro(
        gyroValues: FloatArray,
        deltaRotationVector: FloatArray,
        timeFactor: Float
    ) {
        val normValues = FloatArray(3)

        // Calculate the angular speed of the sample
        val omegaMagnitude = Math.sqrt(
            (gyroValues[0] * gyroValues[0] + gyroValues[1] * gyroValues[1] + gyroValues[2] * gyroValues[2]).toDouble()
        ).toFloat()

        // Normalize the rotation vector if it's big enough to get the axis
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude
            normValues[1] = gyroValues[1] / omegaMagnitude
            normValues[2] = gyroValues[2] / omegaMagnitude
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        val thetaOverTwo = omegaMagnitude * timeFactor
        val sinThetaOverTwo = Math.sin(thetaOverTwo.toDouble()).toFloat()
        val cosThetaOverTwo = Math.cos(thetaOverTwo.toDouble()).toFloat()
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0]
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1]
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2]
        deltaRotationVector[3] = cosThetaOverTwo
    }

    fun gyroFunction(event: SensorEvent) {
        // don't start until first accelerometer/magnetometer orientation has been acquired
        if (accMagOrientation == null) return

        // initialisation of the gyroscope based rotation matrix
        if (initState) {
            val initMatrix: FloatArray
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation)
            val test = FloatArray(3)
            SensorManager.getOrientation(initMatrix, test)
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix)
            initState = false
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        val deltaVector = FloatArray(4)
        if (timestamp != 0f) {
            val dT = (event.timestamp - timestamp) * NS2S
            System.arraycopy(event.values, 0, gyro, 0, 3)
            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f)
        }

        // measurement done, save current time for next interval
        timestamp = event.timestamp.toFloat()

        // convert rotation vector into rotation matrix
        val deltaMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector)

        // apply the new rotation interval on the gyroscope based rotation matrix
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix)

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrix, gyroOrientation)
    }

    private fun getRotationMatrixFromOrientation(o: FloatArray): FloatArray {
        val xM = FloatArray(9)
        val yM = FloatArray(9)
        val zM = FloatArray(9)
        val sinX = Math.sin(o[1].toDouble()).toFloat()
        val cosX = Math.cos(o[1].toDouble()).toFloat()
        val sinY = Math.sin(o[2].toDouble()).toFloat()
        val cosY = Math.cos(o[2].toDouble()).toFloat()
        val sinZ = Math.sin(o[0].toDouble()).toFloat()
        val cosZ = Math.cos(o[0].toDouble()).toFloat()

        // rotation about x-axis (pitch)
        xM[0] = 1.0f
        xM[1] = 0.0f
        xM[2] = 0.0f
        xM[3] = 0.0f
        xM[4] = cosX
        xM[5] = sinX
        xM[6] = 0.0f
        xM[7] = -sinX
        xM[8] = cosX

        // rotation about y-axis (roll)
        yM[0] = cosY
        yM[1] = 0.0f
        yM[2] = sinY
        yM[3] = 0.0f
        yM[4] = 1.0f
        yM[5] = 0.0f
        yM[6] = -sinY
        yM[7] = 0.0f
        yM[8] = cosY

        // rotation about z-axis (azimuth)
        zM[0] = cosZ
        zM[1] = sinZ
        zM[2] = 0.0f
        zM[3] = -sinZ
        zM[4] = cosZ
        zM[5] = 0.0f
        zM[6] = 0.0f
        zM[7] = 0.0f
        zM[8] = 1.0f

        // rotation order is y, x, z (roll, pitch, azimuth)
        var resultMatrix = matrixMultiplication(xM, yM)
        resultMatrix = matrixMultiplication(zM, resultMatrix)
        return resultMatrix
    }

    private fun matrixMultiplication(A: FloatArray, B: FloatArray): FloatArray {
        val result = FloatArray(9)
        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6]
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7]
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8]
        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6]
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7]
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8]
        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6]
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7]
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8]
        return result
    }

    internal inner class CalculateFusedOrientationTask : TimerTask() {
        override fun run() {
            val oneMinusCoeff = 1.0f - FILTER_COEFFICIENT
            fusedOrientation[0] = (FILTER_COEFFICIENT * gyroOrientation[0]
                    + oneMinusCoeff * accMagOrientation!![0])
            //            Log.d("X:", ""+fusedOrientation[0]);
            fusedOrientation[1] = (FILTER_COEFFICIENT * gyroOrientation[1]
                    + oneMinusCoeff * accMagOrientation[1])
            //            Log.d("Y:", ""+fusedOrientation[1]);
            fusedOrientation[2] = (FILTER_COEFFICIENT * gyroOrientation[2]
                    + oneMinusCoeff * accMagOrientation[2])
            //            Log.d("Z:", ""+fusedOrientation[2]);

            //**********Sensing Danger**********
            val SMV =
                Math.sqrt((accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]).toDouble())
            //                Log.d("SMV:", ""+SMV);
            if (SMV > 25) {
                if (sentRecently == 'N') {
                    Log.d("Accelerometer vector:", "" + SMV)
                    var degreeFloat = (fusedOrientation[1] * 180 / Math.PI).toFloat()
                    var degreeFloat2 = (fusedOrientation[2] * 180 / Math.PI).toFloat()
                    if (degreeFloat < 0) degreeFloat = degreeFloat * -1
                    if (degreeFloat2 < 0) degreeFloat2 = degreeFloat2 * -1
                    //                    Log.d("Degrees:", "" + degreeFloat);
                    if (degreeFloat > 30 || degreeFloat2 > 30) {
                        Log.d("Degree1:", "" + degreeFloat)
                        Log.d("Degree2:", "" + degreeFloat2)
                        handler.post {
                            Toast.makeText(
                                this@IService2.applicationContext,
                                "Sensed Danger! Sending SMS",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //                    Toast.makeText(getApplicationContext(), "Sensed Danger! Sending SMS", Toast.LENGTH_SHORT).show();
//                        val itemIds = ArrayList<String>()
//                        itemIds.add("991199370")
//                        itemIds.add("994833351")
//                        itemIds.add("991199370")
//                        itemIds.add("994833351")
                        for (itemId in StaticObjects.telefonos) {
//                        if (sendCount < 5) {
//                                textMsg = "Sensed Danger here => "+"http://maps.google.com/?q=<"+latitude+">,<"+longitude+">";
                            phoneNum = itemId
                            if (phoneNum != prevNumber && phoneNum != null) {
                                textMsg =
                                    "Lugar del suceso: \n https://www.google.com/maps/@$latitude,$longitude,14z"
//                                    "Sensed Danger here => http://maps.google.com/?q=<$latitude>,<$longitude>"
                                Log.d("Sending-MSG", "onSensorChanged: $sendCount")
                                smsManager.sendTextMessage(phoneNum, null, textMsg, null, null)
                                prevNumber = phoneNum
                                sendCount++
                            }
                            //                        }
                        }
                    } else {
                        handler.post {
                            Toast.makeText(
                                this@IService2.applicationContext,
                                "Sudden Movement! But looks safe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        sendCount++
                    }
                    sentRecently = 'Y'
                    Log.d("Delay", "Delay Start**********")
                    mPeriodicEventHandler.postDelayed(
                        doPeriodicTask,
                        PERIODIC_EVENT_TIMEOUT.toLong()
                    )
                }
            }
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation)
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3)
        }
    }

    companion object {
        const val EPSILON = 0.000000001f
        const val TIME_CONSTANT = 30
        const val FILTER_COEFFICIENT = 0.98f
        private const val NS2S = 1.0f / 1000000000.0f
        private const val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    }
}