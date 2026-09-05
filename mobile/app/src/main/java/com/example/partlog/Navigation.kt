package com.example.partlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.partlog.ui.JobViewModel
import com.example.partlog.ui.screens.HomeScreen
import com.example.partlog.ui.screens.VehicleIdScreen
import com.example.partlog.ui.screens.ComponentDetailsScreen
import com.example.partlog.ui.screens.MeasurementsScreen
import com.example.partlog.ui.screens.PhotoCaptureScreen
import com.example.partlog.ui.screens.WorkshopDetailsScreen
import com.example.partlog.ui.screens.FailureCauseScreen
import com.example.partlog.ui.screens.ConfirmationScreen
import com.example.partlog.ui.screens.SignUpScreen
import com.example.partlog.ui.screens.LoginScreen
import com.example.partlog.ui.screens.LandingScreen

@Composable
fun MainNavigation() {
  val jobViewModel: JobViewModel = viewModel()
  val backStack = rememberNavBackStack(if (jobViewModel.isUserRegistered) Main else Landing)

  Box(modifier = Modifier.fillMaxSize()) {
    NavDisplay(
      backStack = backStack,
      onBack = { backStack.removeLastOrNull() },
      entryProvider =
        entryProvider {
          entry<Landing> {
            LandingScreen(
              viewModel = jobViewModel,
              onLoginClick = { backStack.add(Login) },
              onRegisterClick = { backStack.add(SignUp) },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<Login> {
            LoginScreen(
              viewModel = jobViewModel,
              onLoginSuccess = {
                backStack.clear()
                backStack.add(Main)
              },
              onNavigateToSignUp = {
                backStack.add(SignUp)
              },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<SignUp> {
            SignUpScreen(
              viewModel = jobViewModel,
              onSignUpSuccess = {
                backStack.removeLastOrNull()
              },
              onNavigateToLogin = {
                backStack.removeLastOrNull()
              },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<Main> {
            HomeScreen(
              viewModel = jobViewModel,
              onLogJobClick = {
                backStack.add(VehicleId)
              },
              onLogoutClick = {
                jobViewModel.logout()
                backStack.clear()
                backStack.add(Login)
              },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<VehicleId> {
            VehicleIdScreen(
              viewModel = jobViewModel,
              onNext = {
                if (jobViewModel.componentType.value == "compressor") {
                  backStack.add(PhotoCapture)
                } else {
                  backStack.add(ComponentDetails)
                }
              },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<ComponentDetails> {
            ComponentDetailsScreen(
              viewModel = jobViewModel,
              onNext = {
                if (jobViewModel.componentType.value == "compressor") {
                  backStack.add(Measurements)
                } else {
                  backStack.add(PhotoCapture)
                }
              },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<Measurements> {
            MeasurementsScreen(
              viewModel = jobViewModel,
              onNext = { backStack.add(PhotoCapture) },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<PhotoCapture> {
            PhotoCaptureScreen(
              viewModel = jobViewModel,
              onNext = {
                if (jobViewModel.componentType.value == "compressor") {
                  backStack.add(FailureCause)
                } else {
                  backStack.add(WorkshopDetails)
                }
              },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<WorkshopDetails> {
            WorkshopDetailsScreen(
              viewModel = jobViewModel,
              onNext = { backStack.add(FailureCause) },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<FailureCause> {
            FailureCauseScreen(
              viewModel = jobViewModel,
              onSubmit = {
                jobViewModel.submitJob()
                backStack.add(Confirmation)
              },
              onCancel = { backStack.removeLastOrNull() },
              modifier = Modifier.fillMaxSize()
            )
          }
          entry<Confirmation> {
            ConfirmationScreen(
              viewModel = jobViewModel,
              onGoHome = {
                // Pop all screens back to Main cleanly by resetting the backstack
                backStack.clear()
                backStack.add(Main)
              },
              modifier = Modifier.fillMaxSize()
            )
          }
        },
    )

    if (jobViewModel.isLanguageSwitching.value) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .safeDrawingPadding(),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
        )
        CircularProgressIndicator(color = Color(0xFFFFFFFF))
      }
    }
  }
}
