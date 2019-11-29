package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import edu.newhaven.krikorherlopian.android.vproperty.R
import edu.newhaven.krikorherlopian.android.vproperty.model.Property
import kotlinx.android.synthetic.main.chart_fragment.view.*
import org.eazegraph.lib.models.PieModel

class ChartFragment : Fragment() {
    var root: View? = null
    var list: MutableList<Any> = mutableListOf<Any>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.chart_fragment, container, false)
        val section = arguments?.getInt(ARG_SECTION_NUMBER)
        val db = FirebaseFirestore.getInstance()
        var docRef = db.collection("properties").whereEqualTo("disabled", "N")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var propertyList: MutableList<Property> = document.toObjects(Property::class.java)
                setUpChart(propertyList, section!!)
            }
        }
        return root
    }

    private fun setUpChart(propertyList: MutableList<Property>, section: Int) {
        if (section == 1) {
            var sif = 0
            var con = 0
            var muf = 0
            var tow = 0
            var oth = 0
            var vacantLand = 0//val
            var mob = 0
            var cou = 0
            var apt = 0


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
        }
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