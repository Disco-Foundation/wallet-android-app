package disco.foundation.discowallet.utils

fun String.toListOfInt(): List<Int> {
    var newString = this.replace("]", "")
        .replace("[", "")
        .filter { !it.isWhitespace() }
    return newString.split(",").map { it.toInt() }
}

fun List<Int>.toByteArray() : ByteArray{
    return this.map { it.toByte() }.toByteArray()
}