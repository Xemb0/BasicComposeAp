package com.autobot.watchparty.mainscreens

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autobot.watchparty.database.Movie
import com.autobot.watchparty.database.viewmodels.MovieViewModel
import com.google.firebase.storage.FirebaseStorage
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
import java.io.File

@Composable
fun VideoListScreen(onSelectVideo: (Uri) -> Unit) {
    val movieViewModel: MovieViewModel = viewModel()
    val movies by movieViewModel.movieList.collectAsState()
    val loading by movieViewModel.loading.collectAsState()
    val context = LocalContext.current
    val downloadProgress by movieViewModel.downloadProgress.collectAsState()
    var showConfirmExitDialog by remember { mutableStateOf(false) }

    movieViewModel.listenForMoviesUpdate("123")

    if (loading) {
        LoadingScreen()
    } else {
        VideoList(
            movies = movies,
            onSelectVideo = { onSelectVideo(getFileUri(context, it)) },
            onClickDownload = { uri, fileName ->
                movieViewModel.downloadMovieWithNotification(context, "123", uri, fileName)
            },
            getDownloadStatus = { fileName -> movieViewModel.getDownloadStatus(fileName, context) },
            downloadProgress = downloadProgress,
            onDelete = { fileName ->
                deleteMovie(context, fileName) { success ->
                    if (success) {
                        // Optionally handle the success scenario
                    }
                }
            }
        )
    }
}

fun getFileUri(context: Context, fileName: String): Uri {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)
    return Uri.fromFile(file)
}
@Composable
fun VideoList(
    movies: List<Movie>,
    onSelectVideo: (String) -> Unit,
    onClickDownload: (String, String) -> Unit,
    getDownloadStatus: (String) -> Boolean,
    downloadProgress: Map<String, Float>,
    onDelete: (String) -> Unit // Add this parameter for handling file deletion
) {
    LazyColumn {
        items(movies) { movie ->
            VideoItem(
                movie = movie,
                onPlay = { uri -> onSelectVideo(uri) },
                onClickDownload = { uri, fileName -> onClickDownload(uri, fileName) },
                getDownloadStatus = getDownloadStatus,
                downloadProgress = downloadProgress,
                onDelete = onDelete // Pass the delete function
            )
        }
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
fun VideoItem(
    movie: Movie,
    onPlay: (String) -> Unit,
    onClickDownload: (String, String) -> Unit,
    getDownloadStatus: (String) -> Boolean,
    downloadProgress: Map<String, Float>,
    onDelete: (String) -> Unit // Add this parameter for handling file deletion
) {
    val fileName = movie.name
    val isDownloaded = getDownloadStatus(fileName)
    val progress = downloadProgress[fileName] ?: 0f

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(8.dp)
            .background(MyAppThemeColors.current.myText, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(movie.name.take(20))
            Spacer(modifier = Modifier.height(4.dp))

            if (!isDownloaded) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.width(150.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Column {
            Button(
                onClick = {
                    if (isDownloaded) {
                        onPlay(fileName)
                    } else {
                        onClickDownload(movie.url, fileName)
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

            Spacer(modifier = Modifier.height(4.dp))

            // Delete Button
            Button(
                onClick = {
                    onDelete(fileName) // Handle file deletion
                },
            ) {
                Text(text = "Delete")
            }
        }
    }
}



fun deleteMovie(context: Context, fileName: String, onComplete: (Boolean) -> Unit) {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)
    if (file.exists()) {
        val deleted = file.delete()
        onComplete(deleted)
    } else {
        onComplete(false)
    }
}
