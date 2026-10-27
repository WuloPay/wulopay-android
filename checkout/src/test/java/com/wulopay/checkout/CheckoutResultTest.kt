package com.wulopay.checkout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutResultTest {
    @Test fun parsesThePaidReturn() {
        assertEquals(
            CheckoutResult(CheckoutResult.Status.PAID, "cs_7CRIaOqGBtDY1J7ARiDLS7"),
            CheckoutResult.parse("myshop://checkout?session_id=cs_7CRIaOqGBtDY1J7ARiDLS7&status=paid"),
        )
    }

    @Test fun parsesHttpsAppLinksToo() {
        assertEquals(CheckoutResult.Status.CANCELLED, CheckoutResult.parse("https://shop.example.co.za/app/checkout?status=cancelled").status)
    }

    @Test fun treatsUnexpectedValuesAsUnknown() {
        assertEquals(CheckoutResult(CheckoutResult.Status.UNKNOWN, null), CheckoutResult.parse("myshop://checkout?session_id=%3Cscript%3E&status=hacked"))
        assertEquals(CheckoutResult(CheckoutResult.Status.UNKNOWN, null), CheckoutResult.parse(null))
        assertEquals(CheckoutResult(CheckoutResult.Status.UNKNOWN, null), CheckoutResult.parse("not a url at all"))
    }

    @Test fun acceptsOnlyWuloPayPaymentPages() {
        assertTrue(isWuloPayPaymentPage("https://pay.wulopay.com/c/cs_1", emptySet()))
        assertTrue(isWuloPayPaymentPage("https://pay-sandbox.wulopay.com/c/cs_1", emptySet()))
        assertFalse(isWuloPayPaymentPage("http://pay.wulopay.com/c/cs_1", emptySet()))
        assertFalse(isWuloPayPaymentPage("https://pay.wulopay.com.evil.example/c/cs_1", emptySet()))
        assertTrue(isWuloPayPaymentPage("https://pay.staging.example/c/cs_1", setOf("pay.staging.example")))
    }
}
