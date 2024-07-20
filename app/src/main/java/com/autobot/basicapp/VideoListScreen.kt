package com.autobot.basicapp

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autobot.basicapp.database.Movie
import com.autobot.basicapp.viewmodels.PlayerViewModel
import com.google.firebase.storage.FirebaseStorage
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VideoListScreen(onSelectVideo: (String) -> Unit) {
    val playerViewModel: PlayerViewModel = viewModel()
    val movies by playerViewModel.movies.collectAsState()
    val loading by playerViewModel.loading.collectAsState()

    if (loading) {
        LoadingScreen()
    } else {
        VideoList(movies = movies, onSelectVideo = onSelectVideo)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun VideoList(movies: List<Movie>, onSelectVideo: (String) -> Unit) {
    LazyColumn {
        items(movies) { movie ->
            VideoItem(movie = movie, onClick = onSelectVideo, onDownload = { })
        }
    }
}
@Composable
fun VideoItem(movie: Movie, onClick: (String) -> Unit, onDownload: (String) -> Unit) {
    var isDownloading by rememberSaveable { mutableStateOf(false) }
    var downloadProgress by rememberSaveable { mutableFloatStateOf(0f) }
    var isDownloaded by rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(movie.url) }
            .padding(8.dp)
            .background(MyAppThemeColors.current.myText, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(movie.name)
            Spacer(modifier = Modifier.height(4.dp))

            if (isDownloading) {
                LinearProgressIndicator(
                    progress = { downloadProgress / 100f },
                    modifier = Modifier.wrapContentSize(),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Column {
            Button(
                onClick = {
                    if (isDownloaded) {
                        // Handle play functionality here
                    } else {
                        isDownloading = true
                        // Call the download function
                        onDownload(movie.url)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDownloaded) {
                        MyAppThemeColors.current.primary
                    } else {
                        MyAppThemeColors.current.secondary
                    }
                )
            ) {
                Text(text = if (isDownloaded) "Play" else "Download")
            }
        }
    }

    LaunchedEffect(isDownloading) {
        if (isDownloading) {
            while (downloadProgress < 100f) {
                delay(100) // Simulate download delay
                downloadProgress += 1f // Simulate download progress
            }
            isDownloading = false
            isDownloaded = true // Set to true after download completes
        }
    }

}
fun downloadMovie(context: Context, fileName: String, onProgress: (Float) -> Unit, onComplete: (Boolean) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("movies/$fileName")
    val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)

    storageRef.getFile(localFile).addOnSuccessListener {
        onComplete(true)
    }.addOnFailureListener {
        onComplete(false)
    }.addOnProgressListener { snapshot ->
        val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
        onProgress(progress)
    }
}