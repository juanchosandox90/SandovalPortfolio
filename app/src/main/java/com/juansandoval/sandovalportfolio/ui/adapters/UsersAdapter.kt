package com.juansandoval.sandovalportfolio.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.data.User
import com.juansandoval.sandovalportfolio.ui.activities.ProfileActivity
import kotlinx.android.synthetic.main.users_item_row.view.*

class UserAdapter(databaseQuery: DatabaseReference, var context: Context?) :
    FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>(
        User::class.java,
        R.layout.users_item_row,
        ViewHolder::class.java,
        databaseQuery
    ) {
    var mFirebaseUser: FirebaseUser? = null
    override fun populateViewHolder(viewHolder: ViewHolder?, user: User?, position: Int) {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = mFirebaseUser!!.uid
        val userId = getRef(position).key
        if (currentUserId == userId) {
            viewHolder!!.itemView.visibility = View.GONE
            viewHolder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        viewHolder!!.bindView(user!!, context!!)
        viewHolder.itemView.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
            val dialog: androidx.appcompat.app.AlertDialog = builder.create()
            val dialogLayout =
                LayoutInflater.from(context).inflate(R.layout.activity_dialog_tap_user, null)
            val openProfile = dialogLayout.findViewById<Button>(R.id.openProfileId)
            openProfile.setOnClickListener {
                val profileIntent = Intent(context, ProfileActivity::class.java)
                profileIntent.putExtra("userId", userId)
                context!!.startActivity(profileIntent)
                dialog.dismiss()
            }
            dialog.setView(dialogLayout)
            dialog.setCancelable(true)
            dialog.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameText: String? = null
        var userStatusText: String? = null
        var userProfileImageTxt: String? = null

        fun bindView(users: User, context: Context) {
            val userName = itemView.profileDisplaName
            val userStatus = itemView.profileStatus
            val userProfileImage = itemView.userProfileImg

            userNameText = users.display_name
            userStatusText = users.status
            userProfileImageTxt = users.thumb_image

            userName.text = users.display_name
            userStatus.text = users.status
            val options = RequestOptions().placeholder(R.drawable.ic_account_white)
            Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(userProfileImageTxt)
                .circleCrop()
                .into(userProfileImage)
        }
    }
}