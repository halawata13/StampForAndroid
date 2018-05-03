package net.halawata.stamp

import android.os.Bundle
import net.halawata.stamp.fragment.HistoryMapFragment
import net.halawata.stamp.fragment.TabBarFragment

class HistoryMapActivity : BaseActivity(), TabBarFragment.OnFragmentChangingListener {

    override val tabBarPosition = 2

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        if (savedInstanceState == null) {
            addFragment(HistoryMapFragment())
        }
    }
}
