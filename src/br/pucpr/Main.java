package br.pucpr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main
{

	public static void main( String[] args ) throws IOException
	{
		// Imprime cabeçalho com as infos
		System.out.println( "Linguagens Formais e Compiladores, trabalho 1" );
		System.out
				.println( "\nEquipe:\nAndré Luiz Luppi\nGuilherme Felipe da Silva\nGuilherme Alves Ferreira" );
		System.out.println( "\nCurso: Engenharia da Computação" );

		/*
		 * Entrada 1: O programa devera ler do arquivo de entrada a sequencia de
		 * caracteres com o caminho do arquivo que contem as funcoes de
		 * transicao do AFD ou AFN, o conteudo deve respeitar o seguinte
		 * formato: “(estado_atual,entrada_fita)->prox_estado;” E/OU
		 * “(estado_atual,entrada_fita)->{prox_estado1 ,prox_estado2
		 * ...,prox_estadoN};” As transicoes deterministicas devem seguir o
		 * primeiro padrao e as transicoes nao deterministicas o segundo padrao.
		 * 
		 * Exemplo de arquivo contendo um AFD:
		 * (q0,a)->q1;(q1,b)->q2f;(q2f,a)->q2f
		 * 
		 * Exemplo de arquivo contendo um AFD:
		 * (q0,a)->{q1,q2};(q1,b)->q2f;(q2f,a)->{q2f,q0}
		 */

		// Solicita caminho do arquivo que contém o autômato
		System.out.println( "\nEntre com o caminho do arquivo contendo o automato:" );

		// Cria scanner que irá ler a entrada padrão
		Scanner stdIn = new Scanner( System.in );

		// Cria buffer para ler arquivo e passa string recebida da entrada
		// padrão
		@SuppressWarnings( "resource" )
		BufferedReader buffer = new BufferedReader( new FileReader( stdIn.nextLine() ) );

		// Declara string que irá receber o conteúdo do arquivo
		String line = "", L;

		// Lê conteúdo do arquivo
		while ( ( L = buffer.readLine() ) != null )
			line += L;

		// Cria nova máquina de estados (automato)
		StateMachine machine = new StateMachine( line );

		// Declara string que irá receber sentença a ser testada
		String s;

		// Cria novo scaner para receber a sentença a ser testada
		Scanner sysin = new Scanner( System.in );

		/*
		 * Entrada2: O programa devera entao ler da entrada uma sentenç a,que
		 * pode ou nao pertencer a linguagem.
		 */

		// Imprime solicitação na saída padrão
		System.out.print( "Entre com a sentença a ser testada: " );

		// Lê a string a ser testada e guarda em s
		s = sysin.nextLine();

		// Transforma string em um vetor do tipo char
		char[] inputVet = s.toCharArray();

		// Cria uma lista ligada para os caracteres
		LinkedList<Character> inputList = new LinkedList<Character>();

		// Passa os caracteres do vetor para a lista
		for ( int i = 0; i < inputVet.length; i++ )
		{
			inputList.addLast( inputVet[i] );
		}

		// Identifica ser é AFN ou AFD e joga na saída padrão
		if ( machine.isAfn() )
			System.out.println( "\nAFN:" );
		else
			System.out.println( "\nAFD:" );

		// Chama método para validar se a string pertence ou não à linguagem do
		// autômato
		machine.validateString( machine.getInitState(), inputList );

	}
}
