package com.example.finalproject


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.FinalProjectTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LostAndFoundViewModel = viewModel()
                    LostAndFoundMain(viewModel, applicationContext)
                }
            }
        }
    }
}


@Composable
fun LostAndFoundMain(viewModel: LostAndFoundViewModel, context: Context) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "welcomeScreen") {
        composable("welcomeScreen") {
            WelcomeScreen(navController, viewModel, context)
        }
        composable("listingScreen/{jsonFileName}?isLost={isLost}") { backStackEntry ->
            val isLost = backStackEntry.arguments?.getBoolean("isLost") ?: false
            val jsonFileName = backStackEntry.arguments?.getString("jsonFileName") ?: if (isLost) "mock_data2.json" else "mock_data.json"
            ListingScreen(navController, viewModel, context, isLost)
        }
        composable("addItemScreen?isLost={isLost}") { backStackEntry ->
            val isLost = backStackEntry.arguments?.getBoolean("isLost") ?: false
            AddItemScreen(navController, viewModel, context, isLost)
        }
    }
}

