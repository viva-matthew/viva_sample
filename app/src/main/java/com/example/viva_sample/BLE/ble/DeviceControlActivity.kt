package com.example.viva_sample.BLE.bluetoothlegatt

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
import java.util.*


class DeviceControlActivity : Activity() {

    val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
    val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"

    private var mConnectionState: TextView? = null
    private var mDataField: TextView? = null
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private var mGattServicesList: ExpandableListView? = null
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mEtSendText: EditText? = null
    private var mBtnSend: Button? = null
    private var mSeekbar1: SeekBar? = null


    private var mGattCharacteristics = ArrayList<ArrayList<BluetoothGattCharacteristic>>()
    private var mConnected = false
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null

    private var characteristicTX: BluetoothGattCharacteristic? = null
    private var characteristicRX: BluetoothGattCharacteristic? = null

    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"

    private val binding: ActivityDeviceControlBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_device_control) }


    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService?.initialize()!!) {
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService?.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
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
                mConnected = true
                updateConnectionState(R.string.connected)
                invalidateOptionsMenu()
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false
                updateConnectionState(R.string.disconnected)
                invalidateOptionsMenu()
                clearUI()
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService?.supportedGattServices)
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                Logger.d("## action ==> ${action}")

                Logger.d("## read ==> ${intent.getIntegerArrayListExtra(BluetoothLeService.EXTRA_DATA)}")
                Logger.d("## read.size ==> ${intent.getIntegerArrayListExtra(BluetoothLeService.EXTRA_DATA).size}")
                //Logger.d("## read ==> ${Integer.parseUnsignedInt(intent.getStringExtra(BluetoothLeService.EXTRA_DATA), 8)}")

//                val bytes =
//                    ConstantFunction.encodeToBase64(mBitmapArray.get(i).getUploadImageBitmap(), Bitmap.CompressFormat.JPEG, 100)
//                ImageData



                intent.getIntegerArrayListExtra(BluetoothLeService.EXTRA_DATA).let {
                    //binding.ivBleImage.setImageBitmap(toByteArray().toBitmap())
                }
                intent.getStringExtra(BluetoothLeService.EXTRA_DATA).let {
                    //binding.ivBleImage.setImageBitmap(toByteArray().toBitmap())

                }


                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
            }
        }
    }

    private fun clearUI() {
        mGattServicesList!!.setAdapter(null as SimpleExpandableListAdapter?)
        mDataField!!.setText(R.string.no_data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        val intent = intent
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME)
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)


        // Sets up UI references.
        (findViewById<View>(R.id.device_address) as TextView).text = mDeviceAddress
        mGattServicesList = findViewById<View>(R.id.gatt_services_list) as ExpandableListView
        mGattServicesList!!.setOnChildClickListener(servicesListClickListner)
        mConnectionState = findViewById<View>(R.id.connection_state) as TextView
        mDataField = findViewById<View>(R.id.data_value) as TextView

        mBtnSend = findViewById<View>(R.id.btnSend) as Button
        mEtSendText = findViewById<View>(R.id.etSendText) as EditText

        actionBar.title = mDeviceName
        actionBar.setDisplayHomeAsUpEnabled(true)

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)

        mBtnSend!!.setOnClickListener {
            //sendDataToBLE("read\n")
            sendCommandToBLE("read\n")
        }

        mSeekbar1 = findViewById<View>(R.id.seekbar1) as SeekBar
        mSeekbar1!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                sendDataToBLE(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    fun sendDataToBLE(str: Int) {
        if (mConnected) {
            characteristicTX?.setValue(str, BluetoothGattCharacteristic.FORMAT_UINT8, 0)

            mBluetoothLeService!!.writeCharacteristic(characteristicTX)
            characteristicRX?.let { mBluetoothLeService!!.setCharacteristicNotification(it, true) }


        }
    }

    fun sendCommandToBLE(str: String) {
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


    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread { mConnectionState!!.setText(resourceId) }
    }

    private fun displayData(data: String?) {
        if (data != null) {
            mDataField!!.text = data
        }
    }

    private fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = resources.getString(R.string.unknown_service)
        val unknownCharaString = resources.getString(R.string.unknown_characteristic)
        val gattServiceData = ArrayList<HashMap<String, String?>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String?>>>()
        mGattCharacteristics = ArrayList()

//        for (gattService in gattServices) {
//            val currentServiceData = HashMap<String, String>()
//            uuid = gattService.uuid.toString()
//            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
//
//            // If the service exists for HM 10 Serial, say so.
//            if (SampleGattAttributes.lookup(uuid, unknownServiceString) === "HM 10 Serial") {
//                //isSerial.setText("Yes")
//            } else {
//                //isSerial.setText("No")
//            }
//            currentServiceData[LIST_UUID] = uuid
//            //gattServiceData.add(currentServiceData)
//
//            // get characteristic when UUID matches RX/TX UUID
//            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)
//            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)
//        }

        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String?>()
            uuid = gattService.uuid.toString()
            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            currentServiceData[LIST_UUID] = uuid
            gattServiceData.add(currentServiceData)
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX)
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
        mGattServicesList!!.setAdapter(gattServiceAdapter)
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }

    // extension function to convert byte array to bitmap
    fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }

}