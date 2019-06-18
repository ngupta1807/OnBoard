package com.tipbox.app.frag

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.tipbox.app.Login
import com.tipbox.app.MainActivity
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.restclient.GetApiData
import com.tipbox.app.util.*
import kotlinx.android.synthetic.main.fragment_paymethod.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BankAccount.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BankAccount.newInstance] factory method to
 * create an instance of this fragment.
 */
class BankAccount : Fragment(), View.OnClickListener, ApiCallback {
    override fun result(res: String) {
        var obj=JSONObject(res)
            Log.v("::","data:"+obj)
        if(type.equals("GET")){
                if (!obj.getString("status").equals("0")) {
                    GetApiData().getBankAccount(obj.getJSONObject("BankAccountDetails"),h_name,b_name,i_code,a_number)
                }else{
                    Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }else if(type.equals("POST")){
            if (obj.getString("message").contains("successfully",true)) {
                showDialog()
            }else{
                Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
            }

        }else if(type.equals("UPDATE")){
            if (obj.getString("message").contains("successfully",true)) {
                GetApiData().updateBankAccount(mApplication,activity)
            }else{
                Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showDialog() {
        val dialog = Dialog(mcon)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(com.tipbox.app.R.layout.popup_setting)

        val dialogButton = dialog.findViewById(com.tipbox.app.R.id.cancel) as TextView
        val titl_txt = dialog.findViewById(com.tipbox.app.R.id.titl_txt) as TextView
        val p_icon = dialog.findViewById(com.tipbox.app.R.id.p_icon) as ImageView
        val amt = dialog.findViewById(com.tipbox.app.R.id.amt) as EditText
        val singleframe = dialog.findViewById(com.tipbox.app.R.id.singleframe) as LinearLayout
        val doubleframe = dialog.findViewById(com.tipbox.app.R.id.doubleframe) as LinearLayout
        val _txt = dialog.findViewById(com.tipbox.app.R.id._txt) as TextView
        _txt.visibility=View.GONE
        _txt.text="Success!"
        p_icon.visibility=View.GONE
        amt.visibility=View.GONE
        doubleframe.visibility=View.GONE
        singleframe.visibility=View.VISIBLE
        titl_txt.setText("We have sent an email to your registered email address. Please verify your email to start using Tipboxme services.")
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val save = dialog.findViewById(com.tipbox.app.R.id.save) as TextView
        save.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val ok = dialog.findViewById(com.tipbox.app.R.id.ok) as TextView
        ok.setText("Ok")
        ok.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            activity!!.startActivity(Intent(mcon, Login::class.java))
            activity!!.finish()

        })

        dialog.show()

    }


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null
    @BindView(R.id.h_name)
    lateinit var h_name: EditText
    @BindView(R.id.a_number)
    lateinit var a_number: EditText
    @BindView(R.id.b_name)
    lateinit var b_name: EditText
    @BindView(R.id.i_code)
    lateinit var i_code: EditText
     var mValidate: Validate?=null
     var mApplication: MApplication?=null
    @BindView(R.id.p_registor)
    lateinit var action_registor: Button
    @BindView(com.tipbox.app.R.id.progressbar)
    lateinit var progressbar: ProgressBar
    var mcon: Context?=null
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
        val view = inflater.inflate(R.layout.fragment_paymethod, container, false)
        ButterKnife.bind(this, view)
        mcon=activity
        mApplication = activity!!.application as MApplication
        mValidate = Validate(activity!!)
        action_registor!!.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
        if(!saveObj.getString("userType").equals("0",true)){
            getBankDetail()
            action_registor.setText("Edit")
        }
    }

    private fun getBankDetail() {
        type="GET"
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@BankAccount, mcon, ServerUrls.BANKDETAIL).response(
                mApplication!!.getRestClient()!!.riseService.getBankDetail(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun postBankDetail() {
        type="POST"
        if (Utils.networkStatus(activity!!)) {
            if (
                mValidate!!.isValidName(h_name.getText().toString(), h_name,mcon!!.getString(R.string.e_hname)) &&
                mValidate!!.isValidBankName(a_number.getText().toString(), a_number,mcon!!.getString(R.string.e_aname)) &&
                mValidate!!.isValidName(b_name.getText().toString(), b_name,mcon!!.getString(R.string.e_bname)) &&
                mValidate!!.isValidSwiftCode(i_code.getText().toString(), i_code,mcon!!.getString(R.string.e_iname))) {

                ApiResponse(progressbar, this@BankAccount, mcon, ServerUrls.BANKDETAIL).response(
                    mApplication!!.getRestClient()!!.riseService.saveBankDetail(
                        ApiParameter().bankData(h_name, a_number, b_name, i_code),
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
            }

        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
    private fun updateBankDetail() {
        type="UPDATE"
        if (Utils.networkStatus(activity!!)) {
            if (
                mValidate!!.isValidName(h_name.getText().toString(), h_name,mcon!!.getString(R.string.e_hname)) &&
                mValidate!!.isValidName(a_number.getText().toString(), a_number,mcon!!.getString(R.string.e_aname)) &&
                mValidate!!.isValidName(b_name.getText().toString(), b_name,mcon!!.getString(R.string.e_bname)) &&
                mValidate!!.isValidName(i_code.getText().toString(), i_code,mcon!!.getString(R.string.e_iname))) {

                ApiResponse(progressbar, this@BankAccount, mcon, ServerUrls.BANKDETAIL).response(
                    mApplication!!.getRestClient()!!.riseService.putBankDetail(
                        ApiParameter().bankData(h_name, a_number, b_name, i_code),
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
            }

        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
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

    override fun onClick(view: View) {
        if (view.id == R.id.p_registor) {
            if(p_registor.text.equals("Save"))
                postBankDetail()
            else
                updateBankDetail()
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
        fun newInstance(param1: String, param2: String): BankAccount {
            val fragment = BankAccount()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
    lateinit  var type: String

}// Required empty public constructor
