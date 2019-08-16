# Powerunit-extensions-exceptions

[![Build Status](https://travis-ci.com/powerunit/powerunit-extensions-exceptions.svg?branch=master)](https://travis-ci.com/powerunit/powerunit-extensions-exceptions)[![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-exceptions/depshield.svg)](https://depshield.github.io) [![Total alerts](https://img.shields.io/lgtm/alerts/g/powerunit/powerunit-extensions-exceptions.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/powerunit/powerunit-extensions-exceptions/alerts/)[![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-exceptions/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-exceptions?branch=master)[![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions)[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&utm_medium=referral&utm_content=powerunit/powerunit-extensions-exceptions&utm_campaign=Badge_Coverage)[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-exceptions&amp;utm_campaign=Badge_Grade)[![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions)[![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-exceptions?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-exceptions)[![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-exceptions-master)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions)![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-exceptions.png?style=cut)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=powerunit_powerunit-extensions-exceptions&metric=alert_status)](https://sonarcloud.io/dashboard?id=powerunit_powerunit-extensions-exceptions)[![javadoc](http://javadoc.io/badge/ch.powerunit.extensions/powerunit-extensions-exceptions.svg?color=yellow)](http://javadoc.io/doc/ch.powerunit.extensions/powerunit-extensions-exceptions)

This library provides support to wraps _checked exception_, to be used as target functional interface (which by default only support `RuntimeException`).

The library exposes several functional interface, similar to the one from `java.util.function`, but that may throw exception. Then several methods are provided to convert these exception to `RuntimeException` or lift the function.

For example :

```java
FunctionWithException<String, String, IOException> fonctionThrowingException = ...;

Function<String, String> functionThrowingRuntimeException = FunctionWithException
  .unchecked(fonctionThrowingException);
```

wraps the exception from `functionThrowingException` into a `RuntimeException` (which cause is the original one).

## Usage

_TODO_

## Reference

_TODO_
