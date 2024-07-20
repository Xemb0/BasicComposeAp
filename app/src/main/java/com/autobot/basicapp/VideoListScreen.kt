package com.autobot.basicapp

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autobot.basicapp.database.Movie
import com.autobot.basicapp.viewmodels.PlayerViewModel
import com.google.firebase.storage.FirebaseStorage
import com.launcher.arclauncher.compose.theme.MyAppThemeColors
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun VideoListScreen(onSelectVideo: (Uri) -> Unit) {
    val playerViewModel: PlayerViewModel = viewModel()
    val movies by playerViewModel.movies.collectAsState()
    val loading by playerViewModel.loading.collectAsState()
    val context = LocalContext.current
    val downloadProgress by playerViewModel.downloadProgress.collectAsState()

    if (loading) {
        LoadingScreen()
    } else {
        VideoList(
            movies = movies,
            onSelectVideo = { onSelectVideo(getFileUri(context,it)) },
            onClickDownload = { uri, fileName ->
                playerViewModel.downloadMovieWithNotification(context, uri, fileName)
            },
            getDownloadStatus = { fileName -> playerViewModel.getDownloadStatus(fileName, context) },
            downloadProgress = downloadProgress
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
    downloadProgress: Map<String, Float>
) {
    LazyColumn {
        items(movies) { movie ->
            VideoItem(
                movie = movie,
                onPlay = { uri -> onSelectVideo(uri) },
                onClickDownload = { uri, fileName -> onClickDownload(uri, fileName) },
                getDownloadStatus = getDownloadStatus,
                downloadProgress = downloadProgress
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
    downloadProgress: Map<String, Float>
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
            Text(movie.name)
            Spacer(modifier = Modifier.height(4.dp))

            if (!isDownloaded) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.wrapContentSize(),
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