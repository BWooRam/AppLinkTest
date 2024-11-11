package com.hyundaiht.applink.webview

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hyundaiht.applink.ui.theme.WebViewTestTheme

class OnlyWebViewActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent =  ${intent.data}")
        overridePendingTransition(0, 0)

        enableEdgeToEdge()
        setContent {
            WebViewTestTheme {
                MyApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Log.d(tag, "onNewIntent intent =  ${intent.data}")
    }

    @Composable
    fun WebViewExample(navController: NavController) {
        var url by remember { mutableStateOf("https://www.google.com") }
        var showError by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 버튼 1: 구글로 변경
            Button(onClick = { url = "myapp://open?data=example" }) {
                Text("Go to Test")
            }
            if (showError) {
            } else {
                // WebView를 Compose UI에서 보여주기
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize(1f),
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                    super.onReceivedError(view, request, error)
                                    showError = true
                                    // 로딩 실패 시 URL을 처리하는 로직
                                    val url = request?.url.toString()
                                    println("Error loading URL: $url, Error: ${error?.description}")
                                    if (url.startsWith("myapp://")) {
                                        val name = request?.url?.getQueryParameter("data") ?: "Unknown"
                                        // URL이 "myapp://"으로 시작하면 NewScreen으로 이동
                                        navController.navigate("new_screen/$name")
                                    }
                                    // unknown URL scheme 처리 (예: URL을 못 처리하는 경우)
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        // 예시로 커스텀 메시지 출력
                                        println("Unknown URL scheme: $url")
                                    }
                                }
                                override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                                    super.onReceivedHttpError(view, request, errorResponse)
                                    // HTTP 오류 시 URL을 처리하는 로직
                                    val url = request?.url.toString()
                                    println("HTTP Error loading URL: $url, Error: ${errorResponse?.statusCode}")
                                }
                            }
                            loadUrl(url) // 초기 URL 로드
                        }
                    },
                    update = { webView ->
                        Log.d("MainActivity", "webView = $webView")
                        webView.loadUrl(url) // URL 변경 시마다 새로 로드
                    }
                )
            }
        }
    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController() // NavController를 생성
        // NavHost에서 화면 전환을 관리
        NavHost(navController = navController, startDestination = "webview_screen") {
            composable("webview_screen") {
                WebViewExample(navController) // 웹뷰 화면
            }
            composable("new_screen/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                NewScreen(name = name ?: "Unknown") // 다른 화면으로 이동
            }
        }
    }
    @Composable
    fun NewScreen(name: String) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to the new screen! Received name: $name")
        }
    }
}