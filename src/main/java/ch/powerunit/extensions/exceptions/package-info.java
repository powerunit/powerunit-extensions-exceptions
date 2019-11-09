/**
 * Functional interface similar to the once from {@code java.util.function}, but
 * with {@code throws Exception}.
 * <p>
 * The various functional interface provided by this package exposes several
 * dedicated methods :
 * <ul>
 * <li><b>{@code uncheck()}</b> which converts the functional interface to the
 * once from {@code java.util.function}, having the <i>checked</i> Exception
 * wrapped in a {@code RuntimeException}. By default this will be an instance of
 * {@link ch.powerunit.extensions.exceptions.WrappedException WrappedException}.
 * An additional method <b>{@code unchecked(...)}</b> is provided to statically
 * create unchecked version of the functional interface.</li>
 * <li><b>{@code lift()}</b> and <b>{@code lifted(...)}</b> which converts the
 * functional interface to a version that either return an
 * {@code Optional.empty()} or a default value in case of error.</li>
 * <li><b>{@code ignore()}</b> and <b>{@code ignored(...)}</b> which converts
 * the functional interface to a version that return a default value in case of
 * error.</li>
 * </ul>
 * Additionally, it is also possible to override the generated exception (for
 * the {@code uncheck...} methods) by specifying a {@code Function} to compute
 * the target {@code RuntimeException}.
 *
 * <h2>Examples</h2>
 *
 * It is possible to use the method {@code unchecked} to directly create a
 * functional interface with only runtime exception :
 *
 * <pre>
 * FunctionWithException&lt;String, String, IOException&gt; fonctionThrowingException = ...;
 *
 * Function&lt;String, String&gt; functionThrowingRuntimeException =
 *   FunctionWithException.unchecked(fonctionThrowingException);
 * </pre>
 *
 * When it is required to thrown a specific {@code RuntimeException}, it is also
 * possible to specify it :
 *
 * <pre>
 * FunctionWithException&lt;String, String, IOException&gt; fonctionThrowingException = ...;
 *
 * Function&lt;String, String&gt; functionThrowingRuntimeException =
 *   FunctionWithException.unchecked(
 *                                   fonctionThrowingException,
 *                                   IllegalArgumentException::new
 *                                  );
 * </pre>
 *
 * When the exception should not be thrown in case of error, it is possible to
 * create a {@code Function} with {@code Optional} result :
 *
 * <pre>
 * FunctionWithException&lt;String, String, IOException&gt; fonctionThrowingException = ...;
 *
 * Function&lt;String, Optional&lt;String&gt;&gt; functionWithOptionalResult =
 *   FunctionWithException.lifted(fonctionThrowingException);
 * </pre>
 * 
 * <h2>Exception Mapping</h2>
 * 
 * By default, the exception are transformed into a {@code RuntimeException}
 * using the {@link ch.powerunit.extensions.exceptions.WrappedException}. This
 * behaviour can be changed locally (second argument of the {@code unchecked}
 * methods) or globally (by registering default {@code ExceptionMapper}.
 * 
 * To do the global configuration, it is required first to create the
 * {@code ExceptionMapper}:
 * 
 * <pre>
 * public class MyExceptionMapper implements ExceptionMapper {
 *   public RuntimeException apply(Exception e) {
 *     //TODO
 *   }
 *   public Class&gt;? extends Exception&lt; targetException() {
 *     return //TODO;
 *   }
 *   // Optional, to define the order between the ExceptionMapper
 *   public int order() {
 *     return 100;
 *   }
 * }
 * </pre>
 * 
 * Then, the {@code ExceptionMapper} must be registered :
 * 
 * <pre>
 * module XXX {
 *   requires powerunit.exceptions;
 *   provides ch.powerunit.extensions.exceptions.ExceptionMapper
 *     with ....MyExceptionMapper;
 * }
 * </pre>
 *
 * @see java.util.function
 *
 */
package ch.powerunit.extensions.exceptions;
