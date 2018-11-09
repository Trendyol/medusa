package com.trendyol.medusalib.navigator.transaction

data class NavigatorTransaction(val transactionType: TransactionType) {

    companion object {

        val ATTACH_DETACH: NavigatorTransaction = NavigatorTransaction(TransactionType.ATTACH_DETACH)

        val SHOW_HIDE: NavigatorTransaction = NavigatorTransaction(TransactionType.SHOW_HIDE)
    }
}