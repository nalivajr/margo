# Margo

## Overview
Margo is a small library similar to well-known ButterKnife, but works at runtime and Binds
views and listeners using reflection

## Samples
In source code you can find two modules: margo and app. Module `margo` contains all the code of library. It can be compiled to .aar and used in other applications. Module `app` contains sample activities, which can help to understand how to use Margo.

## Repository
### Maven

```XML
<dependency>
        <groupId>com.github.nalivajr</groupId>`
	<artifactId>margo</artifactId>`
	<version>1.0.1-beta</version>`
</dependency>
```

### Gradle
```Gradle
compile 'com.github.nalivajr:margo:1.0.1-beta'
```