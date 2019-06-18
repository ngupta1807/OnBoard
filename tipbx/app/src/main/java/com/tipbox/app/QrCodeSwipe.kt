
package com.tipbox.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.tipbox.app.adapter.CardStackAdapter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.GetApiData
import com.tipbox.app.util.Common
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import com.tipbox.app.util.Utils
import com.yuyakaido.android.cardstackview.*
import com.yuyakaido.android.cardstackview.sample.Spot
import com.yuyakaido.android.cardstackview.sample.SpotDiffCallback
import kotlinx.android.synthetic.main.fragment_qrcode_view.*
import kotlinx.android.synthetic.main.popup_setting.*
import org.json.JSONObject
import java.util.*

class QrCodeSwipe : AppCompatActivity(), CardStackListener, ApiCallback,View.OnClickListener {
    override fun result(res: String) {
        if (res.contains("successfully", true)) {
            startActivity(Intent(this, ThankuScreen::class.java)
               .putExtra("company_logo",companyLogo)
            )
            finish()
        } else {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.change -> {
                showDialog("change","Do you want to give $"+tip_amt.text.toString()+" as a tip amount.")
            }
            R.id.nav ->{
                onBackPressed()
            }
            R.id.displayview-> {
                mApplication!!.storeStringInPreference(ServerUrls.INFO, "yes")
                info_screen.visibility=View.GONE
                displayview.visibility=View.GONE
                lay_screen.visibility=View.VISIBLE
            }
            R.id.info_screen-> {
                mApplication!!.storeStringInPreference(ServerUrls.INFO, "yes")
                info_screen.visibility=View.GONE
                displayview.visibility=View.GONE
                lay_screen.visibility=View.VISIBLE
            }
        }
    }

