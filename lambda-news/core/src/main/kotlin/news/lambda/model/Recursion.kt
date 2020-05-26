package news.lambda.model

class FunR<T, R>(private val f: (FunR<T, R>) -> (T) -> R) {
    operator fun invoke(a: FunR<T, R>): (T) -> R = f(a)
}

class FunR1<T1, T2, R>(private val f: (FunR1<T1, T2, R>) -> (T1, T2) -> R) {
    operator fun invoke(a: FunR1<T1, T2, R>): (T1, T2) -> R = f(a)
}

class FunR2<T1, T2, T3, R>(private val f: (FunR2<T1, T2, T3, R>) -> (T1, T2, T3) -> R) {
    operator fun invoke(a: FunR2<T1, T2, T3, R>): (T1, T2, T3) -> R = f(a)
}

class FunR3<T1, T2, T3, T4, R>(private val f: (FunR3<T1, T2, T3, T4, R>) -> (T1, T2, T3, T4) -> R) {
    operator fun invoke(a: FunR3<T1, T2, T3, T4, R>): (T1, T2, T3, T4) -> R = f(a)
}

fun <T, R> fix(f: ((T) -> R) -> (T) -> R): (T) -> R =
    FunR<T, R> { g ->
        f { t1 ->
            g(g)(t1)
        }
    }.let { g -> g(g) }

fun <T1, T2, R> fix(f: ((T1, T2) -> R) -> (T1, T2) -> R): (T1, T2) -> R =
    FunR1<T1, T2, R> { g ->
        f { t1, t2 ->
            g(g)(t1, t2)
        }
    }.let { g -> g(g) }

fun <T1, T2, T3, R> fix(f: ((T1, T2, T3) -> R) -> (T1, T2, T3) -> R): (T1, T2, T3) -> R =
    FunR2<T1, T2, T3, R> { g ->
        f { t1, t2, t3 ->
            g(g)(t1, t2, t3)
        }
    }.let { g -> g(g) }

fun <T1, T2, T3, T4, R> fix(f: ((T1, T2, T3, T4) -> R) -> (T1, T2, T3, T4) -> R): (T1, T2, T3, T4) -> R =
    FunR3<T1, T2, T3, T4, R> { g ->
        f { t1, t2, t3, t4 ->
            g(g)(t1, t2, t3, t4)
        }
    }.let { g -> g(g) }
