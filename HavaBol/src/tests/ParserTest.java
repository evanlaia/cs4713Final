package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import havabol.Parser;
import havabol.Scanner;
import havabol.SymbolTable;
import junit.framework.Assert;

public class ParserTest {

	@Test
	public void GenericOperatorsTest() 
	{
		 SymbolTable symbolTable = new SymbolTable();
	        
	        try
	        {
	            // Print a column heading 
	            System.out.printf("%-11s %-12s %s\n"
	                    , "primClassif"
	                    , "subClassif"
	                    , "tokenStr");
	            
	            Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\GenericOperatorsTest.txt", symbolTable);
	            Parser parse = new Parser(scan);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            org.junit.Assert.fail("Problem encountered");
	        }	
	}

}
