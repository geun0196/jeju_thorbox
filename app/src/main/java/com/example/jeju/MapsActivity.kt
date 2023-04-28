package com.example.jeju

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.jeju.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
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

        val one_box_database = database.child("1")

        one_box_database.child("weight").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                val weight = snapshot.value.toString()
                binding.weight.setText("$weight kg")
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        val two_box_database = database.child("2")

        two_box_database.child("weight").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                val weight = snapshot.value.toString()
                binding.weight2.setText("$weight kg")
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        /////////////////////////////////////////////////////
        //버튼 클릭 이벤트
        /////////////////////////////////////////////////////
        binding.button.setOnClickListener {
            val latLng = LatLng(latitude, longitude)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

     var latitude: Double = 0.0
     var longitude: Double = 0.0

    fun updateGPSData(){
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef_latitude : DatabaseReference = database.getReference("gps_latitude")

        myRef_latitude.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var latitude_string = snapshot.value.toString()
                latitude = latitude_string.toDouble()
                moveMap(latitude, longitude)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        val myRef_longitude : DatabaseReference = database.getReference("gps_longitude")

        myRef_longitude.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                var longitude_string = snapshot.value.toString()
                longitude = longitude_string.toDouble()
                moveMap(latitude, longitude)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })
    }

    var cnt = 0
    fun moveMap(latitude: Double, longitude: Double){
        val latLng = LatLng(latitude, longitude)

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng).title("box"))
        if(cnt < 3){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            cnt++
        }
    }

     override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

         thread(start=true){
             while(true){
                 Thread.sleep(2000)
                 updateGPSData()
             }
         }
    }
}
