package grisu.gricli;

import static org.junit.Assert.*;
import grisu.gricli.GricliEnvironment.DateValidator;

import org.junit.Before;
import org.junit.Test;

public class TestValidators {

	private DateValidator dateValidator;

	@Before
	public void setUp(){
		this.dateValidator = new GricliEnvironment.DateValidator();
	}
	
	@Test
	public void testNumericDateValidator() throws Exception{
		assertEquals("1",dateValidator.validate("walltime", "1"));
		
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testNegativeDate() throws Exception {
		dateValidator.validate("walltime", "-10");
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testNotADate() throws Exception {
		dateValidator.validate("walltime", "abcd");
	}
	
	@Test
	public void testSimpleHourDate() throws Exception {
		assertEquals("60", dateValidator.validate("walltime","1h"));
	}
	
	@Test
	public void testComplexDate() throws Exception {
		assertEquals("15153", dateValidator.validate("walltime", "10d12h33m"));
	}

}
