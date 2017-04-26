package havabol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Scanner {

	public Token currentToken;	//the token established with the most recent call to getNext()
	public SymbolTable symbolTable;	//object responsible for providing symbol definitions
	private final static String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n"; // terminate a token
	public String sourceFileNm;	//source code file name
	public ArrayList<String> sourceLineM; //array list of source text lines
	char[] textCharM; //char[] for the current text line
	int iColPos; //Column position within the current text line
	public Token nextToken; //the token following the currentToken
	int iSourceLineNr; 
	public boolean rootCOMMENT = true;
	public boolean bShowToken = false;
	
	public Scanner(String sourceFileNm, SymbolTable symbolTable) throws Exception 
	{
		this.sourceFileNm = sourceFileNm;
		this.symbolTable = symbolTable;
		this.sourceLineM = new ArrayList<String>();
		this.sourceLineM.add("");
		this.iColPos = 0;
		this.iSourceLineNr = 1;
		this.nextToken = new Token("dummy");
		
		BufferedReader reader;
		String currentLine;
		try 
		{
			reader = new BufferedReader(new FileReader(this.sourceFileNm));
			while((currentLine = reader.readLine()) != null) 
			{
				sourceLineM.add(currentLine);
			}
			reader.close();
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
		//System.out.println(iSourceLineNr + " " + sourceLineM.get(iSourceLineNr));
		getNext();
		nextToken.iSourceLineNr = this.iSourceLineNr;
		nextToken.iColPos = 0;
	}

	public String getNext() throws Exception 
	{
		while(iColPos < textCharM.length && Character.isWhitespace(this.textCharM[iColPos]) ){ iColPos++; }
		currentToken = nextToken;
		if( iColPos >= textCharM.length && iSourceLineNr+1 > sourceLineM.size()-1 ){nextToken = new Token();}
		else if( iColPos >= textCharM.length )
		{ 
			do
			{
				iSourceLineNr++;
				//System.out.println(iSourceLineNr + " " + sourceLineM.get(iSourceLineNr));
				textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
				iColPos = 0;
			}
			while( textCharM.length == 0 );
		}
		
		if( !currentToken.tokenStr.isEmpty() && iColPos < textCharM.length ){
			//while( Character.isWhitespace(this.textCharM[iColPos]) ){ iColPos++; } //skip over whitespace
			while(iColPos < textCharM.length && Character.isWhitespace(this.textCharM[iColPos]) )
			{ 
				iColPos++;
				if( iColPos >= textCharM.length ){ getNext(); return currentToken.tokenStr; }
			}
			int oldPos = iColPos;
			nextToken = oldPos == checkDelim(textCharM) ? new Token(sourceLineM.get(iSourceLineNr).substring(iColPos, ++iColPos)) : new Token(sourceLineM.get(iSourceLineNr).substring(oldPos, iColPos));
			nextToken.iColPos = oldPos;
			nextToken.iSourceLineNr = iSourceLineNr;
			if( nextToken.tokenStr.equals("\"") || nextToken.tokenStr.equals("\'") )
			{
 				int[] res = this.validateString(sourceLineM.get(iSourceLineNr).substring(nextToken.iColPos));
				if( res[0] == -1 ){ throw new Exception("String error on line " + iSourceLineNr + "."); }
				nextToken.tokenStr = sourceLineM.get(iSourceLineNr).substring(nextToken.iColPos, nextToken.iColPos+res[1]+1);
				iColPos = nextToken.iColPos + res[1] + 1;
			}
		}
		
		switch(currentToken.tokenStr)
		{
			case "T": case "F":
				currentToken.primClassif = Token.OPERAND;
				currentToken.subClassif = Token.BOOLEAN;
				break;
			case "+": case "-": case "*": case "/": case "#": case "^":
			case ">": case "<": case "!": case "=": case ">=": case "<=": case "!=": case "==":
				switch(nextToken.tokenStr)
				{
					case "=":
						nextToken.tokenStr = currentToken.tokenStr.concat(nextToken.tokenStr);
						nextToken.tokenStr = this.getNext();
						currentToken.iColPos--;
						break;
					case "/":
						iColPos = textCharM.length;
						boolean originalVal = rootCOMMENT;
						if( this.rootCOMMENT )
						{
							rootCOMMENT = false;
						}
						getNext();
						rootCOMMENT = originalVal;
						if( this.rootCOMMENT )
						{
							return getNext();
						}
						break;
				}
				currentToken.primClassif = Token.OPERATOR;
				break;
			case "(": case ":": case ";": case ",": case "[": case "]":
				currentToken.primClassif = Token.SEPARATOR;
				break;
			case ")":
				currentToken.primClassif = Token.RT_PAREN;
				break;
			case "":
				currentToken.primClassif = Token.EOF;
				break;
			default:
				if( Character.isDigit(currentToken.tokenStr.charAt(0)) ) //potentially a numerical constant
				{
					switch(validateNumeric(currentToken.tokenStr))
					{
						case -1:
							throw new Exception("Numerical constant error on line " + iSourceLineNr + ".");
						case 0:
							currentToken.primClassif = Token.OPERAND;
							currentToken.subClassif = Token.INTEGER;
							break;
						case 1:
							currentToken.primClassif = Token.OPERAND;
							currentToken.subClassif = Token.FLOAT;
							break;
					}
				}
				else if( currentToken.tokenStr.charAt(0) == '\'' || currentToken.tokenStr.charAt(0) == '\"' )
				{
					currentToken.primClassif = Token.OPERAND;
					currentToken.subClassif = Token.STRING;
				}
				else
				{
					if( this.symbolTable.getSymbol(currentToken.tokenStr) != null )
					{
						switch(this.symbolTable.getSymbol(currentToken.tokenStr).primClassif)
						{
							case Token.FUNCTION:
								STFunction val = (STFunction) symbolTable.getSymbol(currentToken.tokenStr);
								currentToken.primClassif = val.primClassif;
								currentToken.subClassif = val.definedBy;
								break;
							case Token.CONTROL:
								STControl val2 = (STControl) symbolTable.getSymbol(currentToken.tokenStr);
								currentToken.primClassif = val2.primClassif;
								currentToken.subClassif = val2.subClassif;
								if( currentToken.subClassif == Token.DECLARE )
								{
									this.symbolTable.putSymbol(nextToken.tokenStr, new STIdentifier(nextToken.tokenStr, Token.OPERAND, currentToken.tokenStr, Token.PRIM_CLASS_MAX, "", ""));
								}
								break;
							case Token.OPERATOR:
								STEntry val3 = (STEntry) symbolTable.getSymbol(currentToken.tokenStr);
								currentToken.primClassif = val3.primClassif;
								break;
							case Token.OPERAND:
								currentToken.primClassif = symbolTable.getSymbol(currentToken.tokenStr).primClassif;
								currentToken.subClassif = Token.IDENTIFIER;
								break;
							//default:
								//return currentToken.tokenStr;
						}
					}
					else
					{
						currentToken.primClassif = Token.OPERAND;
						currentToken.subClassif = Token.IDENTIFIER;
					}
				}
				break;
		}
		
		if( bShowToken )
		{
			System.out.print("\n" + "DEBUG: ");
			currentToken.printToken();
		}
		return currentToken.tokenStr;
	}
	
	private int checkDelim(char[] arr) 
	{
		for( int j=iColPos; j < arr.length; j++, iColPos++)
		{	
			for( int i=0; i < delimiters.length(); i++)
			{
				if( arr[j] == delimiters.charAt(i) )
				{
					return j;
				}
			}
		}
		return -1;
	}
	
	private int validateNumeric(String s)
	{
		//-1 not a valid numeric
		//0 int
		//1 float
		//Numeric Constants start with a number
		//No negative Numeric constants
		//int consts do not contain a decimal point
		//float constants contain one decimal point
		if( !Character.isDigit(s.charAt(0)) )
		{
			return -1;
		}
		
		int decimalPoints = 0;
		for(char c : s.toCharArray())
		{
			if( !Character.isDigit(c) )
			{
				if( c == '.' )
				{
					decimalPoints++;
				}
				else
					return -1;
			}
		}
		
		if( decimalPoints > 1 ){ return -1; }
		else if( decimalPoints == 1 ){ return 1; }
		return 0;
	}

	public int[] validateString(String s)
	{
		char[] arr = s.toCharArray();
		char match = arr[0];
		int[] returnable = {0, 0};
		for(int i=1; i < arr.length; i++ )
		{
			if( arr[i] == match )
			{
				returnable[0]++;
				returnable[1] = i;
				return returnable;
			}
			else if( arr[i] == '\\' )
			{
				switch(arr[++i])
				{
					case '\"': case '\'': case 't': case 'n': case 'r':
						break;
					default:
						returnable[0] = -1;
						return returnable;
				}
			}
		}
		
		if( returnable[0] != 1 )
		{
			returnable[0] = -1;
			return returnable;
		}
		return returnable;
	}
}
