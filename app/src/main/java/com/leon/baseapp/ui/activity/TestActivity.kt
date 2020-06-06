package com.leon.baseapp.ui.activity

import android.os.Bundle
import com.leon.baseapp.base.BaseMvpAppCompatActivity
import com.leon.baseapp.base.mvp.IBaseView

/**
 * Author: 千里
 * Date: 2020/6/2 17:52
 * Description:
 */
class TestActivity : BaseMvpAppCompatActivity<IBaseView>() {

    override fun attachLayoutRes(): Int =0;

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun onSuccess(any: Any?) {
    }
}