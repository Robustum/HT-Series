package io.github.hiiragi283.api.extension

inline fun <A1, A2, B> Pair<A1, B>.mapFirst(mapper: (A1) -> A2): Pair<A2, B> = mapper(first) to second

inline fun <A, B1, B2> Pair<A, B1>.mapSecond(mapper: (B1) -> B2): Pair<A, B2> = first to mapper(second)
