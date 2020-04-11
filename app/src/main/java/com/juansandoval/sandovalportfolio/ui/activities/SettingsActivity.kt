package com.juansandoval.sandovalportfolio.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.utils.startLoginActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private var mCurrentUser: FirebaseUser? = null
    private var userStatus: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        supportActionBar!!.title = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val userId = mCurrentUser!!.uid
        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        mDataBase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayName = dataSnapshot.child("display_name").value
                userStatus = dataSnapshot.child("status").value
                settingsStatusText.text = userStatus.toString()
                settingsDisplayName.text = displayName.toString()
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

        settingsChangeStatusBtn.setOnClickListener {
            openDialogChangeStatus()
        }

        settingsLogoutBtn.setOnClickListener {
            logoutDialog()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun openDialogChangeStatus() {
        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.create()
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_dialog_status, null)
        val statusUpdateEt = dialogLayout.findViewById<EditText>(R.id.statusUpdateEt)
        val statusUpdateBtn = dialogLayout.findViewById<Button>(R.id.statusUpdateBtn)
        if (userStatus != null) {
            statusUpdateEt.setText(userStatus.toString())
        } else if (userStatus == null) {
            statusUpdateEt.setText(getString(R.string.enter_your_status_title))
        }
        statusUpdateBtn.setOnClickListener {
            val status = statusUpdateEt.text.toString().trim()
            mDataBase!!.child("status").setValue(status).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.ops_something_went_wrong),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
        dialog.setView(dialogLayout)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun logoutDialog() {
        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.create()
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
