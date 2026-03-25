package com.truvideo.sdk.image.utils

import android.graphics.ColorMatrix

object ColorMatrixUtils {

    fun saturateMatrix(sat: Float): ColorMatrix {
        val invSat = 1 - sat
        val R = 0.213f * invSat
        val G = 0.715f * invSat
        val B = 0.072f * invSat

        return ColorMatrix(
            floatArrayOf(
                R + sat, G, B, 0f, 0f,  // Red
                R, G + sat, B, 0f, 0f,  // Green
                R, G, B + sat, 0f, 0f,  // Blue
                0f, 0f, 0f, 1f, 0f   // Alpha
            )
        )
    }

    fun contrastMatrix(contrast: Float): ColorMatrix {
        val translate = (-0.5f * contrast + 0.5f) * 255
        return ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    fun warmMatrix(intensity: Float = 0.1f): ColorMatrix {
        return ColorMatrix(
            floatArrayOf(
                1.0f + intensity, 0.0f, 0.0f, 0f, 0f,  // Red increased
                0.0f, 1.0f, 0.0f, 0f, 0f,  // Green unchanged
                0.0f, 0.0f, 1.0f - intensity, 0f, 0f,  // Blue decreased
                0.0f, 0.0f, 0.0f, 1f, 0f   // Alpha
            )
        )
    }

    fun concat(matrix1: ColorMatrix, matrix2: ColorMatrix): ColorMatrix {
        val resultArray = FloatArray(20)
        val a = matrix1.array
        val b = matrix2.array

        var index = 0
        for (j in 0 until 20 step 5) {
            for (i in 0..3) {
                resultArray[index++] = a[j + 0] * b[i + 0] + a[j + 1] * b[i + 5] +
                        a[j + 2] * b[i + 10] + a[j + 3] * b[i + 15]
            }
            resultArray[index++] = a[j + 0] * b[4] + a[j + 1] * b[9] +
                    a[j + 2] * b[14] + a[j + 3] * b[19] +
                    a[j + 4]
        }

        return ColorMatrix(resultArray)
    }
}