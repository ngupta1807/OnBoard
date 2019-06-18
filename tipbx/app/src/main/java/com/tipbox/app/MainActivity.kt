package com.tipbox.app

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.JsonObject
import com.tipbox.app.adapter.CustomExpandableListAdapter
import com.tipbox.app.frag.*
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.interfce.FragementImp
import com.tipbox.app.interfce.FragmentCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import kotlinx.android.synthetic.main.ac_main.*
import kotlinx.android.synthetic.main.list_adapter_history.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(),View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
    FragementImp, ApiCallback, FragmentCallback {
    override fun getFragmentName(res: String) {
        setFragement(res)
    }

    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, Login::class.java))
            finish()
        } else {
            Toast.makeText(this, R.string.try_again, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        displayView(p0.itemId)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @BindView(R.id.title)
    lateinit var title: TextView
    @BindView(R.id.drawer_layout)
    lateinit var mDrawerLayout: DrawerLayout
    @BindView(R.id.dashboard)
    lateinit var dashboard: TextView
    @BindView(R.id.profile)
    lateinit var profile: TextView
    @BindView(R.id.p_method)
    lateinit var p_method: TextView
    @BindView(R.id.swipe)
    lateinit var swipe: TextView
    @BindView(R.id.pay_history)
    lateinit var pay_history: TextView
    @BindView(R.id.notification)
    lateinit var notification: TextView
    @BindView(R.id.r_tip)
    lateinit var r_tip: TextView
    @BindView(R.id.view_qrcode)
    lateinit var view_qrcode: TextView
    /*@BindView(R.id.tip_dashboard)
    lateinit var tip_dashboard: TextView
    @BindView(R.id.user_dashboard)
    lateinit var user_dashboard: TextView*/
    @BindView(R.id.nav)
    lateinit var nav: ImageView
    @BindView(R.id.policy)
    lateinit var policy: TextView
    @BindView(R.id.use)
    lateinit var use: TextView
    @BindView(R.id.help)
    lateinit var help: TextView
    @BindView(R.id.logout)
    lateinit var logout: TextView
    @BindView(R.id.imageView)
    lateinit var imageView: RelativeLayout
    @BindView(R.id.loader)
    lateinit var loader: ImageView
    @BindView(R.id.username)
    lateinit var username: TextView
    @BindView(R.id.toolbar)
    lateinit var bar: RelativeLayout
    @BindView(R.id.change)
    lateinit var change: ImageView
    @BindView(R.id.item_img)
    lateinit var item_img: ImageView
    @BindView(R.id.close)
    lateinit var close: ImageView
    @BindView(R.id.give)
    lateinit var give: ImageView
    @BindView(R.id.receive)
    lateinit var receive: ImageView
    var mApplication: MApplication?=null
    var expandableListTitle: ArrayList<String>?=null
    var groupPosition: Int=0
    lateinit var obj: JSONObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)
        ButterKnife.bind(this)
        mApplication = this.application as MApplication
        var toggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            null,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mDrawerLayout.addDrawerListener(toggle)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.syncState()

        /*var mDrawerToggle =  ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

        *//** Called when a drawer has settled in a completely closed state. *//*
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            getSupportActionBar().setTitle(mTitle);
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        *//** Called when a drawer has settled in a completely open state. *//*
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            getSupportActionBar().setTitle(mDrawerTitle);
            session = new SessionManager(getApplicationContext());
            user = session.getUserDetails();
            profilepic.setImageBitmap(StringToBitMap(user.get(SessionManager.KEY_PROFILEPIC)));
            name.setText(user.get(SessionManager.KEY_NAME));
            lastsynced.setText(lastsynced());
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    };*/

        obj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
        var expandableListView = findViewById(R.id.expandableListView) as ExpandableListView
        var expandableListDetail = ExpandableListData.getData(obj.getString("userType"))
        expandableListTitle = ArrayList<String>(expandableListDetail.keys)
        var expandableListAdapter = CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail,this)
        expandableListView.setAdapter(expandableListAdapter)
        expandableListView.setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener { groupPosition ->
           /* expandableListTitle.get(groupPosition)*/
        })

        expandableListView.setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener { groupPosition ->
            /* expandableListTitle.get(groupPosition) */
        })

        expandableListView.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
           /* expandableListTitle.get(groupPosition) */
            setFragement(expandableListDetail.get(expandableListTitle!!.get(groupPosition))!!.get(childPosition))
            false
        })




        dashboard.setOnClickListener(this)
        profile.setOnClickListener(this)
        p_method.setOnClickListener(this)
        swipe.setOnClickListener(this)
        pay_history.setOnClickListener(this)
        notification.setOnClickListener(this)
        r_tip.setOnClickListener(this)
        change.setOnClickListener(this)
        view_qrcode.setOnClickListener(this)
       /* tip_dashboard.setOnClickListener(this)
        user_dashboard.setOnClickListener(this)*/
        logout.setOnClickListener(this)
        policy.setOnClickListener(this)
        use.setOnClickListener(this)
        help.setOnClickListener(this)
        nav.setOnClickListener(this)
        give.setOnClickListener(this)
        receive.setOnClickListener(this)
        username.setOnClickListener(this)
        imageView.setOnClickListener(this)
        item_img.setOnClickListener(this)
        close.setOnClickListener(this)
        setData()

    }



    private fun setData( ) {

        username.isClickable=true
        imageView.isClickable=true
        item_img.isClickable=true

        try {
            if(!obj.getString("userNameFirst").equals("null",true)){
                username.setText(Common().getUserFullName(obj.getString("userNameFirst"),
                    obj.getString("userNameFirst"),obj.getString("userNameFirst")))
            }
            if(!obj.getString("profileImage").equals("null",true)) {
                Glide.with(this).load(obj.getString("profileImage").replace("\"//","/")).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.background = resource
                        }
                    }
                })
            }





            if(mApplication!!.getStringFromPreference("session").equals("give")) {  // qr code == >server response =0
                give.setBackgroundResource(R.drawable.give_active)
                receive.setBackgroundResource(R.drawable.receive_unactive)
            }
            else{
                give.setBackgroundResource(R.drawable.give_unactive)
                receive.setBackgroundResource(R.drawable.receive_active)
            }

            if(intent.extras.get("activity").equals(""))
                displayView(0)
            else
                displayView(Integer.parseInt(intent.extras.get("activity").toString()))
        } catch ( ex:Exception) {
            Log.v("r","er:"+ex.message)
            //displayView(0)
        }
    }


    private fun displayView(position: Int) {
        var fragment: android.support.v4.app.Fragment? = null
        when (position) {
            0 -> {
                nav.visibility=View.VISIBLE
                close.visibility=View.GONE
                title.setTextColor(resources.getColor(R.color.blacki))
                if(obj.getString("userNameFirst").equals("null",true)){
                        title.text = resources.getString(R.string.profile)
                        nav.setTag("nav")
                        bar.setBackgroundResource(R.color.colorAccent)
                        nav.setBackgroundResource(R.drawable.menu_burger_icon)
                        change.visibility = View.VISIBLE
                        fragment = Profile()
                }else {
                    title.text = ""
                    nav.setTag("nav")
                    change.visibility=View.GONE
                    Log.v("Login","sesion : "+mApplication!!.getStringFromPreference("session"))
                    if (mApplication!!.getStringFromPreference("session").equals("give")) {
                        bar.setBackgroundResource(R.color.white)
                        nav.setBackgroundResource(R.drawable.menu_burger_icon)
                        fragment = QrCode()
                    } else {
                        bar.setBackgroundResource(R.color.tips_yr)
                        nav.setBackgroundResource(R.drawable.menu_burger_icon_white)
                        fragment = StreetQrCode()
                    }
                }
            }

            1 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                change.visibility=View.VISIBLE
                title.text = resources.getString(R.string.profile)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                bar.setBackgroundResource(R.color.colorAccent)
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                fragment = Profile()
            }
            2 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.p_method)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Informatiom()
            }
            3 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.swipe)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Setting().newInstance("swipe","")
            }

            5 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.pay_history)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = PaymentHistory()
            }
            6 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.notification)
                title.setTextColor(resources.getColor(R.color.blacki))
                nav.setTag("nav")
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Notification()
            }

            7 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                title.text = resources.getString(R.string.bank)
                fragment = BankAccount()
            }
            8 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.view_qrcode)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                    fragment = QrcodeMine()
            }

            10 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.tip_dashboard)
                nav.setTag("nav")
                change.visibility=View.GONE
                title.setTextColor(resources.getColor(R.color.blacki))
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                //fragment = QrCode()
                fragment = Informatiom()
            }
            11 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.user_dashboard)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = QrCode()
            }
            12 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.policy)
                nav.setTag("nav")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Informatiom()
            }
            13 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = resources.getString(R.string.use)
                title.setTextColor(resources.getColor(R.color.blacki))
                nav.setTag("nav")
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Informatiom()
            }
            14 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.setTextColor(resources.getColor(R.color.blacki))
                title.text = resources.getString(R.string.help)
                nav.setTag("nav")
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.menu_burger_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = Informatiom()
            }
            15 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.setTextColor(resources.getColor(R.color.blacki))
                title.text = resources.getString(R.string.change)
                nav.setTag("back")
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.back_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                fragment = ChangePassword()
            }
            16 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.setTextColor(resources.getColor(R.color.blacki))
                nav.setTag("profile_back")
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.back_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                title.text = resources.getString(R.string.r_tip)
                fragment = ReceivingTipDisplay()
            }
            17 ->{
                nav.visibility=View.VISIBLE
                close.visibility=View.INVISIBLE
                nav.setBackgroundResource(R.drawable.back_btn)
                var boldTypeface = Typeface.create(title.getTypeface(), Typeface.BOLD)
                var regularTypeface = Typeface.create(title.getTypeface(),Typeface.NORMAL)

                var ssb = SimpleSpanBuilder("Tap to Choose Tip", ForegroundColorSpan(ContextCompat.getColor(this, com.tipbox.app.R.color.white))
                    ,CustomTypefaceSpan(regularTypeface)
                )
                ssb += SimpleSpanBuilder.Span(
                    " Amount",
                    ForegroundColorSpan(ContextCompat.getColor(this, com.tipbox.app.R.color.colorPrimary)), CustomTypefaceSpan(boldTypeface)
                )

                title.text = ssb.build()
                title.setTextColor(resources.getColor(R.color.white))
                nav.setTag("swipe")
                change.visibility=View.GONE
                bar.setBackgroundResource(R.color.tips_yr)
                fragment = Setting().newInstance("scan",intent.getStringExtra("p_data"))
            }
            18 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                title.text = ""
                title.setTextColor(resources.getColor(R.color.blacki))
                nav.setTag("nav")
                change.visibility=View.GONE
                bar.setBackgroundResource(R.color.tips_yr)
                nav.setBackgroundResource(R.drawable.menu_burger_icon_white)
                fragment = StreetQrCode()
            }
            19 -> {
                nav.visibility=View.INVISIBLE
                close.visibility=View.VISIBLE
                nav.setTag("display_back")
                title.setTextColor(resources.getColor(R.color.blacki))
                change.visibility=View.GONE
                nav.setBackgroundResource(R.drawable.back_icon)
                bar.setBackgroundResource(R.color.colorAccent)
                title.text = resources.getString(R.string.r_tip)
                fragment = ReceivingTipSet()
            }
            else -> {
            }
        }
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.container, fragment)
            ft.commit()
        }
        if(!title.text.equals("")) {
            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.nav -> {
                backNavHandle()
            }
            R.id.change -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                displayView(15)
            }
            R.id.view_qrcode -> {
                var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
                if(saveObj.getString("userType").equals("0",true)) {
                    showDialog("Please first setup start receiving tip process.")
                }else {
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                    displayView(8)
                }
            }
            R.id.pay_history -> {
                mDrawerLayout.closeDrawer(GravityCompat.START)

                displayView(5)
            }
            R.id.swipe -> {
                mDrawerLayout.closeDrawer(GravityCompat.START)

                displayView(3)
            }
            R.id.username -> {
                setFragement("Profile")
            }
            R.id.imageView -> {
                setFragement("Profile")
            }
            R.id.item_img -> {
                setFragement("Profile")
            }
            R.id.close -> {
                setFragement("Dashboard")
            }

            /*R.id.give -> {
                var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
                if(saveObj.getString("userType").equals("0",true)) {
                    showDialog("Please first setup start receiving tip process.")
                }else {
                    give.setBackgroundResource(R.drawable.give_active)
                    receive.setBackgroundResource(R.drawable.receive_unactive)
                    mApplication!!.storeStringInPreference("session", "give")
                    if (title.text.equals("")) {
                        //mDrawerLayout.closeDrawer(GravityCompat.START)
                        displayView(0)
                    }
                }

            }
            R.id.receive -> {
                var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
                if(saveObj.getString("userType").equals("0",true)) {
                    showDialog("Please first setup start receiving tip process.")
                }else {
                    give.setBackgroundResource(R.drawable.give_unactive)
                    receive.setBackgroundResource(R.drawable.receive_active)
                    mApplication!!.storeStringInPreference("session", "receive")
                    if (title.text.equals("")) {
                        // mDrawerLayout.closeDrawer(GravityCompat.START)
                        displayView(0)
                    }
                }
            }*/
        }
    }

    fun setFragement(res:String){
        if(res.startsWith("Payment")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(2)
        }
        if(res.startsWith("[Start")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(7)
        }
        if(res.startsWith("Sign")){
            showDialog("Do you want to sign out from the app?")
        }
        if(res.startsWith("Privacy")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(12)
        }
        if(res.startsWith("Terms")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(13)
        }
        if(res.startsWith("Help")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(14)
        }
        if(res.equals("Dashboard")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(0)
        }
        if(res.equals("Profile")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(1)
        }
        if(res.equals("Notification")){
            mDrawerLayout.closeDrawer(GravityCompat.START)
            displayView(6)
        }
    }
    override fun onBackPressed() {
        backHandle()
    }


    fun backHandle(){
        if (nav.getTag().equals("back")) {
            displayView(1)
        } else if(nav.getTag().equals("display_back")){
            var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
            if(saveObj.getString("profileImage").equals("null",true)) {
                displayView(7)
            }else{
                displayView(19)
            }
        }else if(nav.getTag().equals("profile_back")){
            var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
            if(saveObj.getString("profileImage").equals("null",true)) {
                displayView(19)
            }else{
                displayView(7)
            }
        }

        else if(nav.getTag().equals("swipe")){
            val intent = Intent(this, BarcodeScan::class.java)
            intent.putExtra("type","qrcode")
            startActivity(intent)
            finish()
        }
        else{
            this.finish()
        }
    }
    fun backNavHandle(){

        if(nav.getTag().equals("back")) {
            displayView(1)
        } else if(nav.getTag().equals("display_back")){
            var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
            if(saveObj.getString("profileImage").equals("null",true)) {
                displayView(7)
            }else{
                displayView(19)
            }
        }else if(nav.getTag().equals("profile_back")){
            var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
            if(saveObj.getString("profileImage").equals("null",true)) {
                displayView(19)
            }else{
                displayView(7)
            }
        }else if(nav.getTag().equals("swipe")){
            val intent = Intent(this, BarcodeScan::class.java)
            intent.putExtra("type","qrcode")
            startActivity(intent)
            finish()
        }else{
            mDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun showDialog( textMsg: String) {
        val dialog = Dialog(this)
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
        val save = dialog.findViewById(com.tipbox.app.R.id.save) as TextView
        _txt.visibility=View.GONE
        p_icon.visibility=View.GONE
        amt.visibility=View.GONE
        if(textMsg.contains("sign")) {
            doubleframe.visibility = View.VISIBLE
            singleframe.visibility = View.GONE
            save.setText("Yes")
            dialogButton.setText("No")
        }else{
            doubleframe.visibility = View.GONE
            singleframe.visibility = View.VISIBLE
            save.setText("Save")
        }
        titl_txt.setText(textMsg)
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })


        save.setOnClickListener(View.OnClickListener {
            if(textMsg.contains("sign")) {
                dialog.dismiss()
                if (Utils.networkStatus(this)) {
                    var imageViewTarget = DrawableImageViewTarget(loader);
                    Glide.with(this).load(R.drawable.zd_loader).into(imageViewTarget);
                    ApiResponse(this, this, ServerUrls.LOGOUT, loader).response(
                        mApplication!!.getRestClient()!!.riseService.logout(
                            ApiParameter().logout(),
                            JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                        ), mApplication!!
                    )

                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                }
            }else {
                dialog.dismiss()
            }
        })

        val ok = dialog.findViewById(com.tipbox.app.R.id.ok) as TextView
        ok.setText("Ok")
        ok.setOnClickListener(View.OnClickListener {
            if(textMsg.startsWith("sign")) {
                if (Utils.networkStatus(this)) {
                    var imageViewTarget = DrawableImageViewTarget(loader);
                    Glide.with(this).load(R.drawable.zd_loader).into(imageViewTarget);
                    ApiResponse(this, this, ServerUrls.LOGOUT, loader).response(
                        mApplication!!.getRestClient()!!.riseService.logout(
                            ApiParameter().logout(),
                            JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                        ), mApplication!!
                    )

                } else {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                }
            }else {
                dialog.dismiss()
            }
        })

        dialog.show()

    }

/*
    override fun onResume() {
        super.onResume()
         mMyBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.v("notify","onrecive")
                showDialog(intent!!.getStringExtra("data"))
            }
        }
        try{
            LocalBroadcastManager.getInstance(this).registerReceiver(mMyBroadcastReceiver, IntentFilter("001"))
        }catch (ex:Exception){
            ex.printStackTrace();
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMyBroadcastReceiver)
    }
    lateinit var mMyBroadcastReceiver: BroadcastReceiver*/
}
