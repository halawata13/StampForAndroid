package net.halawata.stamp

import android.os.Bundle
import net.halawata.stamp.data.Location
import net.halawata.stamp.fragment.OnSingleMapInteractionListener
import net.halawata.stamp.fragment.RecordingFragment
import net.halawata.stamp.fragment.SingleMapFragment
import net.halawata.stamp.fragment.TabBarFragment

class RecordingActivity : BaseActivity(), TabBarFragment.OnFragmentChangingListener, OnSingleMapInteractionListener {

    override val tabBarPosition = 0

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        if (savedInstanceState == null) {
            addFragment(RecordingFragment())
        }
    }

    /**
     * onSingleMapDisplay
     */
    override fun onSingleMapDisplay(location: Location) {
        val fragment = SingleMapFragment()
        val bundle = Bundle()

        bundle.putSerializable("location", location)
        fragment.arguments = bundle

        replaceFragment(fragment)
    }
}
