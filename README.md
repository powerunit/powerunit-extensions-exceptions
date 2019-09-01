# Powerunit-extensions-exceptions

[![Build Status](https://travis-ci.com/powerunit/powerunit-extensions-exceptions.svg?branch=master)](https://travis-ci.com/powerunit/powerunit-extensions-exceptions)[![Known Vulnerabilities](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/powerunit/powerunit-extensions-exceptions?targetFile=pom.xml) [![DepShield Badge](https://depshield.sonatype.org/badges/powerunit/powerunit-extensions-exceptions/depshield.svg)](https://depshield.github.io) [![Total alerts](https://img.shields.io/lgtm/alerts/g/powerunit/powerunit-extensions-exceptions.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/powerunit/powerunit-extensions-exceptions/alerts/)[![Coverage Status](https://coveralls.io/repos/github/powerunit/powerunit-extensions-exceptions/badge.svg?branch=master)](https://coveralls.io/github/powerunit/powerunit-extensions-exceptions?branch=master)[![codecov](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions/branch/master/graph/badge.svg)](https://codecov.io/gh/powerunit/powerunit-extensions-exceptions)[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&utm_medium=referral&utm_content=powerunit/powerunit-extensions-exceptions&utm_campaign=Badge_Coverage)[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54e6f34a650147e48b1864a420695a1c)](https://www.codacy.com/app/mathieu.boretti/powerunit-extensions-exceptions?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=powerunit/powerunit-extensions-exceptions&amp;utm_campaign=Badge_Grade)[![CodeFactor](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions/badge)](https://www.codefactor.io/repository/github/powerunit/powerunit-extensions-exceptions)[![BCH compliance](https://bettercodehub.com/edge/badge/powerunit/powerunit-extensions-exceptions?branch=master)](https://bettercodehub.com/results/powerunit/powerunit-extensions-exceptions)[![codebeat badge](https://codebeat.co/badges/cdebf167-fee0-46b4-b33d-c613f1586a9d)](https://codebeat.co/projects/github-com-powerunit-powerunit-extensions-exceptions-master)[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.powerunit.extensions/powerunit-extensions-exceptions)![mergify-status](https://gh.mergify.io/badges/powerunit/powerunit-extensions-exceptions.png?style=cut)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=powerunit_powerunit-extensions-exceptions&metric=alert_status)](https://sonarcloud.io/dashboard?id=powerunit_powerunit-extensions-exceptions)[![badge](https://report.ci/status/powerunit/powerunit-extensions-exceptions/badge.svg?branch=master)](https://report.ci/status/powerunit/powerunit-extensions-exceptions?branch=master)[![javadoc](http://javadoc.io/badge/ch.powerunit.extensions/powerunit-extensions-exceptions.svg?color=yellow)](http://javadoc.io/doc/ch.powerunit.extensions/powerunit-extensions-exceptions)

**This version doesn't support anymore Java 8. [Please use an older version, for instance the version 1.2.0](https://github.com/powerunit/powerunit-extensions-exceptions/tree/powerunit-extensions-exceptions-1.2.0)**


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
  <version>2.1.0</version>
</dependency>
```

And then just use the interface from the package `ch.powerunit.extensions.exceptions`. Each available interface has a name similar with the one from the `java.util.function` package, but ending with `WithException`. Three essential static entry methods are available :

| Method      | Description                                                                                                                                                                                | Example                                                         |
| ----------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------- |
| `unchecked` | Converts the functional interface to the one without exception, by wrapping the exception to a `RuntimeException`                                                                          | `FunctionWithException<T,R,E>` to `Function<T,R>`           |
| `lifted`    | Converts the functional interface to the one without exception, by returning an `Optional` or a default value in case of exception (or ignore exception for interface without return value) | `FunctionWithException<T,R,E>` to `Function<T,Optional<R>>` |
| `ignored`   | Converts the functional interface to the one without exception, by returning a default value in case of exception (or ignore exception for interface without return value)               | `FunctionWithException<T,R,E>` to `Function<T,R>`           |

Also, non static version (`uncheck`, `lift`, `ignore`) of the methods are available.

The method `unchecked` also support an additional parameter to define how to wrap the exception by passing a `Function<Exception,RuntimeException>` to do it.

An additional method `stage(d)` is also available on several interface.

### `uncheck(ed)`

This method converts the functional interface to the one without exception, by wrapping the exception to a `RuntimeException`.

Three versions of this methods exists :

* `uncheck()` converting the functional interface directly.
  ```java
  FunctionWithException<String,String,IOException> myFunction = ...;
  
  Function<String,String> myUncheckedFunction = myFunction.uncheck();
  ```
* `unchecked(myInterface)` converting the received parameter.
  ```java
  Function<String,String> myUncheckedFunction = FunctionWithException.unchecked(x->x);
  ```
* `unchecked(myInterface,myExceptionMapper)` converting the received parameter, and using the received exception mapper to wrap the exception.
  ```java
  Function<String,String> myUncheckedFunction = 
    FunctionWithException.unchecked(
      x->x,
      IllegalArgumentException::new
    );
  ```
  The resulting exceptions are :
  
  | Original With Exception Interface| Uncheck(ed) without exception mapper | Unchecked with exception mapper |
  | --------------------------------- | ------------------------------------ | -----------------------------------   |
  | _The original exception_          | An instance of `ch.powerunit.extensions.exceptions.WrappedException` having the original exception as cause | A instance of `RuntimeException` returned by the exception mapper |
  
  A more concrete example may be :
  ```java
  Function<Exception, RuntimeException> mapper = 
    ExceptionMapper.forException(
      SQLException.class,
      s -> 
           new WrappedException(
             String.format(
               "%s ; ErrorCode=%s ; SQLState=%s", 
               s.getMessage(),
               s.getErrorCode(), 
               s.getSQLState()
             ), 
             s
           )
    );
  ```
  In this example, it extracts the SQL information from the `SQLException`. The `exceptionMapperFor` method ensures that in case the exception is not a `SQLException`, a standard wrapping is done. Please see [the dedicated section](#exception-mapper) for more information.

### `lift(ed)`

This method converts the functional interface to the one without exception, by returning an `Optional` or a default value in case of exception or ignore the exception for interface without return value.

Two versions of this methods exists :

* `lift()` converting the functional interface directly.
  ```java
  FunctionWithException<String,String,IOException> myFunction = ...;
  
  Function<String,Optional<String>> myLiftedFunction = myFunction.lift();
  ```
* `lifted(myInterface)` converting the received parameter.
  ```java
  Function<String,Optional<String>> myLiftedFunction = FunctionWithException.lifted(x->x);
  ```

### `ignore(d)`

This method converts the functional interface to the one without exception, by returning a default value in case of exception or ignore the exception for interface without return value.

Two versions of this methods exists :

* `ignore()` converting the functional interface directly.
  ```java
  FunctionWithException<String,String,IOException> myFunction = ...;
  
  Function<String,String> myLiftedFunction = myFunction.ignore();
  ```
* `ignored(myInterface)` converting the received parameter.
  ```java
  Function<String,String> myLiftedFunction = FunctionWithException.ignored(x->x);
  ```

### `stage(d)`

This method converts the functional interface to the one without exception, by returning an `CompletionStage`. _This is not available on interface returning primitive type_. 

For functional interface without result, a `CompletionStage<Void>` is returned.

Two versions of this methods exists :

* `stage()` converting the functional interface directly.
  ```java
  FunctionWithException<String,String,IOException> myFunction = ...;
  
  Function<String,CompletionStage<String>> myStagedFunction = myFunction.stage();
  ```
* `staged(myInterface)` converting the received parameter.
  ```java
  Function<String,CompletionStage<String>> myStagedFunction = FunctionWithException.staged(x->x);
  ```

### Exception Mapper

_Since the version 2.0.0, the method `exceptionMapperFor` from interface `ExceptionHandlerSupport` is deprecated in favor of the new one `forException` from interface `ExceptionMapper`._

The various methods `forExceptions` provides a way to chain several Exception Mapper.

Also, some dedicated, _ready to used_, Exception Mapper are provided :

* `sqlExceptionMapper()` - Return an exception mapper that adds to the message of the `RuntimeException` the SQL Error from the underlying exception. **This is only usable when the module java.sql is available**.
* `jaxbExceptionMapper()` - Return an exception mapper that adds to the message of the `RuntimeException` the JAXB Error from the underlying exception. **This is only usable when JAXB is available**.
* `saxExceptionMapper()` - Return an exception mapper that adds to the message of the `RuntimeException` the SAX Error from the underlying exception. **This is only usable when the module java.xml is available**.
* `transformerExceptionMapper()` - Return an exception mapper that adds to the message of the `RuntimeException` the Transformer Error from the underlying exception. **This is only usable when the module java.xml is available**.

## Reference

The following classes are provided:

| Standard functional interface | Exception functional interface         | Unchecked version       | Lifted version                 | Ignored version               | Stage version                           |
| ----------------------------- | -------------------------------------- | ----------------------- | ------------------------------ | -------------- |--------------------|
| `BiFunction<T,U,R>`             | `BiFunctionWithException<T,U,R,E>`       | `BiFunction<T,U,R>`       | `BiFunction<T,U,Optional<R>>`    | `BiFunction<T,U,R>`      | `BiFunction<T,U,CompletionStage<R>>` |
| `BiConsumer<T,U>`               | `BiConsumerWithException<T,U,E>`         | `BiConsumer<T,U>`         | `BiConsumer<T,U>`                | `BiConsumer<T,U>`        | `BiFunction<T,U,CompletionStage<Void>>` |
| `BiPredicate<T,U>`              | `BiPredicateWithException<T,U,E>`        | `BiPredicate<T,U>`        | `BiPredicate<T,U>`               | `BiPredicate<T,U>`       | N/A |
| `BinaryOperator<T>`             | `BinaryOperatorWithException<T,E>`       | `BinaryOperator<T,U>`     | `BinaryFunction<T,T,Optional<T>>` | `BinaryOperator<T>`      | `BinaryFunction<T,T,CompletionStage<T>>` |
| `BooleanSupplier`               | `BooleanSupplierWithException<E>`        | `BooleanSupplier`         | `BooleanSupplier`                | `BooleanSupplier`        | N/A |
| `Consumer<T>`                   | `ConsumerWithException<T,E>`             | `Consumer<T>`             | `Consumer<T>`                    | `Consumer<T>`            | `Function<T,CompletionStage<Void>>` |
| `DoubleBinaryOperator`          | `DoubleBinaryOperatorWithException<E>`   | `DoubleBinaryOperator`    | `DoubleBinaryOperator`           | `DoubleBinaryOperator`   | N/A |
| `DoubleConsumer`                | `DoubleConsumerWithException<E>`         | `DoubleConsumer`          | `DoubleConsumer`                 | `DoubleConsumer`         | `DoubleFunction<CompletionStage<Void>>` |
| `DoubleFunction<R>`             | `DoubleFunctionWithException<R,E>`       | `DoubleFunction<R>`       | `DoubleFunction<Optional<R>>`    | `DoubleFunction<R>`      | `DoubleFunction<CompletionStage<R>>` |
| `DoublePredicate`               | `DoublePredicateWithException<E>`        | `DoublePredicate`         | `DoublePredicate`                | `DoublePredicate`        | N/A |
| `DoubleSupplier`                | `DoubleSupplierWithException<E>`         | `DoubleSupplier`          | `DoubleSupplier`                 | `DoubleSupplier`         | N/A |
| `DoubleToIntFunction`           | `DoubleToIntFunctionWithException<E>`    | `DoubleToIntFunction`     | `DoubleToIntFunction`            | `DoubleToIntFunction`    | N/A |
| `DoubleToLongFunction`          | `DoubleToLongFunctionWithException<E>`   | `DoubleToLongFunction`    | `DoubleToLongFunction`           | `DoubleToLongFunction`   | N/A |
| `DoubleUnaryOperator`           | `DoubleUnaryOperatorWithException<E>`    | `DoubleUnaryOperator`     | `DoubleUnaryOperator`            | `DoubleUnaryOperator`    | N/A |
| `FileFilter`                 | `FileFilterWithException<E>`           | `FileFilter`           | `FileFilter`        | `FileFilter`          | N/A |
| `FilenameFilter`                 | `FilenameFilterWithException<E>`           | `FilenameFilter`           | `FilenameFilter`        | `FilenameFilter`          | N/A |
| `Function<T,R>`                 | `FunctionWithException<T,R,E>`           | `Function<T,R>`           | `Function<T,Optional<R>>`        | `Function<T,R>`          | `Function<T,CompletionStage<R>>` |
| `IntBinaryOperator`             | `IntBinaryOperatorWithException<E>`      | `IntBinaryOperator`       | `IntBinaryOperator`              | `IntBinaryOperator`      | N/A |
| `IntConsumer`                   | `IntConsumerWithException<E>`            | `IntConsumer`             | `IntConsumer`                   | `IntConsumer`            | `IntFunction<CompletionStage<Void>>` |
| `IntFunction<R>`                | `IntFunctionWithException<R,E>`          | `IntFunction<R>`          | `IntFunction<Optional<R>>`       | `IntFunction<R>`         | `IntFunction<CompletionStage<R>>` |
| `IntPredicate`                  | `IntPredicateWithException<E>`           | `IntPredicate`            | `IntPredicate`                   | `IntPredicate`           | N/A |
| `IntSupplier`                   | `IntSupplierWithException<E>`            | `IntSupplier`             | `IntSupplier`                    | `IntSupplier`            | N/A |
| `IntToDoubleFunction`           | `IntToDoubleFunctionWithException<E>`    | `IntToDoubleFunction`     | `IntToDoubleFunction`            | `IntToDoubleFunction`    |N/A |
| `IntToLongFunction`             | `IntToLongFunctionWithException<E>`      | `IntToLongFunction`       | `IntToLongFunction`              | `IntToLongFunction`      |N/A |
| `IntUnaryOperator`              | `IntUnaryOperatorWithException<E>`       | `IntUnaryOperator`        | `IntUnaryOperator`               | `IntUnaryOperator`       |N/A |
| `LongBinaryOperator`            | `LongBinaryOperatorWithException<E>`     | `LongBinaryOperator`      | `LongBinaryOperator`             | `LongBinaryOperator`     |N/A |
| `LongConsumer`                  | `LongConsumerWithException<E>`           | `LongConsumer`            | `LongConsumer`                  | `LongConsumer`           | `LongFunction<CompletionStage<Void>>` |
| `LongFunction<R>`               | `LongFunctionWithException<R,E>`         | `LongFunction<R>`         | `LongFunction<Optional<R>>`      | `LongFunction<R>`        | `LongFunction<CompletionStage<R>>` |
| `LongPredicate`                 | `LongPredicateWithException<E>`          | `LongPredicate`           | `LongPredicate`                  | `LongPredicate`          | N/A |
| `LongSupplier`                  | `LongSupplierWithException<E>`           | `LongSupplier`            | `LongSupplier`                   | `LongSupplier`           | N/A |
| `LongToDoubleFunction`          | `LongToDoubleFunctionWithException<E>`   | `LongToDoubleFunction`    | `LongToDoubleFunction`           | `LongToDoubleFunction`   | N/A |
| `LongToIntFunction`             | `LongToIntFunctionWithException`         | `LongToIntFunction`       | `LongToIntFunction`              | `LongToIntFunction`      |N/A |
| `LongUnaryOperator`             | `LongUnaryOperatorWithException`         | `LongUnaryOperator`       | `LongUnaryOperator`              | `LongUnaryOperator`      |N/A |
| `ObjectInputFilter`                 | `ObjectInputFilterWithException<T,R,E>`           | `ObjectInputFilter`           | `Function<FilterInfo,Optional<Status>>`        | `ObjectInputFilter`          | `Function<FilterInfo,CompletionStage<Status>>` |
| `ObjDoubleConsumer<T>`          | `ObjDoubleConsumerWithException<T,E>`    | `ObjDoubleConsumer<T>`    | `ObjDoubleConsumer<T>`           | `ObjDoubleConsumer<T>`   | `Function<T,Double,CompletionStage<Void>>` |
| `ObjIntConsumer<T>`             | `ObjIntConsumerWithException<T,E>`       | `ObjIntConsumer<T>`       | `ObjIntConsumer<T>`              | `ObjIntConsumer<T>`      | `Function<T,Integer,CompletionStage<Void>>` |
| `ObjLongConsumer<T>`            | `ObjLongConsumerWithException<T,E>`      | `ObjLongConsumer<T>`      | `ObjLongConsumer<T>`             | `ObjLongConsumer<T>`     | `Function<T,Long,CompletionStage<Void>>` |
| `PathMatcher`                  | `PathMatcherWithException<E>`            | `PathMatcher`            | `PathMatcher`                   | `PathMatcher`           | N/A
| `Predicate<T>`                  | `PredicateWithException<T,E>`            | `Predicate<T>`            | `Predicate<T>`                   | `Predicate<T>`           | N/A
| `Runnable`                      | `RunnableWithException<E>`               | `Runnable`                | `Runnable`                       | `Runnable`               | `Supplier<CompletionStage<Void>>`|
| `Supplier<T>`                   | `SupplierWithException<T,E>`             | `Supplier<T>`             | `Supplier<Optional<T>>`          | `Supplier<T>`            | `Supplier<CompletionStage<T>>` |
| `ToDoubleBiFunction<T,U>`       | `ToDoubleBiFunctionWithException<T,U,E>` | `ToDoubleBiFunction<T,U>` | `ToDoubleBiFunction<T,U>`        | `ToDoubleBiFunction<T,U>`| N/A |
| `ToDoubleFunction<T>`           | `ToDoubleFunctionWithException<T,E>`     | `ToDoubleFunction<T>`     | `ToDoubleFunction<T>`            |  `ToDoubleFunction<T>`    | N/A |
| `ToIntBiFunction<T,U>`          | `ToIntBiFunctionWithException<T,U,E>`    | `ToIntBiFunction<T,U>`    | `ToIntBiFunction<T,U>`           | `ToIntBiFunction<T,U>`   | N/A |
| `ToIntFunction<T>`              | `ToIntFunctionWithException<T,E>`        | `ToIntFunction<T>`        | `ToIntFunction<T>`               | `ToIntFunction<T>`       | N/A |
| `ToLongBiFunction<T,U>`         | `ToLongBiFunctionWithException<T,U,E>`   | `ToLongBiFunction<T,U>`   | `ToLongBiFunction<T,U>`          | `ToLongBiFunction<T,U>`  | N/A |
| `ToLongFunction<T>`             | `ToLongFunctionWithException<T,E>`       | `ToLongFunction<T>`       | `ToLongFunction<T>`              | `ToLongFunction<T>`      | N/A |
| `UnaryOperator<T>`              | `UnaryOperatorWithException<T,E>`        | `UnaryOperator<T>`        | `Function<T,Optional<T>>`        | `UnaryOperator<T>`       | `Function<T,CompletionStage<T>>` |
