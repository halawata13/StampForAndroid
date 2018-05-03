package net.halawata.stamp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.halawata.stamp.service.LocationService
import net.halawata.stamp.R
import net.halawata.stamp.data.Location
import net.halawata.stamp.db.DatabaseHelper

class SingleMapFragment : Fragment(), OnMapReadyCallback {

    private var map: GoogleMap? = null

    private lateinit var location: Location

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_single_map, container, false)

        (arguments.getSerializable("location") as? Location)?.let {
            this.location = it

            view.findViewById<TextView>(R.id.locationDatetimeTextView).text = LocationService.convertDisplayDatetime(it.createDate, LocationService.ConvertType.DETAIL)
            view.findViewById<TextView>(R.id.locationLatLngTextView).text = LocationService.convertLatLng(it.latitude, it.longitude, LocationService.ConvertType.DETAIL)

            view.findViewById<EditText>(R.id.locationMemoEditText).run {
                setText(it.memo)

                setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        (v as? EditText)?.text.toString().let {
                            val helper = DatabaseHelper(activity)
                            LocationService(helper).update(Location(location.id, location.latitude, location.longitude, location.createDate, it))
                        }
                    }
                }
            }

        } ?: run {
            Log.e("assertionFailure", "location is not set.")
            activity.finish()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    /**
     * onMapReady
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            val current = LatLng(location.latitude, location.longitude)

            addMarker(MarkerOptions().position(current).title(location.memo))
            moveCamera(CameraUpdateFactory.newLatLng(current))
            moveCamera(CameraUpdateFactory.zoomTo(15F))
        }
    }
}
