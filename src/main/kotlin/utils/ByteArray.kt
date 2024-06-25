package org.example.utils

import java.util.HexFormat

fun ByteArray.toHexString(): String {
    return HexFormat.of().formatHex(this)
}