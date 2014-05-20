package br.pucpr;

import java.util.HashMap;
import java.util.LinkedList;

public class Vertex
{
	private String stateName;
	char[] stringAtDecision;
	private boolean finalState = false;
	private HashMap<String, LinkedList<Vertex>> nextStates = new HashMap<String, LinkedList<Vertex>>();

	public char[] getStringAtDecision()
	{
		return stringAtDecision;
	}

	public void setStringAtDecision( char[] stringAtDecision )
	{
		this.stringAtDecision = stringAtDecision;
	}

	public boolean isFinalState()
	{
		return finalState;
	}

	public void setFinalState( boolean finalState )
	{
		this.finalState = finalState;
	}

	public String getStateName()
	{
		return stateName;
	}

	public void setStateName( String stateName )
	{
		this.stateName = stateName;
	}

	public LinkedList<Vertex> getNextStates( String s )
	{
		return nextStates.get( s );
	}

	public void setNextStates( HashMap<String, LinkedList<Vertex>> nextStates )
	{
		this.nextStates = nextStates;
	}

	public void setNextStates( String key, Vertex state )
	{
		if ( this.nextStates.get( key ) == null )
		{
			LinkedList<Vertex> list = new LinkedList<Vertex>();
			list.add( state );
			this.nextStates.put( key, list );
			return;
		}

		this.nextStates.get( key ).add( state );
	}

}
