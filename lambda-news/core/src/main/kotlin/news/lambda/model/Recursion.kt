package news.lambda.model

typealias Endo<A> = (A) -> A

data class Fix<A, B>(private val f: (Fix<A, B>) -> (A) -> B) {
    operator fun invoke(a: Fix<A, B>): (A) -> B = f(a)
}

data class Fix1<A, B, C>(private val f: (Fix1<A, B, C>) -> (A, B) -> C) {
    operator fun invoke(a: Fix1<A, B, C>): (A, B) -> C = f(a)
}

data class Fix2<A, B, C, D>(private val f: (Fix2<A, B, C, D>) -> (A, B, C) -> D) {
    operator fun invoke(a: Fix2<A, B, C, D>): (A, B, C) -> D = f(a)
}

data class Fix3<A, B, C, D, E>(private val f: (Fix3<A, B, C, D, E>) -> (A, B, C, D) -> E) {
    operator fun invoke(a: Fix3<A, B, C, D, E>): (A, B, C, D) -> E = f(a)
}

fun <A, B> fix(f: Endo<(A) -> B>): (A) -> B =
    Fix<A, B> { g -> f { a -> g(g)(a) } }.let { g -> g(g) }

fun <A, B, C> fix(f: Endo<(A, B) -> C>): (A, B) -> C =
    Fix1<A, B, C> { g -> f { a, b -> g(g)(a, b) } }.let { g -> g(g) }

fun <A, B, C, D> fix(f: Endo<(A, B, C) -> D>): (A, B, C) -> D =
    Fix2<A, B, C, D> { g -> f { a, b, c -> g(g)(a, b, c) } }.let { g -> g(g) }

fun <A, B, C, D, E> fix(f: Endo<(A, B, C, D) -> E>): (A, B, C, D) -> E =
    Fix3<A, B, C, D, E> { g -> f { a, b, c, d -> g(g)(a, b, c, d) } }.let { g -> g(g) }
