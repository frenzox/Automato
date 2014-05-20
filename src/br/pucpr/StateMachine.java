package br.pucpr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

// Classe StateMachine, representa o aut�mato
public class StateMachine
{
	// Declara v�riavel que ir� conter os estados
	private HashMap<String, Vertex> states = new HashMap<String, Vertex>();

	// Declara vari�vel que ir� contar as decis�es realizadas na valida��o por
	// AFN
	private int decisionCounter = 0;

	// Declara vari�vel que ir� identificar se o aut�mato � n�o-determin�stico
	private boolean afn = false;

	// Declara vari�vel que identifica se terminou a verifica��o da string de
	// entrada
	private boolean verified = false;

	// Declara ponteiro pro estado inicial
	private Vertex initState;

	// Construtor, recebe uma string com os estados seguindo o padr�o adotado
	// pelo professor
	StateMachine( String machine )
	{
		// Pelo padr�o, se possui o caracter "{", � um AFN
		if ( machine.contains( "{" ) )
			this.setAfn( true );

		// Quebra string usando o delimitador ";"
		String transitionFunctions[] = machine.split( Pattern.quote( ";" ) );

		// Cria HashSet tempor�rio apenas para eliminar estados duplicados
		HashSet<String> statesTmp = new HashSet<String>();

		// Para cada transi��o, pega os estados, colocando todos os estados no
		// HashMap criado acima
		for ( String w : transitionFunctions )
		{

			statesTmp.add( w.substring( 1, w.indexOf( "," ) ) );
			statesTmp.add( w.substring( w.indexOf( ">" ) + 1 ) );
		}

		// Para cada estado, cria um novo v�rtice e insere no HashMap de estados
		// do aut�mato
		for ( int i = 0; i < statesTmp.size(); i++ )
		{
			Vertex v = new Vertex();

			v.setStateName( statesTmp.toArray()[i].toString() );

			states.put( statesTmp.toArray()[i].toString(), v );

			// Se for q0 ou q0f, seta como estado inicial
			if ( statesTmp.toArray()[i].toString().equals( "q0" )
					|| statesTmp.toArray()[i].toString().equals( "q0f" ) )
				this.setInitState( v );
		}

		// Popula cada estado, identificando se � estado final e adicionando
		// suas transi��es
		for ( int i = 0; i < transitionFunctions.length; i++ )
		{
			String w = transitionFunctions[i];
			String state = w.substring( 1, w.indexOf( "," ) );
			String input = w.substring( w.indexOf( "," ) + 1, w.indexOf( ")" ) );
			String nextState = w.substring( w.indexOf( ">" ) + 1 );

			if ( state.contains( "f" ) )
				states.get( state ).setFinalState( true );
			if ( nextState.contains( "f" ) )
				states.get( nextState ).setFinalState( true );

			if ( nextState.contains( "{" ) )
			{
				nextState = nextState.substring( nextState.indexOf( "{" ) + 1,
						nextState.indexOf( "}" ) );
				String targets[] = nextState.split( Pattern.quote( "," ) );

				for ( String s : targets )
				{
					states.get( state ).setNextStates( input, states.get( s ) );
				}

			}

			states.get( state ).setNextStates( input, states.get( nextState ) );

		}
	}

