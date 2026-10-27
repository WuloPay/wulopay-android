package com.wulopay.checkout

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Opens WuloPay hosted checkout in Custom Tabs, so card details and 3-D Secure happen in a trusted browser and never
 * in a WebView inside your app. The payment page then opens your app link (the session's returnUrl / cancelUrl);
 * read it in that activity with [parseResult].
 */
object WuloPayCheckout {

    /**
     * @param url the session `url` your backend got from WuloPay (never create sessions in the app).
     * @param allowedHosts extra payment page hosts (e.g. a staging host).
     * @throws IllegalArgumentException when [url] isn't an https WuloPay payment page.
     */
    @JvmStatic
    @JvmOverloads
    fun open(context: Context, url: String, allowedHosts: Set<String> = emptySet()) {
        require(isWuloPayPaymentPage(url, allowedHosts)) { "Refusing to open $url: not a WuloPay payment page." }
        val intent = CustomTabsIntent.Builder().setShowTitle(true).build()
        if (context !is android.app.Activity) intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.launchUrl(context, Uri.parse(url))
    }

    /** The result in the activity that handles your app link (`intent.data`). */
    @JvmStatic
    fun parseResult(link: Uri?): CheckoutResult = CheckoutResult.parse(link?.toString())
}
