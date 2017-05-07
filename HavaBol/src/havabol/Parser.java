package havabol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Parser 
{
	//Global Vars
	public Scanner scan;
	public StorageManager sm;	//used for storing variables and their values
	public StorageManager tmpsm;
	public boolean bShowExpr = false;
	public boolean bShowAssign = false;
	public HashMap<String, Token> functionSpots;
	public Token restorePoint;
	public int lineNum = 0;
	public int colPos;
	
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
			if( scan.iSourceLineNr == 6 )
			{
				DEBUG++;//stop here
			}
			scan.getNext();//consume the ;
			Statement();
		}while(scan.currentToken.primClassif != Token.EOF);
	}
	
	public ResultValue invokeLengthFunction() throws Exception
	{
		ResultValue RV;
		scan.getNext();//consume (
		String funcName = scan.currentToken.tokenStr;
		scan.getNext();//consume LENGTH or SPACES
		ResultValue rv = Expr();
		
		if( funcName.equals("LENGTH") )
		{
			if( rv.type == Token.STRING )
			{
				scan.getNext();//consume
				RV = new ResultValue();
				RV.type = Token.INTEGER;
				RV.setValues(Integer.toString(rv.strVal.substring(1, rv.strVal.length()-1).length()));
				return RV;
			}//?? MAY NEED SOME MORE GETNEXTS
			else
			{
				if(rv.type == Token.BOOLEAN) {
					scan.getNext();
					RV = new ResultValue();
					RV.type = Token.INTEGER;
					RV.strVal = "1";
					RV.intVal = 1;
					return RV;
				} else
				{
					handleException("LENGTH Error: Expected String argument!");
					return null;
//				scan.getNext();//consume
//				RV = new ResultValue();
//				RV.type = Token.INTEGER;
//				RV.setValues(Integer.toString(rv.strVal.length()));
//				return RV;
				}
			}
		}
		else if( funcName.equals("SPACES") )
		{	//SPACES FUNCTION
			if( rv.type == Token.STRING )
			{
				RV = new ResultValue();
				RV.type = Token.BOOLEAN;
				
				if( rv.strVal.substring(1, rv.strVal.length()-1).isEmpty() )
				{
					RV.setValues("true");
					scan.getNext();
					return RV;
				}
				for(char c : rv.strVal.substring(1, rv.strVal.length()-1).toCharArray() )
				{
					if( c != ' ' )
					{ 
						RV.setValues("false");
						scan.getNext();
						return RV; 
					}
				}
				
				RV.setValues("true");
				scan.getNext();
				return RV;
			}//?? MAY NEED SOME MORE GETNEXTS
			else
			{
				handleException("SPACES Error: Expected String argument");
				return null;
			}
		}
		else
		{	//ELEM function
			RV = new ResultValue();
			RV.type = Token.INTEGER;
			switch(rv.type)
			{
				case Token.INTARRAY:
					if( funcName.equals("MAXELEM") )
					{
						RV.setValues(Integer.toString(sm.intArrays.get(scan.currentToken.tokenStr).size()));
						while( !scan.currentToken.tokenStr.equals(")") )
						{
							scan.getNext();
						}
						return RV;
					}
					else{
					for(int i=0; i < sm.intArrays.get(scan.currentToken.tokenStr).size(); i++)
					{
						if( sm.intArrays.get(scan.currentToken.tokenStr).get(i) == -10000 )
						{
							RV.setValues(Integer.toString(i));
							while( !scan.currentToken.tokenStr.equals(")") )
							{
								scan.getNext();
							}
							return RV;
						}
					}
						RV.setValues(Integer.toString(sm.intArrays.get(scan.currentToken.tokenStr).size()));
					}
					break;
				case Token.FLOATARRAY:
					if( funcName.equals("MAXELEM") )
					{
						RV.setValues(Integer.toString(sm.floatArrays.get(scan.currentToken.tokenStr).size()));
						while( !scan.currentToken.tokenStr.equals(")") )
						{
							scan.getNext();
						}
						return RV;
					}
					for(int i=0; i < sm.floatArrays.get(scan.currentToken.tokenStr).size(); i++)
					{
						if( sm.floatArrays.get(scan.currentToken.tokenStr).get(i) == -10000.0 )
						{
							RV.setValues(Integer.toString(i));
							while( !scan.currentToken.tokenStr.equals(")") )
							{
								scan.getNext();
							}
							return RV;
						}
					}
					RV.setValues(Integer.toString(sm.floatArrays.get(scan.currentToken.tokenStr).size()));
					break;
				case Token.STRINGARRAY:
					if( funcName.equals("MAXELEM") )
					{
						RV.setValues(Integer.toString(sm.stringArrays.get(scan.currentToken.tokenStr).size()));
						while( !scan.currentToken.tokenStr.equals(")") )
						{
							scan.getNext();
						}
						return RV;
					}
					for(int i=0; i < sm.stringArrays.get(scan.currentToken.tokenStr).size(); i++)
					{
						if( sm.stringArrays.get(scan.currentToken.tokenStr).get(i) == null )
						{
							RV.setValues(Integer.toString(i));
							while( !scan.currentToken.tokenStr.equals(")") )
							{
								scan.getNext();
							}
							return RV;
						}
					}
					RV.setValues(Integer.toString(sm.stringArrays.get(scan.currentToken.tokenStr).size()));
					break;
				case Token.BOOLEANARRAY:
					if( funcName.equals("MAXELEM") )
					{
						RV.setValues(Integer.toString(sm.boolArrays.get(scan.currentToken.tokenStr).size()));
						while( !scan.currentToken.tokenStr.equals(")") )
						{
							scan.getNext();
						}
						return RV;
					}
					for(int i=0; i < sm.boolArrays.get(scan.currentToken.tokenStr).size(); i++)
					{
						if( sm.boolArrays.get(scan.currentToken.tokenStr).get(i) == null )
						{
							RV.setValues(Integer.toString(i));
							while( !scan.currentToken.tokenStr.equals(")") )
							{
								scan.getNext();
							}
							return RV;
						}
					}
					break;
				default:
					handleException("ELEM argument is not an Array");
			}
			
			return RV;
		}
	}
	
	public void invokePrintFunction() throws Exception
	{
		ResultValue RV;
		boolean parenClosed = false;
		scan.getNext();//consume (
		scan.getNext();//consume 'print'
		while( !scan.currentToken.tokenStr.equals(";") )
		{
			RV = Expr();
			scan.getNext();
			String printVal = RV.strVal;
			char[] printArr;
			if( RV.type == Token.STRING)
			{
				if( (printVal.charAt(0) == '\"' || printVal.charAt(0) == '\''))
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
			if( scan.currentToken.primClassif == Token.RT_PAREN ){ 
				parenClosed = true;
				scan.getNext();}
		}
		
		if(!parenClosed) {
			handleException("Print Error: Expected ')'");
		}
		System.out.println();
	}

	//STATEMENT HERE
	public ResultValue Statement() throws Exception 
	{
		ResultValue _RV = null;
		switch(scan.currentToken.primClassif)
		{
			case Token.OPERATOR:
				String var;
				ResultValue rv;;
				ResultValue rv2;
				switch(scan.currentToken.tokenStr) {
					case "+=":
						scan.getNext();
						var = scan.currentToken.tokenStr;
						rv = sm.getValue(var);
						scan.getNext();
						while(scan.currentToken.primClassif != Token.RT_PAREN) {
							rv2 = Expr();
							if(rv.type == Token.INTEGER && rv.type == rv2.type) {
								rv.intVal += rv2.intVal;
								rv.strVal = "" + rv.intVal;
							}
							if(rv.type == Token.FLOAT && rv.type == rv2.type) {
								rv.doubleVal += rv2.doubleVal;
								rv.strVal = "" + rv.doubleVal;
							}
							if(rv.type == Token.STRING && rv.type == rv2.type) {
								rv.strVal += rv2.strVal;
							}
							scan.getNext();
						}
						sm.putVariable(var, rv);
						scan.getNext();
						break;
					case "-=":
						scan.getNext();
						var = scan.currentToken.tokenStr;
						rv = sm.getValue(var);
						scan.getNext();
						while(scan.currentToken.primClassif != Token.RT_PAREN) {
							rv2 = Expr();
							if(rv.type == Token.INTEGER && rv.type == rv2.type) {
								rv.intVal -= rv2.intVal;
								rv.strVal = "" + rv.intVal;
							}
							if(rv.type == Token.FLOAT && rv.type == rv2.type) {
								rv.doubleVal -= rv2.doubleVal;
								rv.strVal = "" + rv.doubleVal;
							}
							if(rv.type == Token.STRING) {
								handleException("-= Error: Expected Int or Float arguments.");
							}
							scan.getNext();
						}
						sm.putVariable(var, rv);
						scan.getNext();
						break;
				}
			case Token.CONTROL:
				if( scan.currentToken.subClassif == Token.FLOW )
				{//IF, WHILE, FOR
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
						ForLoopStart();
						//forLoop2();
					}
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
								if(month == 2 && (year % 4 != 0) && day > 28) {
									handleException("Date error: Invalid date!");
								}
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
					//if( sm.getValue(scan.nextToken.tokenStr) != null ){ throw new Exception("Identifier already declared. Line " + scan.iSourceLineNr); }
					sm.putVariable(scan.nextToken.tokenStr, _RV);
					scan.getNext();//Consume the Data type
					if( scan.nextToken.tokenStr.equals("[") )
					{	//ARRAY DECLARATION
						_RV = arrayDeclaration(_RV);
					}
					else if( !scan.nextToken.tokenStr.equals(";") )
					{
						Statement();
					}
				}
				break;
			case Token.OPERAND:
				if( scan.currentToken.tokenStr.equals("return") )
				{	//returning a value from a user defined function
					scan.getNext();//consume the return
					return Expr();
				}
				if( scan.currentToken.subClassif == Token.IDENTIFIER )
				{ //VARIABLE INITIALIZATION
					_RV = sm.getValue(scan.currentToken.tokenStr);//Get var RV from storage
					if( _RV == null ){ handleException("Undeclared Identifier");}
					scan.getNext();//consume variable Name
					scan.getNext();//consume '='
					_RV.assign(Expr());
				}
				break;
			case Token.SEPARATOR:
				if( scan.currentToken.tokenStr.equals("[") )
				{ //Array Subscript initialization
					if( sm.getValue(scan.nextToken.tokenStr).type == Token.STRING )
					{	//scalar assignments
						scan.getNext();//consume the [
						_RV = sm.getValue(scan.currentToken.tokenStr);
						String resval = _RV.strVal;
						resval = resval.substring(1, resval.length()-1);
						scan.getNext();//consume the var name
						int subscript = Expr().intVal;
						//while( !scan.getNext().equals("]")){}
						scan.getNext();
						if( scan.nextToken.tokenStr.equals("=") )
						{
							scan.getNext();
							scan.getNext();//consume =
							ResultValue val = Expr();
							String strval = val.strVal.substring(1, val.strVal.length()-1);
							_RV.setValues(resval.substring(0, subscript) + strval + resval.substring(subscript+1));
							scan.getNext();
							return _RV;
						}
						else
						{
							_RV = new ResultValue();
							_RV.type = Token.STRING;
							if(subscript > resval.length()) {
								handleException("String Error: Index out of bounds");
							}
							_RV.setValues(Character.toString(resval.charAt(subscript)));
							_RV.type = Token.INTEGER;
							if( scan.currentToken.tokenStr.equals(")") )
							{
								scan.getNext();
							}
							return _RV;
						}
					}
					String arrayName = scan.nextToken.tokenStr;
					scan.getNext();//consume the [
					_RV = sm.getValue(scan.currentToken.tokenStr);//get the arrayName
					scan.getNext();//consume the arrayName
					int subscript = Expr().intVal;
					scan.getNext();//consume the subscript
					//scan.getNext();//consume the ]
					if( scan.nextToken.tokenStr.equals("=") )
					{
						scan.getNext();//consume the ]
						scan.getNext();//consume the =
					}
					rv = null;
					switch(_RV.type)
					{
						case Token.INTARRAY:
							if( scan.currentToken.primClassif == Token.SEPARATOR &&
								!scan.currentToken.tokenStr.equals("[") )
							{
								rv = new ResultValue();
								rv.type = Token.INTEGER;
								rv.setValues(Integer.toString(sm.intArrays.get(arrayName).get(subscript).intValue()));
								return rv;
							}
							else
								sm.intArrays.get(arrayName).set(subscript, Expr().intVal);
							break;
						case Token.FLOATARRAY:
							if( scan.currentToken.primClassif == Token.SEPARATOR &&
							!scan.currentToken.tokenStr.equals("[") )
							{
								rv = new ResultValue();
								rv.type = Token.FLOAT;
								rv.setValues(Double.toString(sm.floatArrays.get(arrayName).get(subscript).doubleValue()));
								return rv;
							}
							else
								sm.floatArrays.get(arrayName).set(subscript, Expr().doubleVal);							
							break;
						case Token.STRINGARRAY:
							if( scan.currentToken.primClassif == Token.SEPARATOR &&
									!scan.currentToken.tokenStr.equals("[") )
							{
								rv = new ResultValue();
								rv.type = Token.STRING;
								rv.setValues(sm.stringArrays.get(arrayName).get(subscript));
								return rv;
							}
							else
								sm.stringArrays.get(arrayName).set(subscript, Expr().strVal);
							break;
						case Token.BOOLEANARRAY:
							if( scan.currentToken.primClassif == Token.SEPARATOR && 
									!scan.currentToken.tokenStr.equals("[") )
							{
								rv = new ResultValue();
								rv.type = Token.BOOLEAN;
								rv.setValues(Boolean.toString(sm.boolArrays.get(arrayName).get(subscript)));
								return rv;
							}
							else
								sm.boolArrays.get(arrayName).set(subscript, Expr().boolVal);
							break;
					}
				}
				else if( scan.currentToken.tokenStr.equals("(") )
				{ //FUNCTION
					switch(scan.nextToken.tokenStr)
					{//BUILT-IN FUNCTIONS HERE
						case "print":
							invokePrintFunction();
							break;
						case "SPACES": case "LENGTH": case "ELEM": case "MAXELEM":
							_RV = invokeLengthFunction();
							break;
						default: //USER FUNCTIONS HERE
							if(scan.iSourceLineNr > lineNum) {
								lineNum = scan.iSourceLineNr;
							}
							colPos = scan.iColPos;
							_RV = runUserFunction();
							break;
					}
					return _RV;
				}
				break;
			case Token.FUNCTION:
				//Define a User Function
				this.buildUserFunction();
				break;
				
		}//End of Main Switch
		if( scan.nextToken.tokenStr.equals(";") )
			scan.getNext();
		return _RV;
	}
	
	private ResultValue runUserFunction() throws Exception
	{
		int argNum = 0;
		STFunction userFunc = null;
		
		while( !scan.getNext().equals(";") )
		{
			if( scan.currentToken.primClassif == Token.FUNCTION )
			{
				userFunc = (STFunction)scan.symbolTable.getSymbol(scan.currentToken.tokenStr);
				userFunc.parentsm = sm;
			}
			else if( scan.currentToken.primClassif != Token.RT_PAREN )
			{
				userFunc.addParmToSM(argNum++, Expr(), scan.currentToken.tokenStr);
			}
		}
		userFunc.endSpot = scan.currentToken;
		sm = userFunc.sm;
		ResultValue functionReturnValue = userFunc.run(this);
		sm = userFunc.parentsm;
		
		while(!(scan.iSourceLineNr > lineNum)) {
			scan.getNext();
			if(scan.currentToken.primClassif == Token.EOF) {
				System.exit(1);
			}
		}
		System.out.println(scan.iSourceLineNr + " " + lineNum);
		lineNum = 0;
		while(scan.iColPos < colPos) {
			scan.getNext();
		}
		colPos = 0;
		return functionReturnValue;
	}

	private ResultValue buildUserFunction() throws Exception 
	{
		String parmString = null;
		STFunction newFunc = null;// = new STFunction(scan.currentToken.tokenStr);
		while( !scan.getNext().equals("enddef") )
		{
			switch(scan.currentToken.tokenStr)
			{
				case "Void":
					newFunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.VOID, Token.USER, 0);
					break;
				case "Int":
					if( newFunc == null )
					{
						newFunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.INTEGER, Token.USER, 0);
						scan.getNext();//consume return type
					}
					else if( parmString == null )
					{
						parmString = scan.currentToken.tokenStr + " ";
					}
					else if( parmString != null)
					{
						parmString += scan.currentToken.tokenStr + " ";
					}
					break;
				case "Float":
					if( newFunc == null )
					{
						newFunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.FLOAT, Token.USER, 0);
						scan.getNext();
					}
					else if( parmString == null )
					{
						parmString = scan.currentToken.tokenStr + " ";
					}
					else
					{
						parmString += scan.currentToken.tokenStr + " ";
					}
					break;
				case "String":
					if( newFunc == null )
					{
						newFunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.STRING, Token.USER, 0);
						scan.getNext();
					}
					else if( parmString == null )
					{
						parmString = scan.currentToken.tokenStr + " ";
					}
					else
					{
						parmString += scan.currentToken.tokenStr + " ";
					}
					break;
				case "Bool":
					if( newFunc == null )
					{
						newFunc = new STFunction(scan.nextToken.tokenStr, Token.FUNCTION, Token.BOOLEAN, Token.USER, 0);
						scan.getNext();
					}
					else if( parmString == null )
					{
						parmString = scan.currentToken.tokenStr + " ";
					}
					else
					{
						parmString += scan.currentToken.tokenStr + " ";
					}
					break;
				case "Ref":
					parmString = "*"; //* means parameter is passed by reference
					break;
				case "["://MAY CHANGE
					parmString += "[] ";
					scan.getNext();//consume the ]
					break;
				case ",": case ")":
					newFunc.parmList.add(parmString);
					parmString = "";//SEE HOW JAVA HANDLES THIS
					break;
				case ":":
					if( newFunc.startSpot == null )
					{
						newFunc.startSpot = new Token(scan.currentToken);
					}
					break;
				default:
					parmString += scan.currentToken.tokenStr;
					break;
					
			}
		}
		newFunc.setArgsNum();
		scan.symbolTable.putSymbol(newFunc.symbol, newFunc);//Add newfunc to the symbol table
		return null;
	}

	//ARRAY DECL HERE
	private ResultValue arrayDeclaration(ResultValue RV) throws Exception
	{
		String arrayName = scan.currentToken.tokenStr;
		int maxElem = -1;
		scan.getNext();//consume the arrayName
		if( !scan.nextToken.tokenStr.equals("]") )
		{	//Max Elem specified
			scan.getNext();//consume [
			maxElem = Expr().intVal;//Integer.parseInt(scan.currentToken.tokenStr);
		}
		
		sm.initArray(arrayName, RV, maxElem);
		int i=0;
		scan.getNext();//consume the '[' or ]
		while( !scan.currentToken.tokenStr.equals(";") )
		{
			if( !scan.currentToken.tokenStr.equals("=") && 
				!scan.currentToken.tokenStr.equals("]") &&
				!scan.currentToken.tokenStr.equals(",") )
			{
				ResultValue rv = Expr();
				rv.type = RV.type;
				sm.addToArray(rv, arrayName, i++);
			}
			scan.getNext();
		}
		return null;
	}

	private ResultValue Expr() throws Exception
	{
		ResultValue _RV = null;
		if( scan.currentToken.primClassif == Token.SEPARATOR )
		{	
			if( scan.currentToken.tokenStr.equals("[") )
			{	//ARRAY
				return Statement();
			}
			else
			{	//OPERATION Expr()
				if( scan.nextToken.tokenStr.equals("LENGTH") ||
					scan.nextToken.tokenStr.equals("SPACES") ||
					scan.nextToken.tokenStr.equals("ELEM") 	 ||
					scan.nextToken.tokenStr.equals("MAXELEM") )
				{	//EXPR IS A FUNCTION CALL
					return Statement();
				}
				scan.getNext();//consume '('
				String operator = scan.currentToken.tokenStr;
				scan.getNext();//consume operator
				_RV = new ResultValue();
				ResultValue v = Expr();
				_RV.type = v.type;
				_RV.assign(v);
				scan.getNext();//Always call getNext after 1st Expr()
				ResultValue rv2 = null;
				
				switch(operator)
				{
					case "+":
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							if(rv2.type != Token.INTEGER && rv2.type != Token.FLOAT) {
								handleException("Addition error: Expected two numeric values");
							}
							_RV.assign(NumericConversion.add(_RV, rv2));
							scan.getNext();
						}
						break;
					case "+=":
//						while(scan.currentToken.primClassif != Token.RT_PAREN) {
//							rv2 = Expr();
//							if(rv2.type != Token.INTEGER && rv2.type != Token.FLOAT) {
//								handleException("Addition error: Expected two numeric values");
//							}
//							_RV.assign(NumericConversion.add(_RV, rv2));
//							scan.getNext();
//						}
						System.out.println(scan.currentToken.tokenStr);
						break;
					case "-":
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							_RV.assign(NumericConversion.subtract(_RV, rv2));
							scan.getNext();
						}
						if( rv2 == null )
						{	//UNARY MINUS
							rv2 = new ResultValue();
							rv2.type = Token.INTEGER;
							rv2.setValues(Integer.toString(-1));
							_RV = NumericConversion.mult(_RV, rv2);
						}
						break;
					case "*":
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							_RV.assign(NumericConversion.mult(_RV, rv2));
							scan.getNext();
						}
						break;
					case "/":
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							_RV.assign(NumericConversion.div(_RV, rv2));
							scan.getNext();
						}
						break;
					case "^":
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							_RV.assign(NumericConversion.exp(_RV, rv2));
							scan.getNext();
						}
						break;
					case "#": //String concatenation
						if( _RV.type != Token.STRING ){ throw new Exception("Only strings can be concatenated. Line " + scan.iSourceLineNr);}
						while( scan.currentToken.primClassif != Token.RT_PAREN )
						{
							rv2 = Expr();
							//if( _RV.type != Token.STRING ){ throw new Exception("Only strings can be concatenated. Line " + scan.iSourceLineNr);}
							_RV.setValues(_RV.strVal.substring(0, _RV.strVal.length()-1) + rv2.strVal.substring(1));
							scan.getNext();
						}
						break;
						case "dateAge":
						rv2 = Expr();
						if(v.type == Token.DATE) {
							switch(rv2.type) {
							case Token.DATE:
								_RV = invokeDateAge(v.dateVal, rv2.dateVal);
								scan.getNext();
								break;
							case Token.STRING:
								_RV = invokeDateAge(v.dateVal, rv2.strVal);
								scan.getNext();
								break;
							default:
								handleException("Date Age error: Expected formats: dateAge(Date, Date) or dateAge(Date, String)");
							}
						}
						//scan.getNext();
						break;
					case "dateDiff":
						rv2 = Expr();
						if(v.dateVal == null || rv2.dateVal == null) {
							handleException("Date Diff error: Expected 2 dates.");
						}
						_RV = invokeDateDiff(v.dateVal, rv2.dateVal);
						scan.getNext();
						break;
					case "dateAdj":
						rv2 = Expr();
						if(v.dateVal == null) {
							handleException("Date Adj error: Expected date and integer.");
						}
						_RV = invokeDateAdj(v.dateVal, rv2.intVal);
						scan.getNext();
						break;
					case "not":
						_RV.setValues(Boolean.toString(!_RV.boolVal));
						break;
					case "and": case "or":
						rv2 = Expr();
						while( !scan.getNext().equals(";") );
						ResultValue nv = new ResultValue();						
						if( operator.equals("and") )
						{
							if( _RV.boolVal && rv2.boolVal )
							{
								nv.setValues("true");
								return nv;
							}
							else
							{
								nv.setValues("false");
								return nv;
							}
						}
						else
						{
							if( _RV.boolVal || rv2.boolVal )
							{
								nv.setValues("true");
								return nv;
							}
							else
							{
								nv.setValues("false");
								return nv;
							}
						}
						//break;
					default://CONDITIONAL OPERATORS
						_RV = Cond(_RV, operator);
						while( !scan.nextToken.tokenStr.equals(";") && !scan.nextToken.tokenStr.equals(":") ){ scan.getNext(); }
						break;
				}
			}
		}
		else
		{ //OPERAND
			return Operand();
		}
		return _RV;
	}
	private ResultValue Cond(ResultValue rv, String operator) throws Exception 
	{
		ResultValue rv2 = Expr();
		switch(operator)
		{
			case ">":
				return NumericConversion.greaterThan(rv, rv2);
			case "<":
				return NumericConversion.lessThan(rv, rv2);
			case "==":
				return NumericConversion.equalTo(rv, rv2);
			case ">=":
				return NumericConversion.greaterThanETo(rv, rv2);
			case "<=":
				return NumericConversion.lessThanETo(rv, rv2);
			case "!=":
				return NumericConversion.notEqualTo(rv, rv2);
		}
		return null;
	}

	//OPERAND HERE
	private ResultValue Operand()
	{
		ResultValue _RV=null;
		switch(scan.currentToken.primClassif)
		{
			case Token.OPERAND:
				if( !(scan.currentToken.subClassif == Token.IDENTIFIER) )
				{	//Operand Constants
					_RV = new ResultValue();
					_RV.type = scan.currentToken.subClassif;
					_RV.setValues(scan.currentToken.tokenStr);
				}
				else
				{	//Operand Variable
					_RV = sm.getValue(scan.currentToken.tokenStr);
					if( _RV == null ){ handleException("Undeclared Identifier"); }
				}
				break;
		}
		return _RV;
	}
	
	public void ifStmtStart() throws Exception
	{
		scan.getNext();//consume 'if'
		ResultValue result = Expr().boolVal ? ifStmtHead() : ifStmtTail();
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
		if(!scan.currentToken.tokenStr.equals(":")) {
			handleException("If error: expected ':'");
		}
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
		if(!scan.currentToken.tokenStr.equals(":")) {
			handleException("If error: expected ':'");
		}
		scan.getNext();//consume ':'
		
		do
		{
			//DONTDOIT = true;
			if(scan.currentToken.primClassif == Token.EOF) {
				handleException("If Error: No 'endif' found!");
			}
			Statement();
			scan.getNext();
		}while(!scan.currentToken.tokenStr.equals("endif"));
		scan.getNext();
		//scan.getNext();//consume endif
		//scan.getNext();//consume ;
		return null;
	}
	
	public ResultValue whileStmt() throws Exception
	{
		int whileLoops=0, endWhiles = 0;
		int lineNr = scan.iSourceLineNr;
		
		scan.getNext();//consume while
		while( Expr().boolVal )
		{
			do
			{
				while( !scan.currentToken.tokenStr.equals(";") && !scan.currentToken.tokenStr.equals(":") ){ scan.getNext(); }//consume ')'
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
	
	public ResultValue ForLoopStart() throws Exception
	{	
		scan.getNext();//consume the for
		String ctName = scan.currentToken.tokenStr;
		scan.getNext();//consume the ct
		Token resetPos = new Token();
		
		if( scan.currentToken.tokenStr.equals("in") )
		{
			resetPos.iColPos = scan.iColPos;
			resetPos.iSourceLineNr = scan.iSourceLineNr;
			scan.getNext();//consume in
			ResultValue arrayStruct = Expr();
			String arrayName = scan.currentToken.tokenStr;
			scan.getNext();//consume arrayName
			ResultValue cStruct = new ResultValue();
			int arrsize;
			switch(arrayStruct.type)
			{
				case Token.STRING:
					cStruct.type = Token.STRING;
					String arrString = arrayStruct.strVal.substring(1, arrayStruct.strVal.length()-1);
					for( char c : arrString.toCharArray() )
					{
						cStruct.setValues(Character.toString(c));
						sm.putVariable(ctName, cStruct);
						forBody();
						//reset position
						if( c != arrString.charAt(arrString.length()-1) )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							resetPos.tokenStr = "dummy";
							scan.nextToken = resetPos;
							scan.getNext();
							scan.getNext();
						}
					}
				break;
				case Token.INTARRAY:
					cStruct.type = Token.INTEGER;
					arrsize = sm.getIntArraySize(arrayName);
					for( int i=0; i < arrsize; i++ )
					{
						cStruct.setValues(Integer.toString(sm.intArrays.get(arrayName).get(i)));
						sm.putVariable(ctName, cStruct);
						forBody();
						//reset position
						if( sm.intArrays.get(arrayName).get(i) != sm.intArrays.get(arrayName).get(arrsize-1) )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							resetPos.tokenStr = "dummy";
							scan.nextToken = resetPos;
							scan.getNext();
							scan.getNext();
						}
					}
					break;
				case Token.FLOATARRAY:
					cStruct.type = Token.FLOAT;
					arrsize = sm.getFloatArraySize(arrayName);
					for( int i=0; i < arrsize; i++ )
					{
						cStruct.setValues(Double.toString(sm.floatArrays.get(arrayName).get(i)));
						sm.putVariable(ctName, cStruct);
						forBody();
						//reset position
						if( sm.floatArrays.get(arrayName).get(i) != sm.floatArrays.get(arrayName).get(arrsize-1) )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							resetPos.tokenStr = "dummy";
							scan.nextToken = resetPos;
							scan.getNext();
							scan.getNext();
						}
					}
					break;
				case Token.STRINGARRAY:
					cStruct.type = Token.STRING;
					arrsize = sm.getStringArraySize(arrayName);
					for( int i=0; i < arrsize; i++ )
					{
						cStruct.setValues(sm.stringArrays.get(arrayName).get(i));
						sm.putVariable(ctName, cStruct);
						forBody();
						//reset position
						if( sm.stringArrays.get(arrayName).get(i) != sm.stringArrays.get(arrayName).get(arrsize-1) )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							resetPos.tokenStr = "dummy";
							scan.nextToken = resetPos;
							scan.getNext();
							scan.getNext();
						}
					}
					break;
				case Token.BOOLEANARRAY:
					cStruct.type = Token.INTEGER;
					arrsize = sm.boolArrays.get(arrayName).size();//CHANGE TO GETBOOLARRAYSIZE
					for( boolean c : sm.boolArrays.get(arrayName) )
					{
						cStruct.setValues(Boolean.toString(c));
						sm.putVariable(ctName, cStruct);
						forBody();
						//reset position
						if( c != sm.boolArrays.get(arrayName).get(arrsize-1) )
						{
							scan.iSourceLineNr = resetPos.iSourceLineNr;
							scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
							scan.iColPos = resetPos.iColPos;
							resetPos.tokenStr = "dummy";
							scan.nextToken = resetPos;
							scan.getNext();
							scan.getNext();
						}
					}
					break;
			}
		}
		else
		{	//For cv=sv to cond by incr: 
			scan.getNext();//consume =
			ResultValue cStruct = new ResultValue();
			cStruct.assign(Expr());
			cStruct.type = Token.INTEGER;
			sm.putVariable(ctName, cStruct);
			scan.getNext();//consume sv
			if( cStruct.type != Token.INTEGER){}//ERROR HERE
			scan.getNext();//consume to
			int cond = Expr().intVal;
			int inc = 1;
			if( scan.nextToken.tokenStr.equals("by") )//optional by
			{
				scan.getNext();//consume preby
				scan.getNext();//consume by
				inc = Expr().intVal;
			}
			while( !scan.currentToken.tokenStr.equals(":") ){scan.getNext();}
			resetPos.iColPos = scan.currentToken.iColPos;
			resetPos.iSourceLineNr = scan.currentToken.iSourceLineNr;
			for( int i=cStruct.intVal; i < cond; i+=inc)
			{
				sm.putVariable(ctName, cStruct);
				forBody();
				if( !(i+inc >= cond) )
				{
					scan.iSourceLineNr = resetPos.iSourceLineNr;
					scan.textCharM = scan.sourceLineM.get(resetPos.iSourceLineNr).toCharArray();
					scan.iColPos = resetPos.iColPos;
					resetPos.tokenStr = "dummy";
					scan.nextToken = resetPos;
					scan.getNext();
					scan.getNext();
				}
				cStruct.setValues(Integer.toString(cStruct.intVal+=inc));
			}			
		}
		
		return null;
	}
	
	public void forBody() throws Exception
	{
		//scan.getNext();//consume the ':"
		
		do
		{
			scan.getNext(); //consume the ';'
			if(scan.currentToken.primClassif == Token.EOF) {
				handleException("For loop error: No 'endfor' found!");
			}
			Statement();
		}while(!scan.currentToken.tokenStr.equals("endfor") && !scan.nextToken.tokenStr.equals("endfor"));
		while(!scan.currentToken.tokenStr.equals("endfor") ){scan.getNext();}
		scan.getNext();
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
	 * Calculates the difference in years between 2 dates, One of the dates being represented as
	 * a String.
	 * @param date1 more recent date
	 * @param date2String date being compared represented as a String
	 * @return difference in years
	 */
	public ResultValue invokeDateAge(Date date1, String date2String) {
		ResultValue RV = new ResultValue();
		RV.type = Token.INTEGER;
		int diff = 0;
		
		if(!date2String.matches("\"(\\d{4}-?\\d{2}-?\\d{2})\"")) {
			handleException("Date Age error: Expected format of 2nd parameter, 'XXXX-XX-XX'");
		}
		int year = (int)NumericConversion.convert(date2String.substring(1,5));
		int month = (int)NumericConversion.convert(date2String.substring(6,8));
		int day = (int)NumericConversion.convert(date2String.substring(9,11));
		if(month == 2 && (year % 4 != 0) && day > 28) {
			handleException("Date error: Invalid date!");
		}
		Date date2 = new Date(year, month, day);
		
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
	 * @return new date
	 */
	public ResultValue invokeDateAdj(Date date, int days) {
		ResultValue RV = new ResultValue();
		RV.type = Token.DATE;
		Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDate());
		calendar.add(Calendar.DATE, days);
		Date newDate = calendar.getTime();
		RV.strVal = "" + (newDate.getYear() + 1900) + "-" + (newDate.getMonth() + 1) + "-" + newDate.getDate();
		RV.dateVal = newDate;
		
		return RV;
	}
	
	/**
	 * Function for handling exceptions
	 * @param message error message
	 */
	public void handleException(String message) {
		ParserException pe = new ParserException(scan.currentToken.iSourceLineNr, message, scan.sourceFileNm);
		System.out.println();
		System.err.println(pe);
		System.exit(-1);
	}
}