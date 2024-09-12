package com.truvideo.sdk.image

import android.content.Context
import androidx.startup.Initializer
import com.truvideo.sdk.image.adapters.TruvideoSdkImageAuthAdapterImpl
import com.truvideo.sdk.image.usecases.EditImageUseCase
import com.truvideo.sdk.image.usecases.GetBitmapUseCase
import com.truvideo.sdk.image.usecases.GetInformationUseCase
import com.truvideo.sdk.image.usecases.SaveBitmapUseCase

@Suppress("unused")
class TruvideoSdkImageInitializer : Initializer<Unit> {
    override fun create(context: Context) {

        val authAdapter = TruvideoSdkImageAuthAdapterImpl()

        val getImageInformationUseCase = GetInformationUseCase()

        val getBitmapUseCase = GetBitmapUseCase()

        val saveBitmapUseCase = SaveBitmapUseCase()

        val editImageUseCase = EditImageUseCase(
            getBitmapUseCase = getBitmapUseCase,
            saveBitmapUseCase = saveBitmapUseCase
        )

        TruvideoSdkImage = TruvideoSdkImageImpl(
            authAdapter = authAdapter,
            getImageInformationUseCase = getImageInformationUseCase,
            editImageUseCase = editImageUseCase
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}