package com.trendyol.medusalib.navigator

import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction

interface OnNavigatorTransactionListener {

    fun getNavigatorTransaction(): NavigatorTransaction
}