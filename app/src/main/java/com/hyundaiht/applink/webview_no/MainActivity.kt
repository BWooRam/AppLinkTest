package com.hyundaiht.applink.webview_no

import android.app.ComponentCaller
import android.content.Context
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hyundaiht.applink.ui.theme.WebViewTestTheme

class MainActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    private val redirectUrl = arrayListOf(
        "myapp://open.event.redirect?item=id",
        "myapp://open.login.redirect?data=example"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent =  ${intent.data}")
        enableEdgeToEdge()
        setContent {
            WebViewTestTheme {
                WebViewExample()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart")
        stateUrl.value = "https://www.google.com"
        stateShowError.value = false
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop")
        stateUrl.value = "https://www.google.com"
        stateShowError.value = false
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Log.d(tag, "onNewIntent intent =  ${intent.data}")
    }

    private var stateUrl = mutableStateOf("https://www.google.com")
    private var stateShowError = mutableStateOf(false)

    @Composable
    fun WebViewExample() {
        var rememberUrl by remember { stateUrl }
        var showError by remember { stateShowError }
        Log.d(tag, "WebViewExample url = $rememberUrl")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    rememberUrl = redirectUrl[0]
                }) {
                Text("Go to Test1Activity")
            }
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = {
                    rememberUrl = redirectUrl[1]
                }) {
                Text("Go to Test2Activity")
            }

            if (!showError) {
                // WebView를 Compose UI에서 보여주기
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize(1f),
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    showError = true
                                    // 로딩 실패 시 URL을 처리하는 로직
                                    val url = request?.url.toString()
                                    Log.d(
                                        this@MainActivity.tag,
                                        "onReceivedError URL: $url, Error: ${error?.description}"
                                    )
                                    if (url.startsWith("myapp://")) {
                                        startRedirect(
                                            context = context,
                                            url = url
                                        )
                                    }

                                    // unknown URL scheme 처리 (예: URL을 못 처리하는 경우)
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        // 예시로 커스텀 메시지 출력
                                        Log.d(this@MainActivity.tag, "Unknown URL scheme: $url")
                                    }
                                }

                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    super.onReceivedHttpError(view, request, errorResponse)
                                    // HTTP 오류 시 URL을 처리하는 로직
                                    val url = request?.url.toString()
                                    Log.d(
                                        this@MainActivity.tag,
                                        "onReceivedHttpError URL: $url, Error: ${errorResponse?.statusCode}"
                                    )
                                }
                            }
                            loadUrl(rememberUrl) // 초기 URL 로드
                        }
                    },
                    update = { webView ->
                        Log.d(tag, "update webView = $webView")
                        webView.loadUrl(rememberUrl) // URL 변경 시마다 새로 로드
                    }
                )
            }
        }
    }

    private fun startRedirect(context: Context, url: String) {
        val intent = Intent(context, AppSchemeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("url", url)
        }
        startActivity(intent)
    }
}