    private fun postDetail() {
        type="POST"
        if (Utils.networkStatus(this)) {
                ApiResponse(progressbar, this@QrCodeSwipe, mcon, ServerUrls.TRANSFERAMOUNT).response(
                    mApplication!!.getRestClient()!!.riseService.transferAmount(
                        ApiParameter().performData(tip_amt.text.toString(), pos, p_id),
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
    fun showDialog(type:String,msg:String) {
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
        val ok = dialog.findViewById(com.tipbox.app.R.id.ok) as TextView
        ok.setText("Ok")
        p_icon.visibility=View.GONE
        amt.visibility=View.GONE

        titl_txt.setText(msg)
        ok.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, MainActivity::class.java)
                .putExtra("activity", "3"))
            finish()

        })

        var doubleframe = dialog.findViewById(com.tipbox.app.R.id.doubleframe) as LinearLayout
        dialogButton.setText("No")
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val save = dialog.findViewById(com.tipbox.app.R.id.save) as TextView
        save.setText("Yes")
        save.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            postDetail()
        })
        if(type.equals("change")) {
            singleframe.visibility = View.GONE
            doubleframe.visibility = View.VISIBLE
        }else{
            singleframe.visibility = View.VISIBLE
            doubleframe.visibility = View.GONE
        }
        dialog.show()

    }

    override fun onBackPressed() {
        startActivity(Intent(this, BarcodeScan::class.java))
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_qrcode_view)
        ButterKnife.bind(this)
        var tip_amt =findViewById<TextView>(R.id.tip_amt)
        p_name =findViewById<TextView>(R.id.p_name)
        change.setOnClickListener(this)
        nav.setOnClickListener(this)
        info_screen.setOnClickListener(this)
        btm_item_image.setOnClickListener(this)
        displayview.setOnClickListener(this)
        val bgDrawable = btm_item_image.getBackground() as LayerDrawable
        viewShape = bgDrawable.findDrawableByLayerId(com.tipbox.app.R.id.gradientShapeBoxDrawble) as GradientDrawable
        mcon=this
        mApplication = this.application as MApplication
        amt= mApplication!!.getStringFromPreference("swipe_amt")
        if(amt.equals("")){
            showDialog("swipe","Please first setup swipe settings")
            info_screen.visibility=View.VISIBLE
            displayview.visibility=View.VISIBLE
            lay_screen.visibility=View.GONE
        }else {
            if(mApplication!!.getStringFromPreference(ServerUrls.INFO).equals("")) {
                info_screen.visibility=View.VISIBLE
                displayview.visibility=View.VISIBLE
                lay_screen.visibility=View.GONE
            }
            else{
                info_screen.visibility=View.GONE
                displayview.visibility=View.GONE
                lay_screen.visibility=View.VISIBLE
            }
            tip_amt.text = "0"
            p_name.text = "Performer Name"
        }

        scroll.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = event.getX()
                        downY = event.getY()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        upX = event.getX()
                        upY = event.getY()
                        var deltaX = downX - upX
                        var deltaY = downY - upY

                        //HORIZONTAL SCROLL
                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            if (Math.abs(deltaX) > min_distance) {
                                // left or right
                            } else {
                                //not long enough swipe...
                                return false
                            }
                        }
                        //VERTICAL SCROLL
                        else {
                            if (Math.abs(deltaY) > min_distance) {
                                // top or down
                                if (deltaY < 0) {
                                    Log.v("TYPE","ACTION TOP TO DOWN")
                                    return true
                                }
                                if (deltaY > 0) {
                                    Log.v("TYPE","ACTION DOWN TO TOP")
                                    if(i==0){
                                        setupCardStackView()
                                    }else {
                                        Log.v("App","d enableTopSwipe image click")
                                        viewShape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
                                        enableTopSwipe("first")
                                    }
                                    return true
                                }
                            } else {
                                //not long enough swipe...
                                return false
                            }
                        }
                        return false
                    }
                }
                return false
            }
        })

         //var pobj=GetApiData().getPerformerData(JSONObject(intent.getStringExtra("p_data")),p_name,logo,mcon,company_img)
         //companyLogo=pobj.getString("companyLogo").replace("\"//","/")
         //p_id=pobj.getString("userId")
    }
    override fun onCardDragging(direction: Direction, ratio: Float) {
        //Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        manager.setStackFrom(StackFrom.Bottom)
        manager.setCanScrollVertical(true)
        swipePos=direction.name
        var currentamt=0
            if (direction.name.equals("Top")) {
                pos = pos + 1
                manager.topPosition=pos
                currentamt = Integer.parseInt(tip_amt.text.toString()) + Integer.parseInt(amt)
                tip_amt.setText(""+currentamt)
            }
            else {
                pos = pos - 1
                manager.topPosition=pos
                currentamt = Integer.parseInt(tip_amt.text.toString()) - Integer.parseInt(amt)
                tip_amt.setText(""+currentamt)
            }
        if(pos ==0){
            Log.v("App","d enableTopSwipe pos 0")
            enableTopSwipe("first")
        }
        else {
            if(pos<0){
                Log.v("App","d disableTopSwipe minu 1")
                disableTopSwipe()
            }else {
                enableTopSwipe("second")
            }
        }
        if (manager.topPosition%2==0)
            viewShape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
        else
            viewShape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
        Log.v("App","d topPosition::"+manager.topPosition)
        Log.v("App","d itemCount::"+adapter.itemCount)
        if(pos>=0) {
            if (direction.name.equals("Bottom")) {
                removePage()
                Log.v("App", "d pos top 1::" + pos + ":: Remove")
                Log.v("App", "d pos top 2::" + Integer.parseInt(mApplication!!.getStringFromPreference("swipe_amt")))
                Log.v("App", "d pos top 3::" + currentamt)
                if (pos == 0) {
                    removeLastPage(1)
                }
            } else {
                Log.v("App", "d pos top::" + pos + ":: Add")
                addSecondPage()
            }
        }else{
            pos=1
            manager.topPosition=pos
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }
    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
    }
    var i:Int= 0
    private fun setupCardStackView() {
        i=1
        initialize()
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        var txt="${textView.tag}"
        Log.v("App","d Appeared position::"+pos)
        if(pos ==0){//&& swipePos.equals("Bottom")
            viewShape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.white))
            Log.v("App","d Appeared enableTopSwipe postion 0")
            enableTopSwipe("first")
        }
    }
    private fun initialize() {
        manager.setVisibleCount(2)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.90f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setCanScrollHorizontal(false)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setDirections(Direction.FREEDOM)
        manager.setOverlayInterpolator(LinearInterpolator())

        var setting = SwipeAnimationSetting.Builder()
        .setDirection(Direction.Top)
        .setDuration(200)
        .setInterpolator(AccelerateInterpolator())
        .build()
        manager.setSwipeAnimationSetting(setting)

        manager.setCanScrollHorizontal(false)
        manager.setCanScrollVertical(true)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = true
            }
        }
    }
    fun disableTopSwipe() {
        tip_amt.text="0"
        scroll.visibility=View.VISIBLE
        cardStackView.visibility=View.GONE
    }


    fun enableTopSwipe(type:String) {
        scroll.visibility=View.GONE
        cardStackView.visibility=View.VISIBLE
        if(type.equals("first")) {
            var tip=0+ Integer.parseInt(amt)
            tip_amt.text=""+tip
            change.visibility=View.VISIBLE
            manager.setDirections(Direction.VERTICAL)
            pos=0
            manager.topPosition=pos
        }
        else {
            manager.setDirections(Direction.VERTICAL)
        }
        manager.setStackFrom(StackFrom.Bottom)

    }
    private fun removeLastPage(value:Int) {
        val old = adapter.getSpots()
        Log.v("App", "d remove last old size::" + old.size)
        val new:List<Spot>
        new = old.subList(0, value)
        val callback = SpotDiffCallback(old, new)
        Log.v("App", "d remove last new size::" + new.size)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removePage() {
        val old = adapter.getSpots()
        Log.v("App", "d remove old size::" + old.size)
        //val new = old.subList(0, old.size-2)
        val new =  old.minus(createSecondSpots())
        val callback = SpotDiffCallback(old, new)
        Log.v("App", "d remove new size::" + new.size)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }


    private fun addFirstPage() {
        val old = adapter.getSpots()
        Log.v("App", "d old size::" + old.size)
        val new = old.plus(createFirstSpots())
        val callback = SpotDiffCallback(old, new)
        Log.v("App", "d new size::" + new.size)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addSecondPage() {
        val old = adapter.getSpots()
        Log.v("App", "d old size::" + old.size)
        val new = old.plus(createSecondSpots())
        val callback = SpotDiffCallback(old, new)
        Log.v("App", "d new size::" + new.size)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createFirstSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        spots.add(Spot(name = "yellow"))
        return spots
    }

    private fun createSecondSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        spots.add(Spot(name = "yellow"))
        spots.add(Spot(name = "white"))
        return spots
    }
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.btm_item_image) lateinit var btm_item_image: RelativeLayout
    @BindView(com.tipbox.app.R.id.logo) lateinit var logo: RelativeLayout
    @BindView(com.tipbox.app.R.id.change) lateinit var change: TextView
    @BindView(com.tipbox.app.R.id.nav) lateinit var nav: ImageView
    @BindView(com.tipbox.app.R.id.lay_screen) lateinit var lay_screen: RelativeLayout
    @BindView(com.tipbox.app.R.id.info_screen) lateinit var info_screen: RelativeLayout
    @BindView(com.tipbox.app.R.id.scroll) lateinit var scroll: RelativeLayout
    @BindView(com.tipbox.app.R.id.displayview) lateinit var displayview: TextView
    @BindView(com.tipbox.app.R.id.item_img) lateinit var company_img: ImageView
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createFirstSpots(),this@QrCodeSwipe,amt,companyLogo) }
    lateinit var viewShape:GradientDrawable
    lateinit var mcon: Context
    lateinit var type: String
    lateinit var p_name: TextView
    var mApplication: MApplication?=null
    var amt: String=""
    var p_id: String=""
    var companyLogo: String=""
    var pos: Int=0
    var pos_new: Int=0
    var swipePos:String=""
    var s_type:Boolean=false
    val min_distance = 100
    var downX: Float=0.0f
    var downY: Float=0.0f
    var upX: Float=0.0f
    var upY: Float=0.0f

}

