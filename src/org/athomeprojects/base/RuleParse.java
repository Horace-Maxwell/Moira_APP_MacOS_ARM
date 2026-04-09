//### This file created by BYACC 1.8(/Java extension  1.14)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";

package org.athomeprojects.base;

//#line 2 "Rule.yacc"

/**/
/*Moira - A Chinese Astrology Charting Program*/
/*Copyright (C) 2004-2015 At Home Projects*/
/**/
/*This program is free software; you can redistribute it and/or modify*/
/*it under the terms of the GNU General Public License as published by*/
/*the Free Software Foundation; either version 2 of the License, or*/
/*(at your option) any later version.*/
/**/
/*This program is distributed in the hope that it will be useful,*/
/*but WITHOUT ANY WARRANTY; without even the implied warranty of*/
/*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the*/
/*GNU General Public License for more details.*/
/**/
/*You should have received a copy of the GNU General Public License*/
/*along with this program; if not, write to the Free Software*/
/*Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA*/
/**/

/*RuleParse.java created using byacc/j from http://byaccj.sourceforge.net/*/
/*Run with the following options in command window*/
/*yacc -Jpackage=org.athomeprojects.base -Jclass=RuleParse -Jsemantic=Object -Jstack=256 -Jthrows -Jnodebug Rule.yacc*/

import java.util.LinkedHashSet;
import java.util.LinkedList;

//#line 45 "RuleParse.java"

public class RuleParse {

	boolean yydebug; // do I want debug output?
	int yynerrs; // number of errors so far
	int yyerrflag; // was there an error?
	int yychar; // the current working character

	// ########## MESSAGES ##########
	// ###############################################################
	// method: debug
	// ###############################################################
	void debug(String msg) {
		if (yydebug)
			System.out.println(msg);
	}

	// ########## STATE STACK ##########
	final static int YYSTACKSIZE = 256; // maximum stack size
	int statestk[] = new int[YYSTACKSIZE]; // state stack
	int stateptr;
	int stateptrmax; // highest index of stackptr
	int statemax; // state when highest index reached

	// ###############################################################
	// methods: state stack push,pop,drop,peek
	// ###############################################################

	final void state_push(int state) {
		try {
			stateptr++;
			statestk[stateptr] = state;
		} catch (ArrayIndexOutOfBoundsException e) {
			int oldsize = statestk.length;
			int newsize = oldsize * 2;
			int[] newstack = new int[newsize];
			System.arraycopy(statestk, 0, newstack, 0, oldsize);
			statestk = newstack;
			statestk[stateptr] = state;
		}
	}

	final int state_pop() {
		return statestk[stateptr--];
	}

	final void state_drop(int cnt) {
		stateptr -= cnt;
	}

	final int state_peek(int relative) {
		return statestk[stateptr - relative];
	}

	// ###############################################################
	// method: init_stacks : allocate and prepare stacks
	// ###############################################################
	final boolean init_stacks() {
		stateptr = -1;
		val_init();
		return true;
	}

	// ###############################################################
	// method: dump_stacks : show n levels of the stacks
	// ###############################################################
	void dump_stacks(int count) {
		int i;
		System.out.println("=index==state====value=     s:" + stateptr + "  v:"
				+ valptr);
		for (i = 0; i < count; i++)
			System.out.println(" " + i + "    " + statestk[i] + "      "
					+ valstk[i]);
		System.out.println("======================");
	}

	// ########## SEMANTIC VALUES ##########
	// ## **user defined:Object
	String yytext;// user variable to return contextual strings
	Object yyval; // used to return semantic vals from action routines
	Object yylval;// the 'lval' (result) I got from yylex()
	Object valstk[] = new Object[YYSTACKSIZE];
	int valptr;

	// ###############################################################
	// methods: value stack push,pop,drop,peek.
	// ###############################################################
	final void val_init() {
		yyval = new Object();
		yylval = new Object();
		valptr = -1;
	}

	final void val_push(Object val) {
		try {
			valptr++;
			valstk[valptr] = val;
		} catch (ArrayIndexOutOfBoundsException e) {
			int oldsize = valstk.length;
			int newsize = oldsize * 2;
			Object[] newstack = new Object[newsize];
			System.arraycopy(valstk, 0, newstack, 0, oldsize);
			valstk = newstack;
			valstk[valptr] = val;
		}
	}

