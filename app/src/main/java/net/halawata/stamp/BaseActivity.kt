package net.halawata.stamp

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import net.halawata.stamp.fragment.TabBarFragment

abstract class BaseActivity : AppCompatActivity(), TabBarFragment.OnFragmentChangingListener {

    abstract val tabBarPosition: Int

    /**
     * タブバーボタンクリック時
     */
    override fun onFragmentChanging(position: Int) {
        // 押したボタンに応じて activity を切り替える
        val intent = when (position) {
            0 -> Intent(this, RecordingActivity::class.java)
            1 -> Intent(this, HistoryListActivity::class.java)
            2 -> Intent(this, HistoryMapActivity::class.java)
            else -> throw RuntimeException()
        }

        intent.flags = Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    /**
     * finish
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * フラグメントを追加する
     */
    protected fun addFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.fragmentContainer, fragment)
                .commit()
    }

    /**
     * フラグメントを入れ替える
     */
    protected fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
    }
}
