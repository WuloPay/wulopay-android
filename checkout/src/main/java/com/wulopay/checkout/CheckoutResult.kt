package com.wulopay.checkout

import java.net.URI
import java.net.URLDecoder

/**
 * What the payment page reported when it sent the shopper back to the app. Display only: ask **your backend** for
 * the session's real status (webhook or retrieve) before treating an order as paid.
 */
data class CheckoutResult(val status: Status, val sessionId: String?) {
    enum class Status { PAID, CANCELLED, EXPIRED, UNKNOWN }

    companion object {
        private val SESSION_ID = Regex("^cs_[0-9A-Za-z]{10,40}$")

        /** Reads the app link WuloPay opened: your returnUrl or cancelUrl plus `?session_id=…&status=…`. */
        @JvmStatic
        fun parse(link: String?): CheckoutResult {
            val query = try { link?.let { URI(it).rawQuery } } catch (_: Exception) { null } ?: return CheckoutResult(Status.UNKNOWN, null)
            val params = query.split('&').mapNotNull { pair ->
                val i = pair.indexOf('=')
                if (i <= 0) null else decode(pair.substring(0, i)) to decode(pair.substring(i + 1))
            }.toMap()
            val status = when (params["status"]) {
                "paid" -> Status.PAID
                "cancelled" -> Status.CANCELLED
                "expired" -> Status.EXPIRED
                else -> Status.UNKNOWN
            }
            return CheckoutResult(status, params["session_id"]?.takeIf { SESSION_ID.matches(it) })
        }

        private fun decode(s: String) = URLDecoder.decode(s, "UTF-8")
    }
}

/** WuloPay's payment page hosts. A session URL from anywhere else is refused unless you allow its host. */
val WULOPAY_PAGE_HOSTS: Set<String> = setOf("pay.wulopay.com", "pay-sandbox.wulopay.com")

internal fun isWuloPayPaymentPage(url: String, allowedHosts: Set<String>): Boolean {
    val uri = try { URI(url) } catch (_: Exception) { return false }
    return uri.scheme.equals("https", ignoreCase = true) &&
        uri.host != null && (WULOPAY_PAGE_HOSTS + allowedHosts).contains(uri.host.lowercase())
}
