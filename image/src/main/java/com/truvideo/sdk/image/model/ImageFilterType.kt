package com.truvideo.sdk.image.model

internal enum class ImageFilterType {
    Default,
    Vivid,
    VividWarm,
    VividCold,
    Dramatic,
    DramaticWarm,
    DramaticCold,
    Sepia,
    Warm,
    Cold,
    Grayscale,
    Noir,
    InverseColor,
}

internal val ImageFilterType.displayName: String
    get() {
        return when (this) {
            ImageFilterType.Default -> "Default"
            ImageFilterType.Vivid -> "Vivid"
            ImageFilterType.VividWarm -> "Vivid Warm"
            ImageFilterType.VividCold -> "Vivid Cold"
            ImageFilterType.Dramatic -> "Dramatic"
            ImageFilterType.DramaticWarm -> "Dramatic Warm"
            ImageFilterType.DramaticCold -> "Dramatic Cold"
            ImageFilterType.Sepia -> "Sepia"
            ImageFilterType.Warm -> "Warm"
            ImageFilterType.Cold -> "Cold"
            ImageFilterType.Grayscale -> "Grayscale"
            ImageFilterType.Noir -> "Noir"
            ImageFilterType.InverseColor -> "Inverse Color"
        }
    }
