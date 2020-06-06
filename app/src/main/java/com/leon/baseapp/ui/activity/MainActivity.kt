package com.leon.baseapp.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.leon.baseapp.R
import com.leon.baseapp.base.BaseMvpAppCompatActivity
import com.leon.baseapp.base.mvp.IBaseView
import com.leon.baseapp.ui.fragment.HomeFragment
import com.leon.baseapp.utils.ext.logi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvpAppCompatActivity<IBaseView>() {
    private var mFragment: Fragment? = null
    private var mHomeFragment: HomeFragment? = null
    override fun attachLayoutRes(): Int = R.layout.activity_main
    override fun initView() {
        super.initView()
        mHomeFragment = HomeFragment.getInstance()
        switchFragment(mHomeFragment, "首页")
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        super.initListener()
        bottom_navigation.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when (it.itemId) {
                R.id.action_home -> {
                    if (mHomeFragment == null) {
                        mHomeFragment = HomeFragment.getInstance()
                    }
                    switchFragment(mHomeFragment, "首页")
                    true
                }
                else -> {
                    false
                }
            }

        }
    }

    override fun onSuccess(any: Any?) {
        logi(any.toString())
    }

    private fun switchFragment(fragment: Fragment?, tag: String) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        if (fragment?.isAdded!!) {
            beginTransaction.hide(mFragment!!).show(fragment).commitAllowingStateLoss()
        } else {
            if (mFragment == null) {
                beginTransaction.replace(R.id.main_frame_layout, fragment).commitAllowingStateLoss()
            } else beginTransaction.hide(mFragment!!).add(R.id.main_frame_layout, fragment, tag).commitNowAllowingStateLoss()
        }
        mFragment = fragment
    }
}
