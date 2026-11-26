package com.rekapnilai.merdeka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rekapnilai.merdeka.ui.navigation.NavGraph
import com.rekapnilai.merdeka.ui.theme.RekapNilaiMerdekaTheme
import com.rekapnilai.merdeka.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as RekapNilaiApp

        setContent {
            RekapNilaiMerdekaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModel.factory(app.repository)
                    )
                    NavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
