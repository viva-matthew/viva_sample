package com.example.viva_sample.BLE.ble

import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.View
import android.widget.*
import android.widget.ExpandableListView.OnChildClickListener
import androidx.databinding.DataBindingUtil
import com.orhanobut.logger.Logger
import xyz.arpith.blearduino.R
import xyz.arpith.blearduino.databinding.ActivityDeviceControlBinding
import java.nio.Buffer
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList


class DeviceControlActivity : Activity() {

    val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
    val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"

    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"

    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null

    private var mBluetoothLeService: BluetoothLeService? = null
    private var mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    private var mConnected = false

    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var characteristicTX: BluetoothGattCharacteristic? = null
    private var characteristicRX: BluetoothGattCharacteristic? = null

    private val binding: ActivityDeviceControlBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_device_control) }


    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            Logger.d("## 서비스 연결")
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service

            if (!mBluetoothLeService?.initialize()!!) {
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService?.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Logger.d("## 서비스 끊어짐")
            mBluetoothLeService = null
        }
    }

    private val servicesListClickListner =
        OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            if (mGattCharacteristics != null) {
                val characteristic = mGattCharacteristics[groupPosition][childPosition]
                val charaProp = characteristic.properties
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_READ > 0) {
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService?.setCharacteristicNotification(
                            mNotifyCharacteristic!!, false
                        )
                        mNotifyCharacteristic = null
                    }
                    mBluetoothLeService?.readCharacteristic(characteristic)
                }
                if (charaProp or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    mNotifyCharacteristic = characteristic
                    mBluetoothLeService?.setCharacteristicNotification(
                        characteristic, true
                    )
                }
                return@OnChildClickListener true
            }
            false
        }


    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Logger.d("## onReceive")
            val action = intent.action
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Logger.d("## ACTION_GATT_CONNECTED")
                mConnected = true
                updateConnectionState(R.string.connected)
                invalidateOptionsMenu()
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Logger.d("## ACTION_GATT_DISCONNECTED")
                mConnected = false
                updateConnectionState(R.string.disconnected)
                invalidateOptionsMenu()
                clearUI()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Logger.d("## ACTION_GATT_SERVICES_DISCOVERED")
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService?.supportedGattServices)
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                Logger.d("## action ==> ${action}")
                Logger.d("## read ==> ${intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA)?.size}")

                binding.ivBleImage.setImageBitmap(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA)?.let { byteArrayToBitmap(it) })
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        val intent = intent
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME)
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)


        // Sets up UI references.
        binding.deviceAddress.text = mDeviceAddress
        binding.gattServicesList.setOnChildClickListener(servicesListClickListner)
        actionBar?.title = mDeviceName
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)

        binding.btnSend.setOnClickListener {
            sendCommandToBLE("read\n")
        }

        binding.seekbar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                //sendDataToBLE(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private fun clearUI() {
        binding.gattServicesList.setAdapter(null as SimpleExpandableListAdapter?)
        binding.dataValue.setText(R.string.no_data)
    }

    private fun sendDataToBLE(str: Int) {
        if (mConnected) {
            characteristicTX?.setValue(str, BluetoothGattCharacteristic.FORMAT_UINT8, 0)

            mBluetoothLeService!!.writeCharacteristic(characteristicTX)
            characteristicRX?.let { mBluetoothLeService!!.setCharacteristicNotification(it, true) }


        }
    }

    private fun sendCommandToBLE(str: String) {
        Logger.d("## sendCommandToBLE")
        characteristicTX?.setValue(str)
        mBluetoothLeService!!.writeCharacteristic(characteristicTX)
        characteristicRX?.let { mBluetoothLeService!!.setCharacteristicNotification(it, true) }

    }


    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            val result: Boolean = mBluetoothLeService!!.connect(mDeviceAddress)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread {
            binding.connectionState.setText(resourceId)
        }
    }

    private fun displayData(data: String?) {
        if (data != null) {
            binding.dataValue.text = data
        }
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        Logger.d("## displayGattServices")
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.unknown_service)
        val unknownCharaString = resources.getString(R.string.unknown_characteristic)
        val gattServiceData = ArrayList<HashMap<String, String?>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String?>>>()
        mGattCharacteristics = ArrayList()

        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String?>()
            uuid = gattService.uuid.toString()
            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            currentServiceData[LIST_UUID] = uuid
            gattServiceData.add(currentServiceData)

            // HM-10
            //characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)
            //characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)

            // ESP
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX) // 요청
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_TX) // 응답

            val gattCharacteristicGroupData = ArrayList<HashMap<String, String?>>()
            val gattCharacteristics = gattService.characteristics
            val charas = ArrayList<BluetoothGattCharacteristic>()

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                charas.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String?>()
                uuid = gattCharacteristic.uuid.toString()
                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
            }
            mGattCharacteristics.add(charas)
            gattCharacteristicData.add(gattCharacteristicGroupData)


        }
        val gattServiceAdapter = SimpleExpandableListAdapter(
            this,
            gattServiceData,
            android.R.layout.simple_expandable_list_item_2,
            arrayOf(LIST_NAME, LIST_UUID),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            gattCharacteristicData,
            android.R.layout.simple_expandable_list_item_2,
            arrayOf(LIST_NAME, LIST_UUID),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        binding.gattServicesList.setAdapter(gattServiceAdapter)
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }


    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        Logger.d("## byteArray.size ==> ${byteArray.size}")
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gatt_services, menu)
        if (mConnected) {
            menu.findItem(R.id.menu_connect).isVisible = false
            menu.findItem(R.id.menu_disconnect).isVisible = true
        } else {
            menu.findItem(R.id.menu_connect).isVisible = true
            menu.findItem(R.id.menu_disconnect).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_connect -> {
                mBluetoothLeService?.connect(mDeviceAddress)
                return true
            }
            R.id.menu_disconnect -> {
                mBluetoothLeService?.disconnect()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // png로 변경 참고 사이트
// https://stackoverflow.com/questions/20656649/how-to-convert-bitmap-to-png-and-then-to-base64-in-android
    // bitmap to string 등등
    //https://youngest-programming.tistory.com/95
}