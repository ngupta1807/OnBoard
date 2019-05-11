package com.sample.app.frag

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.R
import com.sample.app.util.Common
import com.sample.app.util.Validate
import kotlinx.android.synthetic.main.fragment_hashtag.*


class HashTag : Fragment(),View.OnClickListener  {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.action_submit -> {
                if (Validate(mcon).isValidName(hash_tag.text.toString().trim(), hash_tag ,getString(R.string.h_tag))) {
                    Common(mcon).writeData(hash_tag.text.toString().trim())
                    Toast.makeText(mcon,getString(R.string.h_tag)+" saved", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_hashtag, container, false)
        ButterKnife.bind(this, view)
        mcon=activity!!.applicationContext
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submit.setOnClickListener(this)
        hash_tag.append(Common(mcon).readData())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction( uri: Uri)
    }

    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mcon: Context
    @BindView(R.id.action_submit) lateinit var submit: Button
    @BindView(R.id.hash_tag) lateinit var hash_tag: EditText
}