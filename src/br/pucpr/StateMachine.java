package br.pucpr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

// Classe StateMachine, representa o autômato
public class StateMachine
{
	// Declara váriavel que irá conter os estados
	private HashMap<String, Vertex> states = new HashMap<String, Vertex>();

	// Declara variável que irá contar as decisões realizadas na validação por
	// AFN
	private int decisionCounter = 0;

	// Declara variável que irá identificar se o autômato é não-determinístico
	private boolean afn = false;

	// Declara variável que identifica se terminou a verificação da string de
	// entrada
	private boolean verified = false;

	// Declara ponteiro pro estado inicial
	private Vertex initState;

	// Construtor, recebe uma string com os estados seguindo o padrão adotado
	// pelo professor
	StateMachine( String machine )
	{
		// Pelo padrão, se possui o caracter "{", é um AFN
		if ( machine.contains( "{" ) )
			this.setAfn( true );

		// Quebra string usando o delimitador ";"
		String transitionFunctions[] = machine.split( Pattern.quote( ";" ) );

		// Cria HashSet temporário apenas para eliminar estados duplicados
		HashSet<String> statesTmp = new HashSet<String>();

		// Para cada transição, pega os estados, colocando todos os estados no
		// HashMap criado acima
		for ( String w : transitionFunctions )
		{

			statesTmp.add( w.substring( 1, w.indexOf( "," ) ) );
			statesTmp.add( w.substring( w.indexOf( ">" ) + 1 ) );
		}

		// Para cada estado, cria um novo vértice e insere no HashMap de estados
		// do autômato
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

		// Popula cada estado, identificando se é estado final e adicionando
		// suas transições
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

	// Método recursivo que valida a string de entrada
	@SuppressWarnings( "unchecked" )
	public void validateString( Vertex s, LinkedList<Character> stringToTest )
	{

		// Se a execução do autômato já terminou, retorna
		if ( this.isVerified() )
			return;

		// Declara string auxiliar que irá receber o caracter atual
		String aux = "";

		// Cria uma lista ligada para cada chamada, que irá receber a sentença
		// de entrada
		// de forma que quando houver backtrack a string seja restaurada
		LinkedList<Character> input = new LinkedList<Character>();
		input = ( LinkedList<Character> ) stringToTest.clone();

		// Imprime o estado atual e a sentença de entrada
		System.out.print( "(" + s.getStateName() + "," );
		for ( Character a : input )
		{
			System.out.print( a );
		}
		System.out.print( ")" );

		// Trata se a sentençå de entrada está vazia e autômato está em um
		// estado final
		if ( input.isEmpty() && s.isFinalState() )
		{
			System.out.println( "\n\nEstado final: SIM" );
			System.out.println( "Sentença vazia: OK" );
			System.out.println( "ACEITA" );
			this.setVerified( true );
			return;
		}

		// Se sentença de entrada não está vazia, passa primeiro caracter para a
		// string aux
		if ( !input.isEmpty() )
			aux = input.getFirst().toString();

		// Trata se não há estado destino com o caractere atual ou se a sentença
		// de entrada está vazia
		if ( s.getNextStates( aux ) == null || s.getNextStates( aux ).getFirst() == null
				|| input.isEmpty() )
		{
			// Se foi feita alguma decisão, faz backtrack
			if ( this.decisionCounter > 0 )
				System.out.print( " -> BACKTRACK " );
			// Se a sentença de entrada está vazia e não é estado final, rejeita
			else if ( input.isEmpty() )
			{
				System.out.println( "\n\nEstado final: NÃO" );
				System.out.println( "Sentença vazia: OK" );
				System.out.println( "REJEITA" );
			}
			// Se não é estado final e sentença não está vazia também rejeita
			// pois não há mais estado destino
			// com o caractere atual
			else
			{
				System.out.println( "\n\nEstado final: NÃO" );
				System.out.println( "Sentença vazia: NÃO" );
				System.out.println( "REJEITA" );
			}

			return;
		}

		// Consome o primeiro caractere da sentença de entrada
		input.removeFirst();

		// Trata se não é transição não-determinística
		if ( s.getNextStates( aux ).size() == 1 )
		{
			System.out.println( " -> " + s.getNextStates( aux ).getFirst().getStateName() );
			this.validateString( s.getNextStates( aux ).getFirst(), input );
		}
		// Trata se for transição não-determinística
		else
		{
			// Incrementa contador de decisões
			this.decisionCounter++;

			// Declara ponteiro para um estado
			Vertex x;

			for ( int i = 0; i < s.getNextStates( aux ).size() - 1; i++ )
			{
				// Se for ultimo estado, decrementa contador de decisões pois já
				// fez todos os backtracks possíveis
				if ( i == s.getNextStates( aux ).size() - 2 )
					this.decisionCounter--;

				// Aponta para o estado destino
				x = s.getNextStates( aux ).get( i );

				// Se for null, itera
				if ( x.getStateName() == null )
					continue;

				// Se finalizou verificação, sai
				if ( this.isVerified() )
					break;

				// Faz tratamento para mostrar andamento na saída padrão
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

	// Método que retorna identificação se é AFN
	public boolean isAfn()
	{
		return afn;
	}

	// Método para setar se é AFN
	public void setAfn( boolean afn )
	{
		this.afn = afn;
	}

	// Método para retornar estado inicial do autômato
	public Vertex getInitState()
	{
		return initState;
	}

	// Método para setar estado inicial do autômato
	public void setInitState( Vertex initState )
	{
		this.initState = initState;
	}

	// Método que retorna se verificação da sentença de entrada chegou ao fim
	public boolean isVerified()
	{
		return verified;
	}

	// Método para setar se validação da sentença de entrada chegou ao fim
	public void setVerified( boolean verified )
	{
		this.verified = verified;
	}

}
