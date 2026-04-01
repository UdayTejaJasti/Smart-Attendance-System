package com.example.studentattendanceapp

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

object BiometricHelper {

    fun authenticate(activity: AppCompatActivity, onSuccess: () -> Unit) {

        val executor = ContextCompat.getMainExecutor(activity)

        val prompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    onSuccess()
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Student Authentication")
            .setSubtitle("Use fingerprint or device lock")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(info)
    }
}