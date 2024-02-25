package com.moban.view.viewholder

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.moban.LHApplication
import com.moban.R
import com.moban.adapter.linkmart.LinkmartAdapter
import com.moban.model.data.linkmart.Linkmart
import com.moban.util.BitmapUtil
import com.moban.util.Device
import com.moban.util.LHPicasso
import kotlinx.android.synthetic.main.item_linkmart.view.*

/**
 * Created by LenVo on 8/15/18.
 */
class LinkmartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(linkmart: Linkmart, type: Int) {
        val context = itemView.context!!
        val width = if (type == LinkmartAdapter.LINKMART_VERTICAL) Device.getScreenWidth()
        else Device.getScreenWidth() - BitmapUtil.convertDpToPx(context, 30)

        val marginStart = if (type == LinkmartAdapter.LINKMART_VERTICAL) 0
        else BitmapUtil.convertDpToPx(context, 10)

        val layoutParam = RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParam.marginStart = marginStart
        layoutParam.topMargin = BitmapUtil.convertDpToPx(context, 10)
        itemView.layoutParams = layoutParam

        linkmart.images?.firstOrNull()?.let {
            LHPicasso.loadImage(it.url, itemView.item_linkmart_img_gift)
        }
        itemView.item_linkmart_tv_title.text = linkmart.name

        val hasSalePrice = linkmart.salePrice != null && linkmart.salePrice!!.isNotEmpty()

        val regularPrice = linkmart.regularPrice + " " + context.getString(R.string.lhc) + if (hasSalePrice) " - " else ""
        val spannablePrice =  SpannableString(regularPrice)
        if (hasSalePrice) {
            spannablePrice.setSpan(StrikethroughSpan(), 0, regularPrice.length - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val salePrice = linkmart.salePrice + " " + context.getString(R.string.lhc)
            itemView.item_linkmart_tv_coin.text = salePrice
        }
        itemView.item_linkmart_tv_price.text = spannablePrice

        //Category:
        linkmart.categories?.firstOrNull()?.let {
            itemView.item_linkmart_tv_category.text = it.name.toUpperCase()
            val cate = LHApplication.instance.lhCache.linkmartCategories.firstOrNull{ cate -> cate.id == it.id }
            cate?.let {
                it.image?.let {
                    LHPicasso.loadImage(it.url, itemView.item_linkmart_img_category)
                }
            }
        }

        //Brand
        linkmart.brands?.firstOrNull()?.let {
            itemView.item_linkmart_tv_brand.text = it.name

            val brand = LHApplication.instance.lhCache.brands.firstOrNull { brand -> brand.id == it.id }
            brand?.let {
                it.image?.let {
                    LHPicasso.loadImage(it.url, itemView.item_linkmart_img_brand)
                }
            }
        }
    }
}
