package net.halawata.stamp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.halawata.stamp.R
import net.halawata.stamp.db.DatabaseHelper
import net.halawata.stamp.data.Location
import net.halawata.stamp.extension.at0
import net.halawata.stamp.service.DateRangeService
import net.halawata.stamp.service.LocationService

class HistoryMapFragment : Fragment(), OnMapReadyCallback, DateRangeLabelFragment.OnDateRangeLabelListener {

    private lateinit var helper: DatabaseHelper
    private lateinit var locationService: LocationService
    private lateinit var locationList: List<Location>
    private lateinit var map: GoogleMap

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_map, container, false)

        helper = DatabaseHelper(activity)
        locationService = LocationService(helper)

        reloadLocationList()

        val mapFragment = childFragmentManager.findFragmentById(R.id.historyMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    /**
     * onViewCreated
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.title = getString(R.string.history_map)
    }

    /**
     * onMapReady
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        reloadMarker()
    }

    /**
     * 日付期間更新時処理
     */
    override fun onDateRangeChanging() {
        reloadLocationList()
        reloadMarker()
    }

    /**
     * 位置リストを更新する
     */
    private fun reloadLocationList() {
        val dateRange = DateRangeService(activity).load()
        locationList = locationService.fetch(dateRange.from.at0(), dateRange.to.at0())
    }

    /**
     * マーカーを更新する
     */
    private fun reloadMarker() {
        locationList.forEach { location ->
            val current = LatLng(location.latitude, location.longitude)
            map.addMarker(MarkerOptions().position(current).title(location.memo))
        }

        locationList.lastOrNull()?.let {
            map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
            map.moveCamera(CameraUpdateFactory.zoomTo(15F))
        }
    }
}