	final Object val_pop() {
		return valstk[valptr--];
	}

	final void val_drop(int cnt) {
		valptr -= cnt;
	}

	final Object val_peek(int relative) {
		return valstk[valptr - relative];
	}

	// #### end semantic value section ####
	public final static short IDENTIFIER = 257;
	public final static short STRING = 258;
	public final static short EQ = 259;
	public final static short NE = 260;
	public final static short LE = 261;
	public final static short GE = 262;
	public final static short DEF = 263;
	public final static short AGN = 264;
	public final static short APN = 265;
	public final static short ADE = 266;
	public final static short SUE = 267;
	public final static short MUE = 268;
	public final static short DIE = 269;
	public final static short MOE = 270;
	public final static short YYERRCODE = 256;
	final static short yylhs[] = { -1, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 4, 4, 4,
			4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 12, 8, 11,
			11, 9, 9, 13, 13, 14, 14, 14, 14, 14, 14, 14, 14, 10, 10, 10, 15,
			15, };
	final static short yylen[] = { 2, 1, 1, 3, 1, 3, 1, 3, 1, 3, 3, 3, 3, 3, 3,
			1, 3, 3, 1, 3, 3, 3, 1, 4, 1, 2, 2, 2, 2, 2, 2, 2, 2, 0, 6, 1, 3,
			1, 3, 1, 3, 1, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 1, 2, };
	final static short yydefred[] = { 0, 49, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 18, 0, 24, 37, 32, 0, 31, 29, 26, 25, 27,
			28, 30, 0, 0, 0, 39, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 33, 0, 0, 0, 0, 0, 0, 0, 38, 0, 51, 53, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 19, 20, 21, 0, 0, 42, 43, 44, 45, 46, 47, 48, 40, 23,
			0, 0, 34, 0, 0, };
	final static short yydgoto[] = { 14, 34, 16, 17, 18, 19, 20, 21, 22, 23,
			24, 93, 82, 36, 37, 39, };
	final static short yysindex[] = { 143, 0, 0, 143, -107, 143, 143, 143, 143,
			143, 143, 143, 143, 143, 0, -118, -24, -46, -57, 12, -35, 0, -63,
			0, 0, 0, -8, 0, 0, 0, 0, 0, 0, 0, -118, 23, -33, 0, 0, 241, 143,
			143, 143, 143, 143, 143, 143, 143, 143, 143, 143, 143, 143, 143,
			143, 0, 143, 143, 143, 143, 143, 143, 143, 0, 143, 0, 0, -24, -46,
			-57, 12, 12, 12, 12, 12, 12, -35, -35, 0, 0, 0, -93, 143, 0, 0, 0,
			0, 0, 0, 0, 0, 0, -118, -31, 0, 143, -118, };
	final static short yyrindex[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 36, 72, 300, 302, 49, 9, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			-17, 192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 337, 301, 312, 74, 100, 108,
			148, 156, 182, 35, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			-15, 0, 0, 0, -11, };
	final static short yygindex[] = { 0, 4, 11, -1, 14, 280, 17, 78, 457, 0,
			55, 0, 0, 0, -39, 0, };
	final static int YYTABLESIZE = 521;
	static short yytable[];
	static {
		yytable();
	}

