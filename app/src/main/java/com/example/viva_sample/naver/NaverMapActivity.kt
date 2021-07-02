package com.example.viva_sample.naver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.viva_sample.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.orhanobut.logger.Logger


class NaverMapActivity : AppCompatActivity(), OnMapReadyCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naver_map)



        val mapFragment = (supportFragmentManager.findFragmentById(R.id.fragmentNaverMap) as MapFragment)
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(naverMap: NaverMap) {
        Logger.d("## naverMap ==> ${naverMap}")


        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.home)

    }
}