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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkRequest
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
import com.hyundaiht.applink.notification.AppNotificationManager
import com.hyundaiht.applink.security.WebViewSecurity
import com.hyundaiht.applink.ui.theme.WebViewTestTheme
import com.hyundaiht.applink.webview_no.ActivityTestActivity
import com.hyundaiht.applink.webview_no.MainActivity
import com.hyundaiht.applink.webview_no.Test1Activity
import com.hyundaiht.applink.webview_no.Test2Activity
import kotlinx.serialization.Serializable
import kotlin.random.Random

class NavigationActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    private var composeView1: ComposeView? = null
    private var composeView2: ComposeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate hashCode = ${hashCode()}, intent =  ${intent.data}")
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
        Log.d(tag, "onStart hashCode = ${hashCode()}")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop hashCode = ${hashCode()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy hashCode = ${hashCode()}")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(tag, "onNewIntent hashCode = ${hashCode()}, intent =  ${intent.data}")
        updateRequestedDeepLink(intent)

    }

    /**
     * updateRequestedDeepLink
     *
     * @param intent
     */
    private fun updateRequestedDeepLink(intent: Intent) {
        val deepLinkUri = intent.data ?: return
        Log.d(tag, "updateRequestedDeepLink intent deepLinkUri = $deepLinkUri")
        deepLinkState.value = DeepLink(uri = deepLinkUri)
    }

    /**
     * 중첩 클래스 테스트를 위한 Data Class
     *
     * @property friendsList
     */
    @Serializable
    class Navigation(
        val friendsList: List<String>
    )

    /**
     * Safe Args로 네비게이션 탐색을 위한 Data Class
     *
     */
    @Serializable
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

    /**
     * DeepLink
     *
     * @property uri
     */
    data class DeepLink(
        val uri: Uri? = null
    ) {
        enum class Navigation(val uri: Uri) {
            Test1(Uri.parse("myapp://open.login.redirect/event?data={data}"))
        }

        companion object {
            val ALL_DEEP_LINK_NAVIGATION = arrayOf(DeepLink.Navigation.Test1)
        }
    }

    private val deepLinkState = mutableStateOf(DeepLink())

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController() // NavController를 생성
        HandleDeepLink { request ->
            Log.d(tag, "AppNavigation AppNavigation onDeepLink start")
            navController.navigate(request)
        }

        NavHost(navController = navController, startDestination = "main_screen") {
            Log.d(tag, "NavHost start activityHashcode = ${this@NavigationActivity.hashCode()}")
            testNavigation(navController)
//            mainNavigation(navController)
            deeplinkNavigation(navController)
            navigation(navController)
        }
    }

    /**
     * onNewIntent로 온 DeepLink 처리를 위한 메소드
     *
     * @param onDeepLink
     */
    @Composable
    fun HandleDeepLink(onDeepLink: (NavDeepLinkRequest) -> Unit) {
        val rememberDeepLink by remember { deepLinkState }

        LaunchedEffect(rememberDeepLink) {
            Log.d(tag, "AppNavigation LaunchedEffect rememberDeepLink = $rememberDeepLink")
            val deepLinkUri = rememberDeepLink.uri ?: return@LaunchedEffect
            val isWhite = true
            Log.d(tag, "AppNavigation LaunchedEffect isWhite = $isWhite")

            if (isWhite) {
                val result = DeepLink.ALL_DEEP_LINK_NAVIGATION.find {
                    val isSameScheme = it.uri.scheme == deepLinkUri.scheme
                    val isSameHost = it.uri.host ==  deepLinkUri.host
                    val isSamePath = it.uri.path ==  deepLinkUri.path
                    Log.d(tag, "AppNavigation LaunchedEffect Navigation scheme = ${it.uri.scheme}, host = ${it.uri.host}, path = ${it.uri.path}")
                    Log.d(tag, "AppNavigation LaunchedEffect deepLinkUri scheme = ${deepLinkUri.scheme}, host = ${deepLinkUri.host}, path = ${deepLinkUri.path}")
                    return@find isSameScheme && isSameHost && isSamePath
                } ?: return@LaunchedEffect

                val request = NavDeepLinkRequest.Builder.fromUri(deepLinkUri)
//                .setAction()
//                .setMimeType()
                    .build()

                onDeepLink.invoke(request)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun NavGraphBuilder.testNavigation(navController: NavController) {
        //Main
        composable(route = "main_screen") { backStackEntry ->
            MainScreen(onNavigateToMain = {
                val navOptions = NavOptions.Builder().build()
                navController.navigate("main_screen", navOptions = navOptions)
            },
                onNavigateToTest1 = { navController.navigate("test_screen1/testNavigation") },
                onNavigateToTest2 = { navController.navigate("test_screen2") },
                onnNavigateToDialog = { navController.navigate("dialog_screen") })
        }

        //Test2
        composable("test_screen2") { backStackEntry ->
            TestScreen2(onNavigateToTest2 = { navController.navigate("test_screen2") })
        }

        //Dialog
        dialog(
            route = "dialog_screen", dialogProperties = DialogProperties(
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

    /**
     * SettingsScreen
     *
     * @param confirmButtonClick
     * @param dismissButtonClick
     */
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

    @SuppressLint("RestrictedApi")
    private fun NavGraphBuilder.deeplinkNavigation(navController: NavController) {
        val test1NavDeepLink = NavDeepLink.Builder()
            .setUriPattern(DeepLink.Navigation.Test1.uri.toString())
//            .setAction()
//            .setMimeType()
            .build()

        //DeepLink1
        composable(
            route = "${DeepLink.Navigation.Test1.uri.host}${DeepLink.Navigation.Test1.uri.path}?data={data}",
            deepLinks = listOf(test1NavDeepLink),
            arguments = listOf(navArgument("data") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data")
            Log.d(
                tag,
                "test_screen1 activityHashcode = ${this@NavigationActivity.hashCode()}, data = $data"
            )
            TestScreen1(data)
        }
    }

    private fun NavGraphBuilder.navigation(navController: NavController) {
        composable<Navigation> { backStackEntry ->
            val data = backStackEntry.toRoute<Navigation>()
            Log.d("$tag NavGraph", "Navigation data = ${data.friendsList}")
            MainScreen(onNavigateToMain = { navController.navigate(Navigation) },
                onNavigateToTest1 = { navController.navigate("test_screen1/navigation") },
                onNavigateToTest2 = { navController.navigate("test_screen2") },
                onnNavigateToDialog = { navController.navigate("dialog_screen") })
        }
    }

    @SuppressLint("RestrictedApi")
    private fun NavGraphBuilder.mainNavigation(navController: NavController) {
        //Main
        composable<MainNavigation.Main> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Main")
            MainScreen(onNavigateToMain = { navController.navigate(MainNavigation.Main) },
                onNavigateToTest1 = { navController.navigate(MainNavigation.Test1) },
                onNavigateToTest2 = { navController.navigate(MainNavigation.Test2) },
                onnNavigateToDialog = { navController.navigate("dialog_screen") })
        }

        //Test1
        composable<MainNavigation.Test1> { backStackEntry ->
            val data = backStackEntry.toRoute<MainNavigation.Test1>()
            Log.d("$tag NavGraph", "MainNavigation.Test1 data = ${data.data}")
            TestScreen1(data.data)
        }

        //Test2
        composable<MainNavigation.Test2> { backStackEntry ->
            Log.d("$tag NavGraph", "MainNavigation.Test2")
            TestScreen2(onNavigateToTest2 = { navController.navigate(MainNavigation.Test2) })
        }

        navigation(navController)
    }

    /**
     * MainScreen
     *
     * @param onNavigateToMain
     * @param onNavigateToTest1
     * @param onNavigateToTest2
     * @param onnNavigateToDialog
     */
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
                val context = this@NavigationActivity
                startActivity(Intent(context, ActivityTestActivity::class.java))
                startActivity(Intent(context, MainActivity::class.java))
                startActivity(Intent(context, Test1Activity::class.java))
                startActivity(Intent(context, Test2Activity::class.java))
            }) {
                Text("다른 4종 Activity 띄우기")
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Button(modifier = Modifier.wrapContentSize(), onClick = {
                val context = this@NavigationActivity
                val intent = Intent(context, NavigationActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val randomValue = Random.nextInt(0, 99999)
                    data = Uri.parse("myapp://open.login.redirect/event?data=$randomValue")
                }
                AppNotificationManager.sendDeepLinkNotification(context, intent)
            }) {
                Text("DeepLink Notification 테스트\n이미 존재하는 Activity Intent 업데이트")
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )

            Button(modifier = Modifier.wrapContentSize(), onClick = {
                val context = this@NavigationActivity
                val intent = Intent(context, NavigationActivity::class.java).apply {
                    val randomValue = Random.nextInt(0, 99999)
                    data = Uri.parse("myapp://open.login.redirect/event?data=$randomValue")
                }
                AppNotificationManager.sendDeepLinkNotification(context, intent, true)
            }) {
                Text("DeepLink Notification 테스트\n새로 Activity 생성 후 Intent 업데이트")
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
            )

            Button(modifier = Modifier.wrapContentSize(), onClick = {
                val context = this@NavigationActivity
                val intent = Intent(context, NavigationActivity::class.java).apply {
                    val blackList = WebViewSecurity.getBlackList()
                    val randomValue = Random.nextInt(0, blackList.size)
                    Log.d(tag, "잘못된 DeepLink Intent 업데이트 ${blackList[randomValue]}?data=$randomValue")

                    data = Uri.parse("${blackList[randomValue]}?data=$randomValue")
                }
                AppNotificationManager.sendDeepLinkNotification(context, intent, false)
            }) {
                Text("DeepLink Notification 테스트\n잘못된 DeepLink Intent 업데이트")
            }

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToMain.invoke()
            }) {
                Text("Navigation 테스트 Safe Args 형태 MainScreen로 이동")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onnNavigateToDialog.invoke()
            }) {
                Text("Navigation 테스트 dialog_screen로 이동")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToTest1.invoke()
            }) {
                Text("Navigation 테스트 test_screen1로 이동")
            }
            Button(modifier = Modifier.wrapContentSize(), onClick = {
                onNavigateToTest2.invoke()
            }) {
                Text("Navigation 테스트 test_screen2로 이동")
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

    /**
     * TestScreen1
     *
     * @param data
     */
    @Composable
    fun TestScreen1(data: String?) {
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
            Text(text = data ?: "")
        }
    }

    /**
     * Info
     *
     * @property id
     * @property name
     * @property email
     */
    data class Info(
        val id: String, val name: String, var email: String? = null
    )

    /**
     * TestScreen2
     *
     * @param onNavigateToTest2
     */
    @Composable
    fun TestScreen2(
        onNavigateToTest2: () -> Unit,
    ) {
        var name by remember { mutableStateOf("test2") }
        val index by remember { mutableStateOf("99999") }
        //많은 양에 데이터가 Remember에 들어왔을때 테스트를 위한 변수
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

    /**
     * 테스트 더미 랜덤 생성
     *
     * @param count
     * @return
     */
    private fun createInfo(count: Int): List<Info> {
        val temp: MutableList<Info> = mutableListOf()
        for (index in 0 until count) {
            temp.add(Info(id = index.toString(), name = "name$index", email = "email$index"))
        }
        return temp
    }

    /**
     * Compose Remember 초기화가 됐는지 테스트를 위한 코드
     * 그러나, Android 문서 상에 적혀있는데로 런타임으로 코드해도 결과가 안나옴
     *
     * @param currentComposer
     */
    @Deprecated("동작 안함")
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