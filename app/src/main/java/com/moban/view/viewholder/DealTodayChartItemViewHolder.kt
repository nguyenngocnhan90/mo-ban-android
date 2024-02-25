package com.moban.view.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.moban.R
import com.moban.model.data.chart.ChartData
import com.moban.util.Font
import com.moban.util.Util
import kotlinx.android.synthetic.main.item_today_deal_chart.view.*
import java.util.ArrayList

/**
 * Created by LenVo on 6/21/18.
 */
class DealTodayChartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private var chartData: List<ChartData>? = null
    private lateinit var context: Context
    fun bind(data: List<ChartData>?) {
        chartData = data
        context = itemView.context
        initCombinedChart()
        val dataChart = CombinedData()
        dataChart.setData(generateBarData())
        dataChart.setData(generateLineData())

        itemView.today_deal_chart.data = dataChart
        itemView.today_deal_chart.notifyDataSetChanged()
        itemView.today_deal_chart.requestLayout()
    }

    private fun initCombinedChart() {
        itemView.today_deal_chart.description.isEnabled = false
        itemView.today_deal_chart.isScaleXEnabled = false
        itemView.today_deal_chart.isScaleYEnabled = false
        itemView.today_deal_chart.isDragYEnabled = false
        itemView.today_deal_chart.isHighlightPerDragEnabled = false
        itemView.today_deal_chart.drawOrder = arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE)
        setupLegend()
        setupxAxis()
        setupLeftAxis()
        setupRightAxis()
        setupNoDataText()
    }

    private fun setupNoDataText() {
        itemView.today_deal_chart.setNoDataText(context.getString(R.string.no_data))
        itemView.today_deal_chart.setNoDataTextTypeface(Font.regularTypeface(context))
        itemView.today_deal_chart.setNoDataTextColor(Util.getColor(context, R.color.white))
    }

    private fun setupRightAxis() {
        val rightAxis = itemView.today_deal_chart.axisRight
        rightAxis.isEnabled = false
    }

    private fun setupLeftAxis() {
        val leftAxis = itemView.today_deal_chart.axisLeft
        leftAxis.isEnabled = false
        leftAxis.setDrawAxisLine(false)
    }

    private fun setupxAxis() {
        val xAxis = itemView.today_deal_chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0F
        xAxis.granularity = 1F
        xAxis.spaceMax = 1F
        xAxis.textColor = Util.getColor(context, R.color.white_70)
        xAxis.typeface = Font.regularTypeface(context)
        val formatter = IAxisValueFormatter { value, axis ->
            val total = axis?.mEntries?.size
            val month = value.toInt()

            if (month == 0 || (month == (total?.minus(1)))) {
                ""
            } else {
                var result = context.getString(R.string.month_chart) + " "+ month.toString()
                chartData?.let {
                    if (month - 1 < it.size) {
                        result = context.getString(R.string.month_chart) + it[month - 1].month
                    }
                }

                result
            }
        }
        xAxis.valueFormatter = formatter
    }

    private fun setupLegend() {
        val legend = itemView.today_deal_chart.legend
        legend.isWordWrapEnabled = true
        legend.horizontalAlignment =  Legend.LegendHorizontalAlignment.CENTER
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textColor = Util.getColor(context, R.color.color_white_50)
        legend.typeface = Font.regularTypeface(context)
    }

    private fun generateLineData(): LineData {
        val data = LineData()
        chartData?.let {
            val entries: MutableList<Entry> = (0..(it.size-1)).mapTo(ArrayList()) {
                index -> Entry((index + 1).toFloat(), it[index].current.toFloat())
            }

            val color = Util.getColor(context, R.color.colorPrimary)

            val set = LineDataSet(entries, "Hợp Đồng")
            set.color = color
            set.lineWidth = 2F
            set.setCircleColor(color)
            set.circleRadius = 5F
            set.circleHoleRadius = 3F
            set.setCircleColorHole(Util.getColor(context, R.color.colorPrimary70))
            set.setCircleColor(Util.getColor(context, R.color.color_green_border))
            set.fillColor = Util.getColor(context, R.color.color_green_border)

            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            set.setDrawValues(false)

            data.addDataSet(set)
        }
        return data
    }

    private fun generateBarData(): BarData {
        val data = BarData()
        chartData?.let {
            val entries: MutableList<BarEntry> = (0..(it.size - 1)).mapTo(ArrayList()) {
                index -> BarEntry((index + 1).toFloat(), it[index].total.toFloat())
            }
            val set = BarDataSet(entries, "Tổng Giao Dịch")
            val color = Util.getColor(context, R.color.color_blue_70)

            set.color = color
            set.setDrawValues(false)

            data.addDataSet(set)

//        val barSpace = 0.05F
            val barWidth = 0.95F
            data.barWidth = barWidth
//        data.groupBars( 0.0F, 0F, barSpace)
        }
        return data
    }
}