	static void yytable() {
		yytable = new short[] { 91, 22, 53, 47, 15, 48, 40, 51, 63, 15, 94, 64,
				52, 95, 41, 42, 13, 83, 84, 85, 86, 87, 88, 89, 41, 90, 35, 41,
				54, 35, 36, 40, 55, 36, 22, 16, 1, 22, 22, 22, 68, 22, 22, 22,
				22, 22, 22, 15, 22, 8, 15, 67, 15, 15, 15, 49, 69, 50, 81, 26,
				17, 22, 22, 22, 22, 22, 76, 77, 0, 15, 15, 15, 2, 16, 13, 0,
				16, 0, 16, 16, 16, 0, 0, 0, 0, 0, 92, 8, 0, 0, 8, 38, 0, 8, 22,
				16, 16, 16, 17, 96, 14, 17, 15, 17, 17, 17, 0, 0, 11, 8, 8, 8,
				13, 2, 54, 13, 2, 66, 13, 0, 17, 17, 17, 0, 22, 22, 22, 0, 16,
				78, 79, 80, 0, 15, 13, 13, 13, 0, 14, 0, 0, 14, 8, 0, 14, 0,
				11, 0, 12, 11, 1, 2, 11, 17, 0, 0, 9, 0, 0, 16, 14, 14, 14, 0,
				0, 2, 0, 13, 11, 11, 11, 0, 0, 8, 0, 0, 11, 0, 0, 9, 7, 4, 10,
				12, 17, 6, 12, 0, 5, 12, 0, 0, 12, 14, 9, 0, 2, 9, 13, 0, 9,
				11, 43, 44, 45, 46, 10, 8, 12, 12, 12, 0, 0, 0, 0, 0, 9, 9, 9,
				0, 10, 0, 0, 10, 14, 0, 10, 0, 0, 22, 22, 0, 11, 22, 22, 22,
				22, 22, 0, 22, 0, 12, 10, 10, 10, 0, 0, 0, 0, 9, 0, 0, 22, 22,
				22, 0, 0, 0, 22, 22, 22, 22, 22, 22, 22, 0, 13, 0, 15, 15, 15,
				15, 12, 0, 11, 10, 0, 9, 7, 4, 9, 12, 0, 6, 0, 0, 5, 56, 57,
				58, 59, 60, 61, 62, 16, 16, 16, 16, 0, 0, 4, 5, 6, 0, 10, 8,
				10, 0, 8, 8, 8, 8, 7, 0, 0, 0, 22, 0, 0, 17, 17, 17, 17, 70,
				71, 72, 73, 74, 75, 0, 0, 0, 0, 13, 13, 13, 13, 3, 4, 5, 6, 4,
				5, 6, 4, 5, 6, 0, 0, 0, 7, 0, 0, 7, 0, 0, 7, 0, 0, 14, 14, 14,
				14, 6, 13, 0, 65, 11, 11, 11, 11, 0, 0, 7, 0, 0, 0, 0, 3, 0, 0,
				3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 0, 0, 0, 0, 1, 2,
				0, 0, 0, 7, 3, 12, 12, 12, 12, 0, 0, 0, 0, 9, 9, 9, 9, 0, 0, 0,
				0, 0, 4, 5, 6, 0, 0, 0, 3, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 10,
				10, 10, 10, 0, 0, 0, 0, 0, 0, 22, 22, 22, 22, 0, 0, 0, 0, 0,
				25, 3, 27, 28, 29, 30, 31, 32, 33, 35, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				2, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 35, 35, 35, 35, 35,
				35, 35, 0, 35, };
	}

	static short yycheck[];
	static {
		yycheck();
	}