	// M�todo recursivo que valida a string de entrada
	@SuppressWarnings( "unchecked" )
	public void validateString( Vertex s, LinkedList<Character> stringToTest )
	{

		// Se a execu��o do aut�mato j� terminou, retorna
		if ( this.isVerified() )
			return;

		// Declara string auxiliar que ir� receber o caracter atual
		String aux = "";

		// Cria uma lista ligada para cada chamada, que ir� receber a senten�a
		// de entrada
		// de forma que quando houver backtrack a string seja restaurada
		LinkedList<Character> input = new LinkedList<Character>();
		input = ( LinkedList<Character> ) stringToTest.clone();

		// Imprime o estado atual e a senten�a de entrada
		System.out.print( "(" + s.getStateName() + "," );
		for ( Character a : input )
		{
			System.out.print( a );
		}
		System.out.print( ")" );

		// Trata se a senten�� de entrada est� vazia e aut�mato est� em um
		// estado final
		if ( input.isEmpty() && s.isFinalState() )
		{
			System.out.println( "\n\nEstado final: SIM" );
			System.out.println( "Senten�a vazia: OK" );
			System.out.println( "ACEITA" );
			this.setVerified( true );
			return;
		}

		// Se senten�a de entrada n�o est� vazia, passa primeiro caracter para a
		// string aux
		if ( !input.isEmpty() )
			aux = input.getFirst().toString();

		// Trata se n�o h� estado destino com o caractere atual ou se a senten�a
		// de entrada est� vazia
		if ( s.getNextStates( aux ) == null || s.getNextStates( aux ).getFirst() == null
				|| input.isEmpty() )
		{
			// Se foi feita alguma decis�o, faz backtrack
			if ( this.decisionCounter > 0 )
				System.out.print( " -> BACKTRACK " );
			// Se a senten�a de entrada est� vazia e n�o � estado final, rejeita
			else if ( input.isEmpty() )
			{
				System.out.println( "\n\nEstado final: N�O" );
				System.out.println( "Senten�a vazia: OK" );
				System.out.println( "REJEITA" );
			}
			// Se n�o � estado final e senten�a n�o est� vazia tamb�m rejeita
			// pois n�o h� mais estado destino
			// com o caractere atual
			else
			{
				System.out.println( "\n\nEstado final: N�O" );
				System.out.println( "Senten�a vazia: N�O" );
				System.out.println( "REJEITA" );
			}

			return;
		}

		// Consome o primeiro caractere da senten�a de entrada
		input.removeFirst();

		// Trata se n�o � transi��o n�o-determin�stica
		if ( s.getNextStates( aux ).size() == 1 )
		{
			System.out.println( " -> " + s.getNextStates( aux ).getFirst().getStateName() );
			this.validateString( s.getNextStates( aux ).getFirst(), input );
		}
		// Trata se for transi��o n�o-determin�stica
		else
		{
			// Incrementa contador de decis�es
			this.decisionCounter++;

			// Declara ponteiro para um estado
			Vertex x;

			for ( int i = 0; i < s.getNextStates( aux ).size() - 1; i++ )
			{
				// Se for ultimo estado, decrementa contador de decis�es pois j�
				// fez todos os backtracks poss�veis
				if ( i == s.getNextStates( aux ).size() - 2 )
					this.decisionCounter--;

				// Aponta para o estado destino
				x = s.getNextStates( aux ).get( i );

				// Se for null, itera
				if ( x.getStateName() == null )
					continue;

				// Se finalizou verifica��o, sai
				if ( this.isVerified() )
					break;

				// Faz tratamento para mostrar andamento na sa�da padr�o
				if ( i >= 1 )
				{
					System.out.println( s.getStateName() );
					System.out.print( "(" + s.getStateName() + "," + aux );
					for ( Character a : input )
					{
						System.out.print( a );
					}
					System.out.print( ")" );
				}
				System.out.print( " = {" );

				for ( Vertex v : s.getNextStates( aux ) )
				{
					if ( !( v == null ) )
						System.out.print( v.getStateName() + ", " );
				}
				System.out.print( "} -> " );

				System.out.println( x.getStateName() );

				// Chamada recursiva passando estado destino
				this.validateString( x, input );
			}

		}
	}

	// M�todo que retorna identifica��o se � AFN
	public boolean isAfn()
	{
		return afn;
	}

	// M�todo para setar se � AFN
	public void setAfn( boolean afn )
	{
		this.afn = afn;
	}

	// M�todo para retornar estado inicial do aut�mato
	public Vertex getInitState()
	{
		return initState;
	}

	// M�todo para setar estado inicial do aut�mato
	public void setInitState( Vertex initState )
	{
		this.initState = initState;
	}

	// M�todo que retorna se verifica��o da senten�a de entrada chegou ao fim
	public boolean isVerified()
	{
		return verified;
	}

	// M�todo para setar se valida��o da senten�a de entrada chegou ao fim
	public void setVerified( boolean verified )
	{
		this.verified = verified;
	}

}
