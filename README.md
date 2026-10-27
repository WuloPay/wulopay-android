# WuloPay Checkout for Android

Open WuloPay hosted checkout from an Android app in Custom Tabs and read the result from your app link. Card details
and 3-D Secure happen on WuloPay's secure payment page, in a trusted browser, never in a WebView inside your app.
Kotlin, minSdk 24, one dependency (`androidx.browser`).

## Install

Via [JitPack](https://jitpack.io/#WuloPay/wulopay-android):

```kotlin
// settings.gradle.kts
dependencyResolutionManagement { repositories { google(); mavenCentral(); maven("https://jitpack.io") } }
// app/build.gradle.kts
dependencies { implementation("com.github.WuloPay:wulopay-android:1.0.0") }
```

## Use

1. **Your backend** creates the checkout session with its API key (never the app) and sets `returnUrl` and
   `cancelUrl` to your app link, e.g. `myshop://checkout`. It returns the session `url` to the app.
2. Open it:

```kotlin
WuloPayCheckout.open(activity, sessionUrl)
```

3. Handle the app link in an activity:

```xml
<activity android:name=".CheckoutReturnActivity" android:exported="true" android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="myshop" android:host="checkout" />
    </intent-filter>
</activity>
```

```kotlin
val result = WuloPayCheckout.parseResult(intent.data)   // PAID, CANCELLED, EXPIRED or UNKNOWN + sessionId
// then ask your backend for the order's real status
```

`result.status` is what the payment page reported, for display. Mark orders paid on your server, from the
`checkout.session.completed` webhook or by retrieving the session. If the shopper closes the tab instead, your app
simply resumes without an app link; check the order status with your backend.

For safety, `open` only accepts https URLs on WuloPay's payment hosts (add others with `allowedHosts`).

API and webhooks: [wulopay-openapi](https://github.com/WuloPay/wulopay-openapi). Sandbox keys: api@wulopay.com.
MIT licensed. Security problems: see [SECURITY.md](SECURITY.md).
