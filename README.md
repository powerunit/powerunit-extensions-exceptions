# Powerunit-extensions-exceptions

[![Build Status](https://travis-ci.com/powerunit/powerunit-extensions-exceptions.svg?branch=master)](https://travis-ci.com/powerunit/powerunit-extensions-exceptions)[![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-exceptions/depshield.svg)](https://depshield.github.io) [![Total alerts](https://img.shields.io/lgtm/alerts/g/powerunit/powerunit-extensions-exceptions.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/powerunit/powerunit-extensions-exceptions/alerts/)[![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-exceptions/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-exceptions?branch=master)[![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions)[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&utm_medium=referral&utm_content=powerunit/powerunit-extensions-exceptions&utm_campaign=Badge_Coverage)[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-exceptions&amp;utm_campaign=Badge_Grade)[![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions)[![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-exceptions?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-exceptions)[![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-exceptions-master)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions)![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-exceptions.png?style=cut)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=powerunit_powerunit-extensions-exceptions&metric=alert_status)](https://sonarcloud.io/dashboard?id=powerunit_powerunit-extensions-exceptions)[![badge](https://report.ci/status/powerunit/powerunit-extensions-exceptions/badge.svg?branch=master)](https://report.ci/status/powerunit/powerunit-extensions-exceptions?branch=master)[![javadoc](http://javadoc.io/badge/ch.powerunit.extensions/powerunit-extensions-exceptions.svg?color=yellow)](http://javadoc.io/doc/ch.powerunit.extensions/powerunit-extensions-exceptions)

This library provides support to wraps _checked exception_, to be used as target functional interface (which by default only support `RuntimeException`).

The library exposes several functional interface, similar to the one from `java.util.function`, but that may throw exception. Then several methods are provided to convert these exception to `RuntimeException` or lift the function.

For example:

```java
FunctionWithException<String, String, IOException> fonctionThrowingException = ...;

Function<String, String> functionThrowingRuntimeException = FunctionWithException
  .unchecked(fonctionThrowingException);
```

wraps the exception from `IOException` into a `RuntimeException` (which cause is the original one).

## Usage

Add the following dependency to your maven project :

```xml
<dependency>
  <groupId>ch.powerunit.extensions</groupId>
  <artifactId>powerunit-extensions-exceptions</artifactId>
  <version>1.0.0</version>
</dependency>
```

And then just use the interface from the package `ch.powerunit.extensions.exceptions`. Each available interface have a name similar with the one from the `java.util.function` package, but ending with `WithException`. Three essential static entry methods are available :

| Method      | Description                                                                                                                                                                                | Example                                                         |
| ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------- |
| `unchecked` | Converts the functional interface to the one without exception, by wrapping the exception to a `RuntimeException`                                                                          | `FunctionWithException<T,R,E>` to `Function<T,R>`           |
| `lifted`    | Converts the functional interface to the one without exception, by returning an `Optional` or a default value in case of exception (or ignore exception for interface without return value) | `FunctionWithException<T,R,E>` to `Function<T,Optional<R>>` |
| `ignored`   | Converts the functional interface to the one without exception, by returning a default value in case of exception (or ignore exception for interface without return value)               | `FunctionWithException<T,R,E>` to `Function<T,R>`           |

Also, non static version (`uncheck`, `lift`, `ignore`) of the methods are available.

The method `unchecked` also support an additional parameter to define how to wrap the exception by passing a `Function<Exception,RuntimeException>` to do it.

## Reference

The following classes are provided:

| Standard functional interface | Exception functional interface         | Unchecked version       | Lifted version                 | Ignore version                |
| ----------------------------- | -------------------------------------- | ----------------------- | ------------------------------ | -------------- |
| BiFunction<T,U,R>             | BiFUnctionWithException<T,U,R,E>       | BiFunction<T,U,R>       | BiFunction<T,U,Optional<R>>    | BiFunction<T,U,R>      |
| BiConsumer<T,U>               | BiConsumerWithException<T,U,E>         | BiConsumer<T,U>         | BiConsumer<T,U>                | BiConsumer<T,U>        |
| BiPredicate<T,U>              | BiPredicateWithException<T,U,E>        | BiPredicate<T,U>        | BiPredicate<T,U>               | BiPredicate<T,U>       |
| BinaryOperator<T>             | BinaryOperatorWithException<T,E>       | BinaryOperator<T,U>     | BinaryFunction<T,T,Optional<T> | BinaryOperator<T>      |
| BooleanSupplier               | BooleanSupplierWithException<E>        | BooleanSupplier         | BooleanSupplier                | BooleanSupplier        |
| Consumer<T>                   | ConsumerWithException<T,E>             | Consumer<T>             | Consumer<T>                    | Consumer<T>            |
| DoubleBinaryOperator          | DoubleBinaryOperatorWithException<E>   | DoubleBinaryOperator    | DoubleBinaryOperator           | DoubleBinaryOperator   |
| DoubleConsumer                | DoubleConsumerWithException<E>         | DoubleConsumer          | DoubleConsumer                 | DoubleConsumer         |
| DoubleFunction<R>             | DoubleFunctionWithException<R,E>       | DoubleFunction<R>       | DoubleFunction<Optional<R>>    | DoubleFunction<R>      |
| DoublePredicate               | DoublePredicateWithException<E>        | DoublePredicate         | DoublePredicate                | DoublePredicate        |
| DoubleSupplier                | DoubleSupplierWithException<E>         | DoubleSupplier          | DoubleSupplier                 | DoubleSupplier         |
| DoubleToIntFunction           | DoubleToIntFunctionWithException<E>    | DoubleToIntFunction     | DoubleToIntFunction            | DoubleToIntFunction    |
| DoubleToLongFunction          | DoubleToLongFunctionWithException<E>   | DoubleToLongFunction    | DoubleToLongFunction           | DoubleToLongFunction   |
| DoubleUnaryOperator           | DoubleUnaryOperatorWithException<E>    | DoubleUnaryOperator     | DoubleUnaryOperator            | DoubleUnaryOperator    |
| Function<T,R>                 | FunctionWithException<T,R,E>           | Function<T,R>           | Function<T,Optional<R>>        | Function<T,R>          |
| IntBinaryOperator             | IntBinaryOperatorWithException<E>      | IntBinaryOperator       | IntBinaryOperator              | IntBinaryOperator      |
| IntConsumer                   | IntConsumerWithException<E>            | IntConsumer             | IntConsumer                    | IntConsumer            |
| IntFunction<R>                | IntFunctionWithException<R,E>          | IntFunction<R>          | IntFunction<Optional<R>>       | IntFunction<R>         |
| IntPredicate                  | IntPredicateWithException<E>           | IntPredicate            | IntPredicate                   | IntPredicate           |
| IntSupplier                   | IntSupplierWithException<E>            | IntSupplier             | IntSupplier                    | IntSupplier            |
| IntToDoubleFunction           | IntToDoubleFunctionWithException<E>    | IntToDoubleFunction     | IntToDoubleFunction            | IntToDoubleFunction    |
| IntToLongFunction             | IntToLongFunctionWithException<E>      | IntToLongFunction       | IntToLongFunction              | IntToLongFunction      |
| IntUnaryOperator              | IntUnaryOperatorWithException<E>       | IntUnaryOperator        | IntUnaryOperator               | IntUnaryOperator       |
| LongBinaryOperator            | LongBinaryOperatorWithException<E>     | LongBinaryOperator      | LongBinaryOperator             | LongBinaryOperator     |
| LongConsumer                  | LongConsumerWithException<E>           | LongConsumer            | LongConsumer                   | LongConsumer           |
| LongFunction<R>               | LongFunctionWithException<R,E>         | LongFunction<R>         | LongFunction<Optional<R>>      | LongFunction<R>        |
| LongPredicate                 | LongPredicateWithException<E>          | LongPredicate           | LongPredicate                  | LongPredicate          |
| LongSupplier                  | LongSupplierWithException<E>           | LongSupplier            | LongSupplier                   | LongSupplier           |
| LongToDoubleFunction          | LongToDoubleFunctionWithException<E>   | LongToDoubleFunction    | LongToDoubleFunction           | LongToDoubleFunction   |
| LongToIntFunction             | LongToIntFunctionWithException         | LongToIntFunction       | LongToIntFunction              | LongToIntFunction      |
| LongUnaryOperator             | LongUnaryOperatorWithException         | LongUnaryOperator       | LongUnaryOperator              | LongUnaryOperator      |
| ObjDoubleConsumer<T>          | ObjDoubleConsumerWithException<T,E>    | ObjDoubleConsumer<T>    | ObjDoubleConsumer<T>           | ObjDoubleConsumer<T>   |
| ObjIntConsumer<T>             | ObjIntConsumerWithException<T,E>       | ObjIntConsumer<T>       | ObjIntConsumer<T>              | ObjIntConsumer<T>      |
| ObjLongConsumer<T>            | ObjLongConsumerWithException<T,E>      | ObjLongConsumer<T>      | ObjLongConsumer<T>             | ObjLongConsumer<T>     |
| Predicate<T>                  | PredicateWithException<T,E>            | Predicate<T>            | Predicate<T>                   | Predicate<T>           |
| Runnable                      | RunnableWithException<E>               | Runnable                | Runnable                       | Runnable               |
| Supplier<T>                   | SupplierWithException<T,E>             | Supplier<T>             | Supplier<Optional<T>>          | Supplier<T>            |
| ToDoubleBiFunction<T,U>       | ToDoubleBiFunctionWithException<T,U,E> | ToDoubleBiFunction<T,U> | ToDoubleBiFunction<T,U>        | ToDoubleBiFunction<T,U>|
| ToDoubleFunction<T>           | ToDoubleFunctionWithException<T,E>     | ToDoubleFunction<T>     | ToDoubleFunction<T>            |  ToDoubleFunction<T>    |
| ToIntBiFunction<T,U>          | ToIntBiFunctionWithException<T,U,E>    | ToIntBiFunction<T,U>    | ToIntBiFunction<T,U>           | ToIntBiFunction<T,U>   |
| ToIntFunction<T>              | ToIntFunctionWithException<T,E>        | ToIntFunction<T>        | ToIntFunction<T>               | ToIntFunction<T>       |
| ToLongBiFunction<T,U>         | ToLongBiFunctionWithException<T,U,E>   | ToLongBiFunction<T,U>   | ToLongBiFunction<T,U>          | ToLongBiFunction<T,U>  |
| ToLongFunction<T>             | ToLongFunctionWithException<T,E>       | ToLongFunction<T>       | ToLongFunction<T>              | ToLongFunction<T>      |
| UnaryOperator<T>              | UnaryOperatorWithException<T,E>        | UnaryOperator<T>        | Function<T,Optional<T>>        | UnaryOperator<T>       |
