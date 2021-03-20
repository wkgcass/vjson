package vjson.util

import kotlin.reflect.KClass

/**
 * this object is only used to bypass the limitations of jacoco<br>
 * some kotlin generated code (e.g. null check) will never be reached
 */
// #ifdef COVERAGE {{@lombok.Generated}}
@Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
object CastUtils {
  inline fun <R> cast(t: Any?): R {
    return t as R
  }

  inline fun <T : Any> check(t: Any?, type: KClass<T>): Boolean {
    return type.isInstance(t)
  }

  inline fun <T : Any> typeIsExpectedAnd(t: Any?, type: KClass<T>, check: (T) -> Boolean): Boolean {
    return t != null && type.isInstance(t) && check(t as T)
  }

  inline fun <T : Any> typeIsNotExpectedOr(t: Any?, type: KClass<T>, check: (T) -> Boolean): Boolean {
    return t == null || !type.isInstance(t) || check(t as T)
  }
}
