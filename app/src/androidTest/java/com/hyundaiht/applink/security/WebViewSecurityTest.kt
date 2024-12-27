package com.hyundaiht.applink.security

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WebViewSecurityTest {

    private val dummyBlackListUrl = listOf(
        "myapp://openList",
        "myapp://openList/",
        "myapp://openList?code='123",
        "myapp://blackList",
        "myapp://blackList/",
        "myapp://blackList?code='123",
        "https://open.let/",
        "http://opens/",
        "file://open/",
        "file://opens/",
        "FILE://open/",
        "FILE://opens/",
        "File://open/",
        "File://openList/",
        "File://openList/test1/test2/test3",
        "javascript:(function(){})"
    )

    @Test
    fun `checkWhiteList_Black_List_필터링_테스트`() {
        for (blackUrl in dummyBlackListUrl) {
            val isWhite = WebViewSecurity.checkWhiteList(blackUrl)
            println("checkWhiteList_기본_동작_테스트 blackUrl = $blackUrl, isWhite = $isWhite")
            Assert.assertFalse(isWhite)
        }
    }

    private val dummyWhiteListUrl = listOf(
        "myapp://open/data='1234'",
        "myapp://open/code='123'",
        "myapp://open/callback?data='123'",
        "myapp://open/",
        "https://open/data='1234'",
        "https://open/code='123'",
        "https://open/callback?data='123'",
        "https://open/",
        "http://open/data='1234'",
        "http://open/code='123'",
        "http://open/callback?data='123'",
        "http://open/"
    )

    @Test
    fun `checkWhiteList_White_List_통과_테스트`() {
        for (whiteUrl in dummyWhiteListUrl) {
            val isWhite = WebViewSecurity.checkWhiteList(whiteUrl)
            println("checkWhiteList_기본_동작_테스트 whiteUrl = $whiteUrl, isWhite = $isWhite")
            Assert.assertTrue(isWhite)
        }
    }

    @Test
    fun `checkWhiteList_Uri_Path_테스트`() {
        val uri = Uri.parse("File://openList/test1/test2/test3")
        val uriPath = uri.pathSegments
        println("checkWhiteList_Uri_Path_테스트 uri = $uri, uriPath = $uriPath")
    }
}