package com.example.viva_sample.ble

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.orhanobut.logger.Logger
import java.util.*
import kotlin.collections.ArrayList

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
class BluetoothLeService : Service() {
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED
    private val photoByteList: ArrayList<Int> = arrayListOf()
    //private var isStream = true
    var org: ByteArray? = null

    private val mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)

            Logger.d("## onMtuChanged ==> ${BluetoothGatt.GATT_SUCCESS}")
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Logger.d("## onConnectionStateChange")
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Logger.d("## Connected to GATT server.")
                Log.i(
                    TAG, "Attempting to start service discovery:" +
                            mBluetoothGatt!!.discoverServices()
                )
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Logger.d("## onServicesDiscovered")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.requestMtu(259)
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            } else {
                Log.w(
                    TAG,
                    "onServicesDiscovered received: $status"
                )
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            //gatt.requestMtu(256)
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    private fun broadcastUpdate(action: String) {
        Logger.d("## broadcastUpdate1")
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        //Logger.d("## broadcastUpdate2")
        val intent = Intent(action)

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (UUID.fromString(SampleGattAttributes.HM_TX) == characteristic.uuid && isStream) {
        //if (UUID.fromString(SampleGattAttributes.HM_TX) == characteristic.uuid) {
            val flag = characteristic.properties
            var format = -1
            if (flag and 0x01 != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
//                Log.d(TAG, "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8

                val currenctList : ArrayList<Int> = arrayListOf()
                // 20개씩 전부 리스트에 담는다.
                for (i in characteristic.value.indices) {
                    characteristic.getIntValue(format, i)
                    photoByteList.add(characteristic.getIntValue(format, i))
                    currenctList.add(characteristic.getIntValue(format, i))
                }
                org = mergeByteData(org, characteristic.value)
                Logger.d("## org ==> ${org?.size}")
                Logger.d("## currenctList.size ==> ${currenctList?.size}")


                // 스탑코드 자르고 넣을 변수
//                var slice: ByteArray? = null

                val lastValue = 217 // -39
                val secondLastValue = 255 // -1


//                if (photoByteList[photoByteList.lastIndex] == 217 && photoByteList[photoByteList.lastIndex - 1] == 255 && characteristic.value.size < 20) {
                if (org?.let { org!![it.lastIndex] } == lastValue.toByte() && org?.let { org!![it.lastIndex - 1] } == secondLastValue.toByte()) {
                    Logger.d("## 마지막")
                    Logger.d("## photoByteList ==> ${photoByteList.size}")
                    Logger.d("## photoByteList ==> ${photoByteList[photoByteList.lastIndex]}")
                    Logger.d("## photoByteList ==> ${photoByteList[photoByteList.lastIndex - 1]}")
                    Logger.d("## org ==> ${org?.let { org!!.get(it.lastIndex) }}")
                    Logger.d("## org ==> ${org?.get(org!!.lastIndex - 1)}")

//                    isStream = false

                    //slice = org?.lastIndex?.minus(1)?.let { Arrays.copyOfRange(org, 0, it) }!!
                    val slice: ByteArray? = org?.let {
                        Logger.d("## slice ==> ${it[it.lastIndex - 2]}")
                        Arrays.copyOfRange(org, 0, it.lastIndex - 1)
                    }
                    Logger.d("## slice ==> ${slice?.get(slice.lastIndex)}")

                    intent.putExtra(EXTRA_DATA, slice)
                    sendBroadcast(intent)

                }

//                if (!isStream) {
//                    intent.putExtra(EXTRA_DATA, slice)
//                    sendBroadcast(intent)
//                }
            }


        //}
//        else {
//            // For all other profiles, writes the data formatted in HEX.
//            val data = characteristic.value
//            if (data != null && data.size > 0) {
//                val stringBuilder = StringBuilder(data.size)
//                for (byteChar in data) stringBuilder.append(String.format("%02X ", byteChar))
//                intent.putExtra(EXTRA_DATA, """${String(data)}$stringBuilder""".trimIndent())
//            }
//        }

    }

    // 바이트어레이 합치
    // 20개씪 들어오는걸 기존거와 계속 합친다 마지막 신호가 오기전까지..
    // 다 합쳐졌을경우 바이트어레이를 액티비티에 넘기고
    fun mergeByteData(src: ByteArray?, obj: ByteArray?): ByteArray? {
        if (src == null || src.size < 0) return obj
        if (obj == null || obj.size < 0) return src
        val data = ByteArray(src.size + obj.size)
        System.arraycopy(src, 0, data, 0, src.size)
        System.arraycopy(obj, 0, data, src.size, obj.size)
        return data
    }

    inner class LocalBinder : Binder() {
        val service: BluetoothLeService
            get() = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder {
        Logger.d("## onBind")
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close()
        return super.onUnbind(intent)
    }

    private val mBinder: IBinder = LocalBinder()

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    fun initialize(): Boolean {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.")
                return false
            }
        }
        mBluetoothAdapter = mBluetoothManager!!.adapter
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            return if (mBluetoothGatt!!.connect()) {
                mConnectionState = STATE_CONNECTING
                true
            } else {
                false
            }
        }
        val device = mBluetoothAdapter!!.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        mBluetoothDeviceAddress = address
        mConnectionState = STATE_CONNECTING
        return true
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * `BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)`
     * callback.
     */
    fun disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.disconnect()
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    fun close() {
        if (mBluetoothGatt == null) {
            return
        }
        mBluetoothGatt!!.close()
        mBluetoothGatt = null
    }

    /**
     * Request a read on a given `BluetoothGattCharacteristic`. The read result is reported
     * asynchronously through the `BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)`
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        Logger.d("## readCharacteristic")
        mBluetoothGatt!!.readCharacteristic(characteristic)
    }

    /**
     * Write to a given char
     * @param characteristic The characteristic to write to
     */
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?) {
        // int to uINT8
        Log.d(TAG, "BluetoothLeService:writeCharacteristic")
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.writeCharacteristic(characteristic)
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        Logger.d("## setCharacteristicNotification")
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        mBluetoothGatt!!.setCharacteristicNotification(characteristic, enabled)

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT == characteristic.uuid) {
            val descriptor = characteristic.getDescriptor(
                UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)
            )
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            mBluetoothGatt!!.writeDescriptor(descriptor)
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after `BluetoothGatt#discoverServices()` completes successfully.
     *
     * @return A `List` of supported services.
     */
    val supportedGattServices: List<BluetoothGattService>?
        get() = if (mBluetoothGatt == null) null else mBluetoothGatt!!.services

    companion object {
        private val TAG = BluetoothLeService::class.java.simpleName
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"
        val UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT)
        val UUID_HM_RX_TX = UUID.fromString(SampleGattAttributes.HM_RX_TX)

        val UUID_HM_RX = UUID.fromString(SampleGattAttributes.HM_RX)
        val UUID_HM_TX = UUID.fromString(SampleGattAttributes.HM_TX)
    }
}

// mtu 응답 갯수
// rx 특성2개 노티피랑 리드