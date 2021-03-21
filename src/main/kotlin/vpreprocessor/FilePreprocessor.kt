package vpreprocessor

import vjson.CharStream.Companion.from
import vjson.ex.ParserException
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.util.*

object FilePreprocessor {
  private val extensionMap: MutableMap<String, PreprocessorOptions> = HashMap()

  init {
    extensionMap["kt"] = PreprocessorOptions.KT
    extensionMap["java"] = PreprocessorOptions.JAVA
  }

  @JvmStatic
  @Throws(IOException::class, ParserException::class)
  fun process(rootDir: String, params: ProcessParams) {
    val rootDirFile = File(rootDir)
    if (!rootDirFile.isDirectory) {
      throw IOException("\$rootDir is not a directory")
    }
    process("", rootDirFile, params)
  }

  @Throws(IOException::class, ParserException::class)
  private fun process(relativePath: String, dir: File, params: ProcessParams) {
    val ls = dir.listFiles() ?: return
    for (f in ls) {
      val nextPath = relativePath + "/" + f.name
      if (f.isDirectory) {
        process(nextPath, f, params)
      } else if (f.isFile) {
        processFile(nextPath, f, params)
      }
    }
  }

  @Throws(IOException::class, ParserException::class)
  private fun processFile(relativePath: String, file: File, params: ProcessParams) {
    var opts: PreprocessorOptions? = null
    for ((ext, o) in extensionMap) {
      if (file.name.endsWith(".$ext")) {
        opts = o
        break
      }
    }
    if (opts == null) {
      return
    }
    if (!params.pathFilter(relativePath)) {
      return
    }
    params.currentFile(relativePath)
    val content = read(file)
    val ctx = PreprocessorContext(opts)
    params.contextInitializer(ctx)
    val preprocessor = Preprocessor(ctx)
    val builder = StringBuilder()
    preprocessor.process(from(content), builder)
    val result = builder.toString()
    FileOutputStream(file).use { output ->
      output.write(result.toByteArray())
      output.flush()
    }
  }

  @Throws(IOException::class)
  private fun read(file: File): String {
    FileReader(file).use { reader ->
      val cbuf = CharArray(1024)
      val sb = StringBuilder()
      while (true) {
        val sz = reader.read(cbuf)
        if (sz < 0) {
          break
        }
        sb.append(cbuf, 0, sz)
      }
      return sb.toString()
    }
  }

  class ProcessParams @JvmOverloads constructor(
    val contextInitializer: (PreprocessorContext) -> Unit,
    val pathFilter: (String) -> Boolean,
    val currentFile: (String) -> Unit = {}
  )
}
