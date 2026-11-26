package com.rekapnilai.merdeka.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rekapnilai.merdeka.ui.screens.*
import com.rekapnilai.merdeka.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object TujuanPembelajaran : Screen("tp")
    object Siswa : Screen("siswa")
    object InputNilai : Screen("input_nilai/{siswaId}") {
        fun createRoute(siswaId: Long) = "input_nilai/$siswaId"
    }
    object RekapSiswa : Screen("rekap_siswa/{siswaId}") {
        fun createRoute(siswaId: Long) = "rekap_siswa/$siswaId"
    }
    object RekapTp : Screen("rekap_tp")
    object Backup : Screen("backup")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTp = { navController.navigate(Screen.TujuanPembelajaran.route) },
                onNavigateToSiswa = { navController.navigate(Screen.Siswa.route) },
                onNavigateToRekapTp = { navController.navigate(Screen.RekapTp.route) },
                onNavigateToBackup = { navController.navigate(Screen.Backup.route) },
                onSiswaClick = { siswaId ->
                    navController.navigate(Screen.InputNilai.createRoute(siswaId))
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.TujuanPembelajaran.route) {
            TujuanPembelajaranScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Siswa.route) {
            SiswaScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSiswaClick = { siswaId ->
                    navController.navigate(Screen.InputNilai.createRoute(siswaId))
                }
            )
        }
        composable(
            route = Screen.InputNilai.route,
            arguments = listOf(navArgument("siswaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getLong("siswaId") ?: 0L
            InputNilaiScreen(
                siswaId = siswaId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLihatRekap = {
                    navController.navigate(Screen.RekapSiswa.createRoute(siswaId))
                }
            )
        }
        composable(
            route = Screen.RekapSiswa.route,
            arguments = listOf(navArgument("siswaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val siswaId = backStackEntry.arguments?.getLong("siswaId") ?: 0L
            RekapSiswaScreen(
                siswaId = siswaId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RekapTp.route) {
            RekapTpScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Backup.route) {
            BackupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
