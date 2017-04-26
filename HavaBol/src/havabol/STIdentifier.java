package havabol;

public class STIdentifier extends STEntry
{
	int dclType; //Int, String, Float, Date, Bool
	int structure; //Primitive, Fixed array, unbound array
	String parm; //Parameter type: not a parm, by reference, by value
	String nonLocal; //nonlocal base address ref 0-local 1-sorrounding 2-K-sorrounding 99-Global
	
	public STIdentifier(String symbol, int prime, String dcltype, int struc, String parm, String nonlocal)
	{
		super(symbol, prime);
		switch(dcltype)
		{
		case "Int":
			dclType = Token.INTEGER;
			break;
		case "Float":
			dclType = Token.FLOAT;
			break;
		case "String":
			dclType = Token.STRING;
			break;
		}
		
		structure = struc;
		this.parm = parm;
		nonLocal = nonlocal;
	}
}
