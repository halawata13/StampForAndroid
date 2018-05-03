package net.halawata.stamp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import net.halawata.stamp.service.LocationService
import net.halawata.stamp.R
import net.halawata.stamp.data.Location

class LocationListAdapter(private val context: Context) : BaseExpandableListAdapter() {

    var groups = ArrayList<String>()
    var children = ArrayList<ArrayList<Location>>()
    var isEditing = false

    override fun getGroup(groupPosition: Int): String {
        return groups[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.location_list_group, parent, false)
        view.findViewById<TextView>(R.id.locationListGroupTitle).text = groups[groupPosition]

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return children[groupPosition].size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Location {
        return children[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return getGroup(groupPosition).hashCode().toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.location_list_item, parent, false)
        val location = getChild(groupPosition, childPosition)

        // メモが入力されているかで表示内容を変更
        if (location.memo.isEmpty()) {
            view.findViewById<TextView>(R.id.locationListTitle).text = LocationService.convertLatLng(location.latitude, location.longitude, LocationService.ConvertType.PLAIN)
            view.findViewById<TextView>(R.id.locationListLatLng).text = ""
        } else {
            view.findViewById<TextView>(R.id.locationListTitle).text = location.memo
            view.findViewById<TextView>(R.id.locationListLatLng).text = LocationService.convertLatLng(location.latitude, location.longitude, LocationService.ConvertType.PLAIN)
        }

        view.findViewById<TextView>(R.id.locationListTime).text = LocationService.convertDisplayTime(location.createDate, LocationService.ConvertType.PLAIN)

        // 編集モードかどうかで表示内容を変更
        if (isEditing) {
            view.findViewById<RelativeLayout>(R.id.locationListRowView).background = context.getDrawable(R.color.list_view_delete_background)
            view.findViewById<ImageView>(R.id.locationListDeleteImageView).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.locationListDetailImageView).visibility = View.GONE
        } else {
            view.findViewById<RelativeLayout>(R.id.locationListRowView).background = context.getDrawable(android.R.color.white)
            view.findViewById<ImageView>(R.id.locationListDeleteImageView).visibility = View.GONE
            view.findViewById<ImageView>(R.id.locationListDetailImageView).visibility = View.VISIBLE
        }

        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return getChild(groupPosition, childPosition).hashCode().toLong()
    }

    override fun getGroupCount(): Int {
        return groups.size
    }

    fun setData(data: List<Location>) {
        val groups = ArrayList<String>()
        val children = ArrayList<ArrayList<Location>>()
        val temp = LinkedHashMap<String, ArrayList<Location>>()

        // 日付ごとにグループ化
        data.reversed().forEach { location ->
            val dateString = LocationService.convertDisplayDate(location.createDate, LocationService.ConvertType.DETAIL)

            if (!temp.containsKey(dateString)) {
                temp[dateString] = ArrayList()
            }

            temp[dateString]?.add(location)
        }

        temp.forEach { (key, value) ->
            groups.add(key)
            children.add(value)
        }

        this.groups = groups
        this.children = children
    }
}
