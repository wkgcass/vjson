package vpreprocessor

data class PreprocessorOptions(
  val nestedComment: Boolean,
) {
  constructor(opts: PreprocessorOptions) : this(
    nestedComment = opts.nestedComment,
  )

  companion object {
    @JvmField
    val KT = PreprocessorOptions(
      nestedComment = true,
    )

    @JvmField
    val JAVA = PreprocessorOptions(
      nestedComment = false,
    )
  }
}
