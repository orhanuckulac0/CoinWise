package com.example.investmenttracker.domain.use_case.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

// to prevent copy pasting to editText
fun EditText.setDecimalInput() {

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null && !s.toString().matches(Regex("^[0-9.]*$"))) {
                setText(s.toString().replace(Regex("[^0-9.]"), ""))
                setSelection(text.length)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
