package com.hyundaiht.applink.security

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import org.jetbrains.annotations.TestOnly

object WebViewSecurity {
    private val tag = javaClass.simpleName

    /**
     * checkWhiteList
     *
     * @param request
     * @return
     */
    fun checkWhiteList(request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: throw NullPointerException("WebResourceRequest url is null")
        return checkWhiteList(uri)
    }

    /**
     * checkWhiteList
     *
     * @param requestUrl
     * @return
     */
    fun checkWhiteList(requestUrl: String?): Boolean {
        //요청한 URL이 Null 일 때
        if (requestUrl == null) {
            return false
        }

        val uri = Uri.parse(requestUrl)
        return checkWhiteList(uri)
    }

    /**
     * checkWhiteList
     *
     * @param uri
     * @return
     */
    fun checkWhiteList(uri: Uri): Boolean {
        val scheme = uri.scheme
        val host = uri.host

        var isSchema = false
        var isHost = false

        //Scheme 검증
        for (whiteUrl in getWhiteSchemeList()) {
            if (scheme == whiteUrl) {
                isSchema = true
            }
        }

        //Host 검증
        for (whiteUrl in getWhiteHostList()) {
            if (host == whiteUrl) {
                isHost = true
            }
        }

        return isSchema && isHost
    }

    /**
     * getWhiteList
     * 서브 도메인 악용을 막기 위해 슬래쉬(/)로 끝나도록 명확한 검증 도메인명 작성
     * @return
     */
    private fun getWhiteSchemeList(): List<String> = listOf(
        "myapp",
        "http",
        "https",
        "file"  //sq.html을 읽기 위한 테스트용
    )

    /**
     * getWhiteList
     * 서브 도메인 악용을 막기 위해 슬래쉬(/)로 끝나도록 명확한 검증 도메인명 작성
     * @return
     */
    private fun getWhiteHostList(): List<String> = listOf(
        "open.login.redirect",
        "open",
        "www.google.com",
        "" //sq.html을 읽기 위한 테스트용
    )

    /**
     * getBlackList
     *
     * @return
     */
    fun getBlackList(): List<String> = listOf(
        "myapp://open.login.redirect1/event",
        "myapp://open.login.redirect.test/event",
        "http://open.login.redirect/event",
        "https://open.login.redirect/event"
    )

    /**
     * checkWhiteList 걸치고 loadUrl 실행
     *
     * @param url
     */
    fun WebView.safeLoadUrl(url: String) {
        val isWhite = checkWhiteList(url)
        Log.d("safeLoadUrl", "isWhite = $isWhite, url = $url")

        if (isWhite) {
            changeSafeSetting(false)
            loadUrl(url) // URL 변경 시마다 새로 로드
        } else {
            changeSafeSetting(true)
        }

//        changeSafeSetting(false)  //보안 테스트 용
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun WebView.changeSafeSetting(isSetting: Boolean) {
        Log.d("changeSafeSetting", "isSetting = $isSetting, url = $url")
        if (isSetting) {
            settings.allowFileAccess = false
            settings.domStorageEnabled = false
            settings.javaScriptEnabled = false
        } else {
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
        }
    }
}