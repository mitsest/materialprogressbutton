package com.mitsest.functional

inline fun <reified A, reified B, reified C> ((A) -> B).curry(crossinline g: (B) -> C): (A) -> C {
    return { it -> g.invoke(this.invoke(it)) }
}

inline fun <reified A, reified B, reified C> ((A) -> B).curry(crossinline g: suspend (B) -> C): suspend (A) -> C {
    return { it -> g.invoke(this.invoke(it)) }
}

inline fun <reified A, reified B, reified C> (suspend (A) -> B).curry(crossinline g: suspend (B) -> C): suspend (A) -> C {
    return { it -> g.invoke(this.invoke(it)) }
}



fun <T> f(g: () -> T) = suspend { g() }

inline fun <reified A, reified B> A.pipe(crossinline g: (A) -> B): B {
    return g.invoke(this)
}

suspend inline fun <reified A, reified B> A.pipe(crossinline g: suspend (A) -> B): B {
    return g.invoke(this)
}


inline fun <reified A, reified B, reified C> ((A) -> B).compose(crossinline g: (A) -> C): (A) -> Pair<B, C> {
    return { a ->
        Pair(this.invoke(a), g.invoke(a))
    }
}

inline fun <reified A, reified B, reified C> ((A) -> B).curryCompose(crossinline g: (B) -> C): (A) -> Pair<B, C> {
    return { a ->
        val b = this.invoke(a)
        Pair(b, g.invoke(b))
    }
}


inline fun <reified A, reified B, reified C, reified D> ((A) -> Pair<B, C>).composeTriple(
    crossinline g: (A) -> D
): (A) -> Triple<B, C, D> {
    return { a ->
        val pair = this.invoke(a)
        Triple(pair.first, pair.second, g.invoke(a))
    }
}

inline fun <reified A, reified B> ((A) -> B).composeList(crossinline g: (A) -> B): (A) -> MutableList<B> {
    return { a ->
        mutableListOf(this.invoke(a), g.invoke(a))
    }
}

inline fun <reified A, reified B> ((A) -> MutableList<B>).appendToList(crossinline g: (A) -> B): (A) -> MutableList<B> {
    return { a ->
        this.invoke(a).apply { add(g.invoke(a)) }
    }
}

fun <A, B> ((A) -> B).runIf(condition: Boolean): (A) -> B? = if (condition) {
    this
} else {
    { null }
}

inline fun <reified A, reified B, reified C> ((A) -> B).ifCurry(
    vararg conditions: Pair<((B) -> Boolean), (B) -> C>,
    crossinline `else`: (B) -> C
): (A) -> C {
    return { a ->
        val b = this.invoke(a)
        var c: C? = null
        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(b)) {
                c = func.invoke(b)
                break
            }
        }
        c ?: `else`.invoke(b)
    }
}

inline fun <reified A, reified B, reified C> (suspend (A) -> B).`if`(
    vararg conditions: Pair<((B) -> Boolean), suspend (B) -> C>
): suspend (A) -> C? {
    return { a ->
        val b = this.invoke(a)
        var c: C? = null
        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(b)) {
                c = func.invoke(b)
                break
            }
        }
        c
    }
}

inline fun <reified A, reified B, reified C> ((A) -> B).`if`(
    vararg conditions: Pair<((B) -> Boolean), suspend (B) -> C>,
    crossinline `else`: suspend (B) -> C
): suspend (A) -> C {
    return { a ->
        val b = this.invoke(a)
        var c: C? = null
        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(b)) {
                c = func.invoke(b)
                break
            }
        }
        c ?: `else`.invoke(b)
    }
}


inline fun <reified A, reified B> `if`(
    vararg conditions: Pair<((A) -> Boolean), (A) -> B>,
    crossinline `else`: (A) -> B
): (A) -> B {
    return { a ->
        var b: B? = null
        for (pair in conditions) {
            val (condition, func) = pair
            if (condition.invoke(a)) {
                b = func.invoke(a)
                break
            }
        }
        b ?: `else`.invoke(a)
    }
}

inline fun <reified A> Boolean.toCallback(): (A) -> Boolean {
    return { this }
}
