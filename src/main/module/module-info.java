/**
 * This is the module containing all the support for functional interface with
 * exception support.
 * 
 * @see ch.powerunit.extensions.exceptions
 */
module powerunit.exceptions {
	exports ch.powerunit.extensions.exceptions;

	requires static java.sql;
	requires static java.xml;
	requires static org.apache.commons.collections4;
}