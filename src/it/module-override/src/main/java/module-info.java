module powerunit.test {
	requires powerunit.exceptions;

	provides ch.powerunit.extensions.exceptions.ExceptionMapper
			with ch.powerunittest.samples.MyExceptionMapper, ch.powerunittest.samples.MyExceptionMapper2;
}