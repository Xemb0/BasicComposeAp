import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.autobot.basicapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.firebase.storage.FirebaseStorage

@Composable
fun RoomScreen() {
    var videoPath by remember { mutableStateOf<Uri?>(null) }
    val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with Head Icon and Add User Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Head Icon (User who created the room)
            currentUser?.let {
                ProfileImage(photoUrl = it.photoUrl.toString())
            }

            UploadButton { uri ->
                uploadMovie(uri) { path ->
                    if (path != null) {
                        videoPath = uri
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Plus Icon to add more users
            IconButton(
                onClick = { /* Add user functionality */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Add User Icon",
                    tint = Color.Black // Customize tint color as needed
                )
            }
        }

        // Vertical Column for Room Details
        Column(modifier = Modifier.padding(16.dp)) {
            // Placeholder for Room Details
            Text("Room Details", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for User List (Vertical Column)
            Column {
                repeat(1) { index ->
                    UserListItem(name = "User $index")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Show video player if videoPath is set
        videoPath?.let {
            ExoPlayerVideoPlayer(it)
        }
    }
}

@Composable
fun UploadButton(onUploadMovie: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(context, it) // Implement this function to get the file name
            if (fileName != null) {
                onUploadMovie(uri)
            }
        }
    }

    Button(onClick = {
        launcher.launch("video/*")
    }) {
        Text("Upload Movie")
    }
}

fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }
    return fileName
}

@Composable
fun ProfileImage(photoUrl: String) {
    val painter: Painter = // Placeholder while loading
        rememberAsyncImagePainter(ImageRequest.Builder // Placeholder if loading fails
            (LocalContext.current).data(data = photoUrl).apply(block = fun ImageRequest.Builder.() {
            placeholder(R.drawable.ic_launcher_background) // Placeholder while loading
            error(R.drawable.ic_launcher_background) // Placeholder if loading fails
        }).build()
        )

    Image(
        painter = painter,
        contentDescription = "Profile Picture",
        modifier = Modifier.size(48.dp),
    )
}

@Composable
fun UserListItem(name: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "User Icon",
            tint = Color.Black // Customize tint color as needed
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(name)
    }
}


@Composable
fun ExoPlayerVideoPlayer(uri: Uri) {
    val context = LocalContext.current

    var exoPlayer by remember {
        mutableStateOf<ExoPlayer?>(null)
    }

    DisposableEffect(uri) {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        onDispose {
            exoPlayer?.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                controllerAutoShow = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}



fun uploadMovie( uri: Uri, onComplete: (Uri?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val movieRef = storageRef.child("movies/${uri.lastPathSegment}")

    movieRef.putFile(uri)
        .addOnSuccessListener {
            onComplete(uri)
        }
        .addOnFailureListener {
            onComplete(null)
        }
}
