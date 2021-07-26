package com.example.viva_sample.realtimedb

class ChatData(var userName: String, var message: String) {
    constructor() : this(userName = "", message = "")
}