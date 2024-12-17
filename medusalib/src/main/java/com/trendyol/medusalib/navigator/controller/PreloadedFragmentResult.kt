package com.trendyol.medusalib.navigator.controller

sealed interface PreloadedFragmentResult {

    data object Success: PreloadedFragmentResult

    data object FallbackSuccess: PreloadedFragmentResult

    data object NotFound: PreloadedFragmentResult
}