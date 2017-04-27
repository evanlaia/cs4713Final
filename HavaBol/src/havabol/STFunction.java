package havabol;

import java.util.ArrayList;
import java.util.HashMap;

public class STFunction extends STEntry 
{
	int returnType; //Int, Float, String, Bool, Void
	int definedBy; //user, builtin
	int numArgs; //the # of args, varargs is for variable length
	ArrayList<String> parmList; //reference to an ArrayList of formal paramaters
	SymbolTable symbolTable; //reference to the functions symbol table if it is a userdefined function
	StorageManager sm;
	HashMap<String, Boolean> byReference;
	
	public STFunction(String sym, int prim, int ret, int definedby, int numargs)
	{
		super(sym, prim);
		returnType = ret;
		definedBy = definedby;
		numArgs = numargs;
		parmList = new ArrayList<String>();
		sm = new StorageManager();
		byReference = new HashMap<String, Boolean>();
		
		if( definedby == Token.USER )
		{
			symbolTable = new SymbolTable();
		}
	}
}
