package com.autobot.basicapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.autobot.basicapp.exoplayer.MainViewModel
import com.autobot.basicapp.signin.GoogleAuthUiClient
import com.autobot.basicapp.signin.SignInScreen
import com.autobot.basicapp.signin.UserData
import com.google.android.gms.auth.api.identity.Identity
import com.launcher.arclauncher.compose.theme.MyAppThemeComposable
import com.plcoding.composegooglesignincleanarchitecture.presentation.sign_in.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModels()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppThemeComposable {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = NavScreenSignUp
                    ) {
                        composable<NavScreenSignUp> {
                            ScreenSignUp(

                                onSignUpClick = { userData ->
                                    navController.navigate(
                                        NavScreenCreateRoom(
                                            userId = userData.userId?:"",
                                            username = userData.username,
                                            profilePictureUrl = userData.profilePictureUrl
                                        )
                                    )
                                }
                            )
                        }
                        composable<NavScreenCreateRoom> {
                            val args = it.toRoute<NavScreenCreateRoom>()
                            ScreenCreateRoom(
                                userData = UserData(
                                    userId = args.userId,
                                    username = args.username,
                                    profilePictureUrl = args.profilePictureUrl
                                ),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    }
                                },
                                onCreateRoom = { roomId ->
                                    mainViewModel.createRoom(roomId)
                                    navController.navigate(
                                        NavScreenRoom(
                                            roomId = roomId,
                                            userId = args.userId,
                                            username = args.username,
                                            profilePictureUrl = args.profilePictureUrl
                                        )
                                    )
                                },
                                onJoinRoom = {
                                    navController.navigate(
                                        NavScreenJoinRoom(
                                            userId = args.userId,
                                            username = args.username,
                                            profilePictureUrl = args.profilePictureUrl
                                        )
                                    )
                                }
                            )
                        }
                        composable<NavScreenRoom> {
                            val args = it.toRoute<NavScreenRoom>()
                            val userData = UserData(
                                userId = args.userId,
                                username = args.username,
                                profilePictureUrl = args.profilePictureUrl
                            )
                            ScreenRoom(
                                roomId = args.roomId,
                                userData = userData,
                                onExit = {
                                    navController.popBackStack()
                                },
                                onUpload = {
                                    navController.navigate(NavScreenUpload)
                                }
                            )
                        }
                        composable<NavScreenJoinRoom> {
                            val args = it.toRoute<NavScreenJoinRoom>()
                            val userData = UserData(
                                userId = args.userId,
                                username = args.username,
                                profilePictureUrl = args.profilePictureUrl
                            )
                            ScreenJoinRoom(userData = userData,
                                onJoinPartyClick = { roomId ->
                                    navController.navigate(
                                        NavScreenRoom(
                                            roomId = roomId,
                                            userId = args.userId,
                                            username = args.username,
                                            profilePictureUrl = args.profilePictureUrl
                                        )
                                    )
                                },
                                onCancelClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<NavScreenUpload> {
                          ScreenUpload(onExit = {
                            navController.popBackStack()
                          })
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ScreenSignUp(onSignUpClick: (UserData)-> Unit) {

        val viewModel = viewModel<SignInViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = Unit) {
            if (googleAuthUiClient.getSignedInUser() != null) {
                onSignUpClick(googleAuthUiClient.getSignedInUser() ?: return@LaunchedEffect)
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                    }
                }
            }
        )

        LaunchedEffect(key1 = state.isSignInSuccessful) {
            if (state.isSignInSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Sign in successful",
                    Toast.LENGTH_LONG
                ).show()
                onSignUpClick(googleAuthUiClient.getSignedInUser() ?: return@LaunchedEffect)
                viewModel.resetState()
            }
        }

        SignInScreen(
            state = state,
            onSignInClick = {
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            }
        )
    }

}

@Serializable
object NavScreenSignUp


@Serializable
object NavScreenUpload
@Serializable
data class NavScreenCreateRoom(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)

@Serializable
data class NavScreenRoom(
    val roomId: String,
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)

@Serializable
data class NavScreenJoinRoom(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)