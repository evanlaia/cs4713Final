package havabol;

public class STControl extends STEntry
{
	int subClassif;

	public STControl(String symbol, int primeclass, int subclass) 
	{
		super(symbol, primeclass);
		this.subClassif = subclass;
	}
}
