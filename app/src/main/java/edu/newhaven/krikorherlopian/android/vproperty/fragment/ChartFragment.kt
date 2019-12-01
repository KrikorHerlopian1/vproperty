package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.chart_fragment.*
import kotlinx.android.synthetic.main.chart_fragment.view.*
import org.eazegraph.lib.models.PieModel
import java.util.*

class ChartFragment : Fragment() {
    var root: View? = null
    var section = 0
    var setUp = false
    var visible = true
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.chart_fragment, container, false)
        section = arguments?.getInt(ARG_SECTION_NUMBER)!!
        return root
    }

    override fun onResume() {
        if (!visible)
            update()
        super.onResume()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && visible) {
            update()
            visible = false
        }

    }

    fun update() {
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("properties").whereEqualTo("disabled", "N")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var propertyList: MutableList<Property> = document.toObjects(Property::class.java)
                setUpChart(propertyList, section)
            }
        }
    }


    private fun setUpChart(propertyList: MutableList<Property>, section: Int) {
        if (section == 1) {
            var sif = 0
            var con = 0
            var muf = 0
            var apt = 0

            for (propert in propertyList) {
                if (propert.homeFacts.homeType.equals("CON"))
                    con++
                else if (propert.homeFacts.homeType.equals("SIF"))
                    sif++
                else if (propert.homeFacts.homeType.equals("MUF"))
                    muf++
                else if (propert.homeFacts.homeType.equals("APT"))
                    apt++
            }

            root!!.mChart.setDrawGridBackground(false)
            root!!.mChart.setDrawBorders(false)
            root!!.mChart.clear()
            root!!.mChart.visibility = View.VISIBLE
            root!!.piechart.visibility = View.GONE
            val yVals = ArrayList<BarEntry>()
            yVals.add(BarEntry(0f, sif.toFloat(), "SIF"))
            yVals.add(BarEntry(1f, con.toFloat(), "CON"))
            yVals.add(BarEntry(2f, muf.toFloat(), "MUF"))
            yVals.add(BarEntry(3f, apt.toFloat(), "APT"))
            var set: BarDataSet? = null
            mChart.setPinchZoom(false)
            mChart.isFocusable = false
            mChart.isDoubleTapToZoomEnabled = false
            mChart.setScaleEnabled(false)
            mChart.isClickable = false
            mChart.setDrawValueAboveBar(true)
            mChart.setDrawBarShadow(false)
            mChart.setDrawGridBackground(false)
            mChart.description.isEnabled = false

            mChart.setTouchEnabled(false)
            mChart.xAxis.granularity = java.lang.Float.parseFloat("1")

            mChart.xAxis.setDrawLabels(true)
            mChart.axisLeft.isEnabled = false
            val xLabel = ArrayList<String>()
            xLabel.add(resources.getString(R.string.single_family))
            xLabel.add(resources.getString(R.string.condo))
            xLabel.add(resources.getString(R.string.multi_family))
            xLabel.add(resources.getString(R.string.apartment))
            mChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)

            mChart.axisRight.isEnabled = true

            mChart.axisRight.spaceMax = 1.0f
            mChart.legend.isEnabled = false

            mChart.xAxis.isEnabled = true
            mChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            mChart.xAxis.setDrawLabels(true)
            mChart.xAxis.setDrawAxisLine(false)
            mChart.xAxis.setDrawGridLines(false)
            mChart.xAxis.setDrawLimitLinesBehindData(false)
            mChart.xAxis.textSize = 10.0f
            mChart.xAxis.textColor = resources.getColor(R.color.colorPrimaryDark)
            set = BarDataSet(yVals, "")

            set.setDrawIcons(false)

            val colors = ArrayList<Int>()
            val COLORS_0 = intArrayOf(rgb("#a377a6"))
            val COLORS_1 = intArrayOf(rgb("#855988"))
            val COLORS_2 = intArrayOf(rgb("#483475"))
            val COLORS_3 = intArrayOf(rgb("070b34"))
            for (c in COLORS_0)
                colors.add(c)
            for (c in COLORS_1)
                colors.add(c)
            for (c in COLORS_2)
                colors.add(c)
            for (c in COLORS_3)
                colors.add(c)

            set.colors = colors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set)


            val data = BarData(dataSets)
            data.setValueTextSize(12f)
            data.setValueTextColor(R.color.colorAccent)
            data.barWidth = 0.9f
            mChart.visibility = View.VISIBLE
            mChart.data = data
            mChart.invalidate()
            mChart.animateY(2000)
        } else if (section == 2) {
            var forSale = 0
            var forRent = 0
            for (propert in propertyList) {
                if (propert.homeFacts.isRent)
                    forRent++
                else
                    forSale++
            }
            var total = forSale + forRent

            root!!.mChart.visibility = View.GONE
            root!!.piechart.visibility = View.VISIBLE

            root!!.piechart.clearChart()
            root!!.piechart.addPieSlice(
                PieModel(
                    resources.getString(R.string.for_rent) + ": " + forRent + " " + resources.getString(
                        R.string.properties
                    ),
                    "%.${2}f".format(((forRent * 100.0) / total)).toFloat()
                    , Color.parseColor("#855988")
                )
            )
            root!!.piechart.addPieSlice(
                PieModel(
                    resources.getString(R.string.for_sale) + ": " + +forSale + " " + resources.getString(
                        R.string.properties
                    ),
                    "%.${2}f".format(((forSale * 100.0) / total)).toFloat()
                    , Color.parseColor("#483475")
                )
            )
            root!!.piechart.startAnimation()
            setUp = true
        }
    }

    fun rgb(hex: String): Int {
        val color = java.lang.Long.parseLong(hex.replace("#", ""), 16).toInt()
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color shr 0 and 0xFF
        return Color.rgb(r, g, b)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): ChartFragment {
            return ChartFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}