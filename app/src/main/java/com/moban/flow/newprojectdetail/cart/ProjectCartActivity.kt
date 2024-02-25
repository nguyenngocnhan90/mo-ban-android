package com.moban.flow.newprojectdetail.cart

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.moban.R
import com.moban.adapter.project.BlockAdapter
import com.moban.adapter.project.BlockDataAdapter
import com.moban.adapter.project.FloorAdapter
import com.moban.constant.Constant
import com.moban.flow.projectdetail.booking.ProjectBookingActivity
import com.moban.flow.projects.booking.BookingListActivity
import com.moban.handler.ApartmentItemHandler
import com.moban.handler.BlockItemHandler
import com.moban.handler.FloorItemHandler
import com.moban.model.data.project.*
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_project_cart.*
import kotlinx.android.synthetic.main.dialog_list.view.*
import kotlinx.android.synthetic.main.item_project_detail_empty.view.*
import kotlinx.android.synthetic.main.nav.view.*

class ProjectCartActivity : BaseMvpActivity<IProjectCartView, IProjectCartPresenter>(), IProjectCartView {
    override var presenter: IProjectCartPresenter = ProjectCartPresenterIml()
    private lateinit var project: Project
    private var blockAdapter: BlockAdapter = BlockAdapter()
    private val blockDataAdapter: BlockDataAdapter = BlockDataAdapter()
    private var currentBlock: Block? = null
    private var currentFloor: Floor? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_project_cart
    }
    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(context: Context, project: Project) {
            val intent = Intent(context, ProjectCartActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        project_cart_nav.nav_imgBack.setOnClickListener {
            finish()
        }
        project_cart_nav.nav_tvTitle.text = "Giỏ Hàng"
        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }

        project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        bindingData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.PROJECT_BOOKING_REQUEST) {
            val code = data?.getIntExtra(ProjectBookingActivity.BUNDLE_KEY_CODE, 0)
            if (code == 1) {
                currentBlock?.let {
                    presenter.loadApartments(project.id, it.id)
                }
                BookingListActivity.start(this, project)
            }
        }
    }

    private fun bindingData() {
        if (project.blocks.isEmpty()) {
            project_cart_view_empty?.visibility = View.VISIBLE
            project_cart_view_empty?.project_detail_empty_text?.text = getContext()
                    .getString(R.string.project_detail_cart_empty_text)
            project_cart_view_container?.visibility = View.GONE
            project_cart_view_status.visibility = View.GONE
            return
        }

        project_cart_view_container?.visibility = View.VISIBLE
        project_cart_view_empty?.visibility = View.GONE

        val blocks = project.blocks
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        project_cart_block_recycleView.layoutManager = layoutManager
        project_cart_block_recycleView.adapter = blockAdapter
        blockAdapter.updateBlocks(blocks)
        if (currentBlock == null && blocks.isNotEmpty()) {
            currentBlock = blocks[0]
            presenter.loadApartments(project.id, currentBlock!!.id)
        }
        blockAdapter.listener = object : BlockItemHandler {
            override fun onClicked(block: Block?, position: Int) {
                project_cart_pie_chart.visibility = if (block == null) View.VISIBLE else View.GONE
                project_cart_block_content.visibility = if (block != null) View.VISIBLE else View.GONE
                currentBlock = block
                block?.let {
                    presenter.loadApartments(project.id, it.id)
                }
            }
        }
    }

    override fun bindingApartments(blockData: BlockData, blockId: Int) {
        val blockStatistics = blockData.statistics
        project_cart_tv_total.text = blockStatistics.count_Total.toString()
        project_cart_tv_num_empty.text = blockStatistics.count_Trong.toString()
        project_cart_tv_num_deposited.text = blockStatistics.count_Coc.toString()
        project_cart_tv_num_booked.text = blockStatistics.count_Datcho.toString()
        project_cart_tv_num_contract.text = blockStatistics.count_Hopdong.toString()

        val numFloors = getString(R.string.has_num_floors)
                .replace("\$N", blockData.list.count().toString())
        project_cart_tv_num_floors.text = numFloors

        currentBlock?.let {
            if (it.id == blockId) {
                val layoutManager = LinearLayoutManager(this)
                project_cart_floor_recycleView.layoutManager = layoutManager
                project_cart_floor_recycleView.adapter = blockDataAdapter
                blockDataAdapter.updateFloors(blockData.list)
                blockDataAdapter.listener = object : ApartmentItemHandler {
                    override fun onClicked(apartment: Apartment) {
                        ProjectBookingActivity.start(this@ProjectCartActivity, project,
                                it, apartment)
                    }
                }
            }
        }


        val name = if (blockData.list.isEmpty()) "" else blockData.list[0].group
        val floorName = getString(R.string.floor) + " " + name
        project_cart_tv_floor_name.text = floorName
        project_cart_tv_floor_name.setOnClickListener {
            val alertDialog = AlertDialog.Builder(getContext()).create()
            val inflater = layoutInflater
            val convertView = inflater.inflate(R.layout.dialog_list, null) as View
            convertView.dialog_list_title.text = getContext().getText(R.string.project_detail_cart_select_floor)
                    .toString().toUpperCase()
            val linearLayoutManager = LinearLayoutManager(getContext())
            convertView.dialog_list_recycle.layoutManager = linearLayoutManager

            val floorAdapter = FloorAdapter()
            floorAdapter.setFloorsList(blockData.list)
            floorAdapter.listener = object : FloorItemHandler {
                override fun onClicked(floor: Floor, position: Int) {
                    onClickFloorSelected(floor, position)
                    alertDialog.dismiss()
                }
            }
            convertView.dialog_list_recycle.adapter = floorAdapter
            convertView.dialog_list_close.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.setView(convertView)
            alertDialog.show()
        }
    }

    private fun onClickFloorSelected(floor: Floor, position: Int) {
        currentFloor = floor
        val floorName = getContext().getText(R.string.project_detail_floor).toString() + " " + floor.group
        project_cart_tv_floor_name.text = floorName

        project_cart_floor_recycleView.scrollToPosition(position)
    }
}
