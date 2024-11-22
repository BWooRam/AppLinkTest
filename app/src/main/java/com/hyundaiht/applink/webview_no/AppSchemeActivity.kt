package com.hyundaiht.applink.webview_no

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.TaskStackBuilder

/**
 * AppLink
 *  - 다중으로 App Link를 열일이 있을까? 그럼 Activity Intent 관련 정책이 필요할듯
 */
class AppSchemeActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent url = ${intent.getStringExtra("url")}")
        val redirectEvent = getRedirectEvent(intent) ?: return

        startRedirectActivity(
            clazz = redirectEvent.clazz,
            bundle = redirectEvent.bundle
        )
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }

    data class RedirectEvent(
        val clazz: Class<out ComponentActivity>,
        val bundle: Bundle
    )

    private fun getRedirectEvent(intent: Intent): RedirectEvent? {
        val url = intent.getStringExtra("url") ?: return null
        val targetUri = Uri.parse(url)
        Log.d(tag, "getRedirectEvent url = $url")
        return when {
            url.startsWith("myapp://open.login.redirect") -> {
                val data = targetUri?.getQueryParameter("data")
                Log.d(tag, "getRedirectEvent url = myapp://open.login.redirect, data = $data")
                val bundle = Bundle().apply {
                    putString("data", data)
                }
                RedirectEvent(
                    clazz = Test1Activity::class.java,
                    bundle = bundle
                )
            }

            url.startsWith("myapp://open.event.redirect") -> {
                val item = targetUri?.getQueryParameter("item")
                Log.d(tag, "getRedirectEvent url = myapp://open.event.redirect, item = $item")
                val bundle = Bundle().apply {
                    putString("item", item)
                }
                RedirectEvent(
                    clazz = Test2Activity::class.java,
                    bundle = bundle
                )
            }

            else -> null
        }
    }

    private fun startRedirectActivity(
        bundle: Bundle,
        clazz: Class<out ComponentActivity>
    ) {
        val appLinkIntent = Intent(this@AppSchemeActivity, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("bundle", bundle)
        }

        if (isTaskRoot) {
            TaskStackBuilder.create(this).apply {
                val mainIntent = Intent(this@AppSchemeActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                addNextIntentWithParentStack(mainIntent)
                addNextIntent(appLinkIntent)
            }.startActivities()
        } else {
            startActivity(appLinkIntent)
        }
        finish()
    }

    private fun needAddMainForParent(intent: Intent): Boolean =
        when (intent.component?.className) {
            MainActivity::class.java.name -> false
            else -> true
        }
}