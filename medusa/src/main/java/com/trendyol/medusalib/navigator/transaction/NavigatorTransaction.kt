package com.trendyol.medusalib.navigator.transaction

data class NavigatorTransaction(val transactionType: TransactionType) {

    companion object {
        @JvmField
        val ATTACH_DETACH: NavigatorTransaction = NavigatorTransaction(TransactionType.ATTACH_DETACH)

        @JvmField
        val SHOW_HIDE: NavigatorTransaction = NavigatorTransaction(TransactionType.SHOW_HIDE)
    }
}