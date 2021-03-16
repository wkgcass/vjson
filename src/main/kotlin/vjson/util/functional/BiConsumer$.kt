package vjson.util.functional

interface `BiConsumer$`<T, U> : Function2<T, U, Unit> {
  fun accept(t: T, u: U)

  override
  /*#ifndef KOTLIN_NATIVE {{ */@JvmDefault/*}}*/
  fun invoke(t: T, u: U) {
    accept(t, u)
  }
}
