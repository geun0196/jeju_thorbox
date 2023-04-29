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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val boxList = mutableListOf<Box>()

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

        val box1 = Box(database.child("1"), binding.weight, "box1")
        boxList.add(box1)

        val box2 = Box(database.child("2"), binding.weight2, "box2")
        boxList.add(box2)

        /////////////////////////////////////////////////////
        //버튼 클릭 이벤트
        //=> 클릭시 일단은 1번 카트로 카메라 움직이게 해놨음. 추후 수정해야함
        /////////////////////////////////////////////////////
        binding.button.setOnClickListener {
            val latLng = LatLng(boxList[0].latitude, boxList[0].longitude)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18f))
        }

    }

    inner class Box(databaseRef: DatabaseReference, textView: TextView, title: String) {
        var title = title
        var latitude: Double = 0.0
        var longitude: Double = 0.0

        init {
            databaseRef.child("weight").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val weight = snapshot.value.toString()
                    textView.text = "$weight kg"
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })

            databaseRef.child("gps_latitude").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var latitudeString = snapshot.value.toString()
                    latitude = latitudeString.toDouble()
                    updateMap()
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })

            databaseRef.child("gps_longitude").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.
                    var longitudeString = snapshot.value.toString()
                    longitude = longitudeString.toDouble()
                    updateMap()
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })
        }

        var cnt = 0
        private fun updateMap() {
            val latLng = LatLng(latitude, longitude)
            mMap.clear()
            for (box in boxList) {
                mMap.addMarker(MarkerOptions().position(LatLng(box.latitude, box.longitude)).title(box.title))
            }

            if(cnt < 2){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                cnt++
            }
        }
    }

     override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }
}
