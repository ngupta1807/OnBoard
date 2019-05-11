package com.wfi.beaconscanner.features.blockedList

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.wfi.beaconscanner.R
import com.wfi.beaconscanner.features.beaconList.BeaconsRecyclerViewAdapter
import com.wfi.beaconscanner.features.beaconList.ControlsBottomSheetDialog
import com.wfi.beaconscanner.models.BeaconSaved
import com.wfi.beaconscanner.utils.extensionFunctions.component
import io.realm.Realm
import io.realm.RealmResults
import javax.inject.Inject

/**
 * Created by nisha.
 */
class BlockedActivity : AppCompatActivity() {
    // *********** Declare Used Variables *********//*
    companion object {
        const val TAG = "BlockedActivity"
    }

    @Inject lateinit var realm: Realm

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.empty_view) lateinit var emptyView: ConstraintLayout
    @BindView(R.id.beacons_rv) lateinit var beaconsRv: RecyclerView

    lateinit var beaconResults: RealmResults<BeaconSaved>
    /**
     * Every Activity created is passed through a sequence of method calls.
     * onCreate() is the first of these calls.
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked)
        ButterKnife.bind(this)
        component().inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.blacklist)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        beaconsRv.layoutManager = LinearLayoutManager(this)
        beaconsRv.setHasFixedSize(true)
        beaconsRv.adapter = BeaconsRecyclerViewAdapter(beaconResults, this, object : BeaconsRecyclerViewAdapter.OnControlsOpen {
            override fun onOpenControls(beacon: BeaconSaved) {
                val bsDialog = ControlsBottomSheetDialog.newInstance(beacon, true)
                bsDialog.show(supportFragmentManager, bsDialog.tag)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
