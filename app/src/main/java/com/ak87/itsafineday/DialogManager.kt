package com.ak87.itsafineday

import android.app.AlertDialog
import android.content.Context

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(context.getString(R.string.enable_location))
        dialog.setMessage(context.getString(R.string.wont_enabled_location))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.button_text_ok)) { _,_,->
            listener.onClick()
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.button_text_cancel)) { _, _,->
            dialog.dismiss()
        }
        dialog.show()
    }
    interface Listener {
        fun onClick()
    }
}