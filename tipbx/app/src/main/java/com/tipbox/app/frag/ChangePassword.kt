package com.tipbox.app.frag

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
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.tipbox.app.R

import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import org.json.JSONObject
import com.tipbox.app.interfce.ApiCallback


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChangePassword.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ChangePassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePassword : Fragment(), View.OnClickListener, ApiCallback {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
            Common().changeFragment(activity,"1")
        } else {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null
     var mValidate: Validate?=null
     var mApplication: MApplication?=null

    @BindView(com.tipbox.app.R.id.action_login)
    lateinit var action_login: Button
    @BindView(com.tipbox.app.R.id.new_pass)
    lateinit var new_pass: EditText
    @BindView(com.tipbox.app.R.id.old_pass)
    lateinit var old_pass: EditText
    @BindView(com.tipbox.app.R.id.c_new_pass)
    lateinit var c_new_pass: EditText
    @BindView(com.tipbox.app.R.id.progressbar)
    lateinit var progressbar: ProgressBar
    var mcon:Context?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_change, container, false)
        ButterKnife.bind(this, view)
        mcon=activity
        mApplication = activity!!.application as MApplication
        mValidate = Validate(activity!!)
        action_login!!.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onClick(view: View) {
        if (view.id == com.tipbox.app.R.id.action_login) {
            validate()
        }
    }

    private fun validate() {
        Log.d("device Token", "token:.." + mApplication!!.getRegToken(activity!!))
        if (Utils.networkStatus(activity!!)) {
            if (mValidate!!.isValidOldPassword(old_pass!!.text.toString(), old_pass!!) &&
                mValidate!!.isValidNewPassword(new_pass!!.text.toString(), new_pass!!) &&
                mValidate!!.isValidConfirmPassword(c_new_pass!!.text.toString(), c_new_pass!!,new_pass!!.text.toString()) &&
                mValidate!!.isValidNewOldPassword(new_pass!!.text.toString(), new_pass!!,old_pass!!.text.toString())
            ) {
                ApiResponse(progressbar, this@ChangePassword, mcon, ServerUrls.CHANGEPASSWORD).response(
                    mApplication!!.getRestClient()!!.riseService.changePassword(
                        ApiParameter().changePwdData(new_pass, old_pass),
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
            }
        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Informatiom.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): ChangePassword {
            val fragment = ChangePassword()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