	static void yycheck() {
		yycheck = new short[] { 93, 0, 37, 60, 0, 62, 124, 42, 41, 0, 41, 44,
				47, 44, 38, 61, 123, 56, 57, 58, 59, 60, 61, 62, 41, 64, 41,
				44, 91, 44, 41, 124, 40, 44, 33, 0, 0, 36, 37, 38, 41, 40, 41,
				42, 43, 44, 45, 38, 47, 0, 41, 40, 43, 44, 45, 43, 42, 45, 54,
				4, 0, 60, 61, 62, 63, 64, 49, 50, -1, 60, 61, 62, 0, 38, 0, -1,
				41, -1, 43, 44, 45, -1, -1, -1, -1, -1, 82, 38, -1, -1, 41, 13,
				-1, 44, 93, 60, 61, 62, 38, 95, 0, 41, 93, 43, 44, 45, -1, -1,
				0, 60, 61, 62, 38, 41, 91, 41, 44, 39, 44, -1, 60, 61, 62, -1,
				123, 124, 125, -1, 93, 51, 52, 53, -1, 124, 60, 61, 62, -1, 38,
				-1, -1, 41, 93, -1, 44, -1, 38, -1, 0, 41, 257, 258, 44, 93,
				-1, -1, 0, -1, -1, 124, 60, 61, 62, -1, -1, 93, -1, 93, 60, 61,
				62, -1, -1, 124, -1, -1, 33, -1, -1, 36, 37, 38, 0, 40, 124,
				42, 38, -1, 45, 41, -1, -1, 44, 93, 38, -1, 124, 41, 124, -1,
				44, 93, 259, 260, 261, 262, 63, 64, 60, 61, 62, -1, -1, -1, -1,
				-1, 60, 61, 62, -1, 38, -1, -1, 41, 124, -1, 44, -1, -1, 37,
				38, -1, 124, 41, 42, 43, 44, 45, -1, 47, -1, 93, 60, 61, 62,
				-1, -1, -1, -1, 93, -1, -1, 60, 61, 62, -1, -1, -1, 257, 258,
				259, 260, 261, 262, 263, -1, 123, -1, 259, 260, 261, 262, 124,
				-1, 33, 93, -1, 36, 37, 38, 124, 40, -1, 42, -1, -1, 45, 264,
				265, 266, 267, 268, 269, 270, 259, 260, 261, 262, -1, -1, 0, 0,
				0, -1, 63, 64, 124, -1, 259, 260, 261, 262, 0, -1, -1, -1, 124,
				-1, -1, 259, 260, 261, 262, 43, 44, 45, 46, 47, 48, -1, -1, -1,
				-1, 259, 260, 261, 262, 0, 38, 38, 38, 41, 41, 41, 44, 44, 44,
				-1, -1, -1, 38, -1, -1, 41, -1, -1, 44, -1, -1, 259, 260, 261,
				262, 61, 123, -1, 125, 259, 260, 261, 262, -1, -1, 61, -1, -1,
				-1, -1, 41, -1, -1, 44, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, 93, 93, 93, -1, -1, -1, -1, 257, 258, -1, -1, -1, 93, 263,
				259, 260, 261, 262, -1, -1, -1, -1, 259, 260, 261, 262, -1, -1,
				-1, -1, -1, 124, 124, 124, -1, -1, -1, 93, -1, -1, -1, -1, -1,
				124, -1, -1, -1, -1, 259, 260, 261, 262, -1, -1, -1, -1, -1,
				-1, 259, 260, 261, 262, -1, -1, -1, -1, -1, 3, 124, 5, 6, 7, 8,
				9, 10, 11, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				257, 258, -1, -1, -1, -1, 263, -1, -1, -1, -1, -1, -1, -1, -1,
				56, 57, 58, 59, 60, 61, 62, -1, 64, };
	}

	final static short YYFINAL = 14;
	final static short YYMAXTOKEN = 270;
	final static String yyname[] = { "end-of-file", null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, "'!'", null, null, "'$'",
			"'%'", "'&'", null, "'('", "')'", "'*'", "'+'", "','", "'-'", null,
			"'/'", null, null, null, null, null, null, null, null, null, null,
			null, null, "'<'", "'='", "'>'", "'?'", "'@'", null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, "'['", null, "']'", null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			"'{'", "'|'", "'}'", null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, "IDENTIFIER", "STRING", "EQ", "NE", "LE", "GE",
			"DEF", "AGN", "APN", "ADE", "SUE", "MUE", "DIE", "MOE", };
	final static String yyrule[] = { "$accept : top", "top : logical_or",
			"logical_or : logical_and",
			"logical_or : logical_or '|' logical_and",
			"logical_and : equality", "logical_and : logical_and '&' equality",
			"equality : relational_expr",
			"equality : equality '=' relational_expr",
			"relational_expr : additive_expr",
			"relational_expr : relational_expr '<' additive_expr",
			"relational_expr : relational_expr '>' additive_expr",
			"relational_expr : relational_expr LE additive_expr",
			"relational_expr : relational_expr GE additive_expr",
			"relational_expr : relational_expr EQ additive_expr",
			"relational_expr : relational_expr NE additive_expr",
			"additive_expr : multiplicative_expr",
			"additive_expr : additive_expr '+' multiplicative_expr",
			"additive_expr : additive_expr '-' multiplicative_expr",
			"multiplicative_expr : postfix",
			"multiplicative_expr : multiplicative_expr '*' postfix",
			"multiplicative_expr : multiplicative_expr '/' postfix",
			"multiplicative_expr : multiplicative_expr '%' postfix",
			"postfix : unary", "postfix : unary '[' logical_or ']'",
			"unary : primary", "unary : '@' unary", "unary : '%' unary",
			"unary : '$' unary", "unary : '?' unary", "unary : '*' unary",
			"unary : '!' unary", "unary : '-' unary", "unary : DEF unary",
			"$$1 :", "unary : '&' identifier '(' $$1 argument ')'",
			"argument : logical_or", "argument : argument ',' logical_or",
			"primary : identifier", "primary : '(' expression ')'",
			"expression : assignment",
			"expression : expression ',' assignment",
			"assignment : logical_or", "assignment : unary AGN assignment",
			"assignment : unary APN assignment",
			"assignment : unary ADE assignment",
			"assignment : unary SUE assignment",
			"assignment : unary MUE assignment",
			"assignment : unary DIE assignment",
			"assignment : unary MOE assignment", "identifier : IDENTIFIER",
			"identifier : STRING", "identifier : '{' name '}'",
			"name : postfix", "name : name postfix", };

