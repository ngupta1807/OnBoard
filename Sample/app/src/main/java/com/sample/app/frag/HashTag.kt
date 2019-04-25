package com.sample.app.Frag

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.R


class Setting() : Fragment(),View.OnClickListener  {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.user -> {
                street.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                user.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            R.id.street -> {
                user.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                street.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
            else -> {
            }
        }
    }

    private var mListener: OnFragmentInteractionListener? = null
    @BindView(R.id.user) lateinit var user: TextView
    @BindView(R.id.street) lateinit var street: TextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "Setting"
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
    public interface OnFragmentInteractionListener{
        fun onFragmentInteraction( uri: Uri)
    }


}