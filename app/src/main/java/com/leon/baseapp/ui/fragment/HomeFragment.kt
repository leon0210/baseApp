package com.leon.baseapp.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cxz.wanandroid.http.RetrofitHelper
import com.leon.baseapp.R
import com.leon.baseapp.base.BaseMvpFragment
import com.leon.baseapp.base.IFactory
import com.leon.baseapp.base.mvp.IBaseView
import com.leon.baseapp.entity.BannerData
import com.leon.baseapp.ui.adapter.HomeBannerAdapter
import com.leon.baseapp.utils.ext.getQuickColor
import com.leon.baseapp.utils.ext.getQuickLayoutInflater
import com.leon.baseapp.utils.ext.showToast
import com.leon.baseapp.utils.permission.OnPermission
import com.leon.baseapp.utils.permission.Permission
import com.leon.baseapp.utils.permission.XXPermissions
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.common_fragment_refresh_layout.*
import kotlinx.android.synthetic.main.layout_banner.*


/**
 * Author: 千里
 * Date: 2020/6/3 10:48
 * Description:
 */
class HomeFragment : BaseMvpFragment<IBaseView>() {
    private val mData = mutableListOf("判断是否有文件读写权限", "获取权限", "获取权限2")
    private val mBannerData = mutableListOf<BannerData>()
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context?.applicationContext) }
    private val mAdapter: BaseQuickAdapter<String, BaseViewHolder> by lazy {
        object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_home, mData) {
            override fun convert(helper: BaseViewHolder?, item: String?) {
                helper?.run {
                    setText(R.id.tv_title, item)
                }
            }
        }
    }

    override fun attachLayoutRes(): Int = R.layout.common_fragment_refresh_layout

    override fun initView(view: View) {
        super.initView(view)
        recyclerView.run {
            setHasFixedSize(true)
            adapter = mAdapter
            layoutManager = mLinearLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    context?.applicationContext,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        val view = context?.getQuickLayoutInflater(R.layout.layout_banner)
        val banner = view?.findViewById<Banner<Any, HomeBannerAdapter>>(R.id.banner)
        banner?.run {
            adapter = HomeBannerAdapter(mBannerData)
            indicator = CircleIndicator(context)
            setBannerRound(BannerUtils.dp2px(5f));
            setIndicatorSelectedWidth(BannerUtils.dp2px(6f).toInt())
        }
        mAdapter.run {
            bindToRecyclerView(recyclerView)
            addHeaderView(view)
        }
        refreshLayout.run {
            setEnableHeaderTranslationContent(false)
            (refreshHeader as MaterialHeader).setColorSchemeColors(getQuickColor(R.color.colorPrimary))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initListener() {
        super.initListener()
        mAdapter.setOnItemClickListener { adapter, view, position ->
//            XPopup.Builder(activity).asConfirm("标题", "内容") {
//                showToast(mData[position].title)
//            }.show()

//            XPopup.Builder(context).asInputConfirm("我是标题", "请输入内容。") {
//                showToast("输入 $it")
//            }.show()

//            XPopup.Builder(context) //.maxWidth(600)
//                .asCenterList(
//                    "请选择一项", arrayOf("条目1", "条目2", "条目3", "条目4")
//                ) { position, text ->  showToast("输入 $text") }
//                .show()
//            XPopup.Builder(context)
//                .asLoading("正在加载中")
//                .show()
            when (position) {
                0 -> {
                    if (ContextCompat.checkSelfPermission(
                            context!!, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        showToast("没有权限")
                    }
                }
                1 -> {
                    if (ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                activity!!,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(
                                activity!!,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                1
                            )
                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted
                    }
                }
                2 -> {
                    XXPermissions.with(activity as AppCompatActivity) // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                        .constantRequest()
                        // 支持请求6.0悬浮窗权限8.0请求安装权限
//                        .permission(Permission.REQUEST_INSTALL_PACKAGES)
                        // 不指定权限则自动获取清单中的危险权限
                        .permission(Permission.Group.STORAGE)
                        .request(object : OnPermission {
                            override fun hasPermission(granted: List<String?>?, all: Boolean) {
                                if (all) {
                                    showToast("获取权限成功")
                                } else {
                                    showToast("获取权限成功，部分权限未正常授予")
                                }
                            }

                            override fun noPermission(denied: List<String?>?, quick: Boolean) {
                                if (quick) {
                                    showToast("被永久拒绝授权，请手动授予权限")
                                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(activity)
                                } else {
                                    showToast("获取权限失败")
                                }
                            }
                        })
                }
            }
        }
        refreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                super.onRefresh(refreshLayout)
                isRefresh = true
                doRequest()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                super.onLoadMore(refreshLayout)
                refreshLayout.setEnableRefresh(false)
                doRequest()
            }
        })
    }


    override fun doRequest() {
        /*mPresenter?.doRequest({ RetrofitHelper.service.getTopArticles() }, {
            val data = it.data
            mAdapter.run {
                if (isRefresh) {
                    replaceData(data)
                    refreshLayout.finishRefresh()
                } else {
                    addData(data)
                }
            }
            refreshLayout.finishLoadMoreWithNoMoreData()
            if (mAdapter.data.isEmpty()) {
                mLayoutStatusView?.showEmpty()
            } else {
                mLayoutStatusView?.showContent()
            }
            refreshLayout.setEnableRefresh(true)
        })*/
        mPresenter?.doRequest({ RetrofitHelper.service.getBanners() }, {
            mBannerData.clear()
            mBannerData.addAll(it.data)
            banner.adapter.notifyDataSetChanged()
        })
    }

    override fun onFailed(errorMsg: String?, errorCode: Int?) {
        super.onFailed(errorMsg, errorCode)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableRefresh(true)
    }

    override fun onError(e: Throwable) {
        super.onError(e)
        refreshLayout.finishRefresh()
        refreshLayout.setEnableRefresh(true)
        mLayoutStatusView?.showError()
    }

    companion object : IFactory<HomeFragment> {
        override fun newInstance(): HomeFragment = HomeFragment()
    }

    override fun onPause() {
        super.onPause()
        banner?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        banner?.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}