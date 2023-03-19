package com.example.investmenttracker.domain.use_case.util

import android.app.Dialog
import android.content.Context
import com.example.investmenttracker.R

fun showProgressDialog(context: Context): Dialog{
    val progressDialog = Dialog(context)
    progressDialog.setContentView(R.layout.progress_bar)
    progressDialog.setCancelable(false)
    return progressDialog
}

fun cancelProgressDialog(dialog: Dialog){
    dialog.dismiss()
}


