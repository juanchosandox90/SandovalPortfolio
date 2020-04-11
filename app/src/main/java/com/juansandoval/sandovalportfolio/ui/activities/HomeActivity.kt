package com.juansandoval.sandovalportfolio.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.ui.adapters.SectionPagerAdapter
import com.juansandoval.sandovalportfolio.utils.startLoginActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    var sectionPagerAdapter: SectionPagerAdapter? = null
    private var mCurrentUser: FirebaseUser? = null
    private var mDataBase: DatabaseReference? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setPager()
        if (intent.extras != null) {
            userId = intent!!.extras!!.get("userId").toString()
            mCurrentUser = FirebaseAuth.getInstance().currentUser
            mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
            setupProfile()
        }
    }

    private fun setupProfile() {
        mDataBase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayName = dataSnapshot.child("display_name").value.toString()
                supportActionBar!!.title = "Welcome: $displayName"
            }

            override fun onCancelled(dbError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.ops_something_went_wrong),
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        })
    }

    override fun onBackPressed() {
        logoutDialog()
    }

    private fun setPager() {
        sectionPagerAdapter = SectionPagerAdapter(this)
        dashboardViewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(mainTabs, dashboardViewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "Users"
                    }
                    1 -> {
                        tab.text = "Work"
                    }
                    2 -> {
                        tab.text = "Skills"
                    }
                    3 -> {
                        tab.text = "About"
                    }
                    4 -> {
                        tab.text = "Contact"
                    }
                }
            }).attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                logoutDialog()
            }
            R.id.settings_action -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        return true
    }


    private fun logoutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val dialog: androidx.appcompat.app.AlertDialog = builder.create()
        val dialogLayout =
            LayoutInflater.from(this).inflate(R.layout.activity_dialog_logout, null)
        val logoutBtn = dialogLayout.findViewById<Button>(R.id.logoutBtn)
        val cancelBtn = dialogLayout.findViewById<Button>(R.id.cancelBtn)
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startLoginActivity()
            finish()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setView(dialogLayout)
        dialog.setCancelable(true)
        dialog.show()
    }
}
