package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import havabol.Scanner;

public class ScannerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception
	{
		Scanner scan = new Scanner("C:\\Users\\DMWin7VM\\workspace\\HavaBol\\src\\InputFiles\\p1input1.txt", null);
		while( !scan.getNext().isEmpty() )
		{
			System.out.println(scan.currentToken.tokenStr);
		}
	}

}
