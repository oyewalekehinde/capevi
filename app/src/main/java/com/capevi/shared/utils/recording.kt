package com.capevi.shared.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.capevi.app.ui.theme.Black
import com.capevi.app.ui.theme.Blue
import com.capevi.app.ui.theme.Neutral
import com.capevi.viewmodels.SharedViewModel
import kotlinx.coroutines.delay
import java.io.File

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String? = null

    fun startRecording(context: Context) {
        val fileName = "audio_${System.currentTimeMillis()}.3gp"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        outputFilePath = "${storageDir?.absolutePath}/$fileName"

        mediaRecorder =
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }
    }

    fun stopRecording(): String? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        return outputFilePath
    }

    fun getAmplitude(): Int = mediaRecorder?.maxAmplitude ?: 0
}

@Composable
fun WaveformView(amplitude: Int) {
    val barCount = 50
    val maxAmplitude = 32767
    val normalizedAmplitude = amplitude.toFloat() / maxAmplitude

    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val barWidth = size.width / barCount
        val centerY = size.height / 2

        for (i in 0 until barCount) {
            val barHeight = (centerY * normalizedAmplitude) * (i % 2 + 1)
            drawRect(
                color = Blue[900]!!,
                topLeft = Offset(i * barWidth, centerY - barHeight / 2),
                size = Size(barWidth, barHeight),
            )
        }
    }
}

@Composable
fun RecordingUI(
    navController: NavHostController,
    context: Context,
    audioRecorder: AudioRecorder,
    onRecordingSaved: (String?) -> Unit,
) {
    var isRecording by remember { mutableStateOf(false) }
    var filePath by remember { mutableStateOf<String?>(null) }
    var amplitude by remember { mutableIntStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0L) }
    // This coroutine will update the amplitude continuously while recording
    LaunchedEffect(isRecording) {
        val startTime = System.currentTimeMillis()
        while (isRecording) {
            amplitude = audioRecorder.getAmplitude()
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000
            delay(100) // Update every 100ms
        }
    }
    // Convert elapsed time to minutes and seconds
    val minutes = elapsedTime / 60
    val seconds = elapsedTime % 60
    val timeDisplay = String.format("%02d:%02d", minutes, seconds)
    Scaffold { innerPadding ->

        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.Black),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .background(
                            color = Color.White,
                        ).height(30.dp)
                        .fillMaxWidth(1f)
                        .padding(horizontal = 20.dp),
            ) {
                Text(
                    modifier = Modifier.clickable { navController.popBackStack() },
                    text = "Done",
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Black[950]!!,
                            fontWeight = FontWeight.W500,
                            textAlign = TextAlign.Center,
                        ),
                )
                if (isRecording) {
                    Text(
                        text = timeDisplay,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Neutral[950]!!,
                                fontWeight = FontWeight.W500,
                                textAlign = TextAlign.Center,
                            ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            if (isRecording) {
                WaveformView(amplitude = amplitude)
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.6f))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {
                        if (isRecording) {
                            filePath = audioRecorder.stopRecording()
                            onRecordingSaved(filePath)
                            isRecording = false
                        } else {
                            audioRecorder.startRecording(context)
                            isRecording = true
                            elapsedTime = 0L
                        }
                    },
                ) {
                    Text(if (isRecording) "Stop Recording" else "Start Recording")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

//    if (!isRecording && filePath != null) {
//        Text("Recording saved at $filePath")
//    }
}

fun playAudio(filePath: String) {
    val mediaPlayer =
        MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }

    mediaPlayer.setOnCompletionListener {
        it.release()
    }
}

@Composable
fun RecordingScreen(
    navController: NavHostController,
    viewModel: SharedViewModel,
) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    RecordingUI(audioRecorder = audioRecorder, context = context, navController = navController) { savedFilePath ->
        if (savedFilePath != null) {
            val soundUri = filePathToUri(context, savedFilePath)
            viewModel.addMedia(soundUri)
            Toast
                .makeText(
                    context,
                    "Sound evidence captured",
                    Toast.LENGTH_SHORT,
                ).show()
            Log.d("RecordingUI", "Recording saved at: $savedFilePath")
        }
    }
}

// Helper function to convert file path to URI
private fun filePathToUri(
    context: Context,
    filePath: String,
): Uri {
    val file = File(filePath)
    return FileProvider.getUriForFile(
        context,
        context.applicationContext.packageName + ".provider",
        file,
    )
}
