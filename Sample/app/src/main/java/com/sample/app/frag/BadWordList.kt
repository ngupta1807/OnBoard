package com.sample.app.frag

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.R
import com.sample.app.database.AccessDatabase
import com.sample.app.param.BWord
import com.sample.app.util.Validate
import com.sample.app.intrface.AdapterCallback


class BadWordList : Fragment(),View.OnClickListener, AdapterCallback {
    override fun onClickCallback(itemModel: BWord) {
        aDatabase.deleteRecord(itemModel.id,activity,badWordList,this@BadWordList,itemModel.title)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
           R.id.action_submit -> {
                if (Validate(mcon).isValidName(b_word.text.toString().trim(), b_word,getString(com.sample.app.R.string.black_list))) {
                    aDatabase.saveRecord(b_word.text.toString().trim(),b_word)
                    aDatabase.viewRecord(badWordList,this@BadWordList)
                }
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.sample.app.R.layout.fragment_blacklist, container, false)
        ButterKnife.bind(this, view)
        mcon=activity!!.applicationContext
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        submit.setOnClickListener(this)
        txtList = ArrayList()
        aDatabase = AccessDatabase(mcon)
        var mLayoutManager = LinearLayoutManager(mcon)
        badWordList.setLayoutManager(mLayoutManager)
        badWordList.setItemAnimator(DefaultItemAnimator())
        badWordList.addItemDecoration(DividerItemDecoration(mcon, LinearLayoutManager.VERTICAL));
        aDatabase.viewRecord(badWordList,this@BadWordList)

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
    private lateinit var txtList: ArrayList<BWord>
    private lateinit var aDatabase: AccessDatabase
    @BindView(com.sample.app.R.id.action_submit) lateinit var submit: Button
    @BindView(com.sample.app.R.id.b_word) lateinit var b_word: EditText
    @BindView(com.sample.app.R.id.bad_word_list) lateinit var badWordList: RecyclerView
}