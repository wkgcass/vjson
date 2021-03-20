## preprocessor

The preprocessor uses some expressions such as <code>#ifdef</code>, <code>#ifndef</code> to add/remove code based on compiling arguments.

The expressions and the code to be added are written in comments, e.g.

```kotlin
// #ifdef KOTLIN_NATIVE {{ @ThreadLocal }}
// or
/* #ifdef KOTLIN_NATIVE {{ @ThreadLocal }} */
```

The code to be removed are written normally, e.g.

```kotlin
/* #ifndef KOTLIN_NATIVE {{ */ @JvmField /* }} */
val a = 1
```

You may also use `else`, e.g.

```kotlin
// #ifdef KOTLIN_NATIVE {{ @ThreadLocal }}
val a = /* #ifdef KOTLIN_NATIVE {{ 1 }} else {{ */ ThreadLocal.withInitial { 1 } /* }} */
```
