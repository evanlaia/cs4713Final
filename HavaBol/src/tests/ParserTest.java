package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import havabol.Parser;
import havabol.STFunction;
import havabol.Scanner;
import havabol.SymbolTable;
import havabol.Token;
import junit.framework.Assert;

public class ParserTest {

	//@Test
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
	            
	            Scanner scan = new Scanner("src/InputFiles/GenericOperatorsTest.txt", symbolTable);
	            Parser parse = new Parser(scan);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            org.junit.Assert.fail("Problem encountered");
	        }	
	}
	
	@Test
	public void PrimitiveDatatypesTest()
	{
		SymbolTable symbolTable = new SymbolTable();
		 try
	        {
	            // Print a column heading 
	            System.out.printf("%-11s %-12s %s\n"
	                    , "primClassif"
	                    , "subClassif"
	                    , "tokenStr");
	            
	            //Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\PrimitiveDatatypesTest.txt", symbolTable);
	            Scanner scan = new Scanner("src/InputFiles/PrimitiveDatatypesTest.txt", symbolTable);
	            Parser parse = new Parser(scan);
	            org.junit.Assert.assertEquals(10, parse.sm.getValue("x").intVal);
	            org.junit.Assert.assertEquals(11, parse.sm.getValue("x2").intVal);
	            org.junit.Assert.assertEquals(3.14, parse.sm.getValue("y").doubleVal, .0001);
	            org.junit.Assert.assertEquals(4.2454, parse.sm.getValue("y2").doubleVal, .0001);
	            org.junit.Assert.assertEquals("\"Hello\"", parse.sm.getValue("s").strVal);
	            org.junit.Assert.assertEquals("\"World\"", parse.sm.getValue("s2").strVal);
	            org.junit.Assert.assertEquals("T", parse.sm.getValue("b").strVal);
	            org.junit.Assert.assertEquals("F", parse.sm.getValue("b2").strVal);
	            org.junit.Assert.assertEquals(10, parse.sm.getValue("x3").intVal);
	            org.junit.Assert.assertEquals(3.14, parse.sm.getValue("y3").doubleVal, .0001);
	            org.junit.Assert.assertEquals("\"Hello\"", parse.sm.getValue("s3").strVal);
	            org.junit.Assert.assertEquals("T", parse.sm.getValue("b3").strVal);
	            org.junit.Assert.assertEquals(10, parse.sm.getValue("x4").intVal);
	            org.junit.Assert.assertEquals(3.14, parse.sm.getValue("y4").doubleVal, .0001);
	            org.junit.Assert.assertEquals("\"Hello\"", parse.sm.getValue("s4").strVal);
	            org.junit.Assert.assertEquals("T", parse.sm.getValue("b4").strVal);
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            org.junit.Assert.fail("Problem encountered");
	        }	
	}

	//@Test 
	public void IdentifierAlreadyDeclaredTest()
	{
		SymbolTable symbolTable = new SymbolTable();
		 try
	        {
	            // Print a column heading 
	            System.out.printf("%-11s %-12s %s\n"
	                    , "primClassif"
	                    , "subClassif"
	                    , "tokenStr");
	            
	            //Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\PrimitiveDatatypesTest.txt", symbolTable);
	            Scanner scan = new Scanner("src/InputFiles/VariableAlreadyDeclaredTest.txt", symbolTable);
	            Parser parse = new Parser(scan);
	            org.junit.Assert.fail("Was supposed to throw an exception");
	        }
	        catch (Exception e)
	        {
	            
	        }	
	}
	
		//@Test 
		public void IdentifierUnDeclaredTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            //Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\PrimitiveDatatypesTest.txt", symbolTable);
		            Scanner scan = new Scanner("src/InputFiles/UndeclaredIdentifierTest.txt", symbolTable);
		            Parser parse = new Parser(scan);
		            org.junit.Assert.fail("Was supposed to throw an exception");
		        }
		        catch (Exception e)
		        {
		            
		        }	
		}
		
		@Test 
		public void ArrayTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            //Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\PrimitiveDatatypesTest.txt", symbolTable);
		            Scanner scan = new Scanner("src/InputFiles/ArrayTest.txt", symbolTable);
		            Parser parse = new Parser(scan);
		            
		            org.junit.Assert.assertEquals(5, parse.sm.intArrays.get("temp").get(4).intValue());
		            org.junit.Assert.assertEquals(3.4, parse.sm.floatArrays.get("farr").get(2).doubleValue(), .0001);
		            org.junit.Assert.assertEquals("\"works\"", parse.sm.stringArrays.get("sarr").get(2));
		            org.junit.Assert.assertEquals(false, parse.sm.boolArrays.get("barr").get(1));
		            org.junit.Assert.assertEquals(3, parse.sm.intArrays.get("temp2").get(2).intValue());
		            org.junit.Assert.assertEquals(4.5, parse.sm.floatArrays.get("farr2").get(3).doubleValue(), .0001);
		            org.junit.Assert.assertEquals("\"fred\"", parse.sm.stringArrays.get("sarr2").get(2));
		            org.junit.Assert.assertEquals(false, parse.sm.boolArrays.get("barr2").get(2));
		            
		            org.junit.Assert.assertEquals(-10000, parse.sm.intArrays.get("temp3").get(2).intValue());
		            org.junit.Assert.assertEquals(3, parse.sm.intArrays.get("temp4").get(2).intValue());
		            org.junit.Assert.assertEquals(-10000.0, parse.sm.floatArrays.get("farr3").get(1).doubleValue(), .0001);
		            org.junit.Assert.assertEquals(2.0, parse.sm.floatArrays.get("farr4").get(1).doubleValue(), .0001);
		            org.junit.Assert.assertEquals(null, parse.sm.stringArrays.get("sarr3").get(2));
		            org.junit.Assert.assertEquals("\"World\"", parse.sm.stringArrays.get("sarr4").get(2));
		            org.junit.Assert.assertEquals(null, parse.sm.boolArrays.get("barr3").get(0));
		            org.junit.Assert.assertEquals(true, parse.sm.boolArrays.get("barr4").get(0));
		            org.junit.Assert.assertEquals(100, parse.sm.intArrays.get("temp3").get(1).intValue());
		            org.junit.Assert.assertEquals(3.14, parse.sm.floatArrays.get("farr3").get(7).doubleValue(), .0001);
		            org.junit.Assert.assertEquals("\"insertedastring\"", parse.sm.stringArrays.get("sarr3").get(8));
		            org.junit.Assert.assertEquals(false, parse.sm.boolArrays.get("barr3").get(2));
		            org.junit.Assert.assertEquals(4, parse.sm.intArrays.get("temp").get(2).intValue());
		            org.junit.Assert.assertEquals(4.5, parse.sm.floatArrays.get("farr").get(3).doubleValue(), .0001);
		            org.junit.Assert.assertEquals("\"sfsss\"", parse.sm.stringArrays.get("sarr").get(3));
		            org.junit.Assert.assertEquals(true, parse.sm.boolArrays.get("barr").get(2));
		            //org.junit.Assert.assertEquals(7, parse.sm.getValue("x").intVal);		            
		        }
		        catch (Exception e)
		        {
		        	e.printStackTrace();
		            org.junit.Assert.fail("problem occured");
		        }	
		}
		
		@Test
		public void NumericOperatorTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            //Scanner scan = new Scanner("\\workspace\\HavaBol\\src\\InputFiles\\PrimitiveDatatypesTest.txt", symbolTable);
		            Scanner scan = new Scanner("src/InputFiles/NumericOperatorTest.txt", symbolTable);
		            Parser parse = new Parser(scan);
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("x").intVal);
		            org.junit.Assert.assertEquals(44, parse.sm.getValue("x2").intVal);
		            org.junit.Assert.assertEquals(1, parse.sm.getValue("y").intVal);
		            org.junit.Assert.assertEquals(40, parse.sm.getValue("y2").intVal);
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("z").intVal);
		            org.junit.Assert.assertEquals(32, parse.sm.getValue("z2").intVal);
		            org.junit.Assert.assertEquals(2, parse.sm.getValue("k").intVal);
		            org.junit.Assert.assertEquals(15, parse.sm.getValue("k2").intVal);
		            org.junit.Assert.assertEquals(9, parse.sm.getValue("l").intVal);
		            org.junit.Assert.assertEquals(256, parse.sm.getValue("l2").intVal);		            
		        
		            org.junit.Assert.assertEquals(6.0, parse.sm.getValue("f").doubleVal, .001);
		            org.junit.Assert.assertEquals(16.23, parse.sm.getValue("f2").doubleVal, .001);
		            org.junit.Assert.assertEquals(52.22, parse.sm.getValue("f3").doubleVal, .001);
		            org.junit.Assert.assertEquals(10372.8, parse.sm.getValue("f4").doubleVal, .001);
		            org.junit.Assert.assertEquals(2.0, parse.sm.getValue("f5").doubleVal, .001);
		            org.junit.Assert.assertEquals(65536, parse.sm.getValue("f6").doubleVal, .001);
		            
		            org.junit.Assert.assertEquals(5, parse.sm.getValue("sumx").intVal, .001);
		            org.junit.Assert.assertEquals(3.0, parse.sm.getValue("sumy").doubleVal, .001);
		            org.junit.Assert.assertEquals(140, parse.sm.getValue("lastsum").intVal, .001);
		            org.junit.Assert.assertEquals(-4, parse.sm.getValue("negx").intVal);
		            org.junit.Assert.assertEquals(-44, parse.sm.getValue("negx2").intVal);
		            org.junit.Assert.assertEquals(3.14, parse.sm.getValue("newpi").doubleVal, .001);
		            
		            org.junit.Assert.assertEquals("\"Hello World\"", parse.sm.getValue("s2").strVal);
		            //org.junit.Assert.assertEquals(3.14, parse.sm.getValue("newpi").doubleVal, .001);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            org.junit.Assert.fail("Problem encountered");
		        }	
		}
		
		@Test
		public void ConditionalOperatorTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            Scanner scan = new Scanner("src/InputFiles/ConditionalOperatorTest.txt", symbolTable);
		            Parser parse = new Parser(scan);
		           
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b2").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b3").boolVal);
		            org.junit.Assert.assertEquals(false, parse.sm.getValue("b4").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b5").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b6").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b7").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("b8").boolVal);
		            org.junit.Assert.assertEquals(false, parse.sm.getValue("b9").boolVal);
		            org.junit.Assert.assertEquals(false, parse.sm.getValue("b10").boolVal);
		            org.junit.Assert.assertEquals(2.22, parse.sm.getValue("f").doubleVal, .001);
		            org.junit.Assert.assertEquals("\"equal they are\"", parse.sm.getValue("eq").strVal);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            org.junit.Assert.fail("Problem encountered");
		        }	
		}
		
		@Test
		public void p4ExprTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            Scanner scan = new Scanner("src/InputFiles/p4Expr.txt", symbolTable);
		            Parser parse = new Parser(scan);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            org.junit.Assert.fail("Problem encountered");
		        }	
		}
		
		@Test
		public void p4ArrayTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			try
			{	            
				Scanner scan = new Scanner("src/InputFiles/p4Array.txt", symbolTable);
				Parser parse = new Parser(scan);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				org.junit.Assert.fail("Problem encountered");
			}	
		}
		
		@Test
		public void p4StringTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			try
			{	            
				Scanner scan = new Scanner("src/InputFiles/p4String.txt", symbolTable);
				Parser parse = new Parser(scan);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				org.junit.Assert.fail("Problem encountered");
			}	
		}
		
		@Test
		public void p4FuncTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			try
			{	            
				Scanner scan = new Scanner("src/InputFiles/p4Func.txt", symbolTable);
				Parser parse = new Parser(scan);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				org.junit.Assert.fail("Problem encountered");
			}	
		}
		
		
		
		@Test
		public void BuiltInFunctionsTest()
		{
			SymbolTable symbolTable = new SymbolTable();
			 try
		        {
		            // Print a column heading 
		            System.out.printf("%-11s %-12s %s\n"
		                    , "primClassif"
		                    , "subClassif"
		                    , "tokenStr");
		            
		            Scanner scan = new Scanner("src/InputFiles/BuiltInFunctionTest.txt", symbolTable);
		            Parser parse = new Parser(scan);
		            
		            org.junit.Assert.assertEquals(5, parse.sm.getValue("x").intVal);
		            org.junit.Assert.assertEquals(5, parse.sm.getValue("x2").intVal);
		            org.junit.Assert.assertEquals(25, parse.sm.getValue("x3").intVal);
		            org.junit.Assert.assertEquals(25, parse.sm.getValue("x4").intVal);
		            org.junit.Assert.assertEquals(11, parse.sm.getValue("x5").intVal);
		            
		            org.junit.Assert.assertEquals(false, parse.sm.getValue("b").boolVal);
		            org.junit.Assert.assertEquals(true, parse.sm.getValue("c").boolVal);
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("elem").intVal);
		            org.junit.Assert.assertEquals(5, parse.sm.getValue("elem2").intVal);
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("felem").intVal);
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("felem2").intVal);
		            org.junit.Assert.assertEquals(5, parse.sm.getValue("selem").intVal);
		            org.junit.Assert.assertEquals(3, parse.sm.getValue("selem2").intVal);		            
		            org.junit.Assert.assertEquals(10, parse.sm.getValue("smax").intVal);	            
		            org.junit.Assert.assertEquals(4, parse.sm.getValue("fmax").intVal);
		        }
		        catch (Exception e)
		        {
		            e.printStackTrace();
		            org.junit.Assert.fail("Problem encountered");
		        }	
		}
	
	//@Test
	public void UserDefinedFunctionsTest()
	{
		SymbolTable symbolTable = new SymbolTable();
		try
		{	            
			Scanner scan = new Scanner("src/InputFiles/UserDefinedFunctionsTest.txt", symbolTable);
			Parser parse = new Parser(scan);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			org.junit.Assert.fail("Problem encountered");
		}	
	}
}
