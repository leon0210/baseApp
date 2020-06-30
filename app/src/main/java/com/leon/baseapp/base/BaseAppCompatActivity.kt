package com.leon.baseapp.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.color.CircleView
import com.cxz.multiplestatusview.MultipleStatusView
import com.cxz.wanandroid.receiver.NetworkChangeReceiver
import com.leon.baseapp.R
import com.leon.baseapp.constant.Constant
import com.leon.baseapp.event.Event
import com.leon.baseapp.event.NetworkChangeEvent
import com.leon.baseapp.utils.*
import com.leon.baseapp.utils.ext.getQuickColor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseAppCompatActivity : AppCompatActivity() {
    /**
     * check login
     */
    protected var isLogin: Boolean by Preference(Constant.LOGIN_KEY, false)

    /**
     * 缓存上一次的网络状态
     */
    private var hasNetwork: Boolean by Preference(Constant.HAS_NETWORK_KEY, true)

    /**
     * 网络状态变化的广播
     */
    private var mNetworkChangeReceiver: NetworkChangeReceiver? = null

    /**
     * theme color
     */
    private var mThemeColor: Int = SettingUtil.getColor()

    /**
     * 多种状态的 View 的切换
     */
    private var mLayoutStatusView: MultipleStatusView? = null

    /**
     * 提示View
     */
    private lateinit var mTipView: View
    private lateinit var mWindowManager: WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams
    //todo 这里需要一个公共的加载框
//    protected var mLoadingDialog: LoadingDialog? = null

    /**
     * 布局文件id
     */
    @LayoutRes
    abstract fun attachLayoutRes(): Int

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 初始化数据
     */
    abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 开始请求
     */
    open fun startRequest() {}

    /**
     * 是否使用 EventBus
     */
    open val isRegisterEventBus: Boolean
        get() = false

    /**
     * 是否需要显示 TipView
     */
    open fun enableNetworkTip(): Boolean = true

    /**
     * 无网状态—>有网状态 的自动重连操作，子类可重写该方法
     */
    open fun doReConnected() {
        startRequest()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutRes())
        KeyBoardUtil.forbidAutoOpen(this)
        if (isRegisterEventBus) {
            EventBus.getDefault().register(this)
        }
        initView()
        initTipView()
        initData(savedInstanceState)
        initListener()
        startRequest()
    }

    override fun onResume() {
        // 动态注册网络变化广播
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(mNetworkChangeReceiver, filter)
        super.onResume()
        initColor()
        // 在无网络情况下打开APP时，系统不会发送网络状况变更的Intent，需要自己手动检查

        // 1.第一次进入界面会导致 start() 方法走两次
        // 2.后台切换到前台时，会调用 start() 方法执行相应的操作
        // 此处不应该调用，删掉，修改 #13
        // checkNetwork(hasNetwork)
    }

    open fun initColor() {
        mThemeColor = if (!SettingUtil.getIsNightMode()) {
            SettingUtil.getColor()
        } else {
            getQuickColor(R.color.colorPrimary)
        }
        StatusBarUtil.setColor(this, mThemeColor, 0)
        if (this.supportActionBar != null) {
            this.supportActionBar?.setBackgroundDrawable(ColorDrawable(mThemeColor))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = CircleView.shiftColorDown(mThemeColor)
//            // 最近任务栏上色
//            val tDesc = ActivityManager.TaskDescription(
//                    getString(R.string.app_name),
//                    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher),
//                    mThemeColor)
//            setTaskDescription(tDesc)
            if (SettingUtil.getNavBar()) {
                window.navigationBarColor = CircleView.shiftColorDown(mThemeColor)
            } else {
                window.navigationBarColor = Color.BLACK
            }
        }
    }

    /**
     * 初始化 TipView
     */
    @SuppressLint("InflateParams")
    private fun initTipView() {
        mTipView = layoutInflater.inflate(R.layout.layout_network_tip, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mLayoutParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        mLayoutParams.gravity = Gravity.TOP
        mLayoutParams.x = 0
        mLayoutParams.y = 0
        mLayoutParams.windowAnimations = R.style.anim_float_view // add animations
    }

    open fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        startRequest()
    }

    protected fun initToolbar(toolbar: Toolbar, homeAsUpEnabled: Boolean, title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
    }

    /**
     * 子类重写接收到分发到事件
     */
    open fun receiveEvent(event: Any?) {}

    /**
     * 子类重写接受到分发的粘性事件
     */
    open fun receiveStickyEvent(event: Any?) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventBusCome(event: Event<*>?) {
        if (event?.data is NetworkChangeEvent) {
            val networkChangeEvent = event.data as NetworkChangeEvent
            hasNetwork = networkChangeEvent.isConnected
            if (enableNetworkTip()) {
                if (networkChangeEvent.isConnected) {
                    doReConnected()
                    if (this::mTipView.isInitialized && mTipView.parent != null) {
                        mWindowManager.removeView(mTipView)
                    }
                } else {
                    if (mTipView.parent == null) {
                        mWindowManager.addView(mTipView, mLayoutParams)
                    }
                }
            }
        }
        event?.let { receiveEvent(it.data) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onStickyEventBusCome(event: Event<*>?) {
        event?.let { receiveStickyEvent(it.data) }
    }

    /**
     * 点击外部关闭软键盘
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            if (!KeyBoardUtil.isSoftInputShow(this)) return super.dispatchTouchEvent(ev)
            val v = currentFocus
            if (KeyBoardUtil.isShouldHideInput(v, ev)) {
                KeyBoardUtil.closeKeyBord(v as EditText, this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 点击软键盘的Enter按钮 关闭软键盘
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            val b = KeyBoardUtil.isSoftInputShow(this@BaseAppCompatActivity)
            if (b) KeyBoardUtil.closeKeyBord(this@BaseAppCompatActivity)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Fragment 逐个出栈
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onPause() {
        if (mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver)
            mNetworkChangeReceiver = null
        }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegisterEventBus) {
            EventBus.getDefault().unregister(this)
        }
        CommonUtil.fixInputMethodManagerLeak(this)
        BaseApplication.getRefWatcher(this)?.watch(this)
    }

    override fun finish() {
        super.finish()
        if (this::mTipView.isInitialized && mTipView.parent != null) {
            mWindowManager.removeView(mTipView)
        }
    }
}