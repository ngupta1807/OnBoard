package com.tipbox.app.frag


import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import android.widget.TextView
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import com.tipbox.app.BarcodeScan
import com.tipbox.app.QrCodeSwipe
import com.tipbox.app.R
import com.tipbox.app.ThankuScreen
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.restclient.GetApiData
import com.tipbox.app.util.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_qrcode_view.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.*
import kotlinx.android.synthetic.main.popup_setting.*
import org.json.JSONObject


class Setting : Fragment() ,View.OnClickListener, ApiCallback {

    fun newInstance(s: String,data:String): Setting {
        val newFragment = Setting()
        val bundle = Bundle()
        bundle.putString("actType", s)
        bundle.putString("p_data", data)
        newFragment.setArguments(bundle)
        return newFragment

    }
    override fun result(res: String) {
        if (res.contains("successfully", true)) {
            startActivity(Intent(mcon, ThankuScreen::class.java)
                .putExtra("company_logo",companyLogo)
            )
            activity!!.finish()
        } else {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
        //var obj=JSONObject(res)
        /*if(type.equals("GET")) {
            if(obj.getInt("status")==0){
            }
            else{
                var amt = obj.getJSONObject("UserSwipeSettings").getString("userSwipeAmt")
                mApplication!!.storeStringInPreference("swipe_amt",amt.toString())
                if (amt.equals("2")) {
                    viewVisible(one_dolar, one_select, one_tip, one_price, one_swipe)
                    viewHide(two_dollar, two_select, two_tip, two_price, two_swipe)
                    viewHide(three_dollar, three_select, three_tip, three_price, three_swipe)
                    viewHide(custom, custom_select, custom_tip, custom_price, custom_swipe)
                    var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black)))
                    ssb += SimpleSpanBuilder.Span(
                        "ADD CUSTOM",
                        ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black))
                    )

                    custom_swipe.text = ssb.build()
                    custom_swipe.setAllCaps(false)
                } else if (amt.equals("3")) {
                    viewHide(one_dolar, one_select, one_tip, one_price, one_swipe)
                    viewVisible(two_dollar, two_select, two_tip, two_price, two_swipe)
                    viewHide(three_dollar, three_select, three_tip, three_price, three_swipe)
                    viewHide(custom, custom_select, custom_tip, custom_price, custom_swipe)
                    var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black)))
                    ssb += SimpleSpanBuilder.Span(
                        "ADD CUSTOM",
                        ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black))
                    )

                    custom_swipe.text = ssb.build()
                    custom_swipe.setAllCaps(false)
                } else if (amt.equals("5")) {
                    viewHide(one_dolar, one_select, one_tip, one_price, one_swipe)
                    viewHide(two_dollar, two_select, two_tip, two_price, two_swipe)
                    viewVisible(three_dollar, three_select, three_tip, three_price, three_swipe)
                    viewHide(custom, custom_select, custom_tip, custom_price, custom_swipe)
                    var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black)))
                    ssb += SimpleSpanBuilder.Span(
                        "ADD CUSTOM",
                        ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black))
                    )

                    custom_swipe.text = ssb.build()
                    custom_swipe.setAllCaps(false)
                } else {
                    custom_price.setText("$" + amt)
                    viewHide(one_dolar, one_select, one_tip, one_price, one_swipe)
                    viewHide(two_dollar, two_select, two_tip, two_price, two_swipe)
                    viewHide(three_dollar, three_select, three_tip, three_price, three_swipe)
                    viewVisible(custom, custom_select, custom_tip, custom_price, custom_swipe)
                    custom_swipe.text = "$ PER SWIPE \n EDIT CUSTOM"
                    var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.white)))
                    ssb += SimpleSpanBuilder.Span(
                        "EDIT CUSTOM",
                        ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)),UnderlineSpan()
                    )

                    custom_swipe.text = ssb.build()
                    custom_swipe.setAllCaps(false)
                }
            }
        }else */

        if(type.equals("POST")){


            /*showResultDialog(obj.getString("message"))
            var amt = obj.getJSONObject("UserSwipeSettings").getString("userSwipeAmt")
            mApplication!!.storeStringInPreference("swipe_amt",amt)
            if(actType.equals("scan")){
                val intent = Intent(mcon, BarcodeScan::class.java)
                intent.putExtra("type","setting")
                startActivity(intent)
                activity!!.finish()
            }

            if(postType.equals("custom")) {
                    custom_swipe.text = "$ PER SWIPE \n EDIT CUSTOM"
                    var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.white)))
                    ssb += SimpleSpanBuilder.Span(
                        "EDIT CUSTOM",
                        ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)),UnderlineSpan()
                    )

                    custom_swipe.text = ssb.build()
                    custom_swipe.setAllCaps(false)
                    viewHide(one_dolar, one_select, one_tip, one_price, one_swipe)
                    viewHide(two_dollar, two_select, two_tip, two_price, two_swipe)
                    viewHide(three_dollar, three_select, three_tip, three_price, three_swipe)
                    viewVisible(custom, custom_select, custom_tip, custom_price, custom_swipe)
            }else{
                var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.black)))
                ssb += SimpleSpanBuilder.Span(
                    "EDIT CUSTOM",
                    ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)),UnderlineSpan()
                )
                custom_swipe.text = ssb.build()
                custom_swipe.setAllCaps(false)
            }*/

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.tipbox.app.R.id.one_dolar -> {
                viewVisible(one_dolar,one_select,one_tip,one_price,one_swipe)
                viewHide(two_dollar,two_select,two_tip,two_price,two_swipe)
                viewHide(three_dollar,three_select,three_tip,three_price,three_swipe)
                viewHide(custom,custom_select,custom_tip,custom_price,custom_swipe)
                postType="2"
                updateSwipeSetting("2")
            }
            com.tipbox.app.R.id.two_dollar -> {
                viewHide(one_dolar,one_select,one_tip,one_price,one_swipe)
                viewVisible(two_dollar,two_select,two_tip,two_price,two_swipe)
                viewHide(three_dollar,three_select,three_tip,three_price,three_swipe)
                viewHide(custom,custom_select,custom_tip,custom_price,custom_swipe)
                postType="3"
                updateSwipeSetting("3")
            }
            com.tipbox.app.R.id.three_dollar -> {
                viewHide(one_dolar,one_select,one_tip,one_price,one_swipe)
                viewHide(two_dollar,two_select,two_tip,two_price,two_swipe)
                viewVisible(three_dollar,three_select,three_tip,three_price,three_swipe)
                viewHide(custom,custom_select,custom_tip,custom_price,custom_swipe)
                postType="5"
                updateSwipeSetting("5")
            }
           com.tipbox.app.R.id.custom -> {
               if(custom_price.text.equals("$?")){
                   showDialog()
               }else {
                   postType="custom"
                   viewHide(one_dolar,one_select,one_tip,one_price,one_swipe)
                   viewHide(two_dollar,two_select,two_tip,two_price,two_swipe)
                   viewHide(three_dollar,three_select,three_tip,three_price,three_swipe)
                   viewVisible(custom,custom_select,custom_tip,custom_price,custom_swipe)
                   updateSwipeSetting(custom_price.text.toString().replace("$",""))
               }
            }
            com.tipbox.app.R.id.custom_select -> {
                //showDialog()
            }com.tipbox.app.R.id.custom_swipe -> {
                showDialog()
            }
            com.tipbox.app.R.id.pay_now -> {
                    type="POST"
                    if (Utils.networkStatus(mcon)) {
                        ApiResponse(progressbar, this@Setting, mcon, ServerUrls.TRANSFERAMOUNT).response(
                            mApplication!!.getRestClient()!!.riseService.transferAmount(
                                ApiParameter().performData(amt_price, 1, p_id),
                                JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                            ), mApplication!!
                        )
                    } else {
                        Toast.makeText(mcon, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
            }

        }
    }

    fun viewVisible(bg : RelativeLayout,select:ImageView,tip:TextView,price:TextView,swipe:TextView){
        bg.setBackgroundResource(com.tipbox.app.R.drawable.dark_box)
        tip.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
        price.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
        swipe.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
        select.visibility = View.VISIBLE
        var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.white)))
        if(price.text.equals("$?")) {
            ssb += SimpleSpanBuilder.Span(
                "ADD CUSTOM",
                ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)), UnderlineSpan()
            )
        }else {
            ssb += SimpleSpanBuilder.Span(
                "EDIT CUSTOM",
                ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)), UnderlineSpan()
            )
        }

        custom_swipe.text = ssb.build()
        custom_swipe.setAllCaps(false)


    }
    fun viewHide(bg : RelativeLayout,select:ImageView,tip:TextView,price:TextView,swipe:TextView){
        bg.setBackgroundResource(com.tipbox.app.R.drawable.box_bg)
        tip.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.blacki))
        price.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.blacki))
        swipe.setTextColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.blacki))
        select.visibility = View.GONE

        var ssb = SimpleSpanBuilder("\$ PER SWIPE \n", ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.blacki)))
        if(price.text.equals("$?")) {
            ssb += SimpleSpanBuilder.Span(
                "ADD CUSTOM",
                ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)), UnderlineSpan()
            )
        }else {
            ssb += SimpleSpanBuilder.Span(
                "EDIT CUSTOM",
                ForegroundColorSpan(ContextCompat.getColor(mcon, R.color.colorPrimary)), UnderlineSpan()
            )
        }
        custom_swipe.text = ssb.build()
        custom_swipe.setAllCaps(false)
    }

    fun showDialog() {
        val dialog = Dialog(getActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(com.tipbox.app.R.layout.popup_setting)

        val dialogButton = dialog.findViewById(com.tipbox.app.R.id.cancel) as TextView
        val amt = dialog.findViewById(com.tipbox.app.R.id.amt) as EditText
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val save = dialog.findViewById(com.tipbox.app.R.id.save) as TextView
        save.setOnClickListener(View.OnClickListener {
            if (
                mValidate!!.isValidName(amt.getText().toString(), amt,mcon!!.getString(R.string.e_amount)) &&
                mValidate!!.isValidAmount(amt.getText().toString(), amt,mcon!!.getString(R.string.e_amount))
            ) {
                custom_price.setText("$" + amt.text.toString())
                postType = "custom"

                viewHide(one_dolar, one_select, one_tip, one_price, one_swipe)
                viewHide(two_dollar, two_select, two_tip, two_price, two_swipe)
                viewHide(three_dollar, three_select, three_tip, three_price, three_swipe)
                viewVisible(custom, custom_select, custom_tip, custom_price, custom_swipe)
                updateSwipeSetting(custom_price.text.toString().replace("$", ""))
                dialog.dismiss()
            }
        })

        dialog.show()

    }

    fun showResultDialog(result:String) {
        val dialog = Dialog(getActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(com.tipbox.app.R.layout.popup_setting)

        val dialogButton = dialog.findViewById(com.tipbox.app.R.id.ok) as TextView
        val amt = dialog.findViewById(com.tipbox.app.R.id.amt) as EditText
        val p_icon = dialog.findViewById(com.tipbox.app.R.id.p_icon) as ImageView
        val titl_txt = dialog.findViewById(com.tipbox.app.R.id.titl_txt) as TextView
        val doubleframe = dialog.findViewById(com.tipbox.app.R.id.doubleframe) as LinearLayout
        val singleframe = dialog.findViewById(com.tipbox.app.R.id.singleframe) as LinearLayout
        p_icon.visibility=View.GONE
        amt.visibility=View.GONE
        doubleframe.visibility=View.GONE
        singleframe.visibility=View.VISIBLE
        titl_txt.setText(result)
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_setting, container, false)
        ButterKnife.bind(this, view)
        mcon=activity!!
        mApplication = activity!!.application as MApplication
            mValidate = Validate(activity!!)
        val args = arguments
        actType = args!!.getString("actType")

        Log.v("actType?:","p_data"+args!!.getString("p_data"))
        var pobj= GetApiData().getPerformerData(JSONObject(args!!.getString("p_data")),p_name,logo,mcon)
        Log.v("actType?:","actType"+pobj.getString("companyLogo"))
        companyLogo=pobj.getString("companyLogo").replace("\"//","/")
        p_id=pobj.getString("userId")
        return view
    }
    private fun getSwipeSetting() {
        type="GET"
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@Setting, mcon, ServerUrls.SWIPESETTING).response(
                mApplication!!.getRestClient()!!.riseService.viewSwipeSetting(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSwipeSetting(amt_price:String) {
        this.amt_price=amt_price
        pay_now.visibility=View.VISIBLE
        tip_amt.setText(""+amt_price)
        /*type="POST"
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@Setting, mcon, ServerUrls.SWIPESETTING).response(
                mApplication!!.getRestClient()!!.riseService.updateSwipeSetting(
                    ApiParameter().updateSwipe(amt_price),
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        one_dolar.setOnClickListener(this)
        two_dollar.setOnClickListener(this)
        three_dollar.setOnClickListener(this)
        custom.setOnClickListener(this)

        one_select.setOnClickListener(this)
        two_select.setOnClickListener(this)
        three_select.setOnClickListener(this)
        custom_select.setOnClickListener(this)

        custom_swipe.setOnClickListener(this)
        pay_now.setOnClickListener(this)
        var boldTypeface = Typeface.create(tv.getTypeface(), Typeface.BOLD)
        var regularTypeface = Typeface.create(tv.getTypeface(), Typeface.NORMAL)

        var ssb = SimpleSpanBuilder("Tap to Choose Tip ", CustomTypefaceSpan(regularTypeface))
        ssb += SimpleSpanBuilder.Span(
            "Amount", CustomTypefaceSpan(boldTypeface))
        ssb += SimpleSpanBuilder.Span(
            "\n for each Swipe", CustomTypefaceSpan(regularTypeface))
        tv.text = ssb.build()
        tv.setAllCaps(false)

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
    interface OnFragmentInteractionListener{
        fun onFragmentInteraction( uri: Uri)
    }

    var mApplication: MApplication?=null
    lateinit  var mcon: Context
    lateinit  var type: String
    var postType: String=""
    var actType: String=""
    private var mListener: OnFragmentInteractionListener? = null
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.one_dolar) lateinit var one_dolar: RelativeLayout
    @BindView(com.tipbox.app.R.id.two_dollar) lateinit var two_dollar: RelativeLayout
    @BindView(com.tipbox.app.R.id.three_dollar) lateinit var three_dollar: RelativeLayout
    @BindView(com.tipbox.app.R.id.custom) lateinit var custom: RelativeLayout
    @BindView(com.tipbox.app.R.id.one_select) lateinit var one_select: ImageView
    @BindView(com.tipbox.app.R.id.two_select) lateinit var two_select: ImageView
    @BindView(com.tipbox.app.R.id.three_select) lateinit var three_select: ImageView
    @BindView(com.tipbox.app.R.id.custom_select) lateinit var custom_select: ImageView

    @BindView(com.tipbox.app.R.id.one_tip) lateinit var one_tip: TextView
    @BindView(com.tipbox.app.R.id.one_price) lateinit var one_price: TextView
    @BindView(com.tipbox.app.R.id.one_swipe) lateinit var one_swipe: TextView

    @BindView(com.tipbox.app.R.id.two_tip) lateinit var two_tip: TextView
    @BindView(com.tipbox.app.R.id.two_price) lateinit var two_price: TextView
    @BindView(com.tipbox.app.R.id.two_swipe) lateinit var two_swipe: TextView

    @BindView(com.tipbox.app.R.id.three_tip) lateinit var three_tip: TextView
    @BindView(com.tipbox.app.R.id.three_price) lateinit var three_price: TextView
    @BindView(com.tipbox.app.R.id.three_swipe) lateinit var three_swipe: TextView

    @BindView(com.tipbox.app.R.id.custom_tip) lateinit var custom_tip: TextView
    @BindView(com.tipbox.app.R.id.custom_price) lateinit var custom_price: TextView
    @BindView(com.tipbox.app.R.id.custom_swipe) lateinit var custom_swipe: TextView
    @BindView(com.tipbox.app.R.id.tv) lateinit var tv: TextView
    @BindView(com.tipbox.app.R.id.p_name) lateinit var p_name: TextView
    @BindView(com.tipbox.app.R.id.tip_amt) lateinit var tip_amt: TextView
    @BindView(com.tipbox.app.R.id.logo) lateinit var logo: RelativeLayout
    @BindView(com.tipbox.app.R.id.pay_now) lateinit var pay_now: Button
    var mValidate: Validate?=null
    var p_id: String=""
    var companyLogo: String=""
    var amt_price: String=""
}