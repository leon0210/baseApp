package com.leon.baseapp.ui.fragment

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cxz.wanandroid.http.RetrofitHelper
import com.leon.baseapp.R
import com.leon.baseapp.base.BaseMvpFragment
import com.leon.baseapp.base.mvp.IBaseView
import com.leon.baseapp.entity.Article
import com.leon.baseapp.entity.BannerData
import com.leon.baseapp.entity.HttpResult
import com.leon.baseapp.ui.adapter.HomeBannerAdapter
import com.leon.baseapp.utils.ext.getQuickColor
import com.leon.baseapp.utils.ext.getQuickLayoutInflater
import com.leon.baseapp.utils.ext.showToast
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.youth.banner.Banner
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.common_fragment_refresh_layout.*
import kotlinx.android.synthetic.main.layout_banner.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.security.auth.callback.Callback

/**
 * Author: 千里
 * Date: 2020/6/3 10:48
 * Description:
 */
class HomeFragment : BaseMvpFragment<IBaseView>() {
    private val mData = mutableListOf<Article>()
    private val mBannerData = mutableListOf<BannerData>()
    private val mLinearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context?.applicationContext) }
    private val mAdapter: BaseQuickAdapter<Article, BaseViewHolder> by lazy {
        object : BaseQuickAdapter<Article, BaseViewHolder>(R.layout.item_home, mData) {
            override fun convert(helper: BaseViewHolder?, item: Article?) {
                helper?.run {
                    setText(R.id.tv_title, item?.title)
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

    override fun initListener() {
        super.initListener()
        mAdapter.setOnItemClickListener { adapter, view, position ->
            showToast(mData[position].title)
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
        mPresenter?.doRequest(RetrofitHelper.service.getTopArticles()) {
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
        }
//        mPresenter?.doRequest(RetrofitHelper.service.getBanners()) {
//            mBannerData.clear()
//            mBannerData.addAll(it.data)
//            banner.adapter.notifyDataSetChanged()
//        }
    }

    companion object {
        fun getInstance(): HomeFragment = HomeFragment()
    }

    override fun onPause() {
        super.onPause()
        banner?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        banner?.destroy()
    }
}