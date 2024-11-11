package com.hyundaiht.applink.webview_no

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

class AppSchemeActivity : ComponentActivity() {
    private val tag = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate intent = $intent")
        val redirectEvent = getRedirectEvent(intent) ?: return

        startRedirectActivity(
            context = this@AppSchemeActivity,
            clazz = redirectEvent.clazz,
            bundle = redirectEvent.bundle
        )
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
        context: Context,
        bundle: Bundle,
        clazz: Class<out ComponentActivity>
    ) {
        val intent = Intent(context, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("bundle", bundle)
        }
        startActivity(intent)
        finish()
    }

}