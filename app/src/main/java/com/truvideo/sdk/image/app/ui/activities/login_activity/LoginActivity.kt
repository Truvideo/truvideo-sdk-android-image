package com.truvideo.sdk.image.app.ui.activities.login_activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.truvideo.sdk.components.login.TruvideoLoginComponent
import com.truvideo.sdk.core.TruvideoSdk
import com.truvideo.sdk.image.app.ui.activities.main_activity.MainActivity
import com.truvideo.sdk.image.app.ui.theme.TruvideoSdkImageTheme
import truvideo.sdk.common.model.TruvideoSdkEnvironment
import truvideo.sdk.common.sdk_common

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sdk_common.configuration.environment = TruvideoSdkEnvironment.RC
//        TruvideoSdk.clearAuthentication()

        setContent {
            TruvideoSdkImageTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val apiKey = when (sdk_common.configuration.environment) {
            TruvideoSdkEnvironment.DEV -> ""
            TruvideoSdkEnvironment.BETA -> "VS2SG9WK"
            TruvideoSdkEnvironment.RC -> "VS2SG9WK" // Ours
//            TruvideoSdkEnvironment.RC -> "0EeGlpbESu" // Reynolds
//            TruvideoSdkEnvironment.PROD -> "EPhPPsbv7e" // ours
            TruvideoSdkEnvironment.PROD -> "5esxyUUl0t" // Reynolds
            else -> ""
        }

        val secret = when (sdk_common.configuration.environment) {
            TruvideoSdkEnvironment.DEV -> ""
            TruvideoSdkEnvironment.BETA -> "ST2K33GR"
            TruvideoSdkEnvironment.RC -> "ST2K33GR" // Ours
//            TruvideoSdkEnvironment.RC -> "QDjx0T9RyD" // Reynolds
//            TruvideoSdkEnvironment.PROD -> "9lHCnkfeLl" // Ours
            TruvideoSdkEnvironment.PROD -> "PCRE0bdAce" // Reynolds
            else -> ""
        }

        Box(Modifier.fillMaxSize()) {
            TruvideoLoginComponent(
                apiKey = apiKey,
                secret = secret,
                isAuthenticated = { TruvideoSdk.isAuthenticated },
                isAuthenticationExpired = { TruvideoSdk.isAuthenticationExpired },
                generatePayload = { TruvideoSdk.generatePayload() },
                authenticate = { apiKey, payload, signature -> TruvideoSdk.authenticate(apiKey, payload, signature) },
                init = { TruvideoSdk.initAuthentication() },
                callback = {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}