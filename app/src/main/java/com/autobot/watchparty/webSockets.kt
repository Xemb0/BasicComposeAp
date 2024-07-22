//package com.autobot.basicapp
//
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.firebase.database.tubesock.WebSocket
//
//val client = OkHttpClient()
//
//fun createWebSocket(url: String, listener: WebSocketListener): WebSocket {
//    val request = Request.Builder().url(url).build()
//    return client.newWebSocket(request, listener)
//}
//
//class PlaybackManager(private val exoPlayer: SimpleExoPlayer, private val webSocket: WebSocket) {
//    private var isHost: Boolean = false
//
//    fun setHost(host: Boolean) {
//        isHost = host
//    }
//
//    fun syncPlaybackPosition(position: Long) {
//        if (isHost) {
//            webSocket.send(position.toString())
//        } else {
//            exoPlayer.seekTo(position)
//        }
//    }
//
//    fun handleWebSocketMessage(message: String) {
//        val position = message.toLong()
//        if (!isHost) {
//            exoPlayer.seekTo(position)
//        }
//    }
//}
//
//@Composable
//fun PlaybackScreen(uri: Uri, playbackManager: PlaybackManager) {
//    ExoPlayerView(uri)
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(1000)
//            playbackManager.syncPlaybackPosition(exoPlayer.currentPosition)
//        }
//    }
//}
//
//val playbackManager = PlaybackManager(exoPlayer, createWebSocket("wss://example.com/sync", object : WebSocketListener() {
//    override fun onMessage(webSocket: WebSocket, text: String) {
//        playbackManager.handleWebSocketMessage(text)
//    }
//}))
//
//playbackManager.setHost(isHost)
