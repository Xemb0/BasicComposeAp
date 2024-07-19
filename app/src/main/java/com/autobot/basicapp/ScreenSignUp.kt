package com.autobot.basicapp

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autobot.basicapp.signin.GoogleAuthUiClient
import com.autobot.basicapp.signin.SignInScreen
import com.google.android.gms.auth.api.identity.Identity
import com.plcoding.composegooglesignincleanarchitecture.presentation.sign_in.SignInViewModel
import kotlinx.coroutines.launch

