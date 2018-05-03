package net.halawata.stamp

import android.os.Bundle
import net.halawata.stamp.data.Location
import net.halawata.stamp.fragment.SingleMapFragment

class SingleMapActivity : BaseActivity() {

    override val tabBarPosition = -1

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        if (savedInstanceState == null) {
            (intent.getSerializableExtra("location") as? Location).let {
                val bundle = Bundle()
                bundle.putSerializable("location", it)

                val singleMapFragment = SingleMapFragment()
                singleMapFragment.arguments = bundle
                addFragment(singleMapFragment)
            }
        }
    }
}
