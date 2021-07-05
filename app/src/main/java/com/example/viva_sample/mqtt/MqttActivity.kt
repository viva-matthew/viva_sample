package com.example.viva_sample.mqtt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityMqttBinding
import com.orhanobut.logger.Logger
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class MqttActivity : AppCompatActivity() {
    private val binding: ActivityMqttBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_mqtt) }

    //private val mqttClient = MqttClient("tcp://localhost:1883", MqttClient.generateClientId(), null)
    private val mqttClient = MqttAndroidClient(this, "tcp://broker.hivemq.com:1883", MqttClient.generateClientId())
    //private val mqttClient = MqttAndroidClient(this, "tcp://192.168.50.36:1883", MqttClient.generateClientId())

    private val TOPIC = "MATTHEW"
    private lateinit var mqttAdapter: MqttAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mqtt)
        binding.lifecycleOwner = this

        initUI()
        initMqtt() // 구독 먼저

        binding.btnPublish.setOnClickListener {

        }

        binding.btnSubscribe.setOnClickListener {
//            initMqtt()
        }

        binding.btnSend.setOnClickListener {
            sendChat()
        }
    }

    // 메세지 보낼때
    private fun sendChat() {
        val id = "Matthew HI"
        val content: String = binding.etSendText.text.toString()
        if (content == "") {
        } else {
            val json = JSONObject()
            try {
                json.put("id", id)
                json.put("content", content)
                mqttClient.publish(TOPIC, MqttMessage(json.toString().toByteArray()))
            } catch (e: Exception) {
            }
            binding.etSendText.text.clear()
        }
    }


    private fun initUI() {
        mqttAdapter = MqttAdapter()
        binding.rvChat.adapter = mqttAdapter


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !== PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.WRITE_SETTINGS),
                    0
                )

            }
        }


    }


    private fun getMqttConnectionOption(): MqttConnectOptions? {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.setWill("aaa", "I am going offline".toByteArray(), 1, true)
        return mqttConnectOptions
    }

    private fun initMqtt() {
        Logger.d("## initMqtt")


        // 구독 신청
        val token: IMqttToken = mqttClient.connect(getMqttConnectionOption()) //mqtttoken 이라는것을 만들어 connect option을 달아줌
        Logger.d("## token ==>${token.client.clientId}")
        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Logger.d("## Connect_success")
                try {
                    mqttClient!!.subscribe(TOPIC, 0) //연결에 성공하면 jmlee 라는 토픽으로 subscribe함
                } catch (e: MqttException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {   //연결에 실패한경우
                Logger.e("## connect_fail Failure $exception")
            }
        }



        // 구독하여 메시지를 받을 경우
        mqttClient!!.setCallback(object : MqttCallback {
            //클라이언트의 콜백을 처리하는부분
            override fun connectionLost(cause: Throwable) {}

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {    //모든 메시지가 올때 Callback method
                if (topic == TOPIC) {     //topic 별로 분기처리하여 작업을 수행할수도있음
                    val msg = String(message.payload)
                    Logger.d("## arrive message :  $msg")
                    val json = JSONObject(String(message.payload, StandardCharsets.UTF_8))
                    mqttAdapter.add(ChatItem(json.getString("id"), json.getString("content")))
                    runOnUiThread { mqttAdapter.notifyDataSetChanged() }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {}
        })
    }

}
