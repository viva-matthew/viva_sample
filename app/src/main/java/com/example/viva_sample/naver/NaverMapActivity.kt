package com.example.viva_sample.naver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.viva_sample.R
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class NaverMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private val naverMap: NaverMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naver_map)



//        val mapFragment = (supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment)

//
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
//            ?: MapFragment.newInstance().also {
//                supportFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
//            }
//        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(p0: NaverMap) {

    }
}