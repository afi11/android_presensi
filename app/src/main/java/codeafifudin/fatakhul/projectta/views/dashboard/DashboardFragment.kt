package codeafifudin.fatakhul.projectta.views.dashboard

import android.R.attr.data
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import codeafifudin.fatakhul.projectta.MainActivity
import codeafifudin.fatakhul.projectta.R
import codeafifudin.fatakhul.projectta.controllers.DashboardController
import codeafifudin.fatakhul.projectta.models.ChartPresensi
import codeafifudin.fatakhul.projectta.utils.GetWaktu
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.text.DecimalFormat


class DashboardFragment : Fragment() {

    lateinit var thisParent: MainActivity
    lateinit var v: View
    lateinit var getWaktu: GetWaktu

    var pieEntriesMasuk: MutableList<PieEntry> = ArrayList()
    var pieEntriesPulang: MutableList<PieEntry> = ArrayList()
    var pieEntriesPresensi: MutableList<PieEntry> = ArrayList()
    var pieEntriesTugas: MutableList<PieEntry> = ArrayList()
    var dashboardController : DashboardController ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        thisParent = activity as MainActivity
        dashboardController = DashboardController(thisParent)
        getWaktu = GetWaktu(thisParent)
        v = inflater.inflate(R.layout.fragment_dashboard, container, false)
        v.txtDayNow.setText(getWaktu.getCurrentDay())
        v.txtTglNow.setText(getWaktu.currentDate())

        return v
    }

    override fun onStart() {
        super.onStart()
        getDataPresensi()
        getItemDashboard()
    }

    private fun getDataPresensi() {
        dashboardController!!.getPresensiChart("jam_masuk"){ data ->
            pieEntriesMasuk.clear()
            for (i in 0 until data.size){
                pieEntriesMasuk.add(PieEntry(data.get(i).data.toFloat(), data.get(i).label))
            }
            getPieChartPresensi(v.pieChartJamMasuk,pieEntriesMasuk)
        }
        dashboardController!!.getPresensiChart("jam_pulang"){ data ->
            pieEntriesPulang.clear()
            for (i in 0 until data.size){
                pieEntriesPulang.add(PieEntry(data.get(i).data.toFloat(), data.get(i).label))
            }
            getPieChartPresensi(v.pieChartJamPulang,pieEntriesPulang)
        }
        dashboardController!!.getPresensiChart("presensi"){ data ->
            pieEntriesPresensi.clear()
            for (i in 0 until data.size){
                pieEntriesPresensi.add(PieEntry(data.get(i).data.toFloat(), data.get(i).label))
            }
            getPieData(v.pieChartPresensiLainnya,pieEntriesPresensi)
        }
        dashboardController!!.getTugasChart { data ->
            pieEntriesTugas.clear()
            for(i in 0 until data.size){
                pieEntriesTugas.add(PieEntry(data.get(i).data.toFloat(), data.get(i).label))
            }
            getPieDataTugas(v.pieChartTugas,pieEntriesTugas)
        }
    }

    private fun getItemDashboard(){
        dashboardController!!.getItemDashboard {
            v.txtJamMasuk.setText(it.get(0).jamMasuk)
            v.txtJamPulang.setText(it.get(0).jamPulang)
            v.txtStatusMasuk.setText(it.get(0).ketMasuk)
            v.txtStatusPulang.setText(it.get(0).ketPulang)
        }
    }

    private fun getPieChartPresensi(pieChart: PieChart, pieEntries: MutableList<PieEntry>)
    {
        var colors : ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#dff936"))
        colors.add(Color.parseColor("#e0c216"))
        colors.add(Color.parseColor("#ef8009"))
        colors.add(Color.parseColor("#e54e4e"))
        colors.add(Color.parseColor("#01ba9e"))

        val pieDataSetDevice = PieDataSet(pieEntries, "")
        pieDataSetDevice.valueTextSize = 12f
        pieDataSetDevice.setColors(colors)

        val pieData = PieData(pieDataSetDevice)
        pieData.setValueTextSize(12f)
        pieDataSetDevice.valueFormatter = MyValueFormatter(DecimalFormat("###,###,##0.0"),pieChart)
        pieDataSetDevice.setDrawValues(true)
        pieChart.isDrawHoleEnabled = true
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 14f
        pieChart.setDrawEntryLabels(false)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun getPieData(pieChart: PieChart, pieEntries: MutableList<PieEntry>){
        var colors : ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#01ba9e"))
        colors.add(Color.parseColor("#4793f7"))
        colors.add(Color.parseColor("#e54e4e"))

        val pieDataSetDevice = PieDataSet(pieEntries, "")
        pieDataSetDevice.valueTextSize = 12f
        pieDataSetDevice.setColors(colors)

        val pieData = PieData(pieDataSetDevice)
        pieData.setValueTextSize(12f)
        pieDataSetDevice.valueFormatter = MyValueFormatter(DecimalFormat("###,###,##0.0"),pieChart)
        pieDataSetDevice.setDrawValues(true)
        pieChart.isDrawHoleEnabled = true
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 14f
        pieChart.setDrawEntryLabels(false)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun getPieDataTugas(pieChart: PieChart, pieEntries: MutableList<PieEntry>){
        var colors : ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#e54e4e"))
        colors.add(Color.parseColor("#01ba9e"))
        colors.add(Color.parseColor("#4793f7"))
        colors.add(Color.parseColor("#dff936"))


        val pieDataSetDevice = PieDataSet(pieEntries, "")
        pieDataSetDevice.valueTextSize = 12f
        pieDataSetDevice.setColors(colors)

        val pieData = PieData(pieDataSetDevice)
        pieData.setValueTextSize(12f)
        pieDataSetDevice.valueFormatter = MyValueFormatter(DecimalFormat("###,###,##0.0"),pieChart)
        pieDataSetDevice.setDrawValues(true)
        pieChart.isDrawHoleEnabled = true
        pieChart.legend.isEnabled = true
        pieChart.legend.textSize = 14f
        pieChart.setDrawEntryLabels(false)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.data = pieData
        pieChart.invalidate()
    }

}

class MyValueFormatter(format: DecimalFormat, pieChart: PieChart?) : PercentFormatter() {
    lateinit var mFormat: DecimalFormat
    var mPieChart: PieChart?
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble()) + "%"
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
        return if (mPieChart != null && mPieChart!!.isUsePercentValuesEnabled) {
            getFormattedValue(value)
        } else {
            mFormat.format(value.toDouble())
        }
    }

    init {
        mFormat = format
        mPieChart = pieChart
    }
}
