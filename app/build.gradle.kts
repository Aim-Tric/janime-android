plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

android {
    namespace = "cn.codebro.j_anime_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.codebro.j_anime_android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://127.0.0.1/api/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"http://127.0.0.1/api/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding {
            enable = true
        }
    }
}

dependencies {
    // ksp
    compileOnly("com.google.devtools.ksp:symbol-processing-api:1.9.0-1.0.13")
    // android base
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // android lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    // android navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    // network with gson and retrofit2 and okhttp
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // room
    val roomVersion = "2.5.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // datastore with preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    // glide image loading framework
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.14.2")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.11.0") {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
    // android media
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer-exo2:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-arm64:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-armv7a:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-armv5:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-x86:v8.5.0-release-jitpack")
    implementation("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-x64:v8.5.0-release-jitpack")
    // paging and recycler view
    val pagingVersion = "3.1.1"
    implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
//    implementation("androidx.room:room-paging:$pagingVersion")
    // work manager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // hutool加密工具
    implementation("cn.hutool:hutool-core:5.8.29")
    implementation("cn.hutool:hutool-crypto:5.8.29")
    // 国密加密库
    implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
