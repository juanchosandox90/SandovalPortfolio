package com.juansandoval.sandovalportfolio.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.juansandoval.sandovalportfolio.R
import kotlinx.android.synthetic.main.fragment_dialog.*

class CustomDialog(
    context: Context,
    private val liveData: LiveData<Pair<Int?, String?>>,
    private val owner: LifecycleOwner
) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        liveData.observe(owner, Observer {
            viewFlipper.displayedChild = it.first ?: 0

            this.setCancelable(it.first != 0)

            if (!it.second.isNullOrEmpty()) failMsg.text = it.second
        })
    }
}