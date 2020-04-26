package com.juansandoval.sandovalportfolio.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.utils.TypeWriteTextView
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment() {

    private var mUserDatabase: DatabaseReference? = null
    private lateinit var textTitle: TypeWriteTextView
    private lateinit var textDescription: TypeWriteTextView
    private lateinit var textDescription2: TypeWriteTextView

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
                textTitle.animateText(titleAboutMe)
                textTitle.setCharDelay(20)
                textDescription.animateText(descriptionAboutMe)
                textDescription.setCharDelay(20)
                textDescription2.animateText(descriptionAboutMe2)
                textDescription2.setCharDelay(20)
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
