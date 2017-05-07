package havabol;

import java.util.ArrayList;
import java.util.HashMap;

public class STFunction extends STEntry 
{
	public int returnType; //Int, Float, String, Bool, Void
	public int definedBy; //user, builtin
	public int numArgs; //the # of args, varargs is for variable length
	public ArrayList<String> parmList; //reference to an ArrayList of formal paramaters
	public SymbolTable symbolTable; //reference to the functions symbol table if it is a userdefined function
	public StorageManager sm;
	public StorageManager parentsm;
	public Token startSpot = null;
	public Token endSpot = null;
	
	public STFunction(String sym, int prim, int ret, int definedby, int numargs)
	{
		super(sym, prim);
		returnType = ret;
		definedBy = definedby;
		numArgs = numargs;
		parmList = new ArrayList<String>();
		sm = new StorageManager();
		
		if( definedby == Token.USER )
		{
			symbolTable = new SymbolTable();
		}
	}

	public void setArgsNum() 
	{
		this.numArgs = parmList.size();
	}

	public void addParmToSM(int argNum, ResultValue RV, String arrayName) 
	{	//parm will look something like *Float f or Float f or 
		boolean byRef = false;
		if( parmList.get(argNum).contains("[]") )
		{	//ARRAY
			//String parmName = parmList.get(argNum).split(" ");
			if( parmList.get(argNum).contains("*") )
			{	//BY REF
				byRef = true;
				sm.putVariable(parmList.get(argNum).split(" ")[2], RV);
			}
			else
			{	//By VAL
				sm.putVariable(parmList.get(argNum).split(" ")[2], new ResultValue(RV, RV.type));
			}
			
			switch( parentsm.getValue(arrayName).type )
			{
				case Token.INTARRAY:
					if( byRef )
					{
						sm.intArrays.put(parmList.get(argNum).split(" ")[2], parentsm.intArrays.get(arrayName));
					}
					break;
				case Token.FLOATARRAY:
					if( byRef )
					{
						sm.floatArrays.put(parmList.get(argNum).split(" ")[2], parentsm.floatArrays.get(arrayName));						
					}
					else
					{
						sm.floatArrays.put(parmList.get(argNum).split(" ")[2], new ArrayList<Double>(parentsm.floatArrays.get(arrayName)));
					}
					break;
				case Token.STRINGARRAY:
					if( byRef )
					{
						sm.stringArrays.put(parmList.get(argNum).split(" ")[2], parentsm.stringArrays.get(arrayName));						
					}
					else
					{
						sm.stringArrays.put(parmList.get(argNum).split(" ")[2], new ArrayList<String>(parentsm.stringArrays.get(arrayName)));
					}
					break;
				case Token.BOOLEANARRAY:
					if( byRef )
					{
						sm.boolArrays.put(parmList.get(argNum).split(" ")[2], parentsm.boolArrays.get(arrayName));						
					}
					else
					{
						sm.boolArrays.put(parmList.get(argNum).split(" ")[2], new ArrayList<Boolean>(parentsm.boolArrays.get(arrayName)));
					}
					break;
			}
		}
		else
		{	//VARIABLE
			if( parmList.get(argNum).contains("*") )
			{	//BY REF
				sm.putVariable(parmList.get(argNum).split(" ")[1], RV);
			}
			else
			{	//By VAL
				sm.putVariable(parmList.get(argNum).split(" ")[1], new ResultValue(RV, RV.type));
			}
		}
	}

	public ResultValue run(Parser p) throws Exception
	{
		ResultValue returnVal = null;
		p.scan.iSourceLineNr = startSpot.iSourceLineNr;
		p.scan.textCharM = p.scan.sourceLineM.get(startSpot.iSourceLineNr).toCharArray();
		p.scan.iColPos = startSpot.iColPos;
		p.scan.nextToken = this.startSpot;
		p.scan.getNext();
		p.scan.getNext();
		
		while(!p.scan.getNext().equals("enddef"))
		{
			if( !p.scan.currentToken.tokenStr.equals(";"))
				returnVal = p.Statement();
		}
		
		p.scan.iSourceLineNr = endSpot.iSourceLineNr;
		p.scan.textCharM = p.scan.sourceLineM.get(endSpot.iSourceLineNr).toCharArray();
		p.scan.iColPos = endSpot.iColPos;
		p.scan.nextToken = this.endSpot;
		p.scan.getNext();
		p.scan.getNext();
		return returnVal;
	}
}


















