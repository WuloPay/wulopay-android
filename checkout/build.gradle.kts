plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

group = "com.wulopay"
version = "1.0.0"

android {
    namespace = "com.wulopay.checkout"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    publishing { singleVariant("release") { withSourcesJar() } }
}

kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }

dependencies {
    implementation("androidx.browser:browser:1.9.0")
    testImplementation("junit:junit:4.13.2")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.wulopay"
            artifactId = "checkout"
            version = project.version.toString()
            afterEvaluate { from(components["release"]) }
            pom {
                name.set("WuloPay Checkout for Android")
                description.set("Open WuloPay hosted checkout in Custom Tabs and read the result from your app link.")
                url.set("https://github.com/WuloPay/wulopay-android")
                licenses { license { name.set("MIT"); url.set("https://opensource.org/license/mit") } }
            }
        }
    }
}
