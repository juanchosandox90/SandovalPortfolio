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
import com.juansandoval.sandovalportfolio.data.Work
import kotlinx.android.synthetic.main.work_item_row.view.*

class WorkAdapter(databaseQuery: DatabaseReference, var context: Context?) :
    FirebaseRecyclerAdapter<Work, WorkAdapter.ViewHolder>(
        Work::class.java,
        R.layout.work_item_row,
        ViewHolder::class.java,
        databaseQuery
    ) {

    override fun populateViewHolder(viewHolder: ViewHolder?, work: Work?, position: Int) {
        val workId = getRef(position).key
        viewHolder!!.bindView(work!!, context!!)
        viewHolder.itemView.setOnClickListener {
            // TODO: Go to work details activity.
            Toast.makeText(context, "Work Id: $workId", Toast.LENGTH_LONG).show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var workNameText: String? = null
        private var workRoleText: String? = null
        private var workImageTxt: String? = null

        fun bindView(work: Work, context: Context) {
            val workName = itemView.workCompanyTitle
            val workRole = itemView.workRoleDescription
            val workImage = itemView.workImage

            workNameText = work.company_title
            workRoleText = work.work_role
            workImageTxt = work.work_image

            workName.text = work.company_title
            workRole.text = work.work_role
            val options = RequestOptions().placeholder(R.drawable.ic_account_white)
            Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(workImageTxt)
                .circleCrop()
                .into(workImage)
        }
    }
}