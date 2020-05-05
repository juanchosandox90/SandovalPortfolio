package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var mCurrentUser: FirebaseUser? = null
    private var mDataBase: DatabaseReference? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
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
                val status = dataSnapshot.child("status").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val image = dataSnapshot.child("image").value.toString()

                profileDisplayNameTxt.text = displayName
                profileStatusTxt.text = status
                profileEmailText.text = email

                val options = RequestOptions().placeholder(R.drawable.ic_account_white)
                Glide.with(applicationContext)
                    .setDefaultRequestOptions(options)
                    .load(image)
                    .circleCrop()
                    .into(profileImageIv)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
