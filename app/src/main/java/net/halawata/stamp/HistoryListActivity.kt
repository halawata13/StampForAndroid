package net.halawata.stamp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import net.halawata.stamp.data.Location
import net.halawata.stamp.fragment.HistoryListFragment
import net.halawata.stamp.fragment.OnSingleMapInteractionListener
import net.halawata.stamp.fragment.TabBarFragment

class HistoryListActivity : BaseActivity(), TabBarFragment.OnFragmentChangingListener, OnSingleMapInteractionListener {

    override val tabBarPosition = 1

    private lateinit var fragment: HistoryListFragment

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)

        if (savedInstanceState == null) {
            fragment = HistoryListFragment()
            addFragment(fragment)
        }
    }

    /**
     * onCreateOptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history_list, menu)
        return true
    }

    /**
     * onOptionsItemSelected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.historyListMenuTrashboxItem -> {
                fragment.isEditing = fragment.isEditing != true
            }
        }

        return true
    }

    /**
     * onSingleMapDisplay
     */
    override fun onSingleMapDisplay(location: Location) {
        val intent = Intent(this, SingleMapActivity::class.java)
        intent.putExtra("location", location)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
