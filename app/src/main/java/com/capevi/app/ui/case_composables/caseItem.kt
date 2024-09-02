package com.capevi.app.ui.case_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Blue
import com.capevi.data.model.CaseModel
import com.capevi.navigation.Routes
import com.capevi.shared.utils.formatDateText
import com.capevi.viewmodels.SharedViewModel
import gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

@Composable
fun caseItem(
    case: CaseModel,
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    Box(
        modifier =
            Modifier
                .padding(bottom = 10.dp)
                .clickable {
                    val caseTojson = gson.toJson(case)
                    val encodedJson = URLEncoder.encode(caseTojson, StandardCharsets.UTF_8.toString())
                    viewModel.clearList()
                    navController.navigate(Routes.ViewCase.route + "/$encodedJson")
                },
    ) {
        Box(
            modifier =
                Modifier
                    .height(74.dp)
                    .fillMaxWidth(1f)
                    .background(Blue[900]!!, RoundedCornerShape(12.dp))
                    .padding(start = 5.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier =
                    Modifier
                        .height(74.dp)
                        .fillMaxWidth(1f)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(1f),
                    ) {
                        Text(
                            text = case.title,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    color = Black[950]!!,
                                    fontWeight = FontWeight.W500,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        CaseStatus(case.status ?: "")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = "Clock",
                            modifier = Modifier.padding(end = 5.dp),
                            tint = Black[600]!!,
                        )
                        Text(
                            text = case.loggedAt.format(DateTimeFormatter.ofPattern("hh:mma")),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 12.sp,
                                    color = Black[600]!!,
                                    fontWeight = FontWeight.W400,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier =
                                Modifier
                                    .height(
                                        4.dp,
                                    ).width(4.dp)
                                    .background(color = Black[600]!!, shape = RoundedCornerShape(4.dp)),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = "CALENDAR",
                            modifier = Modifier.padding(end = 5.dp),
                            tint = Black[600]!!,
                        )
                        Text(
                            text = formatDateText(case.loggedAt),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 12.sp,
                                    color = Black[600]!!,
                                    fontWeight = FontWeight.W400,
                                    textAlign = TextAlign.Center,
                                ),
                        )
                    }
                }
            }
        }
    }
}
