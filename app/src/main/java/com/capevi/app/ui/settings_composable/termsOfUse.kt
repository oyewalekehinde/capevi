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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun termsOfUseScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Use") },
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
                text = "Capevi - Terms of Use",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                "Last Updated: 25th August 2024",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Normal),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "1. Introduction",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Welcome to Capevi. These Terms of Use (\"Terms\") govern your use of the Capevi mobile application (the \"App\") provided by Capevi Tech (\"we,\" \"us,\" or \"our\"). By downloading, installing, or using Capevi, you agree to comply with and be bound by these Terms. If you do not agree to these Terms, please do not use the App.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "2. Use of the App",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )

            Text(
                "2.1 Eligibility",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "You must be at least 18 years old to use Capevi. By using the App, you represent and warrant that you meet this age requirement and have the legal capacity to enter into these Terms.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "2.2 License",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We grant you a limited, non-exclusive, non-transferable, revocable license to use Capevi for personal and professional purposes, subject to these Terms. You agree not to use the App for any unlawful or prohibited activities.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "2.3 User Responsibilities",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                    "You are responsible for ensuring that your use of the App complies with all applicable laws, regulations, and these Terms. You must not:\n" +
                        "- Use the App to capture, store, or share any illegal or unauthorized content.\n" +
                        "- Attempt to gain unauthorized access to the App or its systems.\n" +
                        "- Interfere with or disrupt the functionality of the App.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "3. Content and Data",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )

            Text(
                "3.1 User-Generated Content",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Capevi allows you to capture and store various types of evidence, including images, audio recordings, and other media files. You retain ownership of any content you create using the App, but you grant us a non-exclusive, worldwide, royalty-free license to use, store, and process the content for the purpose of providing the App's services.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "3.2 Data Security",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We prioritize the security of your data. However, we cannot guarantee absolute security. You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "3.3 Data Retention",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We will retain your data for as long as necessary to provide the App’s services, comply with legal obligations, or resolve disputes.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "4. Privacy",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Your use of Capevi is also governed by our Privacy Policy, which explains how we collect, use, and protect your personal information. By using the App, you agree to the terms of our Privacy Policy.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "5. Intellectual Property",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )

            Text(
                "5.1 Ownership",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "All intellectual property rights in the App, including but not limited to software, trademarks, and logos, are owned by us or our licensors. You may not use our intellectual property without our prior written consent.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "5.2 Restrictions",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text =
                    "You agree not to:\n" +
                        "- Modify, reverse engineer, or create derivative works based on the App.\n" +
                        "- Use any automated systems or software to extract data from the App for commercial purposes.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "6. Disclaimer of Warranties",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Capevi is provided on an \"as-is\" and \"as-available\" basis. We do not guarantee that the App will be error-free, secure, or always available. We disclaim all warranties, express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, and non-infringement.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "7. Limitation of Liability",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "To the fullest extent permitted by law, we will not be liable for any direct, indirect, incidental, special, or consequential damages arising out of or related to your use of the App, even if we have been advised of the possibility of such damages.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "8. Termination",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We reserve the right to suspend or terminate your access to Capevi at our sole discretion, without notice, if you violate these Terms or for any other reason.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "9. Changes to Terms",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We may update these Terms from time to time. We will notify you of any changes by posting the updated Terms in the App. Your continued use of Capevi after any such changes constitutes your acceptance of the new Terms.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "10. Governing Law",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "These Terms are governed by and construed in accordance with the laws of the United Kingdom, without regard to its conflict of law principles. Any disputes arising under these Terms will be subject to the exclusive jurisdiction of the courts in the United Kingdom.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "11. Contact Us",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "If you have any questions or concerns about these Terms, please contact us at support@capevi.com.",
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
