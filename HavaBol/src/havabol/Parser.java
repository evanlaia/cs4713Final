package havabol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Parser 
{

	Scanner scan;
	private StorageManager sm;	//used for storing variables and their values
	private StorageManager tmpsm;
	boolean bShowExpr = false;
	public boolean bShowAssign = false;
	HashMap<String, Token> functionSpots;
	Token restorePoint;
	
	public Parser(Scanner scan) throws Exception
	{
		sm = new StorageManager();
		functionSpots = new HashMap<String, Token>();
		this.scan = scan;
		restorePoint = new Token();
		//scan.getNext();
		runProgram();
	}

	private void runProgram() throws Exception 
	{
		int DEBUG = 1;
		do
		{
			if( scan.iSourceLineNr == 9 )
			{
				DEBUG++;//stop here
			}
			scan.getNext();//consume the ;
			Statement();
		}while(scan.currentToken.primClassif != Token.EOF);
	}

	public ResultValue Statement() throws Exception 
	{
		ResultValue _RV = null;
		
		switch(scan.currentToken.primClassif)
		{
			case Token.CONTROL:
				if( scan.currentToken.subClassif == Token.FLOW )
				{
					if( scan.currentToken.tokenStr.equals("while") )
					{
						//WHILE STMT CODE
						whileStmt();
					}	
					else if( scan.currentToken.tokenStr.equals("if") )
					{
						//IFSTMT CODE HERE
						ifStmtStart();
					}
					else
					{
						forLoopInt();
					}
					break;
				}
				else if( scan.currentToken.subClassif == Token.DECLARE )
				{	//VARIABLE DECLARATIONS
					_RV = new ResultValue();
					switch(scan.currentToken.tokenStr)
					{
						case "Int":
							_RV.type = Token.INTEGER;
							break;
						case "Float":
							_RV.type = Token.FLOAT;
							break;
						case "Bool":
							_RV.type = Token.BOOLEAN;
							break;
						case "String":
							_RV.type = Token.STRING;
							break;
						case "Date":
							_RV.type = Token.DATE;
							String vName = scan.nextToken.tokenStr;
							//sm.putVariable(scan.nextToken.tokenStr, _RV);
							scan.getNext();
							scan.getNext();//consume the variable
							scan.getNext();//consume the '='
							if(!scan.currentToken.tokenStr.matches("\"(\\d{4}-?\\d{2}-?\\d{2})\"")) {
								handleException("Date error: Expected format, 'XXXX-XX-XX'");
							} else {
								_RV.strVal = scan.currentToken.tokenStr;
								_RV.strVal = _RV.strVal.substring(1, _RV.strVal.length() - 1);
								
								int year = (int)NumericConversion.convert(_RV.strVal.substring(0,4));
								int month = (int)NumericConversion.convert(_RV.strVal.substring(5,7));
								int day = (int)NumericConversion.convert(_RV.strVal.substring(8,10));
								Date date = new Date(year, month, day);
								_RV.dateVal = date;
//								SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
//								Calendar calendar = new GregorianCalendar(2013, 10, 28);
								sm.putVariable(vName, _RV);
							}
							scan.getNext();
							return _RV;
						default:
							//ERROR INVALID DATATYPE
							break;
					}
					sm.putVariable(scan.nextToken.tokenStr, _RV);
					scan.getNext();
					Statement();
					return _RV;
				}
			case Token.OPERAND:
				if( scan.currentToken.tokenStr.equals("debug") )
				{
					scan.getNext();//consume debug;
					switch(scan.currentToken.tokenStr)
					{
					case "Expr":
						if( scan.nextToken.tokenStr.equals("on") )
							this.bShowExpr = true;
						else
							this.bShowExpr = false;
						break;
					case "Token":
						if( scan.nextToken.tokenStr.equals("on") )
							scan.bShowToken = true;
						else
							scan.bShowToken = false;
						break;
					case "Assign":
						if( scan.nextToken.tokenStr.equals("on") )
							this.bShowAssign = true;
						else
							this.bShowAssign = false;
						break;
					}
					scan.getNext();//consume expr
				}
				else if( scan.currentToken.subClassif == Token.IDENTIFIER )
				{	//VARIABLE INITIALIZATION
					String var = scan.currentToken.tokenStr;
					if( !scan.nextToken.tokenStr.equals(";") )// ';' means variable was just declared
					{
						if( scan.nextToken.tokenStr.equals("[") )
						{//ARRAY INITIALIZATION
							if( scan.currentToken.tokenStr.equals("strM") )//DELETE THIS
							{
								sm.getClass();
							}
							switch(sm.getValue(scan.currentToken.tokenStr).type)
							{
								case Token.INTEGER:
									_RV = new ResultValue();
									_RV.type = Token.INTARRAY;
									String arrayName = scan.currentToken.tokenStr;
									scan.getNext();//consume the arrayName
									scan.getNext();//consume the first '['
									if( scan.currentToken.tokenStr.equals("]") )
									{	//arrayName[] = valueList CASE
										scan.getNext();//consume ']'
										scan.getNext();//consume '='
										sm.initializeIntArray(arrayName, -1);
										do{
											sm.insertInteger(Operand().intVal, arrayName, true);
											if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
										}
										while( !scan.nextToken.tokenStr.equals(";") );
										sm.insertInteger(Operand().intVal, arrayName, true);
									}
									else
									{	//arrayName[MaxElem] CASE
										int maxElem = Integer.parseInt(expr().strVal);
										scan.getNext();//consume maxElem
										sm.initializeIntArray(arrayName, maxElem);
										//scan.getNext();//consume ']'
										if( !scan.nextToken.tokenStr.equals(";") )
										{	//array was initialized
											scan.getNext();//consume ']'
											scan.getNext(); //consume the '='
											do{
												sm.insertInteger(Operand().intVal, arrayName, false);
												if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
											}
											while( !scan.nextToken.tokenStr.equals(";") );
											sm.insertInteger(Operand().intVal, arrayName, false);
										}
										else
										{	//array was only declared
											//sm.initializeIntArray(arrayName, maxElem);
										}
									}
									sm.putVariable(arrayName, _RV);
									break;
								case Token.FLOAT: //FLOAT ARRAY CODE HERE
									_RV = new ResultValue();
									_RV.type = Token.FLOATARRAY;
									String farrayName = scan.currentToken.tokenStr;
									scan.getNext();//consume the arrayName
									scan.getNext();//consume the first '['
									if( scan.currentToken.tokenStr.equals("]") )
									{	//arrayName[] = valueList CASE
										scan.getNext();//consume ']'
										scan.getNext();//consume '='
										sm.initializeFloatArray(farrayName, -1);
										do{
											sm.insertFloat(Operand().doubleVal, farrayName, true);
											if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
										}
										while( !scan.nextToken.tokenStr.equals(";") );
										sm.insertFloat(Operand().doubleVal, farrayName, true);
									}
									else
									{	//arrayName[MaxElem] CASE
										int maxElem = Integer.parseInt(expr().strVal);
										scan.getNext();//consume maxElem
										sm.initializeFloatArray(farrayName, maxElem);
										//scan.getNext();//consume ']'
										if( !scan.nextToken.tokenStr.equals(";") )
										{	//array was initialized
											scan.getNext();//consume ']'
											scan.getNext(); //consume the '='
											do{
												sm.insertFloat(Operand().doubleVal, farrayName, false);
												if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
											}
											while( !scan.nextToken.tokenStr.equals(";") );
											sm.insertFloat(Operand().doubleVal, farrayName, false);
										}
										else
										{	//array was only declared
											//sm.initializeIntArray(arrayName, maxElem);
										}
									}
									sm.putVariable(farrayName, _RV);
									break;
								case Token.STRING://STRING ARRAY CODE HERE
									_RV = new ResultValue();
									_RV.type = Token.STRINGARRAY;
									String sarrayName = scan.currentToken.tokenStr;
									scan.getNext();//consume the arrayName
									scan.getNext();//consume the first '['
									if( scan.currentToken.tokenStr.equals("]") )
									{	//arrayName[] = valueList CASE
										scan.getNext();//consume ']'
										scan.getNext();//consume '='
										sm.initializeStringArray(sarrayName, -1);
										do{
											sm.insertString(Operand().strVal, sarrayName, true);
											if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
										}
										while( !scan.nextToken.tokenStr.equals(";") );
										sm.insertString(Operand().strVal, sarrayName, true);
									}
									else
									{	//arrayName[MaxElem] CASE
										int maxElem = Integer.parseInt(expr().strVal);
										scan.getNext();//consume maxElem
										sm.initializeStringArray(sarrayName, maxElem);
										//scan.getNext();//consume ']'
										if( !scan.nextToken.tokenStr.equals(";") )
										{	//array was initialized
											scan.getNext();//consume ']'
											scan.getNext(); //consume the '='
											do{
												sm.insertString(Operand().strVal, sarrayName, false);
												if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
											}
											while( !scan.nextToken.tokenStr.equals(";") );
											sm.insertString(Operand().strVal, sarrayName, false);
										}
										else
										{	//array was only declared
											//sm.initializeIntArray(arrayName, maxElem);
										}
									}
									sm.putVariable(sarrayName, _RV);
									break;
								case Token.BOOLEAN: //BOOLEAN ARRAY CODE HERE
									_RV = new ResultValue();
									_RV.type = Token.BOOLEANARRAY;
									String barrayName = scan.currentToken.tokenStr;
									scan.getNext();//consume the arrayName
									scan.getNext();//consume the first '['
									if( scan.currentToken.tokenStr.equals("]") )
									{	//arrayName[] = valueList CASE
										scan.getNext();//consume ']'
										scan.getNext();//consume '='
										sm.initializeBoolArray(barrayName, -1);
										do{
											sm.insertBoolean(Operand().boolVal, barrayName, true);
											if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
										}
										while( !scan.nextToken.tokenStr.equals(";") );
										sm.insertBoolean(Operand().boolVal, barrayName, true);
									}
									else
									{	//barrayName[MaxElem] CASE
										int maxElem = Integer.parseInt(expr().strVal);
										scan.getNext();//consume maxElem
										sm.initializeBoolArray(barrayName, maxElem);
										//scan.getNext();//consume ']'
										if( !scan.nextToken.tokenStr.equals(";") )
										{	//array was initialized
											scan.getNext();//consume ']'
											scan.getNext(); //consume the '='
											do{
												sm.insertBoolean(Operand().boolVal, barrayName, false);
												if( !scan.nextToken.tokenStr.equals(";") ){scan.getNext();scan.getNext();}
											}
											while( !scan.nextToken.tokenStr.equals(";") );
											sm.insertBoolean(Operand().boolVal, barrayName, false);
										}
										else
										{	//array was only declared
											//sm.initializeIntArray(arrayName, maxElem);
										}
									}
									sm.putVariable(barrayName, _RV);
									break;
							}
						}
						else
						{
							_RV = sm.getValue(scan.currentToken.tokenStr);
							switch(_RV.type)
							{
							case Token.INTARRAY:
								String arrayName = scan.currentToken.tokenStr;
								int len = sm.intArrays.get(arrayName).size();
								scan.getNext();//consume the left array
								scan.getNext();//consume the '='
								int rlen = sm.intArrays.get(scan.currentToken.tokenStr).size(); //?? NEED TO PROBABLY GET THE ARRAY SIZE THROUGH THE STORAGE MANGAGER
								if( len > rlen )
								{
									for(int i=0; i < rlen; i++)
									{
										sm.insertInteger(sm.intArrays.get(scan.currentToken.tokenStr).get(i).intValue(), arrayName, false);
									}
								}
								else //rlen > len or ==
								{
									for(int i=0; i < len; i++)
									{
										sm.insertInteger(sm.intArrays.get(scan.currentToken.tokenStr).get(i).intValue(), arrayName, false);
									}
								}
								scan.getNext();//consume the right array
								return null;
								//break;
							}
							if( _RV == null ){}//ERROR UNDECLARED IDENTIFIER
							scan.getNext();//consume the variable
							scan.getNext();//consume the '='
							_RV.assign(expr());
							if( this.bShowAssign )
							{
								System.out.println("\nDEBUG: " + var + " = " + _RV.strVal);
							}
						}
					}
				}
				else
				{
					//SYNTAX ERROR???
				}
				break;
			case Token.FUNCTION:
				if( scan.currentToken.subClassif == Token.BUILTIN )
				{
					STFunction func = (STFunction) scan.symbolTable.getSymbol(scan.currentToken.tokenStr);
					switch(scan.currentToken.tokenStr)
					{	//BUILT-IN FUNCTIONS HERE
						case "print":
							invokePrintFunction(func);
							break;
						case "LENGTH":
							invokeLENGTHFunction(func);
							break;
						case "MAXLENGTH":
							break;
						case "SPACES":
							this.invokeSPACESFunction(null);
							break;
						case "ELEM":
							scan.getNext();//consume ELEM
							scan.getNext();//consume '('
							invokeELEMFunction();
							break;
						case "MAXELEM":
							this.invokeMAXELEMFunction();//invokeMAXELEM();
							break;
					}
				}
				else if( scan.currentToken.subClassif == Token.USER)
				{	//USER DEFINED FUNCTIONS CODE HERE
					if( functionSpots.get(scan.currentToken.tokenStr) == null )
					{
						buildUserDefinedFunction();
					}
					else
					{
						restorePoint.iSourceLineNr = scan.currentToken.iSourceLineNr + 1;
						restorePoint.iColPos = 0;
						invokeUserFunction();
						return null;
					}
				}
				break;
			case Token.SEPARATOR:
				if( scan.currentToken.tokenStr.equals("[") && sm.getValue(scan.nextToken.tokenStr).type == Token.STRING )
				{	//String variable insert
					scan.getNext();//consume the '['
					_RV = sm.getValue(scan.currentToken.tokenStr);
					scan.getNext();//consume the variable
					int subscript = expr().intVal;
					scan.getNext();//consume the subscript expr()
					scan.getNext();//consume the ']'
					scan.getNext();//consume the '='
					//String value = expr().strVal;
					_RV.strVal = _RV.strVal.substring(0, subscript+1) + expr().strVal.substring(1, expr().strVal.length()-1) + _RV.strVal.substring(subscript+2, _RV.strVal.length());
				}
				else if( scan.currentToken.tokenStr.equals("[") )
				{ //ARRAY STATEMENT
					scan.getNext();//consume the '['
					String arrayName = scan.currentToken.tokenStr;
					scan.getNext();//consume the arrayName
					/*switch(sm.getValue(arrayName).type)
					{
						case Token.INTARRAY:
							{int subscript = expr().intVal;
							scan.getNext();//consume the subscript
							scan.getNext();//consume the ']'
							if( scan.currentToken.tokenStr.equals("=") )
							{
								scan.getNext();//consume the '='
								_RV = sm.getValue(arrayName);
								_RV.assign(expr());
								sm.addToArray(_RV, arrayName, subscript);
							}
							else
							{//ARRAY STATEMENT ERROR
								
							}}
							break;
						case Token.FLOATARRAY:
							int subscript = expr().intVal;
							scan.getNext();//consume the subscript
							scan.getNext();//consume the ']'
							if( scan.currentToken.tokenStr.equals("=") )
							{
								scan.getNext();//consume the '='
								_RV = sm.getValue(arrayName);
								_RV.assign(expr());
								sm.addToArray(_RV, arrayName, subscript);
							}
							else
							{//ARRAY STATEMENT ERROR
								
							}
							break;
						case Token.STRINGARRAY:
							break;
						case Token.BOOLEANARRAY:
							break;
					}*/
					int subscript = expr().intVal;
					scan.getNext();//consume the subscript
					scan.getNext();//consume the ']'
					if( scan.currentToken.tokenStr.equals("=") )
					{
						scan.getNext();//consume the '='
						_RV = sm.getValue(arrayName);
						_RV.assign(expr());
						sm.addToArray(_RV, arrayName, subscript);
					}
					else
					{//ARRAY STATEMENT ERROR
						
					}
				}
				break;
		}
		if( _RV == null ){}//ERROR POTENTIALLY BAD INPUT??
		scan.getNext();
		return _RV;
	}
	
	private void invokeUserFunction() throws Exception
	{
		String functionName = scan.currentToken.tokenStr;
		ArrayList<String> passedInparms = new ArrayList<String>();
		ArrayList<ResultValue> parmResults = new ArrayList<ResultValue>();
		STFunction func = (STFunction) scan.symbolTable.getSymbol(scan.currentToken.tokenStr);
		scan.getNext();//consume the function name
		scan.getNext();//consume the '('
		
		
		for( int i=0; i < func.numArgs-1; i++)
		{//Get the names of the passed in parameters and get values: can be functions, expr(), etc. 
			passedInparms.add(scan.currentToken.tokenStr);
			parmResults.add(expr());
			while( !scan.currentToken.tokenStr.equals(",") ){ scan.getNext(); }//consume the parm name
			scan.getNext();//consume the ','
		}
		passedInparms.add(scan.currentToken.tokenStr);
		parmResults.add(expr());
		
		Token reset = functionSpots.get(functionName);
		scan.iColPos = reset.iColPos;
		scan.iSourceLineNr = reset.iSourceLineNr;
		scan.textCharM = scan.sourceLineM.get(scan.iSourceLineNr).toCharArray();
		scan.getNext();
		scan.getNext();
		scan.getNext();//consume the 'def'
		scan.getNext();//consume the return type
		scan.getNext();//consume function name
		scan.getNext();//consume the '(
		
		//swap the storage manager
		tmpsm = sm;
		sm = func.sm;
		
		boolean isref = false;
		for( int i=0; i < func.numArgs; i++ )
		{
			if( scan.currentToken.tokenStr.equals("Ref") ){
				scan.getNext();//consume the 'Ref'
				isref = true;
			}
			switch(scan.currentToken.tokenStr)
			{
				case "Float":
					if( scan.nextToken.tokenStr.equals("[") )
					{	//parm is an array
						scan.getNext();//consume datatype
						scan.getNext();//consume [
						scan.getNext();//consume ]
						if( isref )
						{	//PassedByReference
							sm.copyFloatArray(scan.currentToken.tokenStr, this.tmpsm.floatArrays.get(passedInparms.get(i)));
							sm.putVariable(scan.currentToken.tokenStr, tmpsm.getValue(passedInparms.get(i)));
						}
					}
					else
					{	//parm is not array
						scan.getNext();//consume datatype
						if( isref )
						{
							sm.putVariable(scan.currentToken.tokenStr, tmpsm.getValue(passedInparms.get(i)));
						}
						else
						{
							sm.putVariable(scan.currentToken.tokenStr, parmResults.get(i));
						}
					}
					break;
				case "Int":
					if( scan.nextToken.tokenStr.equals("[") )
					{	//parm is an array
						scan.getNext();//consume datatype
						scan.getNext();//consume [
						scan.getNext();//consume ]
						if( isref )
						{	//PassedByReference
							sm.copyIntArray(scan.currentToken.tokenStr, this.tmpsm.intArrays.get(passedInparms.get(i)));
							sm.putVariable(scan.currentToken.tokenStr, tmpsm.getValue(passedInparms.get(i)));
						}
					}
					else
					{	//parm is not array
						scan.getNext();//consume datatype
						if( isref )
						{	//passing by reference, need same copy of RV
							sm.putVariable(scan.currentToken.tokenStr, tmpsm.getValue(passedInparms.get(i)));
						}
						else
						{	//Passing by value, need a new copy of RV
							ResultValue rv = new ResultValue();
							rv.type = parmResults.get(i).type;
							rv.assign(parmResults.get(i));
							sm.putVariable(scan.currentToken.tokenStr, rv);
						}
					}
					break;
			
			}
			isref = false;
			scan.getNext();//consume the variable name
			scan.getNext();//consume the ','
		}
		
		do
		{
			scan.getNext();//consume the ;
			Statement();
		}while(!scan.nextToken.tokenStr.equals("enddef"));
	}

	private void buildUserDefinedFunction() throws Exception 
	{
		//StorageManager sm2 = new StorageManager();
		Token t = new Token();
		t.iSourceLineNr = scan.currentToken.iSourceLineNr;
		t.iColPos = scan.currentToken.iColPos;
		scan.getNext();//consume the 'def'
		STFunction userfunc = null;
		switch(scan.currentToken.tokenStr)
		{ 				//public STFunction(String sym, int prim, int ret, int definedby, int numargs)
			case "Void":
				userfunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.VOID, Token.USER, 0);//numargs 0 for now
				break;
		}
		scan.getNext();//consume the return type
		functionSpots.put(scan.currentToken.tokenStr, t);
		scan.symbolTable.putSymbol(scan.currentToken.tokenStr, userfunc);
		scan.getNext();//consume the function name
		int numOfArgs = 0;
		while( !scan.currentToken.tokenStr.equals(":") )
		{
			scan.getNext();
			if( scan.currentToken.tokenStr.equals(",") )
			{
				numOfArgs++;
			}
		}
		userfunc.numArgs = numOfArgs+1;
		
		while( !scan.currentToken.tokenStr.equals("enddef") )//CHANGE THIS FOR NESTED FUNCTIONS
		{
			scan.getNext();
		}
		
	}

	private void forLoopInt() throws Exception
	{	//for iVal in iCM by 2:
	    //	print("\t", iVal);
		//endfor;
		//FOR ITEM IN ARRAY CASE FIRST
		scan.getNext();//consume the for
		if( scan.nextToken.tokenStr.equals("=") ){forLoop2();return;}
		Token resetPos = new Token();
		resetPos.iColPos = scan.iColPos;
		resetPos.iSourceLineNr = scan.iSourceLineNr;
		String ctString = scan.currentToken.tokenStr;
		scan.getNext();//consume the counter (CHECK IF COUNTER IS IN STORAGE MANGER)
		int ct = 0;//HAVE TO CHANGE THIS LATER WHEN INIT COUNTER TO SOMETHING OTHER THAN ZERO
		scan.getNext();//consume the 'in'
		ResultValue rv = new ResultValue();//to put counter in sm
		
		if( !scan.nextToken.tokenStr.equals("by") ){
		switch(sm.getValue(scan.currentToken.tokenStr).type)
		{
			case Token.INTARRAY:
				rv.type = Token.INTEGER;
				int n = sm.getIntArraySize(scan.currentToken.tokenStr);
				while(ct < n)
				{
					int val = sm.intArrays.get(scan.currentToken.tokenStr).get(ct);
					rv.setValues(Integer.toString(val));
					sm.putVariable(ctString, rv);
					scan.getNext();//consume the arrayName
					forBody();
					if( ct+1 < n )
					{
						scan.iSourceLineNr = resetPos.iSourceLineNr;
						scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
						scan.iColPos = resetPos.iColPos;
						scan.getNext();
						scan.getNext();
						sm.deleteVariable(ctString);
					}
					ct++;
				}
				break;
			case Token.STRINGARRAY:
                rv.type = Token.STRING;
                int len = sm.getStringArraySize(scan.currentToken.tokenStr);
                while(ct < len)
                {
                    //String val = "" + sm.getValue(scan.currentToken.tokenStr).strVal.substring(1, len-1).charAt(ct);
                	String val = sm.stringArrays.get(scan.currentToken.tokenStr).get(ct);
                    rv.setValues(val);
                    sm.putVariable(ctString, rv);
                    scan.getNext();//consume the arrayName
                    forBody();
                    if( ct+1 < len )
                    {
                        scan.iSourceLineNr = resetPos.iSourceLineNr;
                        scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
                        scan.iColPos = resetPos.iColPos;
                        scan.getNext();
                        scan.getNext();
                        sm.deleteVariable(ctString);
                    }
                    ct++;
                }
				break;
			case Token.FLOATARRAY:
				rv.type = Token.FLOAT;
				int n2 = sm.getFloatArraySize(scan.currentToken.tokenStr);
				while(ct < n2)
				{
					double val = sm.floatArrays.get(scan.currentToken.tokenStr).get(ct);
					rv.setValues(Double.toString(val));
					sm.putVariable(ctString, rv);
					scan.getNext();//consume the arrayName
					forBody();
					if( ct+1 < n2 )
					{
						scan.iSourceLineNr = resetPos.iSourceLineNr;
						scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
						scan.iColPos = resetPos.iColPos;
						scan.getNext();
						scan.getNext();
						sm.deleteVariable(ctString);
					}
					ct++;
				}
				break;
		}
		}
		else
		{
			int inc = 0;
			switch(sm.getValue(scan.currentToken.tokenStr).type)
			{
				case Token.INTARRAY:
					rv.type = Token.INTEGER;
					int n = sm.getIntArraySize(scan.currentToken.tokenStr);
					while(ct < n)
					{
						int val = sm.intArrays.get(scan.currentToken.tokenStr).get(ct);
						rv.setValues(Integer.toString(val));
						sm.putVariable(ctString, rv);
						scan.getNext();//consume the arrayName
						scan.getNext();//consume the 'by'
						if( inc == 0 )
						{
							inc = Integer.parseInt(scan.currentToken.tokenStr);
							scan.getNext();//consume the increment value
						}
						forBody();
						if( ct+1 < n )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							scan.getNext();
							scan.getNext();
							sm.deleteVariable(ctString);
						}
						ct += inc;
					}
					break;
				case Token.FLOATARRAY:
					rv.type = Token.FLOAT;
					int n2 = sm.getFloatArraySize(scan.currentToken.tokenStr);
					while(ct < n2)
					{
						double val = sm.floatArrays.get(scan.currentToken.tokenStr).get(ct);
						rv.setValues(Double.toString(val));
						sm.putVariable(ctString, rv);
						scan.getNext();//consume the arrayName
						scan.getNext();//consume the 'by'
						if( inc == 0 )
						{
							inc = Integer.parseInt(scan.currentToken.tokenStr);
							scan.getNext();//consume the increment value
						}
						forBody();
						if( ct+1 < n2 )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							scan.getNext();
							scan.getNext();
							sm.deleteVariable(ctString);
						}
						ct += inc;
					}
					break;
			}
		}
	}
	
	public void forLoop2() throws Exception
	{	// i is init
		//for i = 0 to ELEM(iCM):
		    //print("\t", [iCM i]);
		//scan.getNext();//consume the for
		String ctStr = scan.currentToken.tokenStr;
		ResultValue rv=null;
		if( sm.getValue(scan.currentToken.tokenStr) == null )
		{ //implicit declaration
			scan.getNext();//consume the var
			scan.getNext();//consume the '='
			rv = expr();
			scan.getNext();//consume the value
			sm.putVariable(ctStr, rv);
		}
		else
			rv = Statement();
		
		int ct = rv.intVal;
		scan.getNext();//consume 'to'
		Token resetPos = new Token();
		resetPos.iColPos = scan.currentToken.iColPos;
		resetPos.iSourceLineNr = scan.currentToken.iSourceLineNr;
		int n = expr().intVal;
		while( !scan.currentToken.tokenStr.equals(":") && !scan.currentToken.tokenStr.equals("by") )
			scan.getNext();//consume the 2nd expr()
		
		if( !scan.currentToken.tokenStr.equals("by") ){
		while(ct < n)
		{
			while( !scan.currentToken.tokenStr.equals(":") )
				scan.getNext();//consume the 2nd expr()

			forBody();
			if( ct+1 < n )
			{
				scan.iSourceLineNr = resetPos.iSourceLineNr;
				scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
				scan.iColPos = resetPos.iColPos;
				scan.getNext();
				scan.getNext();
			}
			ct++;
			sm.getValue(ctStr).setValues(Integer.toString(ct));
		}}
		else
		{
			scan.getNext();//consume the 'by'
			int inc = Integer.parseInt(scan.currentToken.tokenStr);
			while(ct < n)
			{
				while( !scan.currentToken.tokenStr.equals(":") )
					scan.getNext();//consume the 2nd expr()

				forBody();
				if( ct+inc < n )
				{
					scan.iSourceLineNr = resetPos.iSourceLineNr;
					scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
					scan.iColPos = resetPos.iColPos;
					scan.getNext();
					scan.getNext();
				}
				ct += inc;
				sm.getValue(ctStr).setValues(Integer.toString(ct));
			}
		}
	}
	
	public void forBody() throws Exception
	{	//we know that the forloop condition turned true so just perform statements until 'endfor'
		scan.getNext(); //consume the ':'
		do
		{
			Statement();
			scan.getNext(); //consume the ';'
		}while(!scan.currentToken.tokenStr.equals("endfor") );
	}
	
	
	
	/**
	 * Function that returns the maximum size of an array
	 */
	private int invokeMAXELEMFunction() throws Exception
	{
		int i=0;
		switch(sm.getValue(scan.currentToken.tokenStr).type) {
			case Token.INTARRAY:
				i = sm.intArrays.get(scan.currentToken.tokenStr).size();
				break;
			case Token.FLOATARRAY:
				i = sm.floatArrays.get(scan.currentToken.tokenStr).size();
				break;
			case Token.STRINGARRAY:
				i = sm.stringArrays.get(scan.currentToken.tokenStr).size();
				break;
		}
		scan.getNext();
		return i;
	}
	
	private int invokeELEMFunction() throws Exception 
	{	
		if( scan.currentToken.tokenStr.equals("ELEM") ){scan.getNext();scan.getNext();}//consume ELEM & '('
		int i=0;
		switch( sm.getValue(scan.currentToken.tokenStr).type )
		{
		case Token.INTARRAY:
			int size = sm.intArrays.get(scan.currentToken.tokenStr).size();
			while( i < size && sm.intArrays.get(scan.currentToken.tokenStr).get(i) != -10000 ){i++;}
			break;
		case Token.FLOATARRAY:
			int fsize = sm.floatArrays.get(scan.currentToken.tokenStr).size();
			while( i < fsize && sm.floatArrays.get(scan.currentToken.tokenStr).get(i) != -10000.0){i++;}
			break;
		case Token.STRINGARRAY:
            while( sm.stringArrays.get(scan.currentToken.tokenStr).get(i) != null
            && i < sm.stringArrays.get(scan.currentToken.tokenStr).size()){i++;}
            break;
		}
		
		scan.getNext();//consume the arrayname
		return i;	
	}

	private int invokeLENGTHFunction(STFunction func) throws Exception
	{//first token 
		scan.getNext();//Consume the Function Name
		scan.getNext();//Consume the '('
		while( scan.currentToken.primClassif != Token.RT_PAREN )
		{
			if( scan.currentToken.primClassif == Token.OPERAND )
			{	//LITERALS AND VARIABLES FOR LENGTH
				ResultValue RV = expr();
				String printVal = RV.strVal;
				char[] printArr;
				if( RV.type == Token.STRING)
				{
					printArr = printVal.substring(1, printVal.length()-1).toCharArray();
				}
				else
				{
					printArr = printVal.toCharArray();
				}
				scan.getNext();//DELETE THIS AND ALL BELOW AND MAKE A GLOBAL RETURN
				return printArr.length;
			}
			else if( scan.currentToken.tokenStr.equals("(") )
			{ //expr() for Length
				ResultValue RV = expr();
				String printVal = RV.strVal;
				char[] printArr;
				if( RV.type == Token.STRING)
				{
					printArr = printVal.substring(1, printVal.length()-1).toCharArray();
				}
				else
				{
					printArr = printVal.toCharArray();
				}
				scan.getNext();//DELETE THIS AND ALL BELOW AND MAKE A GLOBAL RETURN
				return printArr.length;
			}
			else if( scan.currentToken.primClassif == Token.FUNCTION )
			{	//FUNCTION ARGS FOR LENGTH
				int x;
				switch(scan.currentToken.tokenStr) {
                /*case "LENGTH":
                    x = this.invokeLENGTHFunction(null);
                    System.out.println(x);
                    break;
                case "ELEM":
                    scan.getNext();
                    scan.getNext();
                    x = this.invokeELEMFunction();
                    System.out.print(x);
                    break;
                case "MAXELEM":
                    scan.getNext();
                    scan.getNext();
                    x = this.invokeMAXELEMFunction();
                    System.out.print(x);
                    break;*/
                case "SPACES":
    				int length = invokeSPACESFunction(null).length();
    				scan.getNext();//DELETE THIS AND ALL BELOW AND MAKE A GLOBAL RETURN
    				return length;
                }
			}
			else
			{	//ERROR MAYBE???
				if( !scan.currentToken.tokenStr.equals(","))
					throw new Exception("print function: expected ')' received '" + scan.currentToken.tokenStr + "'");
			}
			scan.getNext();
		}
		return 0;
	}
	
	private String invokeSPACESFunction(STFunction func) throws Exception
	{//first token 
		scan.getNext();//Consume the Function Name
		scan.getNext();//Consume the '('
		while( scan.currentToken.primClassif != Token.RT_PAREN )
		{
			if( scan.currentToken.subClassif == Token.FUNCTION )
			{//FUNCTION ARG FOR SPACES
				
			}
			else if( scan.currentToken.primClassif == Token.OPERAND )
			{ //Variable and Constant for SPACES
				if( expr().strVal.substring(1, expr().strVal.length()-1).isEmpty() )
				{
					scan.getNext();//consume var
					//scan.getNext();//consume ')'
					return "T";
				}
				for(char c : expr().strVal.substring(1, expr().strVal.length()-1).toCharArray() )
				{
					if( c != ' ' )
					{ 
						scan.getNext();//consume var
						//scan.getNext();//consume ')'
						return "F"; 
					}
				}
				return "T";
			}
			else//ADD OTHERS HERE
			{
				
			}
		}
		return null;
	}

	private void invokePrintFunction(STFunction func) throws Exception
	{
		scan.getNext();//Consume the Function Name
		scan.getNext();//Consume the '('
		while( scan.currentToken.primClassif != Token.RT_PAREN )
		{
			if( scan.currentToken.primClassif == Token.OPERAND )
			{	//LITERALS AND VARIABLES FOR PRINT
				ResultValue RV = Operand();
				String printVal = RV.strVal;
				char[] printArr;
				if( RV.type == Token.STRING)
				{
					if( (scan.currentToken.tokenStr.charAt(0) == '\"' || scan.currentToken.tokenStr.charAt(0) == '\''))
						printArr = printVal.substring(1, printVal.length()-1).toCharArray();
					else
						printArr = printVal.toCharArray();
				}
				else
				{
					printArr = printVal.toCharArray();
				}
				
				for(int i=0; i < printArr.length; i++)
				{
					if( printArr[i] == '\\' )//ESCAPE CHARACTERS CODE
					{
						i++;
						switch(printArr[i])
						{
							case 'n'://newline
								System.out.print("\n");
								break;
							case 't'://tab
								System.out.print("\t");
								break;
							case '\'': case '\"': //quotes escaped
								System.out.print(printArr[i]);
								break;
						}
					}
					else
					{
						if( i < printArr.length )
							System.out.print(printArr[i]);
					}
				}
				System.out.print(" ");
			}
			else if( scan.currentToken.primClassif == Token.FUNCTION )
            {
                int x;
                switch(scan.currentToken.tokenStr) {
                case "LENGTH":
                    x = this.invokeLENGTHFunction(null);
                    System.out.println(x);
                    break;
                case "ELEM":
                    scan.getNext();
                    scan.getNext();
                    x = this.invokeELEMFunction();
                    System.out.print(x);
                    break;
                case "MAXELEM":
                    scan.getNext();
                    scan.getNext();
                    x = this.invokeMAXELEMFunction();
                    System.out.print(x);
                    break;
                case "SPACES":
                	String s = invokeSPACESFunction(null);
                	System.out.print(s);
                	break;
                }
            }
			else if( scan.currentToken.tokenStr.equals("(") )
			{	//EXPR() ARGS FOR PRINT
				ResultValue RV = expr();
				String printVal = RV.strVal;
				char[] printArr = printVal.toCharArray();
				
				for(int i=0; i < printArr.length; i++)
				{
					if( printArr[i] == '\\' )//ESCAPE CHARACTERS CODE
					{
						i++;
						switch(printArr[i])
						{
							case 'n'://newline
								System.out.print("\n");
								break;
							case 't'://tab
								System.out.print("\t");
								break;
							case '\'': case '\"': //quotes escaped
								System.out.print(printArr[i]);
								break;
						}
					}
					else
					{
						if( i < printArr.length )
							System.out.print(printArr[i]);
					}
				}
			}
			else if( scan.currentToken.tokenStr.equals("[") && sm.getValue(scan.nextToken.tokenStr).type == Token.STRING)
			{	//PRINT A SINGLE CHARACTER FROM A STRING VARIABLE
				scan.getNext();//consume '['
				String varString = sm.getValue(scan.currentToken.tokenStr).strVal;
				scan.getNext();//consume the variable
				int subscript = expr().intVal;
				scan.getNext();//consume the subscript expr()
				System.out.print(varString.substring(1, varString.length()-1).charAt(subscript));
			}
			else if( scan.currentToken.tokenStr.equals("[") )
			{	//PRINT AN ARRAY
				scan.getNext();//consume '['
				ResultValue RV = expr();
				String printVal = RV.strVal;
				char[] printArr;
				if( RV.type == Token.STRING)
				{
					printArr = printVal.substring(1, printVal.length()-1).toCharArray();
				}
				else
				{
					printArr = printVal.toCharArray();
				}
				
				for(int i=0; i < printArr.length; i++)
				{
					if( printArr[i] == '\\' )//ESCAPE CHARACTERS CODE
					{
						i++;
						switch(printArr[i])
						{
							case 'n'://newline
								System.out.print("\n");
								break;
							case 't'://tab
								System.out.print("\t");
								break;
							case '\'': case '\"': //quotes escaped
								System.out.print(printArr[i]);
								break;
						}
					}
					else
					{
						if( i < printArr.length )
							System.out.print(printArr[i]);
					}
				}	
			}
			else
			{	//ERROR MAYBE???
				if( !scan.currentToken.tokenStr.equals(","))
					throw new Exception("print function: expected ')' received '" + scan.currentToken.tokenStr + "'");
			}
			scan.getNext();
		}
		System.out.println();
	}

	public ResultValue expr() throws Exception
	{
		ResultValue _RV = null;
		String operator = null;
		String op1 = null;
		String op2 = null;
		if( scan.currentToken.primClassif == Token.FUNCTION )
		{
			STFunction func = (STFunction) scan.symbolTable.getSymbol(scan.currentToken.tokenStr);
			switch(scan.currentToken.tokenStr)
			{//BUILT-IN FUNCTIONS HERE
				//case "print":
					//invokePrintFunction(func);
					//break;
				case "LENGTH":
					int result = invokeLENGTHFunction(func);
					_RV = new ResultValue();
					_RV.type = Token.INTEGER;
					_RV.setValues(Integer.toString(result));
					break;
				case "MAXLENGTH":
					break;
				case "SPACES":
					break;
				case "ELEM":
					_RV = new ResultValue();
					_RV.type = Token.INTEGER;
					_RV.setValues(Integer.toString(invokeELEMFunction()));
					break;
				case "MAXELEM":
					break;
			}
		}
		
		if( scan.currentToken.primClassif == Token.OPERAND || scan.currentToken.tokenStr.equals("["))
		{
			if( scan.currentToken.tokenStr.equals("[") ){scan.getNext();}
			return Operand();
		}
		else
		{//(Op Expr Expr) Case
			if( scan.currentToken.tokenStr.equals("(") )
			{
				scan.getNext();//consume the (
				operator = scan.currentToken.tokenStr;
				ResultValue v1;
				ResultValue v2;
				scan.getNext();
				//if( op1 == null){ op1 = scan.currentToken.tokenStr; }
				//v1 = expr(); scan.getNext();
				if( op1 == null && scan.currentToken.primClassif == Token.OPERAND){ op1 = scan.currentToken.tokenStr; }
				v1 = expr(); scan.getNext();
				if( op1 == null ){ op1 = v1.strVal; }
				while( scan.currentToken.primClassif != Token.RT_PAREN )
				{
					if( scan.currentToken.primClassif == Token.RT_PAREN )
					{
						_RV = NumericConversion.neg(v1);
						return _RV;
					}//UNARY MINUS CASE
					
					if( op2 == null && scan.currentToken.primClassif == Token.OPERAND){ op2 = scan.currentToken.tokenStr; }
					v2 = expr(); scan.getNext();
					if( op2 == null ){ op2 = v2.strVal; }
					
					switch(operator)
					{
						case "*":
							_RV = NumericConversion.mult(v1, v2);
							break;
						case "+":
							_RV = NumericConversion.add(v1, v2);
							break;
						case "-":
							_RV = NumericConversion.subtract(v1, v2);
							break;
						case "/":
							_RV = NumericConversion.div(v1, v2);
							break;
						case "^":
							_RV = NumericConversion.exp(v1, v2);
							break;
						case "#"://STRING CONCAT EXPRESSION
							_RV = NumericConversion.concat(v1, v2);
							break;
						case "dateAge":
							if(v1.dateVal == null || v2.dateVal == null) {
								handleException("Date Age error: Expected 2 dates.");
							}
							_RV = invokeDateAge(v1.dateVal, v2.dateVal);
							break;
						case "dateDiff":
							if(v1.dateVal == null || v2.dateVal == null) {
								handleException("Date Diff error: Expected 2 dates.");
							}
							_RV = invokeDateDiff(v1.dateVal, v2.dateVal);
							break;
						case "dateAdj":
							if(v1.dateVal == null) {
								handleException("Date Adj error: Expected date and integer.");
							}
							_RV = invokeDateAdj(v1.dateVal, v2.intVal);
							break;
					}
					v1 = _RV;
				}
			}
		}
		if( this.bShowExpr )
		{
			
			System.out.println("\nDEBUG: ("+ operator + " " + op1 + " " + op2 + ") = " + _RV.strVal);
		}
		return _RV;
	}

	private ResultValue Operand() throws Exception
	{
		ResultValue _RV = null;
		if( scan.currentToken.primClassif == Token.FUNCTION )
		{
			switch(scan.currentToken.tokenStr)
			{//BUILT-IN FUNCTIONS HERE
				//case "print":
					//invokePrintFunction(func);
					//break;
				case "LENGTH":
					int result = invokeLENGTHFunction(null);
					_RV = new ResultValue();
					_RV.type = Token.INTEGER;
					_RV.setValues(Integer.toString(result));
					break;
				case "MAXLENGTH":
					break;
				case "SPACES":
					break;
				case "ELEM":
					break;
				case "MAXELEM":
					break;
			}
			scan.getNext();
		}
		else if( scan.currentToken.primClassif == Token.OPERAND )
		{
			if( !(scan.currentToken.subClassif == Token.IDENTIFIER) )
			{	//Numeric Consts
				_RV = new ResultValue();
				switch(scan.currentToken.subClassif)
				{
					case Token.INTEGER:
						_RV.type = Token.INTEGER;
						break;
					case Token.FLOAT:
						_RV.type = Token.FLOAT;
						break;
					case Token.STRING:
						_RV.type = Token.STRING;
						String value = scan.currentToken.tokenStr;
						while( scan.nextToken.tokenStr.equals("#") )
						{	//STRING CONCATENATION STARTING WITH LITERAL
							scan.getNext();//consume the string
							scan.getNext();//consume the #
							ResultValue rv = expr();
							if( rv.type == Token.STRING )
							{	//CONCATENATE STRING WITH STRING
								value = value.substring(0, value.length()-1) + rv.strVal.substring(1);
							}
							else
							{
								value = value.substring(0, value.length()-1) + rv.strVal + "\"";
							}
						}
						_RV.setValues(value);
						return _RV;
					case Token.BOOLEAN:
						_RV.type = Token.BOOLEAN;
						break;
				}
				_RV.setValues(scan.currentToken.tokenStr);
			}
			else
			{	//Variable Consts
				_RV = sm.getValue(scan.currentToken.tokenStr);
				if( _RV.type == Token.STRING && scan.nextToken.tokenStr.equals("#") )
				{	
					String value = _RV.strVal;
					while( scan.nextToken.tokenStr.equals("#") )
					{	//STRING CONCATENATION STARTING WITH LITERAL
						scan.getNext();//consume the string
						scan.getNext();//consume the #
						ResultValue rv = expr();
						if( rv.type == Token.STRING )
						{	//CONCATENATE STRING WITH STRING
							value = value.substring(0, value.length()-1) + rv.strVal.substring(1);
						}
						else
						{
							value = value.substring(0, value.length()-1) + rv.strVal + "\"";
						}
					}
					ResultValue returnable = new ResultValue();
					returnable.type = Token.STRING;
					returnable.setValues(value);
					return returnable;
				}
				else if( _RV.type == Token.INTARRAY )
				{
					String arrayName= scan.currentToken.tokenStr;
					scan.getNext();//consume arrayName
					if( scan.currentToken.primClassif == Token.SEPARATOR)
					{//CASE WHERE COPYING AN ARRAY
						
					}
					int subscript = expr().intVal;
					int value = sm.getFromIntArray(arrayName, subscript);
					_RV.setValues(Integer.toString(value));
					scan.getNext();//consume subscript
				}
				else if( _RV.type == Token.FLOATARRAY )
				{
					String arrayName= scan.currentToken.tokenStr;
					scan.getNext();//consume arrayName
					if( scan.currentToken.tokenStr.equals(",") )
					{	//We are looking for an entire array to pass to a function
						return _RV;
					}
					int subscript = expr().intVal;
					double value = sm.getFromFloatArray(arrayName, subscript);
					_RV.setValues(Double.toString(value));
					scan.getNext();//consume subscript
				}
				else if( _RV.type == Token.STRINGARRAY )
				{
					String arrayName= scan.currentToken.tokenStr;
					scan.getNext();//consume arrayName
					int subscript = expr().intVal;
					String value = sm.getFromStringArray(arrayName, subscript);
					_RV.setValues(value);
					scan.getNext();//consume subscript
				}
				else if( _RV.type == Token.BOOLEANARRAY )
				{
					String arrayName= scan.currentToken.tokenStr;
					scan.getNext();//consume arrayName
					int subscript = expr().intVal;
					boolean value = sm.getFromBoolArray(arrayName, subscript);
					_RV.setValues(Boolean.toString(value));
					scan.getNext();//consume subscript
				}
			}
		}
		return _RV;
	}
	
	public ResultValue whileStmt() throws Exception
	{
		int whileLoops=0, endWhiles = 0;
		int lineNr = scan.iSourceLineNr;
		
		scan.getNext();//consume while
		while( Cond().boolVal )
		{
			do
			{
				scan.getNext();
				Statement();
			}while( !scan.nextToken.tokenStr.equals("endwhile") );
			scan.iColPos = 0;
			scan.iSourceLineNr = lineNr;
			scan.textCharM = scan.sourceLineM.get(lineNr).toCharArray();
			scan.getNext();
			scan.getNext();
			scan.getNext();
		}
		
		if( scan.currentToken.tokenStr.equals("while") )
		{
			whileLoops++;
		}
		do
		{
			scan.getNext();
			if( scan.currentToken.tokenStr.equals("while") ){ ++whileLoops; }
			if( scan.currentToken.tokenStr.equals("endwhile")){ ++endWhiles; }
		}while(endWhiles <= whileLoops);
		//scan.getNext();
		return null;
	}
	
	public void ifStmtStart() throws Exception
	{
		scan.getNext();//consume 'if'
		if( scan.currentToken.tokenStr.equals("not") )
		{
			scan.getNext();//consume 'not'
			if( !expr().boolVal )
			{
				scan.getNext();//consume boolean
				ifStmtHead();
			}
			else
			{
				scan.getNext();//consume boolean
				this.ifStmtTail();
			}
			return;
		}
		ResultValue result = Cond().boolVal ? ifStmtHead() : ifStmtTail();
	}
	
	public ResultValue ifStmtHead() throws Exception
	{	//Look into variables declared in the scope of the While loop
		ResultValue value = new ResultValue();
		value.terminatingStr = "endif";
		while(scan.currentToken.primClassif == Token.RT_PAREN ){scan.getNext();}
		scan.getNext();//consume the ':'
		do
		{
			Statement();
			scan.getNext();//consume the ';'
		}while(!scan.currentToken.tokenStr.equals(value.terminatingStr) && !scan.nextToken.tokenStr.equals("else") && !scan.currentToken.tokenStr.equals("else") );
		
		
		int whileLoops = 0, endWhiles=0;
		
		if( !scan.currentToken.tokenStr.equals("endif"))
		{
			do
			{
				scan.getNext();
				if( scan.currentToken.tokenStr.equals("if") ){ ++whileLoops; }
				if( scan.currentToken.tokenStr.equals("endif") ){ ++endWhiles; }
			}while(endWhiles <= whileLoops);
		}
		//scan.getNext();//consume endif
		return value;
	}
	
	private ResultValue ifStmtTail() throws Exception 
	{
		scan.getNext();//consume the ':'
		int whileLoops = 0, endWhiles=0;
		if( scan.currentToken.tokenStr.equals("if") )
		{
			whileLoops++;
		}
		
		if( !scan.currentToken.tokenStr.equals("endif") && !scan.currentToken.tokenStr.equals("else") )
		{
			do
			{
				scan.getNext();
				if( scan.currentToken.tokenStr.equals("if") ){ ++whileLoops; }
				if( scan.currentToken.tokenStr.equals("endif") || scan.currentToken.tokenStr.equals("else") ){ ++endWhiles; }
			}while(endWhiles <= whileLoops);
		}
		if( scan.currentToken.tokenStr.equals("endif") ){ /*scan.getNext();*/ return null; }
		
		else if( !scan.currentToken.tokenStr.equals("else") ){/*Error*/}
		

		scan.getNext();//consume 'else'
		scan.getNext();//consume ':'
		
		do
		{
			//DONTDOIT = true;
			Statement();
			//DONTDOIT = false;
		}while(!scan.nextToken.tokenStr.equals("endif"));
		scan.getNext();
		//scan.getNext();//consume endif
		//scan.getNext();//consume ;
		return null;
	}
	
	private ResultValue Cond() throws Exception
	{
		ResultValue value = new ResultValue();
		value.type = Token.BOOLEAN;
		
		if( scan.currentToken.tokenStr.equals("(") )
		{
			scan.getNext();
			ResultValue v;
			ResultValue v2;
			switch(scan.currentToken.tokenStr)
			{
				case ">":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("["))
					{ //we have an expression
						v = expr();
						scan.getNext();//consume the ')'
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = Operand();
						scan.getNext();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();//consume the ')'
					}
					else
					{	//we have another operand
						v2 = Operand();
					}
					
					switch(v.type)
					{
						case(Token.INTEGER):
							if( v.intVal > v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT):
							if( v.doubleVal > v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING):
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret > 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
					//case(Token.BOOLEAN):
						//break;
					}
					break;
				case "<":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have an expression
						//v = expr();
						ResultValue tmp = expr();
						v = new ResultValue();
						v.type = tmp.type;
						v.assign(tmp);
						scan.getNext();//consume ( or ] 
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = expr();
						scan.getNext();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();
					}
					else
					{	//we have another operand
						v2 = expr();
					}
					switch(v.type)
					{
						case(Token.INTEGER): case Token.INTARRAY:
							if( v.intVal < v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT): case Token.FLOATARRAY:
							if( v.doubleVal < v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING): case Token.STRINGARRAY:
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret < 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
					//case(Token.BOOLEAN):
						//break;
					}
					break;
				case ">=":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have an expression
						v = expr();
						scan.getNext();//consume the ')'
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = Operand();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();//consume the ')'
					}
					else
					{	//we have another operand
						v2 = Operand();
					}
					switch(v.type)
					{
						case(Token.INTEGER):
							if( v.intVal >= v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT):
							if( v.doubleVal >= v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING):
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret >= 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
					//case(Token.BOOLEAN):
						//break;
					}
					break;
				case "<=":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have an expression
						v = expr();
						scan.getNext();//consume the ')'
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = Operand();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();//consume the ')'
					}
					else
					{	//we have another operand
						v2 = Operand();
					}
					switch(v.type)
					{
						case(Token.INTEGER):
							if( v.intVal <= v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT):
							if( v.doubleVal <= v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING):
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret <= 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
					//case(Token.BOOLEAN):
						//break;
					}
					break;
				case "==":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have an expression
						v = expr();
						scan.getNext();//consume the ')'
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = Operand();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();//consume the ')'
					}
					else
					{	//we have another operand
						v2 = Operand();
					}
					switch(v.type)
					{
						case(Token.INTEGER):
							if( v.intVal == v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT):
							if( v.doubleVal == v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING):
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret == 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
						case(Token.BOOLEAN):
							if( v.boolVal == v2.boolVal ){ value.boolVal = true; }
							else{ value.boolVal = true; }
							break;
					}
					break;
				case "!=":
					scan.getNext();//consume the operator
					if( scan.currentToken.tokenStr.equals("(") || scan.currentToken.tokenStr.equals("[") )
					{ //we have an expression
						v = expr();
						scan.getNext();//consume the ')'
					}
					else
					{
						boolean ISCONCAT = false;
						if( scan.nextToken.tokenStr.equals("#") ){ ISCONCAT = true; }
						v = Operand();
						scan.getNext();
						if( ISCONCAT ){ scan.getNext(); }
					}
					
					if( scan.currentToken.tokenStr.equals("(") )
					{ //we have another expression
						v2 = expr();
						scan.getNext();//consume the ')'
					}
					else
					{	//we have another operand
						v2 = Operand();
					}
					switch(v.type)
					{
						case(Token.INTEGER):
							if( v.intVal != v2.intVal )
							{
								value.boolVal = true;
							}	
							else
								value.boolVal = false;
							break;
						case(Token.FLOAT):
							if( v.doubleVal != v2.doubleVal )
							{
								value.boolVal = true;
							}
							else
								value.boolVal = false;
							break;
						case(Token.STRING):
							int ret = v.strVal.compareTo(v2.strVal);
							if( ret != 0 ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
						case(Token.BOOLEAN):
							if( v.boolVal != v2.boolVal ){ value.boolVal = true; }
							else{ value.boolVal = false; }
							break;
					}
					break;
			}
			//scan.getNext();
		}
		else
		{	//STRING COMPARISONS CASES
			ResultValue v;
			ResultValue v2;
			v = Operand();
			scan.getNext();
			String rator = scan.currentToken.tokenStr;
			scan.getNext();//consume the Operator ??MAY NEED TO SWITCH STATEMENT ON OPERATOR
			v2 = Operand();
			switch(v.type)
			{
				case(Token.INTEGER):
					if( v.intVal != v2.intVal )
					{
						value.boolVal = true;
					}	
					else
						value.boolVal = false;
					break;
				case(Token.FLOAT):
					if( v.doubleVal != v2.doubleVal )
					{
						value.boolVal = true;
					}
					else
						value.boolVal = false;
					break;
				case(Token.STRING):
					int ret = v.strVal.compareTo(v2.strVal);
					if( rator.equals("==") )
					{
						if( ret == 0 ){ value.boolVal = true; }
						else{ value.boolVal = false; }
					}
					else
					{
						if( ret != 0 ){ value.boolVal = true;}
						else{ value.boolVal = false; }
					}
					break;
			//case(Token.BOOLEAN):
				//break;
			}
		}
		scan.getNext();
		return value;
	}
	
	/**
	 * Calculates the difference in years between 2 dates.
	 * @param date1 more recent date
	 * @param date2 date being compared
	 * @return difference in years
	 */
	public ResultValue invokeDateAge(Date date1, Date date2) {
		ResultValue RV = new ResultValue();
		RV.type = Token.INTEGER;
		int diff = 0;
		Calendar calendar1 = new GregorianCalendar(date1.getYear(), date1.getMonth() - 1, date1.getDate());
		Calendar calendar2 = new GregorianCalendar(date2.getYear(), date2.getMonth() - 1, date2.getDate());
		if(calendar1.before(calendar2)) {
			handleException("Date Age error: Expected later date as 1st argument.");
		}
		diff = date1.getYear() - date2.getYear();
		if(date1.getMonth() < date2.getMonth()) {
			diff--;
		}
		if(date1.getMonth() == date2.getMonth()) {
			if(date1.getDate() < date2.getDate()) {
				diff--;
			}
		}
		RV.strVal = "" + diff;
		RV.intVal = diff;
		return RV;
	}
	
	/**
	 * Calculates difference in days between 2 dates.
	 * @param date1 more recent date
	 * @param date2 date being compared
	 * @return difference in days
	 */
	public ResultValue invokeDateDiff(Date date1, Date date2) {
		ResultValue RV = new ResultValue();
		RV.type = Token.INTEGER;
		Calendar calendar1 = new GregorianCalendar(date1.getYear(), date1.getMonth() - 1, date1.getDate());
		Calendar calendar2 = new GregorianCalendar(date2.getYear(), date2.getMonth() - 1, date2.getDate());
		if(calendar1.before(calendar2)) {
			handleException("Date Diff error: Expected later date as 1st argument.");
		}
		TimeUnit timeUnit = TimeUnit.DAYS;
		long diff = date1.getTime() - date2.getTime();
		int diffDays = (int)timeUnit.convert(diff, TimeUnit.MILLISECONDS);
		RV.strVal = "" + diffDays;
		RV.intVal = diffDays;
		
		return RV;
	}
	
	/**
	 * Adjusts the date by the specified value.
	 * @param date date to be adjusted
	 * @param days number of days to adjust
	 * @return new date (THIS FUNCTION STILL NEEDS WORK)
	 */
	public ResultValue invokeDateAdj(Date date, int days) {
		ResultValue RV = new ResultValue();
		RV.type = Token.DATE;
		Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDate());
		calendar.add(Calendar.DAY_OF_MONTH, days);
		Date newDate = new Date(calendar.YEAR, calendar.MONTH + 1, calendar.DAY_OF_MONTH);
		RV.strVal = "" + calendar.YEAR + "-" + calendar.MONTH + "-" + calendar.DAY_OF_MONTH;
		RV.dateVal = newDate;
		
		return RV;
	}
	
	/**
	 * Function for handling exceptions
	 * @param message error message
	 */
	public void handleException(String message) {
		ParserException pe = new ParserException(scan.currentToken.iSourceLineNr, message, scan.sourceFileNm);
		System.err.println(pe);
		System.exit(-1);
	}
}