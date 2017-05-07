package havabol;

/**
 * Class used for converting Strings into numeric data types
 */
public class NumericConversion {

	/**
	 * Converts a string to a numeric value, either Integer or Double
	 * 
	 * @param val
	 *            String being converted
	 * @return numeric value of the string
	 */
	public static Number convert(String val) {
		try {
			return Integer.valueOf(val);
		} catch (NumberFormatException e) {
			try {
				return Double.valueOf(val);
			} catch (NumberFormatException eTwo) {
				eTwo.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Returns the numeric value of a ResultValue object
	 * 
	 * @param val
	 *            ResultValue being converted
	 * @return numeric value of a result value
	 */
	public static Number convertResult(ResultValue val) {
		return convert(val.strVal);
	}

	public static ResultValue add(ResultValue v1, ResultValue v2) {
		ResultValue RV = new ResultValue();

		switch (v1.type) {
		case Token.INTEGER:
		case Token.INTARRAY:
			RV.type = Token.INTEGER;
			RV.intVal = v1.intVal + v2.intVal;
			RV.setValues(Integer.toString(RV.intVal));
			break;
		case Token.FLOAT:
		case Token.FLOATARRAY:
			RV.type = Token.FLOAT;
			RV.doubleVal = v1.doubleVal + v2.doubleVal;
			RV.setValues(Double.toString(RV.doubleVal));
			break;
		}

		return RV;
	}

	public static ResultValue subtract(ResultValue v1, ResultValue v2) {
		ResultValue RV = new ResultValue();

		switch (v1.type) {
		case Token.INTEGER:
		case Token.INTARRAY:
			RV.type = Token.INTEGER;
			RV.intVal = v1.intVal - v2.intVal;
			RV.setValues(Integer.toString(RV.intVal));
			break;
		case Token.FLOAT:
		case Token.FLOATARRAY: // POTENTIALLY NEED A BIG DECIMAL HERE FOR CASES
								// LIKE (- 3.12 3)
			RV.type = Token.FLOAT;
			RV.doubleVal = v1.doubleVal - v2.doubleVal;
			RV.setValues(Double.toString(RV.doubleVal));
			break;
		}

		return RV;
	}

	public static ResultValue mult(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		
		switch(v1.type)
		{
			case Token.INTEGER: case Token.INTARRAY:
				RV.type = Token.INTEGER;
				RV.intVal = v1.intVal * v2.intVal;
				RV.setValues(Integer.toString(RV.intVal));
				break;
			case Token.FLOAT: case Token.FLOATARRAY:
				RV.type = Token.FLOAT;
				RV.doubleVal = v1.doubleVal * v2.doubleVal;
				RV.setValues(Double.toString(RV.doubleVal));
				break;
		}
		
		return RV;
	}

	public static ResultValue div(ResultValue v1, ResultValue v2) {
		ResultValue RV = new ResultValue();

		switch (v1.type) {
		case Token.INTEGER:
		case Token.INTARRAY:
			RV.type = Token.INTEGER;
			RV.intVal = v1.intVal / v2.intVal;
			RV.setValues(Integer.toString(RV.intVal));
			break;
		case Token.FLOAT: case Token.FLOATARRAY:
			RV.type = Token.FLOAT;
			RV.doubleVal = v1.doubleVal / v2.doubleVal;
			RV.setValues(Double.toString(RV.doubleVal));
			break;
		}

		return RV;
	}

	public static ResultValue exp(ResultValue v1, ResultValue v2) {
		ResultValue RV = new ResultValue();

		switch (v1.type) {
		case Token.INTEGER:
		case Token.INTARRAY:
			RV.type = Token.INTEGER;
			RV.intVal = (int) Math.pow(v1.intVal, v2.intVal);
			RV.setValues(Integer.toString(RV.intVal));
			break;
		case Token.FLOAT: case Token.FLOATARRAY:
			RV.type = Token.FLOAT;
			RV.doubleVal = Math.pow(v1.doubleVal, v2.doubleVal);
			RV.setValues(Double.toString(RV.doubleVal));
			break;
		}

		return RV;
	}

	// NEGATION
	public static ResultValue neg(ResultValue v1) {
		ResultValue RV = new ResultValue();
		RV.type = v1.type;
		RV.intVal = -v1.intVal;
		RV.doubleVal = -v1.doubleVal;
		RV.strVal = "-" + v1.strVal;
		return RV;
	}
	
	public static ResultValue concat(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		
		RV.type = v1.type;
		RV.strVal = v1.strVal.substring(0, v1.strVal.length()-1) + v2.strVal.substring(1);
		return RV;
	}
	
	public static ResultValue greaterThan(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;
		switch (v1.type)
		{
			case Token.INTEGER:
				if( v1.intVal > v2.intVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.FLOAT:
				if( v1.doubleVal > v2.doubleVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.STRING:
				int result = v1.strVal.compareTo(v2.strVal);
				if( result > 0 )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
		}
		return RV;
	}
	
	public static ResultValue greaterThanETo(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;
		switch (v1.type)
		{
			case Token.INTEGER:
				if( v1.intVal >= v2.intVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.FLOAT:
				if( v1.doubleVal >= v2.doubleVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.STRING:
				int result = v1.strVal.compareTo(v2.strVal);
				if( result >= 0 )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
		}
		return RV;
	}

	public static ResultValue lessThan(ResultValue v1, ResultValue v2) 
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;
		switch (v1.type)
		{
		case Token.INTEGER:
			if( v1.intVal < v2.intVal )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		case Token.FLOAT:
			if( v1.doubleVal < v2.doubleVal )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		case Token.STRING:
			int result = v1.strVal.compareTo(v2.strVal);
			if( result < 0 )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		}
		return RV;
	}
	
	public static ResultValue lessThanETo(ResultValue v1, ResultValue v2) 
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;
		switch (v1.type)
		{
		case Token.INTEGER:
			if( v1.intVal <= v2.intVal )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		case Token.FLOAT:
			if( v1.doubleVal <= v2.doubleVal )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		case Token.STRING:
			int result = v1.strVal.compareTo(v2.strVal);
			if( result <= 0 )
			{
				RV.setValues(Boolean.toString(true));
			}
			else
			{
				RV.setValues(Boolean.toString(false));
			}
			break;
		}
		return RV;
	}

	public static ResultValue equalTo(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;

		switch(v1.type)
		{
			case Token.INTEGER:
				if( v1.intVal == v2.intVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.FLOAT:
				if( v1.doubleVal == v2.doubleVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.STRING:
				int result = v1.strVal.compareTo(v2.strVal);
				if( result == 0 )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
		}
		return RV;
	}
	
	public static ResultValue notEqualTo(ResultValue v1, ResultValue v2)
	{
		ResultValue RV = new ResultValue();
		RV.type = Token.BOOLEAN;

		switch(v1.type)
		{
			case Token.INTEGER:
				if( v1.intVal != v2.intVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.FLOAT:
				if( v1.doubleVal != v2.doubleVal )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
			case Token.STRING:
				int result = v1.strVal.compareTo(v2.strVal);
				if( result != 0 )
				{
					RV.setValues(Boolean.toString(true));
				}
				else
				{
					RV.setValues(Boolean.toString(false));
				}
				break;
		}
		return RV;
	}
}