package com.moban.util

import android.app.Dialog
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.interests.InterestsGroupAdapter
import com.moban.handler.InterestDialogHandler
import com.moban.model.data.user.Interest
import com.moban.model.data.user.InterestGroup
import kotlinx.android.synthetic.main.dialog_list_interests.view.*
import java.util.*


class DialogSelectInterest(private var context: Context,
                           private var interestsList: List<InterestGroup>,
                           private var requireInterestGroup: List<InterestGroup>,
                           private var selectedInterests: List<Interest>,
                           private var selectedInterestsGroup: List<InterestGroup>,
                           private var listener: InterestDialogHandler?) {
    private var interestGroupAdapter = InterestsGroupAdapter()
    val dialog = Dialog(context)

    fun showDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list_interests, null, false)
//        val fontMedium = Typeface.createFromAsset(context.assets,
//                context.resources.getString(R.string.font_medium))
//        val fontRegular = Typeface.createFromAsset(context.assets,
//                context.resources.getString(R.string.font_regular))
        initRecycleView(view)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        val widthDialog = Device.getScreenWidth()*0.9
        val heightDialog = Device.getScreenHeight()*0.9
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = widthDialog.toInt()
        lp.height = heightDialog.toInt()
        lp.gravity = Gravity.CENTER

        dialog.window?.attributes = lp
        dialog.show()

        view.dialog_interests_btn_close.setOnClickListener {
            dialog.dismiss()
        }

        view.dialog_interests_tv_done.setOnClickListener {
            var isValid = true
            for(requireGroup in requireInterestGroup) {
                if (!interestGroupAdapter.selectedSetGroup.containsKey(requireGroup.group)) {
                    isValid = false
                    break
                }
            }

            if (!isValid) {
                DialogUtil.showErrorDialog(context,
                        context.getString(R.string.error_missing_info),
                        context.getString(R.string.error_missing_interest_deal_desc),
                        context.getString(R.string.ok), null)
            } else {
                listener?.onSelected(interestGroupAdapter.selectedSet.values.toList(), interestGroupAdapter.selectedSetGroup)
                dialog.dismiss()
            }
        }
    }

    private fun initRecycleView(view: View) {
        val layoutManager = LinearLayoutManager(context)
        view.dialog_interests_recycleView.layoutManager = layoutManager
        view.dialog_interests_recycleView.adapter = interestGroupAdapter
        val selectedInterestMap: HashMap<Int, Interest> = HashMap()
        for (interest in selectedInterests) {
            selectedInterestMap[interest.id] = interest
        }
        interestGroupAdapter.selectedSet = selectedInterestMap

        val selectedInterestGroupMap: HashMap<String, InterestGroup> = HashMap()
        for (interestGroup in selectedInterestsGroup) {
            selectedInterestGroupMap[interestGroup.group] = interestGroup
        }
        interestGroupAdapter.selectedSetGroup = selectedInterestGroupMap
        interestGroupAdapter.setInterestList(interestsList)
    }
}
