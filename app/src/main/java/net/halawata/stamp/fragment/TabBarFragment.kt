package net.halawata.stamp.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import net.halawata.stamp.BaseActivity
import net.halawata.stamp.R

class TabBarFragment : Fragment() {

    private var currentPosition: Int? = null
    private var listener: OnFragmentChangingListener? = null

    private lateinit var buttons: ArrayList<Button>

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab_bar, container, false)

        buttons = arrayListOf(
                view.findViewById(R.id.recordingTabBarButton),
                view.findViewById(R.id.historyListTabBarButton),
                view.findViewById(R.id.historyMapTabBarButton)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                onButtonPressed(index)
            }
        }

        (activity as? BaseActivity)?.let {
            if (it.tabBarPosition >= 0) {
                // タブバーボタンの選択色と非選択色を変更
                buttons.forEach {
                    it.setTextColor(ContextCompat.getColor(activity, R.color.tab_bar_item_unselected))
                }

                buttons[it.tabBarPosition].setTextColor(ContextCompat.getColor(activity, R.color.tab_bar_item_selected))
            }
        }

        return view
    }

    /**
     * onAttach
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnFragmentChangingListener) {
            listener = context
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
     * タブバーボタンクリック時
     */
    private fun onButtonPressed(position: Int) {
        if (position == currentPosition) {
            return
        }

        currentPosition = position
        listener?.onFragmentChanging(position)
    }

    interface OnFragmentChangingListener {
        fun onFragmentChanging(position: Int)
    }
}
