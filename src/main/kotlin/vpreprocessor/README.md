# vpreprocessor

## intro

The vpreprocessor uses some expressions such as `#ifdef`, `#ifndef` to add/remove code based on compiling arguments.

The expressions and the code to be added are written in comments, e.g.:

```kotlin
// #ifdef KOTLIN_NATIVE {{ @ThreadLocal }}
// or
/* #ifdef KOTLIN_NATIVE {{ @ThreadLocal }} */
```

The code to be removed are written normally, e.g.:

```kotlin
/* #ifndef KOTLIN_NATIVE {{ */ @JvmField /* }} */
val a = 1
```

You may also use `else`, e.g.:

```kotlin
// #ifdef KOTLIN_NATIVE {{ @ThreadLocal }}
val a = /* #ifdef KOTLIN_NATIVE {{ 1 }} else {{ */ ThreadLocal.withInitial { 1 } /* }} */
```

## syntax

### tokenizer

[Tokenizer.kt](https://github.com/wkgcass/vjson/blob/master/src/main/kotlin/vpreprocessor/Tokenizer.kt)

The input is divided into two parts:

1. The source code part (Plain)
2. The statements and expressions part (Macro)

When the tokenizer reads an input, it expects a comment directly following a `#` symbol to start the macro statements, e.g.

```
/* #
// #
/*       # (whitespaces are ignored)
```

After `#` symbol, macro begins, and will end when reachinig the end of the comment:

```
/* # */
// # (new line ends the comment so macro ends as well)
```

To write plain text in macro, use `{{` and `}}`, e.g.:

```
/* #{{ @JvmField }} */
```

Also, the `{{` `}}` block can also contain plain text out of the comments, in this case, the macro is not terminated by the end of the comment, e.g.:

```
/* #ifdef JVM {{ */ @JvmField /* }} else {{ ... }} */
```

To gather multiple statements as a code block, use `{` `}`, e.g.:

```
/* #ifdef foo { {{ bar }} {{ hello }} } else { {{ world }} } */
// #ifdef foo {} (it can also contain zero statements)
```

### parser

[Parser.kt](https://github.com/wkgcass/vjson/blob/master/src/main/kotlin/vpreprocessor/Parser.kt)

#### code block

```
{ ... }
```

A code block may contain zero or more statements.

#### ifdef

```
ifdef varname { ... }
ifdef varname {{ ... }}
```

When the `$varname` is defined, the code block will be executed or the plain text will be rendered.

`ifdef` can follow an `else` block.

#### ifndef

```
ifndef varname { ... }
ifndef varname {{ ... }}
```

When the `$varname` is not defined, the code block will be executed or the plain text will be rendered.

`ifndef` can follow an `else` block.

#### else

```
else { ... }
else {{ ... }}
```

An `else` block can only be used directly after `ifdef` or `ifndef`. When the `if` condition is not satisfied, the `else` block will be executed or the plain text will be rendered.

## source code syntax considerations

The following java/kotlin syntax are taken into consideration:

1. single line comments `//`
2. multi-line comments `/* */`
3. nested comments `/* /* */ */`
4. chars `'a'` `'\"'` `'\''` `'\\'`
5. strings `"abc"` `"\""` `"\'"` `"\\"`
6. multi-line strings `"""abc"def"""`

## develop the vpreprocessor

1. The vpreprocessor code follows rules of writing a compiler frontend.
2. The tokenizer is a state machine which generates one token on each `feed` call.
3. The parser is a hand written recursive descent parser, it translates input charstream into a sequence of statements as AST.
4. Since the syntax is very simple, so the output AST of a parser can be directly executed and the output string generated.
