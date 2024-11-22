package com.hyundaiht.applink.navigation

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.hyundaiht.applink.ui.theme.WebViewTestTheme
import kotlinx.serialization.Serializable

class NavigationActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    private var composeView1: ComposeView? = null
    private var composeView2: ComposeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent =  ${intent.data}")
        enableEdgeToEdge()
        setContent {
            /*val existingComposeView = window.decorView
                .findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0) as? ComposeView
            Log.d(tag, "onCreate existingComposeView =  $existingComposeView")
            existingComposeView?.disposeComposition()*/

            WebViewTestTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Log.d(tag, "onNewIntent intent =  ${intent.data}")
    }

    @Serializable
    class Navigation(
        val friendsList: List<String>
    )

    sealed class MainNavigation {
        @Serializable
        data object Main : MainNavigation()

        @Serializable
        data class Test1(
            val data: String
        ) : MainNavigation()

        /*@Serializable
        data object Test1 : MainNavigation()*/

        @Serializable
        data object Test2 : MainNavigation()
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController() // NavController를 생성

        NavHost(navController = navController, startDestination = MainNavigation.Main) {
            testNavigation(navController)
            mainNavigation(navController)
        }
    }

    private fun NavGraphBuilder.testNavigation(navController: NavController) {
        composable("main_screen") { backStackEntry ->
            MainScreen(navController)
        }
        composable("test_screen1") { backStackEntry ->
            TestScreen1()
        }
        composable("test_screen2") { backStackEntry ->
            TestScreen2(navController)
        }
        dialog(
            route = "dialog_screen",
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) { backStackEntry ->
            SettingsScreen(
                confirmButtonClick = {
                    navController.popBackStack()
                },
                dismissButtonClick = {
                    navController.popBackStack()
                })
        }
    }


    // This screen will be displayed as a dialog
    @Composable
    fun SettingsScreen(confirmButtonClick: () -> Unit, dismissButtonClick: () -> Unit) {
        AlertDialog(
            onDismissRequest = { dismissButtonClick.invoke() },
            title = { Text(text = "경고") },
            text = { Text(text = "작업을 진행하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        confirmButtonClick.invoke()
                    }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        dismissButtonClick.invoke()
                    }) {
                    Text("취소")
                }
            }
        )
    }


    private fun NavGraphBuilder.navigation(navController: NavController) {
        composable<Navigation> { backStackEntry ->
            val data = backStackEntry.toRoute<Navigation>()
            Log.d("$tag NavGraph", "Navigation data = $data")
            MainScreen(navController)
        }
    }

    private fun NavGraphBuilder.mainNavigation(navController: NavController) {
        composable<MainNavigation.Main> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Main")
            MainScreen(navController)
        }
        composable<MainNavigation.Test1> { backStackEntry ->
            val data = backStackEntry.toRoute<MainNavigation.Test1>()
            Log.d("$tag NavGraph", "MainNavigation.Test1 data = $data")
            TestScreen1()
        }
        composable<MainNavigation.Test2> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Test2")
            TestScreen2(navController)
        }
        navigation(navController)
    }

    @Composable
    fun MainScreen(navController: NavController) {
        val currentLifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
        val rememberString by remember { mutableStateOf("") }
        Log.d(tag, "MainScreen currentLifecycleState = $currentLifecycleState")

        LifecycleStartEffect(rememberString) {
            onStopOrDispose {
                Log.d(tag, "MainScreen LifecycleEventEffect onStopOrDispose")
            }
        }

        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            // do something here
            Log.d(tag, "MainScreen LifecycleEventEffect ON_STOP")

        }

        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
            // do something here
            Log.d(tag, "MainScreen LifecycleEventEffect ON_PAUSE")
        }

        DisposableEffect(currentLifecycleState) {
            onDispose {
                Log.d(
                    tag,
                    "MainScreen DisposableEffect onDispose currentLifecycleState = $currentLifecycleState"
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate(Navigation(listOf("Test")))
                }) {
                Text("Go to MainScreen(Navigation)")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.safeNavigation(
                        MainNavigation.Test1
                    ).onFailure { e ->
                        Log.d(tag, "safeNavigation onFailure e = $e")
                    }
                }) {
                Text("Go to TestScreen1(MainNavigation)")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate(MainNavigation.Test2)
                }) {
                Text("Go to TestScreen2(MainNavigation)")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate("dialog_screen")
                }) {
                Text("Go to dialog_screen")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate("test_screen1")
                }) {
                Text("Go to test_screen1")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate("test_screen2")
                }) {
                Text("Go to test_screen1")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate("test_screen2")
                }) {
                Text("Go to test_screen1")
            }
        }
    }

    fun <T : Any> NavController.safeNavigation(
        route: T,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ): Result<Unit> {
        return kotlin.runCatching {
            navigate(route, navOptions, navigatorExtras)
        }
    }

    @Composable
    fun TestScreen1() {
        val rememberString by remember { mutableStateOf("") }

        LifecycleStartEffect(rememberString) {
            onStopOrDispose {
                Log.d(tag, "TestScreen1 LifecycleEventEffect onStopOrDispose")
            }
        }

        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            // do something here
            Log.d(tag, "TestScreen1 LifecycleEventEffect ON_STOP")

        }

        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
            // do something here
            Log.d(tag, "TestScreen1 LifecycleEventEffect ON_PAUSE")
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }

    @Composable
    fun TestScreen2(navController: NavController) {
        var name by remember { mutableStateOf("") }

        LifecycleStartEffect(name) {
            onStopOrDispose {
                Log.d(tag, "TestScreen2 LifecycleEventEffect onStopOrDispose")
            }
        }

        LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
            // do something here
            Log.d(tag, "TestScreen2 LifecycleEventEffect ON_STOP")

        }

        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
            // do something here
            Log.d(tag, "TestScreen2 LifecycleEventEffect ON_PAUSE")
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name)

            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    navController.navigate(MainNavigation.Test2)
                }) {
                Text("Go to TestScreen2(MainNavigation)")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    name = "Test1"
                }) {
                Text("remember Test1")
            }

            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    name = "Test2"
                }) {
                Text("remember Test2")
            }
        }
    }
}