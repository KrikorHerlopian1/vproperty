package edu.newhaven.krikorherlopian.android.vproperty.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import edu.newhaven.krikorherlopian.android.vproperty.R
import kotlinx.android.synthetic.main.question.view.*

class QuestionFragment : Fragment() {

    var root: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.question, container, false)
        Glide.with(context!!).load(arguments?.getInt(arg_image)!!)
            .placeholder(R.drawable.placeholderdetail)
            .into(
                root!!.image1!!
            )
        root?.option1?.text = arguments?.getString(arg_option1)
        root?.option2?.text = arguments?.getString(arg_option2)
        root?.option3?.text = arguments?.getString(arg_option3)
        root?.option4?.text = arguments?.getString(arg_option4)
        return root
    }

    companion object {
        private const val arg_option1 = "arg_option1"
        private const val arg_option2 = "arg_option2"
        private const val arg_image = "arg_image"
        private const val arg_option3 = "arg_option3"
        private const val arg_option4 = "arg_option4"
        private const val arg_correct = "arg_correct"
        /**   private const val ARG_SECTION_NUMBER = "section_number"
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(
            image: Int,
            option1: String,
            option2: String,
            option3: String,
            option4: String,
            correctAnswer: Int
        ): QuestionFragment {
            return QuestionFragment().apply {
                arguments = Bundle().apply {
                    putInt(arg_image, image)
                    putString(arg_option1, option1)
                    putString(arg_option2, option2)
                    putString(arg_option3, option3)
                    putString(arg_option4, option4)
                    putInt(arg_correct, correctAnswer)
                }
            }
        }
    }
}