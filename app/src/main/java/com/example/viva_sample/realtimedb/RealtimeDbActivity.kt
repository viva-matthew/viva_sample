package com.example.viva_sample.realtimedb

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityRealtimeDbBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class RealtimeDbActivity : AppCompatActivity() {
    private val binding: ActivityRealtimeDbBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_realtime_db) }
    private val viewModel: RealtimeDbViewModel by viewModels()

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_db)

        init()
    }

    private fun init() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        val userName = "user" + Random().nextInt(10000) // 랜덤한 유저 이름 설정 ex) user1234


        binding.btnSend.setOnClickListener {
            val chatData = ChatData(userName, binding.etText.text.toString())
            databaseReference.child("testDB").push().setValue(chatData).addOnCompleteListener {
                binding.etText.text.clear()
            }

        }

        databaseReference.child("testDB").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(this@RealtimeDbActivity, "아이템 추가, onChildAdded", Toast.LENGTH_SHORT).show()
                val chatData: ChatData? = dataSnapshot.getValue(ChatData::class.java) // chatData를 가져오고
                binding.tvResult.text = chatData?.userName + ": " + chatData?.message

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(this@RealtimeDbActivity, "아이템 수정, onChildChanged", Toast.LENGTH_SHORT).show()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Toast.makeText(this@RealtimeDbActivity, "아이템 제거, onChildRemoved", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Toast.makeText(this@RealtimeDbActivity, "아이템 이동, onChildMoved", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RealtimeDbActivity, "아이템 취소, onCancelled", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
