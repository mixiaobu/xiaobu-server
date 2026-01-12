package org.xiaobu.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any?.isNullOrEmpty(): Boolean {
    return when (this) {
        null -> true
        is String -> this.isBlank()
        is CharSequence -> this.isBlank()
        is Collection<*> -> this.isEmpty()
        is Map<*, *> -> this.isEmpty()
        is Array<*> -> this.isEmpty()
        is Boolean -> false
        is Number -> false
        else -> false
    }
}

fun Any?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)
