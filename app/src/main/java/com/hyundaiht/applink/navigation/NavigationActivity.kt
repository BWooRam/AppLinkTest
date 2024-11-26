package com.hyundaiht.applink.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.Composer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        Log.d(tag, "onNewIntent intent uri = $uri")

        if (uri != null) {
            deepLinkState.value = DeepLink(uri = uri)
        }
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

    data class DeepLink(
        val uri: Uri? = null
    )

    private val deepLinkState = mutableStateOf(DeepLink())

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController() // NavController를 생성
        val rememberDeepLink by remember { deepLinkState }

        LaunchedEffect(rememberDeepLink) {
            Log.d(tag, "AppNavigation LaunchedEffect rememberDeepLink = $rememberDeepLink")
            val deepLinkUri = rememberDeepLink.uri ?: return@LaunchedEffect

            val request = NavDeepLinkRequest.Builder
                .fromUri(deepLinkUri)
//                .setAction()
//                .setMimeType()
                .build()
            navController.navigate(request)
        }

        ActivityNavigator
        NavHost(navController = navController, startDestination = "main_screen") {
            testNavigation(navController)
//            mainNavigation(navController)
            navigation(navController)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun NavGraphBuilder.testNavigation(navController: NavController) {
        composable(route = "main_screen") { backStackEntry ->
            MainScreen(
                onNavigateToMain = {
                    val navOptions = NavOptions.Builder().build()
                    navController.navigate("main_screen", navOptions = navOptions)
                },
                onNavigateToTest1 = { navController.navigate("test_screen1") },
                onNavigateToTest2 = { navController.navigate("test_screen2") },
                onnNavigateToDialog = { navController.navigate("dialog_screen") }
            )
        }

        val defaultNavDeepLink = NavDeepLink.Builder()
            .setUriPattern("myapp://open.login.redirect")
//            .setAction()
//            .setMimeType()
            .build()
        composable("test_screen1", deepLinks = listOf(defaultNavDeepLink)) { backStackEntry ->
            TestScreen1()
        }
        composable("test_screen2") { backStackEntry ->
            TestScreen2(onNavigateToTest2 = { navController.navigate("test_screen2") })
        }
        dialog(
            route = "dialog_screen",
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) { backStackEntry ->
            SettingsScreen(confirmButtonClick = {
                navController.popBackStack()
            }, dismissButtonClick = {
                navController.popBackStack()
            })
        }
    }

    // This screen will be displayed as a dialog
    @Composable
    fun SettingsScreen(confirmButtonClick: () -> Unit, dismissButtonClick: () -> Unit) {
        AlertDialog(onDismissRequest = { dismissButtonClick.invoke() },
            title = { Text(text = "경고") },
            text = { Text(text = "작업을 진행하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    confirmButtonClick.invoke()
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = {
                    dismissButtonClick.invoke()
                }) {
                    Text("취소")
                }
            })
    }


    private fun NavGraphBuilder.navigation(navController: NavController) {
        composable<Navigation> { backStackEntry ->
            val data = backStackEntry.toRoute<Navigation>()
            Log.d("$tag NavGraph", "Navigation data = ${data.friendsList}")
            MainScreen(
                onNavigateToMain = { navController.navigate(Navigation) },
                onNavigateToTest1 = { navController.navigate("test_screen1") },
                onNavigateToTest2 = { navController.navigate("test_screen2") },
                onnNavigateToDialog = { navController.navigate("dialog_screen") }
            )
        }
    }

    @SuppressLint("RestrictedApi")
    private fun NavGraphBuilder.mainNavigation(navController: NavController) {
        composable<MainNavigation.Main> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Main")
            MainScreen(
                onNavigateToMain = { navController.navigate(MainNavigation.Main) },
                onNavigateToTest1 = { navController.navigate(MainNavigation.Test1) },
                onNavigateToTest2 = { navController.navigate(MainNavigation.Test2) },
                onnNavigateToDialog = { navController.navigate("dialog_screen") }
            )
        }
        composable<MainNavigation.Test1> { backStackEntry ->
            val data = backStackEntry.toRoute<MainNavigation.Test1>()
            Log.d("$tag NavGraph", "MainNavigation.Test1 data = $data")
            TestScreen1()
        }
        composable<MainNavigation.Test2> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Test2")
            TestScreen2(onNavigateToTest2 = { navController.navigate(MainNavigation.Test2) })
        }
        navigation(navController)
    }

    @Composable
    fun MainScreen(
        onNavigateToMain: () -> Unit,
        onNavigateToTest1: () -> Unit,
        onNavigateToTest2: () -> Unit,
        onnNavigateToDialog: () -> Unit,
    ) {
        val currentLifecycleState = LocalLifecycleOwner.current.lifecycle.currentStateAsState()
        val rememberString by remember { mutableStateOf("main") }
        val index by remember { mutableStateOf("111111") }
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
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToMain.invoke()
            }) {
                Text("Go to MainScreen(Navigation)")
            }
            /*
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                navController.safeNavigation(
                    MainNavigation.Test1
                ).onFailure { e ->
                    Log.d(tag, "safeNavigation onFailure e = $e")
                }
            }) {
                Text("Go to TestScreen1(MainNavigation)")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                navController.navigate(MainNavigation.Test2)
            }) {
                Text("Go to TestScreen2(MainNavigation)")
            }*/
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onnNavigateToDialog.invoke()
            }) {
                Text("Go to dialog_screen")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToTest1.invoke()
            }) {
                Text("Go to test_screen1")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToTest2.invoke()
            }) {
                Text("Go to test_screen2")
            }
        }
    }

    private fun <T : Any> NavController.safeNavigation(
        route: T, navOptions: NavOptions? = null, navigatorExtras: Navigator.Extras? = null
    ): Result<Unit> {
        return kotlin.runCatching {
            navigate(route, navOptions, navigatorExtras)
        }
    }

    @Composable
    fun TestScreen1() {
        val rememberString by remember { mutableStateOf("test1") }
        val index by remember { mutableStateOf("88888") }
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

    data class Info(
        val id: String, val name: String, var email: String? = null
    )

    @Composable
    fun TestScreen2(
        onNavigateToTest2: () -> Unit,
    ) {
        var name by remember { mutableStateOf("test2") }
        val index by remember { mutableStateOf("99999") }
        val data by remember { mutableStateOf(createInfo(10000)) }
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
            Text("$name$index")
            Text("$data", maxLines = 1)

            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToTest2.invoke()
            }) {
                Text("Go to TestScreen2(MainNavigation)")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                name = "Test1"
            }) {
                Text("remember Test1")
            }

            Button(modifier = Modifier.wrapContentSize(), onClick = {
                name = "Test2"
            }) {
                Text("remember Test2")
            }
        }
    }

    private fun createInfo(count: Int): List<Info> {
        val temp: MutableList<Info> = mutableListOf()
        for (index in 0 until count) {
            temp.add(Info(id = index.toString(), name = "name$index", email = "email$index"))
        }
        return temp
    }

    private fun viewCompositionGroups(currentComposer: Composer) {
        val size = currentComposer.compositionData.compositionGroups.count()
        Log.d(tag, "ViewCompositionGroups size = $size")

        for (composition in currentComposer.compositionData.compositionGroups) {
            Log.d(tag, "ViewCompositionGroups composition key = ${composition.key}")
            Log.d(tag, "ViewCompositionGroups composition sourceInfo = ${composition.sourceInfo}")
            Log.d(tag, "ViewCompositionGroups composition groupSize = ${composition.groupSize}")
            Log.d(tag, "ViewCompositionGroups composition slotsSize = ${composition.slotsSize}")
            Log.d(tag, "ViewCompositionGroups composition data size = ${composition.data.count()}")
            for (data in composition.data) {
                Log.d(tag, "ViewCompositionGroups composition data = $data")
            }
        }
    }
}