package com.hyundaiht.applink.webview_no

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hyundaiht.applink.ui.theme.WebViewTestTheme

class Test2Activity : ComponentActivity() {
    private val tag = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent =  ${intent.data}")
        enableEdgeToEdge()
        setContent {
            WebViewTestTheme {
                val item = intent.getBundleExtra("bundle")?.getString("item")
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome to the Test1Activity! Received item: $item")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(tag, "onNewIntent intent =  ${intent.data}")
    }

}