package havabol;
import java.util.ArrayList;
/**
 * Storage manager used for storing and accessing variables and their values
 */
import java.util.HashMap;
public class StorageManager {

	private HashMap<String, ResultValue> ht;	//symbol table containing variables and their values
	public HashMap<String, ArrayList<Integer>> intArrays;
	public HashMap<String, ArrayList<Double>> floatArrays;
	public HashMap<String, ArrayList<String>> stringArrays;
	public HashMap<String, ArrayList<Boolean>> boolArrays;
	
	public StorageManager() {
		ht = new HashMap<String, ResultValue>();
		intArrays = new HashMap<String, ArrayList<Integer>>();
		floatArrays = new HashMap<String, ArrayList<Double>>();
		stringArrays = new HashMap<String, ArrayList<String>>();
		boolArrays = new HashMap<String, ArrayList<Boolean>>();
	}
	
	//add new variable or change existing one
	public void putVariable(String key, ResultValue val) {
		ht.put(key, val);
	}
	
	//retrieve value of given variable
	public ResultValue getValue(String key) {
		return ht.get(key);
	}
	
	public void deleteVariable(String s)
	{
		ht.remove(s);
	}
	
	public void initializeIntArray(String arrayName, int maxElem)
	{
		ArrayList<Integer> newarr = new ArrayList<Integer>();
		if( maxElem > 0 ) //a maxElem was specified
		{
			for(int i=0; i < maxElem; i++)
			{
				newarr.add(new Integer(-10000));
			}
		}
		intArrays.put(arrayName, newarr);
	}
	
	public void initializeFloatArray(String arrayName, int maxElem)
	{
		ArrayList<Double> newarr = new ArrayList<Double>();
		if( maxElem > 0 ) //a maxElem was specified
		{
			for(int i=0; i < maxElem; i++)
			{
				newarr.add(new Double(-10000.0));
			}
		}
		floatArrays.put(arrayName, newarr);
	}
	
	public void initializeStringArray(String arrayName, int maxElem)
	{
		ArrayList<String> newarr = new ArrayList<String>();
		if( maxElem > 0 ) //a maxElem was specified
		{
			for(int i=0; i < maxElem; i++)
			{
				newarr.add(null);
			}
		}
		stringArrays.put(arrayName, newarr);
	}
	
	public void initializeBoolArray(String arrayName, int maxElem)
	{
		ArrayList<Boolean> newarr = new ArrayList<Boolean>();
		if( maxElem > 0 ) //a maxElem was specified
		{
			for(int i=0; i < maxElem; i++)
			{
				newarr.add(null);
			}
		}
		boolArrays.put(arrayName, newarr);
	}
	
	public void insertInteger(int i, String s, boolean noMaxElem)
	{
		if( intArrays.get(s).size() != 0 && intArrays.get(s).get(intArrays.get(s).size()-1) == -10000 )
		{
			for( int j=0; j < intArrays.get(s).size(); j++)
			{
				if( intArrays.get(s).get(j).intValue() == -10000 )
				{
					intArrays.get(s).set(j, Integer.valueOf(i));
					return;
				}
			}
		}
		else
		{
			intArrays.get(s).add(new Integer(i));
		}
		//ARRAY OUT OF BOUNDS ERROR
	}

	public void insertFloat(double i, String s, boolean noMaxElem)
	{
		if( floatArrays.get(s).size() != 0 && floatArrays.get(s).get(floatArrays.get(s).size()-1) == -10000.0 )
		{
			for( int j=0; j < floatArrays.get(s).size(); j++)
			{
				if( floatArrays.get(s).get(j).doubleValue() == -10000.0 )
				{
					floatArrays.get(s).set(j, Double.valueOf(i));
					return;
				}
			}
		}
		else
		{
			floatArrays.get(s).add(new Double(i));
		}
		//ARRAY OUT OF BOUNDS ERROR
	}
	
	public void insertString(String i, String s, boolean noMaxElem)
	{
		if( stringArrays.get(s).size() != 0 && stringArrays.get(s).get(stringArrays.get(s).size()-1) == null )
		{
			for( int j=0; j < stringArrays.get(s).size(); j++)
			{
				if( stringArrays.get(s).get(j) == null )
				{
					stringArrays.get(s).set(j, i);
					return;
				}
			}
		}
		else
		{
			stringArrays.get(s).add(i);
		}
		//ARRAY OUT OF BOUNDS ERROR
	}
	
