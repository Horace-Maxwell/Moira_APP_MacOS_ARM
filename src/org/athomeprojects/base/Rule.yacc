%{

//
//Moira - A Chinese Astrology Charting Program
//Copyright (C) 2004-2008 At Home Projects
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//

//RuleParse.java created using byacc/j from http://byaccj.sourceforge.net/
//Run with the following options in command window
//yacc -Jpackage=org.athomeprojects.base -Jclass=RuleParse -Jsemantic=Object -Jstack=256 -Jthrows -Jnodebug Rule.yacc

import java.util.LinkedList;
import java.util.LinkedHashSet;

%}

%token IDENTIFIER STRING EQ NE LE GE DEF AGN APN ADE SUE MUE DIE MOE

%%

top
	: logical_or
		{
			result = $1;
		}
	;

logical_or
	: logical_and
	| logical_or '|' logical_and
		{
			$$ = RuleEntry.evalOr(me, $1, $3);
		}
	;

logical_and
	: equality
	| logical_and '&' equality
		{
			$$ = RuleEntry.evalAnd(me, $1, $3);
		}
	;


equality
	: relational_expr
	| equality '=' relational_expr
		{
			$$ = RuleEntry.setContainment(me, $1, $3);
		}
	;

relational_expr
	: additive_expr
	| relational_expr '<' additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_LT);
		}
	| relational_expr '>' additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_GT);
		}
	| relational_expr LE additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_LE);
		}
	| relational_expr GE additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_GE);
		}
	| relational_expr EQ additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_EQ);
		}
	| relational_expr NE additive_expr
		{
			$$ = RuleEntry.evalRel(me, $1, $3, RuleEntry.OP_NE);
		}
	;
	
additive_expr
	: multiplicative_expr
	| additive_expr '+' multiplicative_expr
		{
			$$ = RuleEntry.evalExpr(me, $1, $3, RuleEntry.OP_ADD);
		}
	| additive_expr '-' multiplicative_expr
		{
			$$ = RuleEntry.evalExpr(me, $1, $3, RuleEntry.OP_SUB);
		}
	;
	
multiplicative_expr
	: postfix
	| multiplicative_expr '*' postfix
		{
			$$ = RuleEntry.evalExpr(me, $1, $3, RuleEntry.OP_MUL);
		}
	| multiplicative_expr '/' postfix
		{
			$$ = RuleEntry.evalExpr(me, $1, $3, RuleEntry.OP_DIV);
		}
	| multiplicative_expr '%' postfix
		{
			$$ = RuleEntry.evalExpr(me, $1, $3, RuleEntry.OP_MOD);
		}
	;
	
postfix
	: unary
	| unary '[' logical_or ']'
		{
			$$ = RuleEntry.indexValue(me, $1, $3);
		}
	;

unary
	: primary 
	| '@' unary
		{
			$$ = RuleEntry.evalVariable(me, '@', $2, false);
		}
	| '%' unary
		{
			$$ = RuleEntry.evalVariable(me, '%', $2, false);
		}
	| '$' unary
		{
			$$ = RuleEntry.evalVariable(me, '$', $2, false);
		}
	| '?' unary
		{
			$$ = RuleEntry.evalBoolean(me, $2);
		}
	| '*' unary
		{
			$$ = RuleEntry.evalHasEntry(me, $2, now);
		}
	| '!' unary
		{
			$$ = RuleEntry.evalNot(me, $2);
		}
	| '-' unary
		{
			$$ = RuleEntry.evalExpr(me, "0", $2, RuleEntry.OP_SUB);
		}
	| DEF unary
		{
			$$ = RuleEntry.evalDefined(me, $2);
		}
	| '&' identifier '('
		{
			arg_list = new LinkedList(); arg_level_list.addFirst(arg_list);
		}
	argument ')'
		{
			$$ = RuleEntry.evalFunction(me, $2, arg_list);
			arg_level_list.removeFirst();
			arg_list = arg_level_list.isEmpty() ? null : ((LinkedList) arg_level_list.getFirst());
		}
	;

argument
	: logical_or
		{
			arg_list.addLast($1);
		}
	| argument ',' logical_or
		{
			arg_list.addLast($3);
		}
	;

primary
	: identifier
	| '(' expression ')'
		{
			$$ = $2;
		}
	;

expression
	: assignment
	| expression ',' assignment
		{
			$$ = $3;
		}
	;

assignment
	: logical_or
	| unary AGN assignment
		{
			$$ = RuleEntry.evalAssign(me, $1, $3, now);
		}
	| unary APN assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = new LinkedHashSet();
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.setUnion(obj, $3), now);
		}
	| unary ADE assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = "0";
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.evalExpr(me, obj, $3, RuleEntry.OP_ADD), now);
		}
	| unary SUE assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = "0";
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.evalExpr(me, obj, $3, RuleEntry.OP_SUB), now);
		}
	| unary MUE assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = "0";
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.evalExpr(me, obj, $3, RuleEntry.OP_MUL), now);
		}
	| unary DIE assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = "0";
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.evalExpr(me, obj, $3, RuleEntry.OP_DIV), now);
		}
	| unary MOE assignment
		{
			Object obj = RuleEntry.evalVariable(me, '$', $1, true);
			if (obj == null) obj = "0";
			$$ = RuleEntry.evalAssign(me, $1, RuleEntry.evalExpr(me, obj, $3, RuleEntry.OP_MOD), now);
		}
	;

identifier
	: IDENTIFIER
	| STRING
	| '{' name '}'
		{
			$$ = $2;
		}
	;

name
	: postfix
	| name postfix
		{
			$$ = RuleEntry.concatString(me, $1, $2);
		}
	;
	
%%

static private char[] multi_key =
	{'>', '<', '+', '-', '*', '/', '%', '.', '!', '=', '?'};
static private String[] multi_val =
	{"=", "=", "=", "=", "=", "=", "=", "=<", "=", "=", "?"};

public int index, max;
public String expr;
public boolean now;

private Object result;
private LinkedList arg_level_list, arg_list;
private RuleParse me;

public void init(String str, boolean multi, boolean trace)
{
	now = multi;
	expr = str;
	index = 0;
	max = expr.length();
	me = trace ? this : null;
	arg_level_list = new LinkedList();
	result = null;
	if (yydebug) debug("--- init parser ---");
}

public Object parse()
{
	yyparse();
 	return result;
}

private void yyerror(String mesg)
{
	throw new ArrayIndexOutOfBoundsException(mesg);
}

private int yylex()
{
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
	} else if (c == '|' || c == '&' || c == '@' || c == '$' || c == ',' || c == '(' ||
		c == ')' || c == '[' || c == ']' || c == '{' || c == '}') {
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

private boolean isIdentifier(char c)
{
	return c == '_' || c == '.' || Character.isDigit(c) || Character.isLowerCase(c) |
		Character.isUpperCase(c) || c > 0xff;
}
