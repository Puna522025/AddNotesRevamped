package com.addnotes.utils

import android.text.TextUtils
import com.addnotes.adapter.ShoppingListData

object ShoppingCalculator {

    fun doSumOfShoppingItems(shoppingArray: ArrayList<ShoppingListData>): Int {
        var sum = 0
        for (i in shoppingArray.indices) {
            if (!TextUtils.isEmpty(shoppingArray[i].value) && !TextUtils.isEmpty(shoppingArray[i].countOfItemsToBuy)) {
                val totalValue: Int =
                    shoppingArray[i].value.toInt() * shoppingArray[i].countOfItemsToBuy
                        .toInt()
                sum += totalValue
            }
        }
        return sum
    }
}