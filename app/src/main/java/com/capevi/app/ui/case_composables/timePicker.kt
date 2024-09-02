package com.capevi.app.ui.case_composables

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.capevi.app.R
import com.capevi.shared.widget.customTextField
import com.capevi.viewmodels.CaseViewModel

@Composable
fun timePicker(
    context: Context,
    initialHour: Int,
    initialMinute: Int,
    caseViewModel: CaseViewModel,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    if (showDialog) {
        val timePickerDialog =
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    caseViewModel.onCaseTimeChanged(String.format("%02d:%02d", hourOfDay, minute))

                    onTimeSelected(hourOfDay, minute)
                    showDialog = false
                },
                initialHour,
                initialMinute,
                true,
            )
        timePickerDialog.setOnDismissListener {
            showDialog = false
        }
        timePickerDialog.show()
    }
    customTextField(
        textValue = caseViewModel.time,
        readOnly = true,
        placeHolder = stringResource(id = R.string.enterTitle) + " " + stringResource(id = R.string.time),
        label = stringResource(id = R.string.timeOfLog),
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Sentences,
        enabled = false,
        onValueChanged =
            caseViewModel::onCaseTimeChanged,
        onTextFieldClick = {
            keyboardController?.hide()
            showDialog = true
        },
    )
}
