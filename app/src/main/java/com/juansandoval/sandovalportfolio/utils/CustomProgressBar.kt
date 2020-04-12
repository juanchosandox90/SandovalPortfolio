package com.juansandoval.sandovalportfolio.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import com.juansandoval.sandovalportfolio.R
import kotlinx.android.synthetic.main.custom_progress_bar.view.*

class CustomProgressBar {

    lateinit var dialog: Dialog

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.custom_progress_bar, null)
        if (title != null) {
            view.cp_title.text = title
        }
        view.cp_bg_view.setBackgroundColor(Color.parseColor("#66000000")) //Background Color
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#222222")) //Box Color
        view.cp_title.setTextColor(Color.WHITE) //Text Color

        dialog = Dialog(context, R.style.CustomProgressBarTheme)
        dialog.setContentView(view)
        dialog.show()

        return dialog
    }

    fun hide() {
        dialog.dismiss()
    }
}