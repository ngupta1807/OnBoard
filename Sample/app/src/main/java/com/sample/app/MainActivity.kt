package com.sample.app

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.sample.app.frag.BadWordList
import com.sample.app.frag.HashTag
import com.sample.app.frag.TweetList
import com.sample.app.intrface.FragementImp
import com.sample.app.util.Validate

class MainActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FragementImp {
    var mValidate: Validate?=null
    var toolbar: Toolbar?=null
    internal var fragment: Fragment? = null

    companion object {
        var top_title: TextView?=null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mValidate = Validate(this)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        top_title = findViewById<View>(R.id.top_title) as TextView
        setSupportActionBar(toolbar)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        displaySelectedScreen(R.id.tweet)

    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        displaySelectedScreen(item.itemId)
        return true
    }

    fun displaySelectedScreen(id: Int) {
        if (id == R.id.tweet) {
            fragment = TweetList()
            top_title!!.text = getString(R.string.tweets)
        } else if (id == R.id.h_tag) {
            fragment = HashTag()
            top_title!!.text = getString(R.string.h_tag)
        } else if (id == R.id.black_list) {
            fragment = BadWordList()
            top_title!!.text = getString(R.string.black_list)
        }
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.content_frame, fragment!!)
            ft.commit()
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun onFragmentInteraction(uri: Uri) {}


}
