package com.juansandoval.sandovalportfolio.ui.adapters

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.data.User
import kotlinx.android.synthetic.main.users_item_row.view.*

class UserAdapter(databaseQuery: DatabaseReference, var context: Context?) :
    FirebaseRecyclerAdapter<User, UserAdapter.ViewHolder>(
        User::class.java,
        R.layout.users_item_row,
        ViewHolder::class.java,
        databaseQuery
    ) {
    override fun populateViewHolder(viewHolder: ViewHolder?, user: User?, position: Int) {
        var userId = getRef(position).key
        viewHolder!!.bindView(user!!, context!!)
        val userName = viewHolder.userNameText
        var status = viewHolder.userStatusText
        var profilePic = viewHolder.userProfileImageTxt
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(context, "User Taped: $userName", Toast.LENGTH_LONG).show()
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