	public void insertBoolean(boolean i, String s, boolean noMaxElem)
	{
		if( boolArrays.get(s).size() != 0 && boolArrays.get(s).get(boolArrays.get(s).size()-1) == null )
		{
			for( int j=0; j < boolArrays.get(s).size(); j++)
			{
				if( boolArrays.get(s).get(j) == null )
				{
					boolArrays.get(s).set(j, i);
					return;
				}
			}
		}
		else
		{
			boolArrays.get(s).add(i);
		}
		//ARRAY OUT OF BOUNDS ERROR
	}
	
	public void addToArray(ResultValue rv, String arrayName, int idx)
	{
		/*switch(rv.type)
		{
			case Token.INTARRAY:
				//intArrays.get(arrayName).set(idx, Integer.valueOf(rv.intVal));
				intArrays.get(arrayName).add(rv.intVal);
				break;
			case Token.FLOATARRAY:
				//floatArrays.get(arrayName).set(idx, Double.valueOf(rv.doubleVal));
				floatArrays.get(arrayName).add(rv.doubleVal);
				break;
			case Token.STRINGARRAY:
				//stringArrays.get(arrayName).set(idx, rv.strVal);
				stringArrays.get(arrayName).add(rv.strVal);
				break;
			case Token.BOOLEANARRAY:
				//boolArrays.get(arrayName).set(idx, rv.boolVal);
				boolArrays.get(arrayName).add(rv.boolVal);
				break;
		}*/
		switch(rv.type)
		{
			case Token.INTEGER: case Token.INTARRAY:
				//intArrays.get(arrayName).set(idx, Integer.valueOf(rv.intVal));
				//intArrays.get(arrayName).add(rv.intVal);
				this.insertInteger(rv.intVal, arrayName, false);
				break;
			case Token.FLOAT: case Token.FLOATARRAY:
				//floatArrays.get(arrayName).set(idx, Double.valueOf(rv.doubleVal));
				//floatArrays.get(arrayName).add(rv.doubleVal);
				this.insertFloat(rv.doubleVal, arrayName, false);
				break;
			case Token.STRING: case Token.STRINGARRAY:
				//stringArrays.get(arrayName).set(idx, rv.strVal);
				//stringArrays.get(arrayName).add(rv.strVal);
				this.insertString(rv.strVal, arrayName, false);
				break;
			case Token.BOOLEAN: case Token.BOOLEANARRAY:
				//boolArrays.get(arrayName).set(idx, rv.boolVal);
				//boolArrays.get(arrayName).add(rv.boolVal);
				this.insertBoolean(rv.boolVal, arrayName, false);
				break;
		}
	}
	
	public int getFromIntArray(String arrayName, int sub)
	{
		return intArrays.get(arrayName).get(sub).intValue();
	}
	
	public double getFromFloatArray(String arrayName, int sub)
	{
		return floatArrays.get(arrayName).get(sub).doubleValue();
	}
	
	public String getFromStringArray(String arrayName, int sub)
	{
		return stringArrays.get(arrayName).get(sub);
	}
	
	public boolean getFromBoolArray(String arrayName, int sub)
	{
		return boolArrays.get(arrayName).get(sub);
	}

	public int getIntArraySize(String tokenStr)
	{
		int j=0;
		for(int i=0; i < intArrays.get(tokenStr).size(); i++, j++)
		{
			if( intArrays.get(tokenStr).get(i) == -10000 ){return i;}
		}
		return j;
	}


	public int getFloatArraySize(String tokenStr)
	{
		int j=0;
		for(int i=0; i < floatArrays.get(tokenStr).size(); i++, j++)
		{
			if( floatArrays.get(tokenStr).get(i) == -10000.0 ){return i;}
		}
		return j;
	}
	
	public int getStringArraySize(String tokenStr)
	{
		for(int i=0; i < stringArrays.get(tokenStr).size(); i++)
		{
			if( stringArrays.get(tokenStr).get(i) == null ){return i;}
		}
		return 0;
	}

	public void copyFloatArray(String s, ArrayList<Double> arrayList)
	{
		floatArrays.put(s, arrayList);
	}

	public void initArray(String arrayName, ResultValue rv, int mv)
	{	
		switch(rv.type)
		{
			case Token.INTEGER:
				this.initializeIntArray(arrayName, mv);
				rv.type = Token.INTARRAY;
				break;
			case Token.FLOAT:
				this.initializeFloatArray(arrayName, mv);
				rv.type = Token.FLOATARRAY;
				break;
			case Token.STRING:
				this.initializeStringArray(arrayName, mv);
				rv.type = Token.STRINGARRAY;
				break;
			case Token.BOOLEAN:
				this.initializeBoolArray(arrayName, mv);
				rv.type = Token.BOOLEANARRAY;
				break;
		}
	}
}