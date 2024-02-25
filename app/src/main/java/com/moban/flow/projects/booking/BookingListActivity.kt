package com.moban.flow.projects.booking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import com.moban.R
import com.moban.adapter.booking.ProjectBookingAdapter
import com.moban.constant.Constant
import com.moban.enum.GACategory
import com.moban.flow.projectdetail.bookingdetail.BookingDetailActivity
import com.moban.flow.projects.booking.newbooking.NewBookingActivity
import com.moban.handler.LoadMoreHandler
import com.moban.handler.ProjectBookingItemHandler
import com.moban.model.data.booking.ProjectBooking
import com.moban.model.data.project.Project
import com.moban.model.response.project.ListProjectBooking
import com.moban.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.activity_project_booking_list.*
import kotlinx.android.synthetic.main.item_project_detail_empty.view.*
import kotlinx.android.synthetic.main.nav.view.*

class BookingListActivity : BaseMvpActivity<IBookingListView, IBookingListPresenter>(), IBookingListView {
    override var presenter: IBookingListPresenter = BookingListPresenterIml()
    var page: Int = 1
    private val bookingListAdapter = ProjectBookingAdapter()
    private lateinit var project: Project

    companion object {
        const val BUNDLE_KEY_PROJECT = "BUNDLE_KEY_PROJECT"

        fun start(context: Context, project: Project) {
            val bundle = Bundle()

            val intent = Intent(context, BookingListActivity::class.java)
            bundle.putSerializable(BUNDLE_KEY_PROJECT, project)
            intent.putExtras(bundle)

            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_project_booking_list
    }

    override fun initialize(savedInstanceState: Bundle?) {
        project_book_list_nav.nav_imgBack.setOnClickListener {
            finish()
        }

        project_book_list_nav.nav_tvTitle.text = getString(R.string.project_booking_title)
        project_book_list_nav.nav_add.visibility = View.VISIBLE
        project_book_list_empty.project_detail_empty_text.text = getString(R.string.project_detail_book_empty_text)
        initRecycleView()

        val bundle = intent.extras
        if (!intent.hasExtra(BUNDLE_KEY_PROJECT)) {
            return
        }
        project = bundle?.getSerializable(BUNDLE_KEY_PROJECT) as Project
        bookingListAdapter.project = project

        project_book_list_nav.nav_add.setOnClickListener {
            NewBookingActivity.start(this@BookingListActivity, project)
        }

        reloadData()
        setGAScreenName("List Booking", GACategory.PROJECT)
    }

    private fun reloadData() {
        page = 1
        project_book_list_refresh.isRefreshing = true
        presenter.loadBookings(project.id, page)
    }

    private fun initRecycleView() {
        project_book_list_refresh.setColorSchemeResources(R.color.colorAccent)
        project_book_list_refresh.setOnRefreshListener {
            reloadData()
        }

        val linearLayoutManager = LinearLayoutManager(this)
        project_book_list_recycleview.layoutManager = linearLayoutManager
        project_book_list_recycleview.adapter = bookingListAdapter

        bookingListAdapter.loadMoreHandler = object : LoadMoreHandler {
            override fun onLoadMore() {
                presenter.loadBookings(project.id, page)
            }
        }

        bookingListAdapter.listener = object : ProjectBookingItemHandler {
            override fun onClicked(projectBooking: ProjectBooking) {
                BookingDetailActivity.start(this@BookingListActivity, projectBooking)
            }
        }
    }

    override fun bindingBookings(bookings: ListProjectBooking, canLoadMore: Boolean) {
        project_book_list_refresh.isRefreshing = false
        if (page == 1 || bookingListAdapter.projectBookings.isEmpty()) {
            project_book_list_empty.visibility = if (bookings.list.isEmpty()) View.VISIBLE else View.GONE
            project_book_list_refresh.visibility = if (bookings.list.isEmpty()) View.GONE else View.VISIBLE

            bookingListAdapter.setBookings(bookings.list, canLoadMore)
        } else {
            bookingListAdapter.appendBookings(bookings.list, canLoadMore)
        }

        page += if (canLoadMore) 1 else 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.NEW_BOOKING_REQUEST) {
            reloadData()
        }
    }
}
