package vjson.util.functional

interface `Consumer$`<T> : Function1<T, Unit> {
  fun accept(t: T)

  override
  /*#ifndef KOTLIN_NATIVE {{ */@JvmDefault/*}}*/
  fun invoke(t: T) {
    accept(t)
  }
}
