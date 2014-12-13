depchecker
==========

Java command line tool that checks maven for newest versions of build.gradle dependencies

Usage:
--------
```
./java -jar depchecker-all-1.0.jar <path to build.gradle>
```

Building runnable jar:
--------
```
./gradlew fatJar
```
Jar will be located at build/libs

__Note:__ you must use java 8 to build project
