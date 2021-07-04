package com.example.viva_sample.BLE.ble

import android.Manifest
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.viva_sample.R

import com.orhanobut.logger.Logger

import java.util.*

class DeviceScanActivity : ListActivity() {

    private lateinit var mBluetoothAdapter: BluetoothAdapter // 스캔 리스트 어댑터
    private lateinit var mLeDeviceListAdapter: LeDeviceListAdapter // 블루투스 어댑터
    private var mScanning: Boolean = false // 스캔 확인 값
    private var mHandler: Handler? = null

    private val MY_PERMISSIONS_REQUEST_LOCATION = 0

    private val REQUEST_ENABLE_BT = 1
    private val SCAN_PERIOD = 20000L // 스캔 주기


    
    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("## onCreate")
        super.onCreate(savedInstanceState)


        actionBar?.setTitle(R.string.title_devices)
        val bar = actionBar
        bar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#3F51B5")))
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !== PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )

            }
        }


        mHandler = Handler()

        // 블루투스가 BLE를 지원하는지 검사
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }


        // 블루투스 어댑터 초기화
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // 블루투스를 지원하는지 검사
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

    }

    // 스캔 시작,중지 버튼이 있는 액션바 메소드
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).isVisible = false
            menu.findItem(R.id.menu_scan).isVisible = true
            menu.findItem(R.id.menu_refresh).actionView = null
        } else {
            menu.findItem(R.id.menu_stop).isVisible = true
            menu.findItem(R.id.menu_scan).isVisible = false
            menu.findItem(R.id.menu_refresh).setActionView(
                R.layout.actionbar_indeterminate_progress
            )
        }
        return true
    }

    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_scan -> {
                mLeDeviceListAdapter.clear()
                scanLeDevice(true)
            }
            R.id.menu_stop -> scanLeDevice(false)
        }
        return true
    }


    
    override fun onResume() {
        super.onResume()

        if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = LeDeviceListAdapter()
        setListAdapter(mLeDeviceListAdapter)
        //binding.rvBookmark.adapter = mLeDeviceListAdapter
        scanLeDevice(true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
            finish()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    
    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
        mLeDeviceListAdapter.clear()
    }

    
    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val device: BluetoothDevice = mLeDeviceListAdapter.getDevice(position)
        val intent = Intent(this, DeviceControlActivity::class.java)
        val deviceControlActivity = DeviceControlActivity()
        intent.putExtra(deviceControlActivity.EXTRAS_DEVICE_NAME, device.name)
        intent.putExtra(deviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.address)
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
            mScanning = false
        }
        startActivity(intent)
    }


    // 스캔 실행 빛 분기 메소드
    
    private fun scanLeDevice(enable: Boolean) {
        if (enable) {
            // 스캔 주기가 지나면 스캔을 중단함
            mHandler!!.postDelayed({
                mScanning = false
                mBluetoothAdapter.stopLeScan(mLeScanCallback)
                invalidateOptionsMenu() // onCreateOptionsMenu 메소드 재 호출
            }, SCAN_PERIOD)
            // 스캔 시작
            mScanning = true
            mBluetoothAdapter.startLeScan(mLeScanCallback)
        } else {
            // 스캔 정지
            mScanning = false
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }
        invalidateOptionsMenu()
    }

    private inner class LeDeviceListAdapter : BaseAdapter() {
        private val mLeDevices: ArrayList<BluetoothDevice> = ArrayList()

        fun addDevice(device: BluetoothDevice) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device)
            }
        }

        fun getDevice(position: Int): BluetoothDevice {
            return mLeDevices[position]
        }

        fun clear() {
            mLeDevices.clear()
        }

        override fun getCount(): Int {
            return mLeDevices.size
        }

        override fun getItem(i: Int): Any {
            return mLeDevices[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
            var view = view
            val viewHolder: ViewHolder

            if (view == null) {
                view =
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.item_device, null)
                viewHolder = ViewHolder()
                viewHolder.deviceAddress = view.findViewById<View>(R.id.device_address) as TextView
                viewHolder.deviceName = view.findViewById<View>(R.id.device_name) as TextView
                view.tag = viewHolder
            } else {
                viewHolder = view.tag as ViewHolder
            }
            val device = mLeDevices[i]
            val deviceName = device.name
            if (deviceName != null && deviceName.length > 0) viewHolder.deviceName?.setText(
                deviceName
            ) else viewHolder.deviceName?.setText(R.string.unknown_device)
            viewHolder.deviceAddress?.setText(device.address)
            return view
        }
    }


    // scanLeDevice 함수가 끝나고 스캔하여 결과를 처리하는 콜백함수
    private val mLeScanCallback =
        LeScanCallback { device, rssi, scanRecord ->
            runOnUiThread {
                mLeDeviceListAdapter.addDevice(device)
                mLeDeviceListAdapter.notifyDataSetChanged()
            }
        }


    internal class ViewHolder {
        lateinit var deviceName: TextView
        lateinit var deviceAddress: TextView
    }

}

