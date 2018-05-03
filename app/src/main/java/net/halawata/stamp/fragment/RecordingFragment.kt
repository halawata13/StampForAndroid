package net.halawata.stamp.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.halawata.stamp.db.DatabaseHelper
import net.halawata.stamp.service.LocationService
import net.halawata.stamp.R

class RecordingFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private var locationManager: LocationManager? = null
    private var location: Location? = null
    private var map: GoogleMap? = null
    private var listener: OnSingleMapInteractionListener? = null

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recording, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<FloatingActionButton>(R.id.locationRecordButton).setOnClickListener {
            location?.let {
                ConfirmDialogFragment().show(childFragmentManager, "ConfirmDialogFragment")
            }
        }

        return view
    }

    /**
     * onViewCreated
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.title = getString(R.string.recording)
    }

    /**
     * onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnSingleMapInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSingleMapInteractionListener")
        }
    }

    /**
     * onDetach
     */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * onStart
     */
    override fun onStart() {
        super.onStart()
        setupLocationManager()
    }

    /**
     * onStop
     */
    override fun onStop() {
        super.onStop()
        locationManager?.removeUpdates(this)
    }

    /**
     * onMapReady
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateMap(location)
    }

    /**
     * onLocationChanged
     */
    override fun onLocationChanged(location: Location?) {
        this.location = location
        updateMap(location)
    }

    /**
     * onStatusChanged
     */
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        setupLocationManager()
    }

    /**
     * onProviderEnabled
     */
    override fun onProviderEnabled(provider: String?) {
    }

    /**
     * onProviderDisabled
     */
    override fun onProviderDisabled(provider: String?) {
    }

    /**
     * 現在地を更新する
     */
    private fun updateMap(location: Location?) {
        location ?: return

        val current = LatLng(location.latitude, location.longitude)
        map?.run {
            addMarker(MarkerOptions().position(current).title(getString(R.string.current_location_marker_title)))
            moveCamera(CameraUpdateFactory.newLatLng(current))
            moveCamera(CameraUpdateFactory.zoomTo(15F))
        }
    }

    /**
     * 権限チェックと LocationManager 初期化
     */
    private fun setupLocationManager() {
        if (ActivityCompat.checkSelfPermission(activity.application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }

        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

                return
            }
        }

        locationManager.requestLocationUpdates(provider, 500, 1F, this)
        location = locationManager.getLastKnownLocation(provider).apply {
            updateMap(this)
        }

        this.locationManager = locationManager
    }

    /**
     * 位置を新規追加する
     */
    private fun createLocation() {
        location?.let {
            val locationService = LocationService(DatabaseHelper(activity))

            try {
                val id = locationService.create(it.latitude, it.longitude)

                locationService.fetch(id)?.let {
                    listener?.onSingleMapDisplay(it)
                }

            } catch (ex: Exception) {
                Toast.makeText(activity, getString(R.string.error_location_create), Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 位置の追加の確認ダイアログ
     */
    class ConfirmDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parentFragment = parentFragment as? RecordingFragment
            val builder = AlertDialog.Builder(activity)

            builder
                    .setTitle(getString(R.string.dialog_text_confirm))
                    .setMessage(getString(R.string.dialog_text_create_location))
                    .setPositiveButton(getString(R.string.dialog_text_ok), { dialogInterface, i ->
                        parentFragment?.createLocation()
                    })
                    .setNegativeButton(getString(R.string.dialog_text_cancel), null)

            return builder.create()
        }

        override fun onPause() {
            super.onPause()
            dismiss()
        }
    }
}
