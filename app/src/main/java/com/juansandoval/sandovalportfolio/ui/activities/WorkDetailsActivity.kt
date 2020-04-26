package com.juansandoval.sandovalportfolio.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import kotlinx.android.synthetic.main.activity_work_details.*

class WorkDetailsActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private var workId: String? = null
    private var webSite: String? = null
    private var company: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_details)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.extras != null) {
            workId = intent!!.extras!!.get("workId").toString()
            mDataBase = FirebaseDatabase.getInstance().reference.child("Work").child(workId!!)
            setupProfile()
        }
    }

    private fun setupProfile() {
        mDataBase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val companyName = dataSnapshot.child("company_title").value.toString()
                val companyRole = dataSnapshot.child("work_role").value.toString()
                val workDescription = dataSnapshot.child("work_description").value.toString()
                val workStartDate = dataSnapshot.child("start_date").value.toString()
                val workEndDate = dataSnapshot.child("end_date").value.toString()
                val workImage = dataSnapshot.child("work_image").value.toString()
                val workSite = dataSnapshot.child("web_site").value.toString()
                webSite = workSite
                company = companyName

                workCompanyNameTxt.text = companyName
                workCompanyRoleTxt.text = companyRole
                workDescriptionTxt.text = workDescription
                workStarDateTxt.text = workStartDate
                workEndDateTxt.text = workEndDate
                webSitePageTxt.text = workSite

                supportActionBar!!.title = companyName

                val options = RequestOptions().placeholder(R.drawable.ic_account_white)
                Glide.with(applicationContext)
                    .setDefaultRequestOptions(options)
                    .load(workImage)
                    .circleCrop()
                    .into(workImageDetailIv)
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
        webSitePageTxt.setOnClickListener {
            openWebView()
        }
    }

    private fun openWebView() {
        val webViewIntent = Intent(this, WebViewActivity::class.java)
        webViewIntent.putExtra("website", webSite)
        webViewIntent.putExtra("company", company)
        startActivity(webViewIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
