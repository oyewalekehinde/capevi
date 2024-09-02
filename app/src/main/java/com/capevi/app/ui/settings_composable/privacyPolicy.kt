package com.capevi.app.ui.settings_composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun privacyPolicyScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(16.dp)
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
        ) {
            Text(
                text = "Privacy Policy",
                style =
                    TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "Last Updated: 25th August 2024",
                style =
                    TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 24.dp),
            )

            Text(
                text = "Introduction",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "At Capevi Tech, we are committed to protecting your personal information and your right to privacy. This Privacy Policy outlines our practices regarding the collection, use, and disclosure of your information when you use our application, Capevi.",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "Information We Collect",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "We collect personal information that you voluntarily provide to us when you register on the application, express an interest in obtaining information about us or our products, and when you participate in activities within the application. The personal information we collect may include your name, email address, and any media files you upload.",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "How We Use Your Information",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "We use the information we collect in the following ways:\n\n1. To provide and maintain our service.\n2. To manage your account.\n3. To contact you regarding updates or informational messages.\n4. To improve our application and services.",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "Your Rights",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "Under UK law, you have the right to access the personal information we hold about you, request correction of any errors, request deletion of your information, and object to processing of your information.",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "Changes to This Privacy Policy",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "We may update our Privacy Policy from time to time to reflect changes in our practices or legal requirements. We will notify you of any changes by posting the new Privacy Policy on this page.",
                style =
                    TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

//        Button(onClick = onAccept) {
//            Text(text = "Accept and Continue")
//        }
        }
    }
}
