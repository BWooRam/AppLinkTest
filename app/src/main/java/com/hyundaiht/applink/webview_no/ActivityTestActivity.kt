package com.hyundaiht.applink.webview_no

import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.TaskStackBuilder
import com.hyundaiht.applink.navigation.NavigationActivity
import com.hyundaiht.applink.notification.AppNotificationManager
import com.hyundaiht.applink.ui.theme.WebViewTestTheme
import kotlin.random.Random

/**
 * AppLink
 *  - 다중으로 App Link를 열일이 있을까? 그럼 Activity Intent 관련 정책이 필요할듯
 */
class ActivityTestActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate hashCode = ${hashCode()}, intent data = ${intent.data}")

        enableEdgeToEdge()
        setContent {
            WebViewTestTheme {
                val context = this@ActivityTestActivity
                Column {
                    Spacer(Modifier.fillMaxWidth().height(100.dp))
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            val intent = Intent(context, ActivityTestActivity::class.java).apply {
                                val randomValue = Random.nextInt(0, 99999)
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                data = Uri.parse("myapp://open.login.redirect/$randomValue")
                            }
                            AppNotificationManager.sendDeepLinkNotification(context, intent)
                        }) {
                        Text("sendDeepLinkNotification ActivityTestActivity")
                    }

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            val intent = Intent(context, MainActivity::class.java).apply {
                                val randomValue = Random.nextInt(0, 99999)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                data = Uri.parse("myapp://open.login.redirect/$randomValue")
                            }
                            AppNotificationManager.sendDeepLinkNotification(context, intent)
                        }) {
                        Text("sendDeepLinkNotification MainActivity")
                    }

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            val intent = Intent(context, Test1Activity::class.java).apply {
                                val randomValue = Random.nextInt(0, 99999)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                data = Uri.parse("myapp://open.login.redirect/$randomValue")
                            }
                            AppNotificationManager.sendDeepLinkNotification(context, intent)
                        }) {
                        Text("sendDeepLinkNotification Test1Activity")
                    }

                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            val intent = Intent(context, Test2Activity::class.java).apply {
                                val randomValue = Random.nextInt(0, 99999)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                data = Uri.parse("myapp://open.login.redirect/$randomValue")
                            }
                            AppNotificationManager.sendDeepLinkNotification(context, intent)
                        }) {
                        Text("sendDeepLinkNotification Test2Activity")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(tag, "onNewIntent hashCode = ${hashCode()}, intent data = ${intent.data}")
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        Log.d(tag, "onNewIntent caller hashCode = ${hashCode()}, intent data = ${intent.data}")
    }
}