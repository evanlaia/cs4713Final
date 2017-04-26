package havabol;

import java.util.ArrayList;

public class STFunction extends STEntry 
{
	int returnType; //Int, Float, String, Bool, Void
	int definedBy; //user, builtin
	int numArgs; //the # of args, varargs is for variable length
	ArrayList<String> parmList; //reference to an ArrayList of formal paramaters
	SymbolTable symbolTable; //reference to the functions symbol table if it is a userdefined function
	
	public STFunction(String sym, int prim, int ret, int definedby, int numargs)
	{
		super(sym, prim);
		returnType = ret;
		definedBy = definedby;
		numArgs = numargs;
		parmList = new ArrayList<String>();
		
		if( definedby == Token.USER )
		{
			symbolTable = new SymbolTable();
		}
	}
	
}
