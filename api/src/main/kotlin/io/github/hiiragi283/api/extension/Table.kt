package io.github.hiiragi283.api.extension

import com.google.common.collect.Table

fun <R, C, V> Table<R, C, V>.forEach(action: (Table.Cell<R, C, V>) -> Unit) {
    cellSet().forEach(action)
}

fun <R, C, V> Table<R, C, V>.forEachNotNull(action: (R, C, V) -> Unit) {
    forEach { (row: R?, column: C?, value: V?) ->
        if (row != null && column != null && value != null) action(row, column, value)
    }
}

operator fun <R, C, V> Table.Cell<R, C, V>.component1(): R? = rowKey

operator fun <R, C, V> Table.Cell<R, C, V>.component2(): C? = columnKey

operator fun <R, C, V> Table.Cell<R, C, V>.component3(): V? = value
