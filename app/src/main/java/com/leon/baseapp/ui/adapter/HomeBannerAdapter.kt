package com.leon.baseapp.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.leon.baseapp.entity.BannerData
import com.leon.baseapp.utils.ext.loadImg
import com.youth.banner.adapter.BannerAdapter

/**
 * Author: 千里
 * Date: 2020/6/5 18:00
 * Description:
 */
class HomeBannerAdapter(list: MutableList<BannerData>) : BannerAdapter<BannerData, BaseViewHolder>(list) {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        val imageView = ImageView(parent!!.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BaseViewHolder(imageView)
    }

    override fun onBindView(holder: BaseViewHolder?, data: BannerData?, position: Int, size: Int) {
        (holder?.itemView as ImageView).loadImg(data?.imagePath)
    }
}