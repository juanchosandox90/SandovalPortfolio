package com.juansandoval.sandovalportfolio.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.utils.CustomProgressBar
import com.juansandoval.sandovalportfolio.utils.startLoginActivity
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private var mDataBase: DatabaseReference? = null
    private var mCurrentUser: AGConnectUser? = null
    private var mStorageRef: StorageReference? = null
    private var userStatus: Any? = null
    private var userEmail: Any? = null
    private var GALLERY_ID: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mCurrentUser = AGConnectAuth.getInstance().currentUser
        supportActionBar!!.title = "Settings"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val userId = mCurrentUser!!.uid
        mStorageRef = FirebaseStorage.getInstance().reference
        mDataBase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        mDataBase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayName = dataSnapshot.child("display_name").value
                userStatus = dataSnapshot.child("status").value
                userEmail = dataSnapshot.child("email").value
                val imageProfile = dataSnapshot.child("image").value.toString()
                settingsStatusText.text = userStatus.toString()
                settingsDisplayName.text = displayName.toString()
                if (!imageProfile!!.equals("default")) {
                    val options = RequestOptions().placeholder(R.drawable.ic_account_white)
                    Glide.with(applicationContext)
                        .setDefaultRequestOptions(options)
                        .load(imageProfile)
                        .circleCrop()
                        .into(settingsProfileId)
                }
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

        settingsChangeEmailBtn.setOnClickListener {
            openDialogChangeEmail()
        }

        settingsImgBtn.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(
                    galleryIntent,
                    getString(R.string.select_image_title)
                ), GALLERY_ID
            )
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

    private fun openDialogChangeEmail() {
        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.create()
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_dialog_email, null)
        val emailUpdateEt = dialogLayout.findViewById<EditText>(R.id.emailUpdateEt)
        val emailUpdateBtn = dialogLayout.findViewById<Button>(R.id.emailUpdateBtn)
        if (userEmail != null) {
            emailUpdateEt.setText(userStatus.toString())
        } else if (userEmail == null) {
            emailUpdateEt.setText(getString(R.string.enter_your_status_title))
        }
        emailUpdateBtn.setOnClickListener {
            val status = emailUpdateEt.text.toString().trim()
            mDataBase!!.child("email").setValue(status).addOnCompleteListener { task: Task<Void> ->
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
            AGConnectAuth.getInstance().signOut()
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

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, dataIntent: Intent?) {
        val progressBar = CustomProgressBar()
        if (requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK) {
            val image: Uri? = dataIntent!!.data
            CropImage.activity(image)
                .setAspectRatio(1, 1)
                .start(this)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(dataIntent)
            if (resultCode == Activity.RESULT_OK) {
                progressBar.show(this, getString(R.string.loading_general))
                val resultUri = result.uri
                val userId = mCurrentUser!!.uid
                val thumbFile = File(resultUri.path)

                val thumbBitMap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(65)
                    .compressToBitmap(thumbFile)

                val byteArray = ByteArrayOutputStream()
                thumbBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)

                val thumbByteArray: ByteArray = byteArray.toByteArray()

                // Images directory
                val filePath = mStorageRef!!
                    .child("sandovalportfolio_profile_images")
                    .child("$userId.jpg")

                // Compressed images - thumbnail
                val thumbFilePath = mStorageRef!!
                    .child("sandovalportfolio_profile_images")
                    .child("thumbs").child("$userId.jpg")

                filePath.putFile(resultUri)
                    .addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                        if (task.isSuccessful) {
                            var downloadUrl = ""
                            var downloadThumbUrl = ""
                            val downloadUri: Task<Uri> = task.result!!.storage.downloadUrl
                            downloadUri.addOnSuccessListener { uri: Uri? ->
                                downloadUrl = uri.toString()
                                downloadThumbUrl = uri.toString()
                            }
                            val uploadTask: UploadTask = thumbFilePath.putBytes(thumbByteArray)
                            uploadTask.addOnCompleteListener { task: Task<UploadTask.TaskSnapshot> ->
                                if (task.isSuccessful) {
                                    val updateObj = HashMap<String, Any>()
                                    updateObj["image"] = downloadUrl
                                    updateObj["thumb_image"] = downloadThumbUrl

                                    mDataBase!!.updateChildren(updateObj)
                                        .addOnCompleteListener { task: Task<Void> ->
                                            if (task.isSuccessful) {
                                                progressBar.hide()
                                            } else {
                                                progressBar.hide()
                                                Toast.makeText(
                                                    this,
                                                    getString(R.string.ops_something_went_wrong),
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }
                                } else {
                                    progressBar.hide()
                                    Toast.makeText(
                                        this,
                                        getString(R.string.ops_something_went_wrong),
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                        } else {
                            progressBar.hide()
                            Toast.makeText(
                                this,
                                getString(R.string.ops_something_went_wrong),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }
            }
        }
    }
}
