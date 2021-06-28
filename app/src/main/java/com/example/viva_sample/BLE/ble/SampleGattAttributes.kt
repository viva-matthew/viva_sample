/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.viva_sample.BLE.ble

import java.util.*

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
object SampleGattAttributes {
    private val attributes: HashMap<String?, String?> = HashMap<String?, String?>()

    // HM-10 기기에서 읽고 쓰기 위한 서비스3 UUID
    var HM_RX_TX = "0000ffe1-0000-1000-8000-00805f9b34fb"

    // ESP 모듈에 사용되는 서비스 UUID
    val HM_RX = "0000FFE1-1218-1000-8000-00805F9B34FB"
    val HM_TX = "0000FFE3-1218-1000-8000-00805F9B34FB"

    var HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
    fun lookup(uuid: String?, defaultName: String): String {
        val name = attributes[uuid]
        return name ?: defaultName
    }

    init {
        // Sample Services.
        attributes["0000180d-0000-1000-8000-00805f9b34fb"] = "Heart Rate Service"
        attributes["0000180a-0000-1000-8000-00805f9b34fb"] = "Device Information Service"
        // Sample Characteristics.
        attributes[HEART_RATE_MEASUREMENT] = "Heart Rate Measurement"
        attributes["00002a29-0000-1000-8000-00805f9b34fb"] = "Manufacturer Name String"
    }
}