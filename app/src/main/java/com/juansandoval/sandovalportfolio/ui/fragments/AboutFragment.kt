package com.juansandoval.sandovalportfolio.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    private var mUserDatabase: DatabaseReference? = null
    private lateinit var textTitle: TextView
    private lateinit var textDescription: TextView
    private lateinit var textDescription2: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("About")
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textTitle = about_title
        textDescription = about_description
        textDescription2 = about_description2
        mUserDatabase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val titleAboutMe = dataSnapshot.child("title").value.toString()
                val descriptionAboutMe = dataSnapshot.child("description").value.toString()
                val descriptionAboutMe2 = dataSnapshot.child("description2").value.toString()
                textTitle.text = titleAboutMe
                textDescription.text = descriptionAboutMe
                textDescription2.text = descriptionAboutMe2
            }

            override fun onCancelled(dbError: DatabaseError) {
                Toast.makeText(
                    context,
                    getString(R.string.ops_something_went_wrong),
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        })
    }

}
