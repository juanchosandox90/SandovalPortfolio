package com.juansandoval.sandovalportfolio.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

import com.juansandoval.sandovalportfolio.R
import com.juansandoval.sandovalportfolio.ui.adapters.MainListAdapter
import com.juansandoval.sandovalportfolio.utils.bindView

class SkillsFragment : Fragment() {

    private lateinit var mainListAdapter: MainListAdapter
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_skills, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView Init
        mainListAdapter = MainListAdapter(context!!)
        recyclerView.adapter = mainListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)
    }

}
