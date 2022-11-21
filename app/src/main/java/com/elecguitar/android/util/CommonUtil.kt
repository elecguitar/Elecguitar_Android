package com.elecguitar.android.util

import java.text.DecimalFormat

class CommonUtil {
    //천단위 콤마
    fun makeComma(num: Int): String {
        var comma = DecimalFormat("#,###")
        return "${comma.format(num)}"
    }
}