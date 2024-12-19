package com.trendyol.medusalib.navigator.controller

/**
 * Represents the result of attempting to show a preloaded fragment.
 */
sealed interface PreloadedFragmentResult {

    /**
     * Indicates that the preloaded fragment was successfully shown.
     */
    data object Success : PreloadedFragmentResult

    /**
     * Indicates that a fallback fragment was used and successfully shown.
     */
    data object FallbackSuccess : PreloadedFragmentResult

    /**
     * Indicates that neither the preloaded fragment nor a fallback fragment was found.
     */
    data object NotFound : PreloadedFragmentResult
}