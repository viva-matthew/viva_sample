//package com.example.viva_sample.mqtt
//
//import android.R
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import org.eclipse.paho.android.service.MqttAndroidClient
//import org.eclipse.paho.client.mqttv3.*
//
//
//class MainActivity : AppCompatActivity() {
//    private var mqttAndroidClient: MqttAndroidClient? = null
//    private var button: Button? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        button = findViewById<View>(R.id.button) as Button
//        mqttAndroidClient = MqttAndroidClient(this, "tcp://" + "192.168.219.103" + ":1883", MqttClient.generateClientId())
//
//        // 2번째 파라메터 : 브로커의 ip 주소 , 3번째 파라메터 : client 의 id를 지정함 여기서는 paho 의 자동으로 id를 만들어주는것
//        try {
//            val token = mqttAndroidClient!!.connect(mqttConnectionOption) //mqtttoken 이라는것을 만들어 connect option을 달아줌
//            token.actionCallback = object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    mqttAndroidClient!!.setBufferOpts(disconnectedBufferOptions) //연결에 성공한경우
//                    Log.e("Connect_success", "Success")
//                    try {
//                        mqttAndroidClient!!.subscribe("jmlee", 0) //연결에 성공하면 jmlee 라는 토픽으로 subscribe함
//                    } catch (e: MqttException) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {   //연결에 실패한경우
//                    Log.e("connect_fail", "Failure $exception")
//                }
//            }
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//
//
//        /*
//
//        *   subscribe 할때 3번째 파라메터에 익명함수 리스너를 달아줄수도있음
//
//        * */
//
//        /*try {
//
//            mqttAndroidClient.subscribe("jmlee!!", 0, new IMqttMessageListener() {
//
//                @Override
//
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//
//
//
//                }
//
//            });
//
//        } catch (MqttException e) {
//
//            e.printStackTrace();
//
//        }*/button.setOnClickListener(object : OnClickListener() {
//            fun onClick(view: View?) {
//                try {
//                    mqttAndroidClient!!.publish("jmlee", "hello , my name is jmlee !".toByteArray(), 0, false)
//
//                    //버튼을 클릭하면 jmlee 라는 토픽으로 메시지를 보냄
//                } catch (e: MqttException) {
//                    e.printStackTrace()
//                }
//            }
//        })
//        mqttAndroidClient!!.setCallback(object : MqttCallback {
//            //클라이언트의 콜백을 처리하는부분
//            override fun connectionLost(cause: Throwable) {}
//
//            @Throws(Exception::class)
//            override fun messageArrived(topic: String, message: MqttMessage) {    //모든 메시지가 올때 Callback method
//                if (topic == "jmlee") {     //topic 별로 분기처리하여 작업을 수행할수도있음
//                    val msg = String(message.payload)
//                    Log.e("arrive message : ", msg)
//                }
//            }
//
//            override fun deliveryComplete(token: IMqttDeliveryToken) {}
//        })
//    }
//
////  175.194.129.232
//}