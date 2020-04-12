package com.juansandoval.sandovalportfolio.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.ui.adapters.UserAdapter
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {

    var mUserDatabase: DatabaseReference? = null
    var userAdapter: UserAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAdapter = UserAdapter(mUserDatabase!!, context)
        userRecyclerViewId.layoutManager = linearLayoutManager
        userRecyclerViewId.adapter = userAdapter
    }
}
