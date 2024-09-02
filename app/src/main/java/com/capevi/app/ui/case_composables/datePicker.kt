package com.capevi.app.ui.case_composables

import android.content.Context
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.capevi.app.R
import com.capevi.shared.utils.formatDate
import com.capevi.shared.widget.customTextField
import com.capevi.viewmodels.CaseViewModel
import java.util.Calendar
import java.util.Date

@Composable
fun showDatePicker(
    context: Context,
    caseViewModel: CaseViewModel,
) {
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()
    val keyboardController = LocalSoftwareKeyboardController.current
    val datePickerDialog =
        android.app.DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                println(month)
                caseViewModel.onCaseDateChanged("$dayOfMonth/${month + 1}/$year")
                Date()
            },
            if (caseViewModel.date.isNotEmpty()) formatDate(caseViewModel.date).year else year,
            if (caseViewModel.date.isNotEmpty()) formatDate(caseViewModel.date).month.value - 1 else month,
            if (caseViewModel.date.isNotEmpty()) {
                formatDate(caseViewModel.date).dayOfMonth
            } else {
                day
            },
        )
    customTextField(
        textValue = caseViewModel.date,
        readOnly = true,
        placeHolder = stringResource(id = R.string.enterTitle) + " " + stringResource(id = R.string.date),
        label = stringResource(id = R.string.dateOfLog),
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences,
        enabled = false,
        onValueChanged =
            caseViewModel::onCaseDateChanged,
        onTextFieldClick = {
            keyboardController?.hide()
            datePickerDialog.show()
        },
    )
}
