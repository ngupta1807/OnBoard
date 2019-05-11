package com.wfi.beaconscanner.utils.extensionFunctions

/**
 * Created by nisha.
 */

fun IntArray.print() : String {
    val output = StringBuilder("[")

    for (i in 0..this.size - 1) {
        output.append(this[i].toString())
        if (i < this.size - 1) {
            output.append(",")
        }
    }

    output.append("]")
    return output.toString()
}