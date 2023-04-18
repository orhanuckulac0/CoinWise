package com.example.investmenttracker.domain.use_case.util

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
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

fun showAddCoinInformationDialog(context: Context): Dialog {
    val informationDialog =  Dialog(context)
    informationDialog.setContentView(R.layout.add_coin_information_dialog)
    informationDialog.setCancelable(true)

    val btnClose = informationDialog.findViewById<AppCompatButton>(R.id.btnCloseInformation)
    btnClose.setOnClickListener {
        informationDialog.dismiss()
    }
    val layoutParams = provideLayoutParams(context, informationDialog)
    informationDialog.window?.attributes = layoutParams
    return informationDialog
}

fun showAboutAppDialog(context: Context): Dialog {
    val aboutAppDialog =  Dialog(context)
    aboutAppDialog.setContentView(R.layout.about_app_dialog)
    aboutAppDialog.setCancelable(true)

    val btnClose = aboutAppDialog.findViewById<AppCompatButton>(R.id.btnCloseAboutApp)
    btnClose.setOnClickListener {
        aboutAppDialog.dismiss()
    }

    val layoutParams = provideLayoutParams(context, aboutAppDialog)
    aboutAppDialog.window?.attributes = layoutParams
    return aboutAppDialog
}

fun provideLayoutParams(context: Context, dialog: Dialog): WindowManager.LayoutParams {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = windowManager.defaultDisplay
        display.getRealMetrics(displayMetrics)
    } else {
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(dialog.window?.attributes)

    // setting width to 95% of display
    layoutParams.width = (displayMetrics.widthPixels * 0.95f).toInt()

    // setting height to 70% of display
    layoutParams.height = (displayMetrics.heightPixels * 0.7f).toInt()
    return layoutParams
}
