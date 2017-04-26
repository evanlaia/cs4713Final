package havabol;
/**
 * Class used to contain data type and resulting values for expressions
 */
public class ResultValue {

	public String strVal;		//display value
	public int intVal;			//int value
	public double doubleVal;	//float value
	public int type;			//data type
	public boolean boolVal;
//	public static final String[] types = //list of possible types
//	{"Unidentified"
//			,"INTEGER"	//1
//			,"FLOAT"	//2
//			,"DOUBLE"	//3
//			,"STRING"	//4
//			,"BOOLEAN"	//5
//	};
	public String terminatingStr;	//used to end a list of things
	
	public ResultValue() 
	{
		intVal = 0;
		doubleVal = 0;
		boolVal = false;
		strVal = "";
	}
	
	public void setIntVal(String val) {
		strVal = val;
		intVal = (Integer)NumericConversion.convert(val);
	}
	
	public void setDoubleVal(String val) {
		strVal = val;
		doubleVal = (Double)NumericConversion.convert(val);
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setValues(String s)
	{
		switch(type)
		{
			case Token.INTEGER: case Token.INTARRAY:
				intVal = Integer.parseInt(s);
				doubleVal = (double)intVal;
				strVal = Integer.toString(intVal);
				break;
			case Token.FLOAT: case Token.FLOATARRAY:
				doubleVal = Double.parseDouble(s);
				intVal = (int)doubleVal;
				strVal = Double.toString(doubleVal);
				break;
			case Token.STRING: case Token.STRINGARRAY:
				strVal = s;
				try
				{ 
					if( s.length() == 1 )
					{
						intVal = Integer.parseInt(s);
						doubleVal = Double.parseDouble(s);
					}
					else
					{
						intVal = Integer.parseInt(s.substring(1, s.length()-1));
						doubleVal = Double.parseDouble(s.substring(1, s.length()-1));
					}
				}catch(NumberFormatException e){;}
				break;
			case Token.BOOLEAN: case Token.BOOLEANARRAY:
				if( s.equals("T") ){ boolVal = true; }
				else if( s.equals("F") ){ boolVal = false; }
				else{}//ERROR INVALID BOOLEAN VALUE
				strVal = s;
				break;
		}
	}

	public void assign(ResultValue rV2) //??POTENTIALLY CHECK FOR INVALID ASSINGNMENT OF DATATYPES (I.E. ASSIGN 3.14 TO AN INT)
	{
		switch(type)
		{
			case Token.INTEGER: case Token.INTARRAY:
				intVal = rV2.intVal;
				this.setValues(Integer.toString(intVal));
				break;
			case Token.FLOAT: case Token.FLOATARRAY:
				doubleVal = rV2.doubleVal;
				this.setValues(Double.toString(doubleVal));
				break;
			case Token.STRING: case Token.STRINGARRAY:
				strVal = rV2.strVal;
				this.setValues(rV2.strVal);
				break;
			case Token.BOOLEAN: case Token.BOOLEANARRAY:
				boolVal = rV2.boolVal;
				this.setValues(rV2.strVal);
				break;
		}
	}
	
}