	// #line 266 "Rule.yacc"

	public int index, max;
	public String expr;
	public boolean now;

	private Object result;
	private LinkedList arg_level_list, arg_list;
	private RuleParse me;

	public void init(String str, boolean multi, boolean trace) {
		now = multi;
		expr = str;
		index = 0;
		max = expr.length();
		me = trace ? this : null;
		arg_level_list = new LinkedList();
		result = null;
		if (yydebug)
			debug("--- init parser ---");
	}

	public Object parse() {
		yyparse();
		return result;
	}

	private void yyerror(String mesg) {
		throw new ArrayIndexOutOfBoundsException(mesg);
	}

	private int yylex() {
		if (index >= max)
			return 0;
		char c = expr.charAt(index++);
		yytext = Character.toString(c);
		if (c == '\"') {
			for (; index < max;) {
				c = expr.charAt(index++);
				if (c == '\"')
					break;
				yytext += Character.toString(c);
			}
			yylval = yytext;
			return STRING;
		} else if (c == '>') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return GE;
			} else {
				return c;
			}
		} else if (c == '<') {
			yylval = yytext;
			if (index < max) {
				char v = expr.charAt(index);
				if (v == '=' || v == '<') {
					index++;
					return (v == '=') ? LE : AGN;
				} else {
					return c;
				}
			} else {
				return c;
			}
		} else if (c == '+') {
			yylval = yytext;
			if (index < max) {
				char v = expr.charAt(index);
				if (v == '=' || v == '<') {
					index++;
					return (v == '=') ? ADE : APN;
				} else {
					return c;
				}
			} else {
				return c;
			}
		} else if (c == '-') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return SUE;
			} else {
				return c;
			}
		} else if (c == '*') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return MUE;
			} else {
				return c;
			}
		} else if (c == '/') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return DIE;
			} else {
				return c;
			}
		} else if (c == '%') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return MOE;
			} else {
				return c;
			}
		} else if (c == '!') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return NE;
			} else {
				return c;
			}
		} else if (c == '=') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '=') {
				index++;
				return EQ;
			} else {
				return c;
			}
		} else if (c == '?') {
			yylval = yytext;
			if (index < max && expr.charAt(index) == '?') {
				index++;
				return DEF;
			} else {
				return c;
			}
		} else if (c == '|' || c == '&' || c == '@' || c == '$' || c == ','
				|| c == '(' || c == ')' || c == '[' || c == ']' || c == '{'
				|| c == '}') {
			yylval = yytext;
			return c;
		} else if (isIdentifier(c)) {
			for (; index < max; index++) {
				c = expr.charAt(index);
				if (!isIdentifier(c))
					break;
				yytext += Character.toString(c);
			}
			yylval = yytext;
			return IDENTIFIER;
		} else {
			yyerror("illegal character");
			return -1;
		}
	}

	private boolean isIdentifier(char c) {
		return c == '_' || c == '.' || Character.isDigit(c)
				|| Character.isLowerCase(c) | Character.isUpperCase(c)
				|| c > 0xff;
	}

	// #line 554 "RuleParse.java"
	// ###############################################################
	// method: yylexdebug : check lexer state
	// ###############################################################
	void yylexdebug(int state, int ch) {
		String s = null;
		if (ch < 0)
			ch = 0;
		if (ch <= YYMAXTOKEN) // check index bounds
			s = yyname[ch]; // now get it
		if (s == null)
			s = "illegal-symbol";
		debug("state " + state + ", reading " + ch + " (" + s + ")");
	}

	// The following are now global, to aid in error reporting
	int yyn; // next next thing to do
	int yym; //
	int yystate; // current parsing state from state table
	String yys; // current token string

	// ###############################################################
	// method: yyparse : parse input and execute indicated items
	// ###############################################################
	int yyparse() {
		boolean doaction;
		init_stacks();
		yynerrs = 0;
		yyerrflag = 0;
		yychar = -1; // impossible char forces a read
		yystate = 0; // initial state
		state_push(yystate); // save it
		val_push(yylval); // save empty value
		while (true) // until parsing is done, either correctly, or w/error
		{
			doaction = true;
			// if (yydebug) debug("loop");
			// #### NEXT ACTION (from reduction table)
			for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
				// if (yydebug)
				// debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
				if (yychar < 0) // we want a char?
				{
					yychar = yylex(); // get next token
					// if (yydebug) debug(" next yychar:"+yychar);
					// #### ERROR CHECK ####
					if (yychar < 0) // it it didn't work/error
					{
						yychar = 0; // change it to default string (no -1!)
						// if (yydebug)
						// yylexdebug(yystate,yychar);
					}
				}// yychar<0
				yyn = yysindex[yystate]; // get amount to shift by (shift index)
				if ((yyn != 0) && (yyn += yychar) >= 0 && yyn <= YYTABLESIZE
						&& yycheck[yyn] == yychar) {
					// if (yydebug)
					// debug("state "+yystate+", shifting to state "+yytable[yyn]);
					// #### NEXT STATE ####
					yystate = yytable[yyn];// we are in a new state
					state_push(yystate); // save it
					val_push(yylval); // push our lval as the input for next
					// rule
					yychar = -1; // since we have 'eaten' a token, say we need
					// another
					if (yyerrflag > 0) // have we recovered an error?
						--yyerrflag; // give ourselves credit
					doaction = false; // but don't process yet
					break; // quit the yyn=0 loop
				}

				yyn = yyrindex[yystate]; // reduce
				if ((yyn != 0) && (yyn += yychar) >= 0 && yyn <= YYTABLESIZE
						&& yycheck[yyn] == yychar) { // we reduced!
					// if (yydebug) debug("reduce");
					yyn = yytable[yyn];
					doaction = true; // get ready to execute
					break; // drop down to actions
				} else // ERROR RECOVERY
				{
					if (yyerrflag == 0) {
						yyerror("syntax error");
						yynerrs++;
					}
					if (yyerrflag < 3) // low error count?
					{
						yyerrflag = 3;
						while (true) // do until break
						{
							if (stateptr < 0) // check for under & overflow here
							{
								yyerror("stack underflow. aborting..."); // note
								// lower
								// case
								// 's'
								return 1;
							}
							yyn = yysindex[state_peek(0)];
							if ((yyn != 0) && (yyn += YYERRCODE) >= 0
									&& yyn <= YYTABLESIZE
									&& yycheck[yyn] == YYERRCODE) {
								// if (yydebug)
								// debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
								yystate = yytable[yyn];
								state_push(yystate);
								val_push(yylval);
								doaction = false;
								break;
							} else {
								// if (yydebug)
								// debug("error recovery discarding state "+state_peek(0)+" ");
								if (stateptr < 0) // check for under & overflow
								// here
								{
									yyerror("Stack underflow. aborting..."); // capital
									// 'S'
									return 1;
								}
								state_pop();
								val_pop();
							}
						}
					} else // discard this token
					{
						if (yychar == 0)
							return 1; // yyabort
						// if (yydebug)
						// {
						// yys = null;
						// if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
						// if (yys == null) yys = "illegal-symbol";
						// debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
						// }
						yychar = -1; // read another
					}
				}// end error recovery
			}// yyn=0 loop
			if (!doaction) // any reason not to proceed?
				continue; // skip action
			yym = yylen[yyn]; // get count of terminals on rhs
			// if (yydebug)
			// debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
			if (yym > 0) // if count of rhs not 'nil'
				yyval = val_peek(yym - 1); // get current semantic value
			switch (yyn) {
			// ########## USER-SUPPLIED ACTIONS ##########
			case 1:
				// #line 37 "Rule.yacc"
			{
				result = val_peek(0);
			}
				break;
			case 3:
				// #line 45 "Rule.yacc"
			{
				yyval = RuleEntry.evalOr(me, val_peek(2), val_peek(0));
			}
				break;
			case 5:
				// #line 53 "Rule.yacc"
			{
				yyval = RuleEntry.evalAnd(me, val_peek(2), val_peek(0));
			}
				break;
			case 7:
				// #line 62 "Rule.yacc"
			{
				yyval = RuleEntry.setContainment(me, val_peek(2), val_peek(0));
			}
				break;
			case 9:
				// #line 70 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_LT);
			}
				break;
			case 10:
				// #line 74 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_GT);
			}
				break;
			case 11:
				// #line 78 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_LE);
			}
				break;
			case 12:
				// #line 82 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_GE);
			}
				break;
			case 13:
				// #line 86 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_EQ);
			}
				break;
			case 14:
				// #line 90 "Rule.yacc"
			{
				yyval = RuleEntry.evalRel(me, val_peek(2), val_peek(0),
						RuleEntry.OP_NE);
			}
				break;
			case 16:
				// #line 98 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, val_peek(2), val_peek(0),
						RuleEntry.OP_ADD);
			}
				break;
			case 17:
				// #line 102 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, val_peek(2), val_peek(0),
						RuleEntry.OP_SUB);
			}
				break;
			case 19:
				// #line 110 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, val_peek(2), val_peek(0),
						RuleEntry.OP_MUL);
			}
				break;
			case 20:
				// #line 114 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, val_peek(2), val_peek(0),
						RuleEntry.OP_DIV);
			}
				break;
			case 21:
				// #line 118 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, val_peek(2), val_peek(0),
						RuleEntry.OP_MOD);
			}
				break;
			case 23:
				// #line 126 "Rule.yacc"
			{
				yyval = RuleEntry.indexValue(me, val_peek(3), val_peek(1));
			}
				break;
			case 25:
				// #line 134 "Rule.yacc"
			{
				yyval = RuleEntry.evalVariable(me, '@', val_peek(0), false);
			}
				break;
			case 26:
				// #line 138 "Rule.yacc"
			{
				yyval = RuleEntry.evalVariable(me, '%', val_peek(0), false);
			}
				break;
			case 27:
				// #line 142 "Rule.yacc"
			{
				yyval = RuleEntry.evalVariable(me, '$', val_peek(0), false);
			}
				break;
			case 28:
				// #line 146 "Rule.yacc"
			{
				yyval = RuleEntry.evalBoolean(me, val_peek(0));
			}
				break;
			case 29:
				// #line 150 "Rule.yacc"
			{
				yyval = RuleEntry.evalHasEntry(me, val_peek(0), now);
			}
				break;
			case 30:
				// #line 154 "Rule.yacc"
			{
				yyval = RuleEntry.evalNot(me, val_peek(0));
			}
				break;
			case 31:
				// #line 158 "Rule.yacc"
			{
				yyval = RuleEntry.evalExpr(me, "0", val_peek(0),
						RuleEntry.OP_SUB);
			}
				break;
			case 32:
				// #line 162 "Rule.yacc"
			{
				yyval = RuleEntry.evalDefined(me, val_peek(0));
			}
				break;
			case 33:
				// #line 166 "Rule.yacc"
			{
				arg_list = new LinkedList();
				arg_level_list.addFirst(arg_list);
			}
				break;
			case 34:
				// #line 170 "Rule.yacc"
			{
				yyval = RuleEntry.evalFunction(me, val_peek(4), arg_list);
				arg_level_list.removeFirst();
				arg_list = arg_level_list.isEmpty() ? null
						: ((LinkedList) arg_level_list.getFirst());
			}
				break;
			case 35:
				// #line 179 "Rule.yacc"
			{
				arg_list.addLast(val_peek(0));
			}
				break;
			case 36:
				// #line 183 "Rule.yacc"
			{
				arg_list.addLast(val_peek(0));
			}
				break;
			case 38:
				// #line 191 "Rule.yacc"
			{
				yyval = val_peek(1);
			}
				break;
			case 40:
				// #line 199 "Rule.yacc"
			{
				yyval = val_peek(0);
			}
				break;
			case 42:
				// #line 207 "Rule.yacc"
			{
				yyval = RuleEntry.evalAssign(me, val_peek(2), val_peek(0), now);
			}
				break;
			case 43:
				// #line 211 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = new LinkedHashSet();
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.setUnion(obj, val_peek(0)), now);
			}
				break;
			case 44:
				// #line 217 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = "0";
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.evalExpr(me, obj, val_peek(0), RuleEntry.OP_ADD), now);
			}
				break;
			case 45:
				// #line 223 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = "0";
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.evalExpr(me, obj, val_peek(0), RuleEntry.OP_SUB), now);
			}
				break;
			case 46:
				// #line 229 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = "0";
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.evalExpr(me, obj, val_peek(0), RuleEntry.OP_MUL), now);
			}
				break;
			case 47:
				// #line 235 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = "0";
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.evalExpr(me, obj, val_peek(0), RuleEntry.OP_DIV), now);
			}
				break;
			case 48:
				// #line 241 "Rule.yacc"
			{
				Object obj = RuleEntry.evalVariable(me, '$', val_peek(2), true);
				if (obj == null)
					obj = "0";
				yyval = RuleEntry.evalAssign(me, val_peek(2), RuleEntry
						.evalExpr(me, obj, val_peek(0), RuleEntry.OP_MOD), now);
			}
				break;
			case 51:
				// #line 252 "Rule.yacc"
			{
				yyval = val_peek(1);
			}
				break;
			case 53:
				// #line 260 "Rule.yacc"
			{
				yyval = RuleEntry.concatString(me, val_peek(1), val_peek(0));
			}
				break;
			// #line 950 "RuleParse.java"
			// ########## END OF USER-SUPPLIED ACTIONS ##########
			}// switch
			// #### Now let's reduce... ####
			// if (yydebug) debug("reduce");
			state_drop(yym); // we just reduced yylen states
			yystate = state_peek(0); // get new state
			val_drop(yym); // corresponding value drop
			yym = yylhs[yyn]; // select next TERMINAL(on lhs)
			if (yystate == 0 && yym == 0)// done? 'rest' state and at first
			// TERMINAL
			{
				// if (yydebug)
				// debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
				yystate = YYFINAL; // explicitly say we're done
				state_push(YYFINAL); // and save it
				val_push(yyval); // also save the semantic value of parsing
				if (yychar < 0) // we want another character?
				{
					yychar = yylex(); // get next character
					if (yychar < 0)
						yychar = 0; // clean, if necessary
					// if (yydebug)
					// yylexdebug(yystate,yychar);
				}
				if (yychar == 0) // Good exit (if lex returns 0 ;-)
					break; // quit the loop--all DONE
			}// if yystate
			else // else not done yet
			{ // get next state and push, for next yydefred[]
				yyn = yygindex[yym]; // find out where to go
				if ((yyn != 0) && (yyn += yystate) >= 0 && yyn <= YYTABLESIZE
						&& yycheck[yyn] == yystate)
					yystate = yytable[yyn]; // get new state
				else
					yystate = yydgoto[yym]; // else go to new defred
				// if (yydebug)
				// debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
				state_push(yystate); // going again, so push state & val...
				val_push(yyval); // for next action
			}
		}// main loop
		return 0;// yyaccept!!
	}

	// ## end of method parse() ######################################

	// ## run() --- for Thread #######################################
	/**
	 * A default run method, used for operating this parser object in the
	 * background. It is intended for extending Thread or implementing Runnable.
	 * Turn off with -Jnorun .
	 */
	public void run() {
		yyparse();
	}

	// ## end of method run() ########################################

	// ## Constructors ###############################################
	/**
	 * Default constructor. Turn off with -Jnoconstruct .
	 */
	public RuleParse() {
		// nothing to do
	}

	/**
	 * Create a parser, setting the debug to true or false.
	 * 
	 * @param debugMe
	 *            true for debugging, false for no debug.
	 */
	public RuleParse(boolean debugMe) {
		yydebug = debugMe;
	}
	// ###############################################################

}
// ################### END OF CLASS ##############################
