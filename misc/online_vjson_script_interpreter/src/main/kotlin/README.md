# Fill this directory

Run `./gradlew clean kotlinJs` in root dir, then copy-paste the whole vjson package into this directory.

```
./gradlew clean kotlinJs
rm -r misc/online_vjson_script_interpreter/src/main/kotlin/vjson/*
cp -r src/main/kotlin/vjson/* misc/online_vjson_script_interpreter/src/main/kotlin/vjson
```
