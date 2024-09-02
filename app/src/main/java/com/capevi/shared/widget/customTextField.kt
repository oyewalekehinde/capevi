package com.capevi.shared.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Neutral
import com.capevi.app.ui.theme.Red

@Composable
fun customTextField(
    textState: MutableState<String>? = null,
    label: String,
    placeHolder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    maxLines: Int = 1,
    minLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    onTextFieldClick: () -> Unit = {},
    textValue: String? = null,
    onValueChanged: ((String) -> Unit)? = null,
    suffixIcon: @Composable (() -> Unit)? = null,
    error: Boolean = false,
    isPassword: Boolean = false,
) {
    val validatedMinLines = if (minLines <= maxLines) minLines else maxLines
    val validatedMaxLines = if (minLines <= maxLines) maxLines else minLines
    OutlinedTextField(
        enabled = enabled,
        value = textValue ?: textState?.value!!,
        onValueChange = onValueChanged ?: { textState?.value = it },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        label = {
            Text(
                text = label,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 12.sp,
                        color = Neutral[950]!!,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center,
                    ),
            )
        },
        placeholder = {
            Text(
                text = placeHolder,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 14.sp,
                        color = Neutral[400]!!,
                        fontWeight = FontWeight.W400,
                        textAlign = TextAlign.Center,
                    ),
            )
        },
        shape = RoundedCornerShape(6.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = capitalization),
        maxLines = validatedMaxLines,
        minLines = validatedMinLines,
        readOnly = readOnly,
        trailingIcon = suffixIcon,
        colors =
            OutlinedTextFieldDefaults.colors(
                disabledTextColor = Black[950]!!,
                disabledContainerColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledBorderColor = if (error) Red[700]!! else MaterialTheme.colorScheme.outline,
                focusedBorderColor = if (error) Red[700]!! else MaterialTheme.colorScheme.outline, // Custom border color when focused
                unfocusedBorderColor = if (error) Red[700]!! else Color.Gray,
//                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
            ),
        modifier =
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .fillMaxWidth(1f)
                .clickable { onTextFieldClick() },
    )
}
