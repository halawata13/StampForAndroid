package net.halawata.stamp.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import net.halawata.stamp.R
import net.halawata.stamp.data.DateRange
import net.halawata.stamp.service.DateRangeService
import net.halawata.stamp.service.LocationService
import java.util.*

class DateRangeLabelFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var datePickerDate: Date? = null
    private var datePickerType: DatePickerType? = null

    private lateinit var dateRangeService: DateRangeService
    private lateinit var dateRange: DateRange
    private lateinit var dateRangeTextView: TextView
    private lateinit var dateRangeFromToView: LinearLayout
    private lateinit var dateRangeFromTextView: TextView
    private lateinit var dateRangeToTextView: TextView

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_date_range_label, container, false)

        dateRangeService = DateRangeService(activity)
        dateRange = dateRangeService.load()

        dateRangeTextView = view.findViewById(R.id.dateRangeTextView)
        dateRangeFromToView = view.findViewById(R.id.dateRangeFromToView)
        dateRangeFromTextView = view.findViewById(R.id.dateRangeFromTextView)
        dateRangeToTextView = view.findViewById(R.id.dateRangeToTextView)

        dateRangeTextView.setOnClickListener {
            DateRangeSelectorDialogFragment().show(childFragmentManager, "DateRangeSelectorDialogFragment")
        }

        dateRangeFromTextView.setOnClickListener {
            datePickerDate = dateRange.from
            datePickerType = DatePickerType.FROM
            DateRangePickerDialogFragment().show(childFragmentManager, "DateRangePickerDialogFragment")
        }

        dateRangeToTextView.setOnClickListener {
            datePickerDate = dateRange.to
            datePickerType = DatePickerType.TO
            DateRangePickerDialogFragment().show(childFragmentManager, "DateRangePickerDialogFragment")
        }

        reloadDisplay()

        return view
    }

    /**
     * DatePicker 更新時のコールバック
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        datePickerDate ?: return

        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        // 変更した日付の期間を保存して更新
        val dateRange = when (datePickerType) {
            DatePickerType.FROM -> {
                DateRange(calendar.time, dateRange.to, dateRange.rangeName)
            }
            DatePickerType.TO -> {
                DateRange(dateRange.from, calendar.time, dateRange.rangeName)
            }
            else -> {
                throw RuntimeException()
            }
        }

        dateRangeService.save(dateRange)
        this.dateRange = dateRange
        reloadDisplay()

        onDateRangeChanging()
    }

    /**
     * 日付期間更新時処理
     */
    private fun onDateRangeChanging() {
        // 親フラグメントに処理を委譲する
        (parentFragment as? OnDateRangeLabelListener)?.onDateRangeChanging()
    }

    /**
     * 表示内容の更新
     */
    private fun reloadDisplay() {
        dateRangeTextView.text = dateRange.rangeName

        if (dateRange.rangeName == DateRangeService.Range.OTHERS.rangeName) {
            // 「期間を指定」する場合は from to を表示する
            dateRangeFromTextView.text = LocationService.convertDisplayDate(dateRange.from, LocationService.ConvertType.PLAIN)
            dateRangeToTextView.text = LocationService.convertDisplayDate(dateRange.to, LocationService.ConvertType.PLAIN)
            dateRangeFromToView.visibility = View.VISIBLE

        } else {
            dateRangeFromToView.visibility = View.GONE
        }
    }

    /**
     * 日付期間を保存する
     */
    private fun saveDateRange(rangeName: String) {
        dateRangeService.save(rangeName)

        dateRange = dateRangeService.load()
        reloadDisplay()
        onDateRangeChanging()
    }

    /**
     * 日付期間変更ダイアログ
     */
    class DateRangeSelectorDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val dateRangeService = DateRangeService(activity)
            val dateRange = dateRangeService.load()
            val parentFragment = parentFragment as? DateRangeLabelFragment
            val builder = AlertDialog.Builder(activity)

            builder
                    .setTitle(getString(R.string.dialog_text_date_range_selector))
                    .setSingleChoiceItems(DateRangeService.labels, DateRangeService.labels.indexOf(dateRange.rangeName), { dialog, which ->
                        val rangeName = DateRangeService.labels[which]
                        parentFragment?.saveDateRange(rangeName)

                        dismiss()
                    })
                    .setNegativeButton(getString(R.string.dialog_text_cancel), null)

            return builder.create()
        }

        override fun onPause() {
            super.onPause()
            dismiss()
        }
    }

    /**
     * DatePicker ダイアログ
     */
    class DateRangePickerDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parentFragment = parentFragment as? DateRangeLabelFragment
            val calendar = Calendar.getInstance()
            calendar.time = parentFragment?.datePickerDate

            return DatePickerDialog(activity, parentFragment, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        }

        override fun onPause() {
            super.onPause()
            dismiss()
        }
    }

    interface OnDateRangeLabelListener {
        fun onDateRangeChanging()
    }

    enum class DatePickerType {
        FROM,
        TO,
    }
}
