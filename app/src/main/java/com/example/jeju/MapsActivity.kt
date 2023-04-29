package com.example.jeju

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.jeju.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /////////////////////////////////////////////////////
        //데이터베이스 접근
        /////////////////////////////////////////////////////
        val database: DatabaseReference
        database = Firebase.database.reference.child("box_number")

        addValueEventListener(database,"1", binding.weight)
        addValueEventListener(database,"2", binding.weight2)

        /////////////////////////////////////////////////////
        //버튼 클릭 이벤트
        //=> 클릭시 일단은 1번 카트로 카메라 움직이게 해놨음. 추후 수정해야함
        /////////////////////////////////////////////////////
        binding.button.setOnClickListener {
            val latLng = LatLng(latitude1, longitude1)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
        }

    }

    fun addValueEventListener(database: DatabaseReference, boxNumber: String, textView: TextView) {
        database.child(boxNumber).child("weight").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val weight = snapshot.value.toString()
                textView.text = "$weight kg"
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })
    }

    var latitude1: Double = 0.0
    var longitude1: Double = 0.0
    var latitude2: Double = 0.0
    var longitude2: Double = 0.0

    fun updateGPSData1(){
        val database: DatabaseReference
        database = Firebase.database.reference.child("box_number").child("1")

        database.child("gps_latitude").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var latitude_string = snapshot.value.toString()
                latitude1 = latitude_string.toDouble()
                moveMap()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        database.child("gps_longitude").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var longitude_string = snapshot.value.toString()
                longitude1 = longitude_string.toDouble()
                moveMap()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })
    }

    fun updateGPSData2(){
        val database: DatabaseReference
        database = Firebase.database.reference.child("box_number").child("2")

        database.child("gps_latitude").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var latitude_string = snapshot.value.toString()
                latitude2 = latitude_string.toDouble()
                moveMap()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        database.child("gps_longitude").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var longitude_string = snapshot.value.toString()
                longitude2 = longitude_string.toDouble()
                moveMap()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })
    }

    var cnt = 0
    fun moveMap(){
        val latLng1 = LatLng(latitude1, longitude1)
        val latLng2 = LatLng(latitude2, longitude2)

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng1).title("box1"))
        mMap.addMarker(MarkerOptions().position(latLng2).title("box2"))

        if(cnt < 3){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15f))
            cnt++
        }
    }

     override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

         thread(start=true){
             while(true){
                 Thread.sleep(2000)
                 updateGPSData1()
                 updateGPSData2()
             }
         }
    }
}
