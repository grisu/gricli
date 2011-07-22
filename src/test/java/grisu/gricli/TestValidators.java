package grisu.gricli;

import static org.junit.Assert.*;
import grisu.gricli.GricliEnvironment.DateValidator;
import grisu.gricli.GricliEnvironment.MemoryValidator;


import org.junit.Before;
import org.junit.Test;

public class TestValidators {

	private DateValidator dateValidator;
	private MemoryValidator memValidator;

	@Before
	public void setUp(){
		this.dateValidator = new GricliEnvironment.DateValidator();
		this.memValidator = new GricliEnvironment.MemoryValidator();
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
	
	@Test
	public void testNumericMemory() throws Exception {
		assertEquals("22",memValidator.validate("memory", "22"));
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testNegativeMemory() throws Exception {
		memValidator.validate("memory", "-10");
	}
	
	@Test(expected=GricliSetValueException.class)
	public void testNotMemory() throws Exception {
		memValidator.validate("memory", "abcd");
	}
	
	@Test
	public void testSimpleMbMemory() throws Exception {
		assertEquals("19", memValidator.validate("mem","19m"));
	}
	
	
	@Test
	public void testSimpleGbMemory() throws Exception {
		assertEquals("1024", memValidator.validate("mem","1g"));
	}
	
	@Test
	public void testComplexMemory() throws Exception {
		assertEquals("10260", memValidator.validate("memory", "10g20m"));
	}

}
