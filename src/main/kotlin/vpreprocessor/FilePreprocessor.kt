/*
 * The MIT License
 *
 * Copyright 2021 wkgcass (https://github.com/wkgcass)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
// #ifndef KOTLIN_NATIVE {{
package vpreprocessor

import vjson.CharStream.Companion.from
import vjson.ex.ParserException
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.util.*
// }}

// #ifdef COVERAGE {{@lombok.Generated}}
// #ifndef KOTLIN_NATIVE {{
object FilePreprocessor {
  private val extensionMap: MutableMap<String, PreprocessorOptions> = HashMap()

  init {
    extensionMap["kt"] = PreprocessorOptions.KT
    extensionMap["java"] = PreprocessorOptions.JAVA
  }

  /* #ifndef KOTLIN_NATIVE {{ */
  @Throws(IOException::class, ParserException::class)
  @JvmStatic // }}
  fun process(rootDir: String, params: ProcessParams) {
    val rootDirFile = File(rootDir)
    if (!rootDirFile.isDirectory) {
      throw IOException("\$rootDir is not a directory")
    }
    process("", rootDirFile, params)
  }

  /* #ifndef KOTLIN_NATIVE {{ */ @Throws(IOException::class, ParserException::class) // }}
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

  /* #ifndef KOTLIN_NATIVE {{ */ @Throws(IOException::class, ParserException::class) // }}
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

  /* #ifndef KOTLIN_NATIVE {{ */ @Throws(IOException::class) // }}
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

  // }}
  // #ifdef COVERAGE {{@lombok.Generated}}
  // #ifndef KOTLIN_NATIVE {{
  class ProcessParams constructor(
    val contextInitializer: (PreprocessorContext) -> Unit,
    val pathFilter: (String) -> Boolean,
    val currentFile: (String) -> Unit
  )
}
// }}
