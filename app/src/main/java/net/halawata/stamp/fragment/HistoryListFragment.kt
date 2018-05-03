package net.halawata.stamp.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import net.halawata.stamp.*
import net.halawata.stamp.db.DatabaseHelper
import net.halawata.stamp.data.Location
import net.halawata.stamp.extension.at0
import net.halawata.stamp.service.DateRangeService
import net.halawata.stamp.service.LocationService
import net.halawata.stamp.view.LocationListAdapter

class HistoryListFragment : Fragment(), DateRangeLabelFragment.OnDateRangeLabelListener {

    /**
     * 編集モード
     */
    var isEditing = false
        set(value) {
            // リスト表示を更新する
            adapter.isEditing = value
            adapter.notifyDataSetChanged()
            field = value
        }

    private var listener: OnSingleMapInteractionListener? = null

    private lateinit var helper: DatabaseHelper
    private lateinit var locationService: LocationService
    private lateinit var locationList: List<Location>
    private lateinit var adapter: LocationListAdapter
    private lateinit var locationListView: ExpandableListView

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_list, container, false)

        helper = DatabaseHelper(activity)
        locationService = LocationService(helper)

        adapter = LocationListAdapter(activity)
        locationListView = view.findViewById(R.id.locationListView)

        locationListView.setAdapter(adapter)

        locationListView.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
            if (isEditing) {
                // 削除モード
                val dialog = DeleteConfirmDialogFragment()
                val arguments = Bundle()
                arguments.putString("id", adapter.getChild(groupPosition, childPosition).id)

                dialog.arguments = arguments
                dialog.show(childFragmentManager, "DeleteConfirmDialogFragment")

            } else {
                // 通常モード
                listener?.onSingleMapDisplay(adapter.getChild(groupPosition, childPosition))
            }

            true
        }

        locationListView.setOnGroupClickListener { parent, view, groupPosition, id ->
            // 開きっぱなし
            true
        }

        onDateRangeChanging()

        return view
    }

    /**
     * onViewCreated
     */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.title = getString(R.string.history_list)
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
     * 日付期間更新時処理
     */
    override fun onDateRangeChanging() {
        val dateRange = DateRangeService(activity).load()

        locationList = locationService.fetch(dateRange.from.at0(), dateRange.to.at0())
        adapter.setData(locationList)
        adapter.notifyDataSetChanged()

        adapter.groups.indices.forEach { i ->
            locationListView.expandGroup(i)
        }
    }

    /**
     * 位置を削除する
     */
    private fun deleteLocation(id: String) {
        try {
            locationService.delete(id)
            onDateRangeChanging()

        } catch (ex: Exception) {
            Toast.makeText(activity, getString(R.string.error_location_delete), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 位置削除の確認ダイアログ
     */
    class DeleteConfirmDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parentFragment = parentFragment as? HistoryListFragment
            val builder = AlertDialog.Builder(activity)

            builder
                    .setTitle(getString(R.string.dialog_text_confirm))
                    .setMessage(getString(R.string.dialog_text_delete_location))
                    .setPositiveButton(getString(R.string.dialog_text_ok), { dialogInterface, i ->
                        arguments.getString("id")?.let {
                            parentFragment?.deleteLocation(it)
                        }
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
