package vpreprocessor

data class PreprocessorOptions(
  val nestedComment: Boolean,
) {
  constructor(opts: PreprocessorOptions) : this(
    nestedComment = opts.nestedComment,
  )

  companion object {
    /*#ifndef KOTLIN_NATIVE {{*/@JvmField/*}}*/
    val KT = PreprocessorOptions(
      nestedComment = true,
    )

    /*#ifndef KOTLIN_NATIVE {{*/@JvmField/*}}*/
    val JAVA = PreprocessorOptions(
      nestedComment = false,
    )
  }
}
