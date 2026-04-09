//
//Moira - A Chinese Astrology Charting Program
//Copyright (C) 2004-2015 At Home Projects
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
package org.athomeprojects.base;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class RuleEntry {
	static public final String OP_GT = ">";

	static public final String OP_GE = ">=";

	static public final String OP_LT = "<";

	static public final String OP_LE = "<=";

	static public final String OP_EQ = "==";

	static public final String OP_NE = "!=";

	static public final char OP_ADD = '+';

	static public final char OP_SUB = '-';

	static public final char OP_MUL = '*';

	static public final char OP_DIV = '/';

	static public final char OP_MOD = '%';

	static private final int RANK_SHIFT = 8;

	static private final int NORMAL_RANK = (1 << (2 * RANK_SHIFT));

	static private final int LEVEL_SHIFT = 24;

	static private final int RANK_MASK = (1 << LEVEL_SHIFT) - 1;

	static private final String INDENT = "    ";

	static private final boolean TRACE_PARSER = false;

	private String name, conditions;

	private boolean plus, never, trace;

	private int level, rank;

	private LinkedList reject;

	private int tag;

	private boolean valid, hidden, evaluated, multi;

	private Object result;

	static private boolean male, show_exception, use_alias, style_use_alias,
			rule_use_alias;

	static private String indent, separator, space, trace_variable;

	static private int single_tag, multi_tag, display_level, fill_max_styles;

	static private Hashtable eval_table, second_eval_table, map_table,
			second_map_table, out_table, style_table, style_map_table,
			rule_table, rule_map_table, rule_out_table;

	static private Hashtable reject_table, state_table, now_state_table;

	static private BaseTab out;

	static private String[] full_zodiac, full_stellar_signs;

	public RuleEntry(String s_name, String s_cond, int s_level, int s_rank,
			boolean s_plus, boolean s_multi, boolean s_never) {
		// process specific reject
		for (int val = s_cond.lastIndexOf('~'); val >= 0; val = s_cond
				.lastIndexOf('~')) {
			if (reject == null)
				reject = new LinkedList();
			reject.add(s_cond.substring(val + 1));
			s_cond = s_cond.substring(0, val);
		}
		name = s_name;
		conditions = s_cond;
		level = s_level;
		rank = s_rank;
		plus = s_plus;
		multi = s_multi;
		never = s_never;
	}

	private Object evalRule(RuleParse parser, boolean check_level,
			boolean no_exception) {
		if (multi) {
			if (now_state_table == null) {
				valid = false;
				return null;
			}
			if (multi_tag != tag)
				initRule();
		} else {
			if (single_tag != tag)
				initRule();
		}
		if (evaluated || check_level && (level < display_level || hidden))
			return result;
		evaluated = true;
		if (parser == null)
			parser = new RuleParse(TRACE_PARSER);
		parser.init(conditions, multi, trace);
		if (trace) {
			out.appendLine(indent + "--- begin tracing \"" + traceName(parser)
					+ "\" ---");
			showParserLocation(parser, null, null, null, null, null, null);
		}
		try {
			result = parser.parse();
		} catch (ArrayIndexOutOfBoundsException e) {
			result = null;
			if (show_exception && !no_exception) {
				out.appendLine(indent + "--- beign \"" + name
						+ "\" failure report (" + e.getMessage() + ") ---");
				showParserLocation(parser, null, null, null, null, null, null);
				showParserLocation(parser, "failed at", null, null, null, null,
						null);
				out.appendLine(indent + "--- end \"" + name
						+ "\" failure report (" + e.getMessage() + ") ---");
			}
		}
		if (result != null) {
			String val = setToString(result);
			valid = val.equals("t");
		} else {
			valid = false;
		}
		if (trace) {
			out.append(indent + "--- end tracing \"" + traceName(parser)
					+ "\" result is ");
			if (result == null)
				out.append("null");
			else
				showSetContent(null, result, 3);
			out.appendLine(" ---");
		}
		if (valid)
			reject(parser);
		if (trace)
			out.appendLine();
		return result;
	}

	static private Object traceEvalRule(RuleParse parser, RuleEntry entry,
			boolean no_exception) {
		boolean trace_sav = entry.trace;
		String indent_sav = indent;
		entry.trace = true;
		indent += INDENT;
		Object obj = entry.evalRule(parser, false, no_exception);
		entry.trace = trace_sav;
		indent = indent_sav;
		return obj;
	}

	private String map(RuleParse parser) {
		if (parser == null)
			parser = new RuleParse(TRACE_PARSER);
		parser.init(conditions, multi, false);
		try {
			Object val = parser.parse();
			return setToString(val);
		} catch (ArrayIndexOutOfBoundsException e) {
			return name;
		}
	}

	private void reject(RuleParse parser) {
		if (reject == null)
			return;
		for (ListIterator iter = reject.listIterator(); iter.hasNext();) {
			parser.init((String) iter.next(), false, false);
			try {
				Object val = parser.parse();
				rejectEntry(parser, setToString(val));
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	private void rejectEntry(RuleParse parser, String key) {
		RuleEntry entry = (RuleEntry) eval_table.get(key);
		if (entry != null) {
			if (!multi && single_tag != entry.tag || multi
					&& multi_tag != entry.tag) {
				entry.initRule();
			}
			if (entry.level > level || entry.level == level
					&& (entry.rank >> RANK_SHIFT) > (rank >> RANK_SHIFT))
				return;
			if (!entry.hidden) {
				entry.hidden = true;
				if (trace) {
					boolean trace_sav = entry.trace;
					String indent_sav = indent;
					entry.trace = true;
					entry.reject(parser);
					entry.trace = trace_sav;
					indent = indent_sav;
				} else {
					entry.reject(parser);
				}
			}
		} else {
			reject_table.put(key, key);
			if (trace) {
				out.appendLine(indent + "    \"" + traceName(parser)
						+ "\" rejects \"" + key + "\"");
			}
		}
	}

	private String traceName(RuleParse parser) {
		String str = name;
		Hashtable table = (second_map_table != null) ? second_map_table
				: map_table;
		RuleEntry entry = (RuleEntry) table.get(name);
		if (entry != null)
			str = entry.map(parser) + "=>" + str;
		return str;
	}

	private void initRule() {
		tag = multi ? multi_tag : single_tag;
		valid = hidden = evaluated = false;
		result = null;
	}

	static public void showSetContent(String prefix, Object object,
			int num_entry) {
		if (prefix != null)
			out.append(prefix);
		if (object instanceof String) {
			out.append((String) object);
		} else {
			int n = 0;
			String fill = null;
			out.append("[");
			for (Iterator iter = ((LinkedHashSet) object).iterator(); iter
					.hasNext();) {
				String val = (String) iter.next();
				if (n > 0) {
					if ((n % num_entry) == 0) {
						if (prefix == null) {
							out.append(separator + "...");
							break;
						}
						out.appendLine();
						if (fill == null)
							fill = " " + Resource.getSpaceFilled(prefix);
						out.append(fill);
					} else {
						out.append(separator);
					}
				}
				out.append(val);
				n++;
			}
			out.append("]");
		}
		if (prefix != null)
			out.appendLine();
	}

	static public Object evalVariable(RuleParse parser, char type, Object a,
			boolean no_exception) throws ArrayIndexOutOfBoundsException {
		String key = Character.toString(type) + setToString(a);
		Object object = (Object) getFromStateTable(key);
		if (object == null) {
			if (use_alias)
				object = evalAlias(parser, key, no_exception);
			if (object == null) {
				if (no_exception)
					return null;
				throw new ArrayIndexOutOfBoundsException(key + " not defined");
			}
		}
		if (object instanceof LinkedHashSet) {
			if (type != '$') // illegal set
				throw new ArrayIndexOutOfBoundsException("illegal set " + key);
			if (((LinkedHashSet) object).size() == 1)
				object = setToString(object);
		}
		if (parser != null)
			showParserLocation(parser, key + "=", object, null, null, null,
					null);
		return object;
	}

	static public Object evalBoolean(RuleParse parser, Object a) {
		String key = setToString(a);
		String t_key = "?" + key;
		String val = (String) getFromStateTable(t_key);
		if (val != null) {
			if (parser != null)
				showParserLocation(parser, t_key + "=", val, null, null, null,
						null);
			return val;
		}
		RuleEntry entry;
		if (use_alias) {
			Object object = evalAlias(parser, t_key, false);
			if (object != null)
				return object;
		}
		entry = (RuleEntry) eval_table.get(key);
		String old_key = "";
		if (entry != null) {
			if (parser != null) {
				Object obj = traceEvalRule(null, entry, false);
				showParserLocation(parser, t_key + "=", obj, null, null, null,
						null);
				return obj;
			} else {
				return entry.evalRule(null, false, false);
			}
		} else if (second_map_table != null) {
			entry = (RuleEntry) second_map_table.get("__" + key);
			if (entry != null) {
				if (entry != null)
					old_key = t_key + "=>";
				key = entry.map(null);
				t_key = "?" + key;
			}
			entry = (RuleEntry) second_eval_table.get(key);
			if (entry != null) {
				if (parser != null) {
					Object obj = traceEvalRule(null, entry, false);
					showParserLocation(parser, old_key + t_key + "=", obj,
							null, null, null, null);
					return obj;
				} else {
					return entry.evalRule(null, false, false);
				}
			}
		}
		if (entry != null)
			showParserLocation(parser, old_key + t_key + "=f", null, null,
					null, null, null);
		return "f";
	}

	static public Object evalAlias(RuleParse parser, String key,
			boolean no_exception) {
		RuleEntry entry = (RuleEntry) map_table.get(key);
		if (entry == null)
			return null;
		String cond = entry.conditions;
		if (key.startsWith("$")
				&& (!cond.startsWith("{") || !cond.endsWith("}")))
			return cond;
		Object object;
		if (parser != null) {
			object = traceEvalRule(null, entry, no_exception);
			showParserLocation(parser, key + "<-", object, null, null, null,
					null);
		} else {
			object = entry.evalRule(null, false, no_exception);
		}
		if (object != null)
			putToStateTable(key, object, entry.multi);
		return object;
	}

	static public Object evalHasEntry(RuleParse parser, Object a, boolean now) {
		String arg = setToString(a);
		Object obj = evalVariable(parser, '$', arg, false);
		String val = emptySet(obj) ? "f" : "t";
		if (parser != null)
			showParserLocation(parser, "*" + arg + "=", val, null, null, null,
					null);
		return val;
	}

	static public Object evalNot(RuleParse parser, Object a) {
		String s_a = RuleEntry.setToString(a);
		String val = (s_a == "t") ? "f" : "t";
		if (parser != null)
			showParserLocation(parser, "!" + s_a + "=", val, null, null, null,
					null);
		return val;
	}

	static public Object evalAnd(RuleParse parser, Object a, Object b) {
		String s_a = setToString(a);
		String s_b = setToString(b);
		String val = (s_a.equals("t") && s_b.equals("t")) ? "t" : "f";
		if (parser != null)
			showParserLocation(parser, s_a + "&" + s_b + "=", val, null, null,
					null, null);
		return val;
	}

	static public Object evalOr(RuleParse parser, Object a, Object b) {
		String s_a = setToString(a);
		String s_b = setToString(b);
		String val = (s_a.equals("t") || s_b.equals("t")) ? "t" : "f";
		if (parser != null) {
			showParserLocation(parser, s_a + "|" + s_b + "=", val, null, null,
					null, null);
		}
		return val;
	}

	static public Object evalAssign(RuleParse parser, Object a, Object b,
			boolean now) throws ArrayIndexOutOfBoundsException {
		String key = '$' + setToString(a);
		putToStateTable(key, b, now);
		if (parser != null) {
			showParserLocation(parser, key + "<<", b, null, null, null, null);
		}
		return b;
	}

	static public Object evalExpr(RuleParse parser, Object a, Object b,
			char type) throws ArrayIndexOutOfBoundsException {
		String s_a = setToString(a);
		String s_b = setToString(b);
		double d_b = FileIO.parseDouble(s_b, Double.NEGATIVE_INFINITY, false);
		if (d_b == Double.NEGATIVE_INFINITY)
			throw new ArrayIndexOutOfBoundsException(s_b + " not a number");
		double d_a = FileIO.parseDouble(s_a, Double.NEGATIVE_INFINITY, false);
		if (d_a == Double.NEGATIVE_INFINITY) {
			if (type == OP_ADD || type == OP_SUB)
				return shiftValue(parser, s_a, s_b, type == OP_ADD);
			throw new ArrayIndexOutOfBoundsException(s_a + " not a number");
		}
		double d;
		if (type == OP_ADD) {
			d = d_a + d_b;
		} else if (type == OP_SUB) {
			d = d_a - d_b;
		} else if (type == OP_MUL) {
			d = d_a * d_b;
		} else if (type == OP_DIV) {
			d = d_a / d_b;
		} else {
			int i = (int) d_b;
			if (i <= 0)
				throw new ArrayIndexOutOfBoundsException(s_b + " <= 0");
			d = d_a % i;
		}
		String val = Double.toString(d);
		if (parser != null) {
			showParserLocation(parser, s_a + type + s_b + "=", val, null, null,
					null, null);
		}
		return val;
	}

	static public Object evalRel(RuleParse parser, Object a, Object b,
			String type) throws ArrayIndexOutOfBoundsException {
		String s_a = setToString(a);
		String s_b = setToString(b);
		double d_a = FileIO.parseDouble(s_a, Double.NEGATIVE_INFINITY, false);
		double d_b = FileIO.parseDouble(s_b, Double.NEGATIVE_INFINITY, false);
		if (d_a == Double.NEGATIVE_INFINITY || d_b == Double.NEGATIVE_INFINITY)
			throw new ArrayIndexOutOfBoundsException(s_a + " or " + s_b
					+ " not a number");
		boolean b_val;
		if (type.equals(OP_GT)) {
			b_val = d_a > d_b;
		} else if (type.equals(OP_LT)) {
			b_val = d_a < d_b;
		} else if (type.equals(OP_GE)) {
			b_val = d_a >= d_b;
		} else if (type.equals(OP_LE)) {
			b_val = d_a >= d_b;
		} else if (type.equals(OP_EQ)) {
			b_val = d_a == d_b;
		} else {
			b_val = d_a != d_b;
		}
		String val = b_val ? "t" : "f";
		if (parser != null) {
			showParserLocation(parser, s_a + type + s_b + "=", val, null, null,
					null, null);
		}
		return val;
	}

	static public Object evalFunction(RuleParse parser, Object a,
			LinkedList arg_list) throws ArrayIndexOutOfBoundsException {
		String func = setToString(a);
		RuleEntry entry = (RuleEntry) map_table.get("&" + func);
		if (entry != null)
			return evalUserFunction(parser, entry, arg_list);
		boolean fail = false;
		Object val = null;
		int num_arg = arg_list.size();
		if (func.equalsIgnoreCase("if")) {
			if (num_arg >= 2) {
				int i = 1;
				for (; i < num_arg; i += 2) {
					if (setToString(evalArg(parser, arg_list.get(i - 1)))
							.equals("t")) {
						val = evalArg(parser, arg_list.get(i));
						break;
					}
				}
				if (i >= num_arg) {
					val = (i == num_arg) ? evalArg(parser, arg_list
							.get(num_arg - 1)) : "";
				}
				if (parser != null) {
					showParserLocation(parser, "&" + func + "()=", val, null,
							null, null, null);
				}
			} else {
				fail = true;
			}
		} else if (func.equalsIgnoreCase("eval")
				|| func.equalsIgnoreCase("map")
				|| func.equalsIgnoreCase("test")) {
			String prefix, suffix;
			if (num_arg >= 2 && num_arg <= 3) {
				if (num_arg == 2) {
					prefix = "";
					suffix = setToString(arg_list.get(1));
				} else {
					prefix = setToString(arg_list.get(1));
					suffix = setToString(arg_list.get(2));
				}
				val = evalSet(parser, arg_list.get(0), prefix, suffix, func);
				if (parser != null) {
					showParserLocation(parser, "&" + func + "()=", val, null,
							null, null, null);
				}
			} else {
				fail = true;
			}
		} else if (func.equalsIgnoreCase("set")) {
			LinkedHashSet set = new LinkedHashSet();
			set.addAll(arg_list);
			val = set;
		} else if (num_arg == 4) {
			Object arg_0 = arg_list.get(0);
			Object arg_1 = arg_list.get(1);
			Object arg_2 = arg_list.get(2);
			Object arg_3 = arg_list.get(3);
			if (func.equalsIgnoreCase("offset")) {
				val = positionOffset(arg_0, arg_1, arg_2, arg_3);
			} else {
				fail = true;
			}
			if (parser != null && !fail) {
				showParserLocation(parser, "&" + func + "()=", val, null, null,
						null, null);
			}
		} else if (num_arg == 3) {
			Object arg_0 = arg_list.get(0);
			Object arg_1 = arg_list.get(1);
			Object arg_2 = arg_list.get(2);
			if (func.equalsIgnoreCase("format")) {
				val = formatString(arg_0, arg_1, arg_2);
			} else if (func.equalsIgnoreCase("prefix")
					|| func.equalsIgnoreCase("suffix")) {
				val = extendString(arg_0, arg_1, arg_2, func
						.equalsIgnoreCase("prefix"));
			} else if (func.equalsIgnoreCase("intersection")) { // set
				// intersection
				val = setTrim(setIntersection(arg_0, arg_1), arg_2);
			} else if (func.equalsIgnoreCase("iter")) {
				val = setIter(parser, arg_0, arg_1, arg_2);
			} else if (func.equalsIgnoreCase("digit")) {
				val = formatDouble(arg_0, arg_1, arg_2);
			} else {
				fail = true;
			}
			if (parser != null && !fail) {
				showParserLocation(parser, "&" + func + "()=", val, null, null,
						null, null);
			}
		} else if (num_arg == 2) {
			Object arg_0 = arg_list.get(0);
			Object arg_1 = arg_list.get(1);
			if (func.equalsIgnoreCase("union")) {
				// set union
				val = setUnion(arg_0, arg_1);
			} else if (func.equalsIgnoreCase("intersection")) {
				// set intersection
				val = setIntersection(arg_0, arg_1);
			} else if (func.equalsIgnoreCase("complement")) {
				// set complement
				val = setComplement(arg_0, arg_1);
			} else if (func.equalsIgnoreCase("contain")) {
				val = setContainString(arg_0, setToString(arg_1));
			} else if (func.equalsIgnoreCase("iter")) {
				val = setIter(parser, arg_0, arg_1, null);
			} else if (func.equalsIgnoreCase("trim")) {
				val = setTrim(arg_0, arg_1);
			} else if (func.equalsIgnoreCase("entry")) {
				val = setEntry(arg_0, arg_1);
			} else if (func.equalsIgnoreCase("split")) {
				val = splitStringToSet(arg_0, arg_1);
			} else {
				fail = true;
			}
			if (parser != null && !fail) {
				showParserLocation(parser, "&" + func + "(", arg_0, ",", arg_1,
						")=", val);
			}
		} else if (num_arg == 1) {
			Object arg = arg_list.getFirst();
			if (func.equalsIgnoreCase("empty")) {
				val = emptySet(arg) ? "t" : "f";
			} else if (func.equalsIgnoreCase("size")) {
				val = Integer.toString((arg instanceof String) ? 1
						: ((LinkedHashSet) arg).size());
			} else if (func.equalsIgnoreCase("import")) {
				val = importSet(arg);
			} else if (func.equalsIgnoreCase("int")
					|| func.equalsIgnoreCase("round")
					|| func.equalsIgnoreCase("abs")) {
				double d = FileIO.parseDouble(setToString(arg),
						Double.NEGATIVE_INFINITY, false);
				if (d == Double.NEGATIVE_INFINITY) {
					fail = true;
				} else {
					if (func.equalsIgnoreCase("round"))
						d += 0.5;
					val = Integer.toString((int) d);
				}
			} else {
				fail = true;
			}
			if (parser != null && !fail) {
				showParserLocation(parser, "&" + func + "(", arg, ")=", val,
						null, null);
			}
		} else {
			fail = true;
		}
		if (fail)
			throw new ArrayIndexOutOfBoundsException("&" + func
					+ " call failed");
		else
			return val;
	}

	static public Object evalUserFunction(RuleParse parser, RuleEntry entry,
			LinkedList arg_list) throws ArrayIndexOutOfBoundsException {
		String val = entry.conditions;
		if (val.startsWith("\""))
			val = val.substring(1);
		int num_arg = arg_list.size();
		for (int i = 0; i < num_arg; i++) {
			char c = (char) ('a' + i);
			String arg = setToString(arg_list.get(i));
			val = val.replaceAll("\\[" + c + "\\]", arg);
		}
		return evalExpression(null, entry.name, val, false, entry.trace);
	}

	static private Object evalExpression(RuleParse parser, String key,
			String value, boolean no_excepiton, boolean trace) {
		value = value.replaceAll(" |\t|\n", "");
		RuleEntry entry = new RuleEntry(key, value, 0, 0, false,
				now_state_table != null, true);
		entry.trace = trace;
		return entry.evalRule(parser, false, no_excepiton);
	}

	static private Object evalArg(RuleParse parser, Object obj) {
		if (!(obj instanceof String) || !((String) obj).startsWith("\""))
			return obj;
		String str = ((String) obj).substring(1);
		return evalExpression(null, "argument", str, false, parser != null);
	}

	static public String setToString(Object a) {
		if (a == null)
			return "";
		if (a instanceof String)
			return (String) a;
		LinkedHashSet s_a = (LinkedHashSet) a;
		String str = "";
		int n = 0;
		for (Iterator iter = s_a.iterator(); iter.hasNext();) {
			if (n++ > 0)
				str += separator;
			Object t = (Object) iter.next();
			if (!(t instanceof String))
				a = null;
			str += (String) t;
		}
		return str;
	}

	static public Object evalDefined(RuleParse parser, Object a) {
		Object val = evalVariable(parser, '$', setToString(a), true);
		return (val != null) ? "t" : "f";
	}

	static public Object evalSet(RuleParse parser, Object a, String prefix,
			String suffix, String func) {
		if (a == null)
			return null;
		boolean test = func.equals("test");
		boolean map = func.equals("map");
		if (a instanceof String) {
			Object obj = evalVariable(parser, '$', prefix + ((String) a)
					+ suffix, true);
			return (obj == null) ? (map ? a : (new LinkedHashSet()))
					: (test ? a : obj);
		}
		LinkedHashSet set = new LinkedHashSet();
		for (Iterator iter = ((LinkedHashSet) a).iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String str = prefix + key + suffix;
			Object obj = evalVariable(parser, '$', str, true);
			if (obj == null) {
				if (map)
					set.add(key);
			} else if (test) {
				set.add(key);
			} else {
				if (obj instanceof LinkedHashSet) {
					set.addAll((LinkedHashSet) obj);
				} else {
					set.add(obj);
				}
			}
		}
		return set;
	}

	static public Object concatString(RuleParse parser, Object a, Object b) {
		return RuleEntry.setToString(a) + RuleEntry.setToString(b);
	}

	static public Object indexValue(RuleParse parser, Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		int n = ((String) b).charAt(0) - '0';
		if (a instanceof String) {
			String val = (String) a;
			if (n >= 0 && n < val.length()) {
				String i_val = val.substring(n, n + 1);
				if (parser != null)
					showParserLocation(parser, val + "[" + n + "]=", i_val,
							null, null, null, null);
				return i_val;
			} else {
				return new LinkedHashSet();
			}
		} else {
			LinkedHashSet set = new LinkedHashSet();
			for (Iterator iter = ((LinkedHashSet) a).iterator(); iter.hasNext();) {
				String val = (String) iter.next();
				if (n >= 0 && n < val.length())
					set.add(val.substring(n, n + 1));
			}
			if (parser != null)
				showParserLocation(parser, "", a, "[" + n + "]=", set, null,
						null);
			return set;
		}
	}

	static public Object shiftValue(RuleParse parser, Object a, Object b,
			boolean plus) {
		String val = setToString(a);
		int shift = ((String) b).charAt(0) - '0';
		String key = val + (plus ? "+" : "-") + b;
		String s_val = (String) getFromStateTable(key);
		if (s_val == null) {
			s_val = shiftValue(val, plus ? shift : (-shift));
			putToStateTable(key, s_val, false);
		}
		if (parser != null)
			showParserLocation(parser, key + "=", s_val, null, null, null, null);
		return s_val;
	}

	static private String shiftValue(String val, int shift)
			throws ArrayIndexOutOfBoundsException {
		int n = FileIO.getArrayIndex(val, full_zodiac);
		if (n >= 0 && shift >= -6 && shift <= 6) {
			n += shift;
			if (n < 0)
				n += 12;
			else if (n >= 12)
				n -= 12;
			return full_zodiac[n];
		}
		n = FileIO.getArrayIndex(val, full_stellar_signs);
		if (n >= 0 && shift >= -14 && shift <= 14) {
			n += shift;
			if (n < 0)
				n += 28;
			else if (n >= 28)
				n -= 28;
			return full_stellar_signs[n];
		}
		throw new ArrayIndexOutOfBoundsException(val + " is not a legal value");
	}

	static public Object setContainment(RuleParse parser, Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a == null || b == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		boolean yes;
		if (a instanceof String) {
			if (b instanceof String) {
				yes = b.equals(a);
			} else {
				yes = ((LinkedHashSet) b).contains(a);
			}
		} else {
			if (b instanceof String) {
				yes = ((LinkedHashSet) a).contains(b);
			} else {
				Object obj = setIntersection((LinkedHashSet) a,
						(LinkedHashSet) b);
				return emptySet(obj) ? "f" : "t";
			}
		}
		if (parser != null)
			showParserLocation(parser, "", a, "?=", b, "=", yes ? "t" : "f");
		return yes ? "t" : "f";
	}

	static private boolean emptySet(Object a) {
		return (a instanceof LinkedHashSet) && ((LinkedHashSet) a).isEmpty();
	}

	static public Object setUnion(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a == null || b == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		if (a instanceof String) {
			if (b instanceof String) {
				if (b.equals(a))
					return b;
				LinkedHashSet set = new LinkedHashSet();
				set.add(a);
				set.add(b);
				return set;
			} else {
				LinkedHashSet s_b = (LinkedHashSet) b;
				if (s_b.contains(a))
					return s_b;
				LinkedHashSet set = new LinkedHashSet();
				set.addAll(s_b);
				set.add(a);
				return set;
			}
		} else {
			if (b instanceof String) {
				LinkedHashSet s_a = (LinkedHashSet) a;
				if (s_a.contains(b))
					return s_a;
				LinkedHashSet set = new LinkedHashSet();
				set.addAll(s_a);
				set.add(b);
				return set;
			} else {
				LinkedHashSet set = new LinkedHashSet();
				set.addAll((LinkedHashSet) a);
				set.addAll((LinkedHashSet) b);
				return set;
			}
		}
	}

	static private Object setIntersection(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a == null || b == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		if (a instanceof String) {
			if (b instanceof String) {
				if (b.equals(a))
					return b;
				else
					return new LinkedHashSet();
			} else {
				LinkedHashSet s_b = (LinkedHashSet) b;
				if (s_b.contains(a))
					return a;
				else
					return new LinkedHashSet();
			}
		} else {
			if (b instanceof String) {
				LinkedHashSet s_a = (LinkedHashSet) a;
				if (s_a.contains(b))
					return b;
				else
					return new LinkedHashSet();
			} else {
				LinkedHashSet s_a = (LinkedHashSet) a;
				LinkedHashSet s_b = (LinkedHashSet) b;
				LinkedHashSet set = new LinkedHashSet();
				for (Iterator iter = s_b.iterator(); iter.hasNext();) {
					String str = (String) iter.next();
					if (s_a.contains(str))
						set.add(str);
				}
				return set;
			}
		}
	}

	static private Object setComplement(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a == null || b == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		if (a instanceof String) {
			if (b instanceof String) {
				if (b.equals(a))
					return new LinkedHashSet();
				else
					return a;
			} else {
				LinkedHashSet s_b = (LinkedHashSet) b;
				if (s_b.contains(a))
					return new LinkedHashSet();
				else
					return a;
			}
		} else {
			if (b instanceof String) {
				LinkedHashSet s_a = (LinkedHashSet) a;
				if (!s_a.contains(b))
					return s_a;
				LinkedHashSet set = new LinkedHashSet();
				set.addAll(s_a);
				set.remove(b);
				return set;
			} else {
				LinkedHashSet set = new LinkedHashSet();
				set.addAll((LinkedHashSet) a);
				for (Iterator iter = ((LinkedHashSet) b).iterator(); iter
						.hasNext();) {
					String str = (String) iter.next();
					if (set.contains(str))
						set.remove(str);
				}
				return set;
			}
		}
	}

	static public Object setContainString(Object a, String str)
			throws ArrayIndexOutOfBoundsException {
		if (a == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		if (a instanceof String)
			return (((String) a).indexOf(str) >= 0) ? a : (new LinkedHashSet());
		LinkedHashSet set = new LinkedHashSet();
		for (Iterator iter = ((LinkedHashSet) a).iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if (key.indexOf(str) >= 0)
				set.add(key);
		}
		return set;
	}

	static public String setIter(RuleParse parser, Object a, Object b, Object c)
			throws ArrayIndexOutOfBoundsException {
		if (a == null || b == null)
			throw new ArrayIndexOutOfBoundsException("null argument");
		String arg = setToString(b);
		if (arg.startsWith("\""))
			arg = arg.substring(1);
		if (a instanceof String) {
			String str = arg.replaceAll("\\[\\]", (String) a);
			return setToString(evalExpression(null, "iter", str, false,
					parser != null));
		} else {
			String sep;
			if (c == null)
				sep = separator;
			else if (c instanceof String)
				sep = (String) c;
			else if (((LinkedHashSet) c).isEmpty())
				sep = "";
			else
				sep = separator;
			String str = "";
			for (Iterator iter = ((LinkedHashSet) a).iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				key = arg.replaceAll("\\[\\]", (String) key);
				if (!str.equals(""))
					str += sep;
				str += setToString(evalExpression(null, "iter", key, true,
						parser != null));
			}
			return str;
		}
	}

	static private Object setTrim(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a != null && b != null) {
			int size = FileIO.parseInt(setToString(b), -1, true);
			if (size > 0) {
				if (a instanceof String) {
					return a;
				} else {
					LinkedHashSet s_a = (LinkedHashSet) a;
					if (s_a.size() <= size)
						return a;
					LinkedHashSet set = new LinkedHashSet();
					for (Iterator iter = ((LinkedHashSet) a).iterator(); size > 0; size--)
						set.add(iter.next());
					return set;
				}
			}
		}
		throw new ArrayIndexOutOfBoundsException("invalid argument");
	}

	static private Object setEntry(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a != null && b != null) {
			int index = FileIO.parseInt(setToString(b), -1, true);
			if (index >= 0) {
				if (a instanceof String) {
					if (index == 0)
						return a;
				} else {
					LinkedHashSet s_a = (LinkedHashSet) a;
					if (index < s_a.size()) {
						for (Iterator iter = ((LinkedHashSet) a).iterator(); index >= 0; index--) {
							Object obj = (Object) iter.next();
							if (index == 0)
								return obj;
						}
					}
				}
			}
		}
		throw new ArrayIndexOutOfBoundsException("invalid argument");
	}

	static private Object splitStringToSet(Object a, Object b)
			throws ArrayIndexOutOfBoundsException {
		if (a != null && b != null) {
			int width = FileIO.parseInt(setToString(b), 0, true);
			if (width > 0) {
				String value = setToString(a);
				if (value.length() / width > 1) {
					LinkedHashSet set = new LinkedHashSet();
					for (int j = 0; j < value.length(); j += width)
						set.add(value.substring(j, j + width));
					return set;
				} else {
					return value;
				}
			}
		}
		throw new ArrayIndexOutOfBoundsException("invalid argument");
	}

	static private Object positionOffset(Object a, Object b, Object c, Object d) {
		double pos_a = FileIO.parseDouble(setToString(a),
				Double.NEGATIVE_INFINITY, true);
		double pos_b = FileIO.parseDouble(setToString(b),
				Double.NEGATIVE_INFINITY, true);
		double offset = FileIO.parseDouble(setToString(c),
				Double.NEGATIVE_INFINITY, true);
		double delta = FileIO.parseDouble(setToString(d),
				Double.NEGATIVE_INFINITY, true);
		if (pos_a != Double.NEGATIVE_INFINITY
				&& pos_b != Double.NEGATIVE_INFINITY
				&& offset != Double.NEGATIVE_INFINITY
				&& delta != Double.NEGATIVE_INFINITY) {
			double gap = Calculate.getDegreeGap(pos_a, pos_b);
			return (gap >= offset - delta && gap <= offset + delta) ? "t" : "f";
		}
		throw new ArrayIndexOutOfBoundsException("invalid argument");
	}

	static private Object importSet(Object a)
			throws ArrayIndexOutOfBoundsException {
		String str = setToString(a);
		if (Resource.hasKey(str)) {
			String[] array = Resource.getStringArray(str);
			if (array.length == 1) {
				return array[0];
			} else {
				LinkedHashSet set = new LinkedHashSet();
				for (int i = 0; i < array.length; i++)
					set.add(array[i]);
				return set;
			}
		}
		throw new ArrayIndexOutOfBoundsException("cannot import " + str);
	}

	static private String formatString(Object a, Object b, Object c) {
		int offset = FileIO.parseInt(setToString(b), 0, false);
		int width = FileIO.parseInt(setToString(c), -1, true);
		String str = setToString(a);
		if (width < 10 || str.equals(""))
			return str;
		boolean neg = offset < 0;
		if (neg)
			offset = -offset;
		boolean new_line = str.endsWith("\n");
		if (new_line)
			str = str.substring(0, str.lastIndexOf("\n"));
		String prefix = "";
		while (prefix.length() < offset)
			prefix += space;
		String n_str = null;
		int i = 0;
		for (; i + width <= str.length(); i += width) {
			if (n_str != null) {
				n_str += "\n" + prefix + str.substring(i, i + width);
			} else {
				n_str = neg ? "" : prefix;
				n_str += str.substring(i, i + width);
			}
		}
		if (i < str.length()) {
			if (n_str != null) {
				n_str += "\n" + prefix + str.substring(i);
			} else {
				n_str = neg ? "" : prefix;
				n_str += str.substring(i);
			}
		}
		if (new_line)
			n_str += "\n";
		return n_str;
	}

	static private Object formatDouble(Object a, Object b, Object c)
			throws ArrayIndexOutOfBoundsException {
		if (a != null && b != null) {
			double d_a = FileIO.parseDouble(setToString(a),
					Double.NEGATIVE_INFINITY, false);
			int width = FileIO.parseInt(setToString(b), -1, true);
			int f_width = FileIO.parseInt(setToString(c), -1, true);
			if (d_a == Double.NEGATIVE_INFINITY || width < 0 || f_width < 0)
				throw new ArrayIndexOutOfBoundsException("illegal number");
			String seq = "";
			for (int i = 1; i < width; i++)
				seq += "#";
			seq += "0.";
			for (int i = 0; i < f_width; i++)
				seq += "0";
			DecimalFormat format = new DecimalFormat(seq);
			return Resource.spacePreFilled(format.format(d_a), width + f_width
					+ 2);
		}
		throw new ArrayIndexOutOfBoundsException("invalid argument");
	}

	static private Object extendString(Object a, Object b, Object c, boolean pre) {
		if (a == null || b == null)
			return null;
		String prefix, suffix;
		if (pre) {
			prefix = setToString(c);
			suffix = "";
		} else {
			prefix = "";
			suffix = setToString(c);
		}
		if (a instanceof String) {
			if (b instanceof String) {
				return a.equals(b) ? (prefix + a + suffix) : a;
			} else {
				return ((LinkedHashSet) b).contains(a) ? (prefix + a + suffix)
						: a;
			}
		} else {
			LinkedHashSet s_b;
			if (b instanceof String) {
				s_b = new LinkedHashSet();
				s_b.add(b);
			} else {
				s_b = (LinkedHashSet) b;
			}
			LinkedHashSet set = new LinkedHashSet();
			for (Iterator iter = ((LinkedHashSet) a).iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				set.add(s_b.contains(key) ? (prefix + key + suffix) : key);
			}
			return set;
		}
	}

	static private void showParserLocation(RuleParse parser, String prefix,
			Object a, String middle, Object b, String suffix, Object c) {
		int len = Math.min(parser.max, 100);
		String str = indent + "    ";
		if (prefix != null) {
			for (int i = 0; i < len; i++)
				str += (parser.index == i) ? "^^" : "  ";
			if (parser.index == parser.max)
				str += "^^";
			out.appendLine(str);
			out.append(indent + "    " + prefix);
			if (a != null) {
				if (a instanceof String) {
					out.append((String) a);
				} else {
					showSetContent(null, a, 3);
				}
			}
			if (middle != null)
				out.append(middle);
			if (b != null) {
				if (b instanceof String) {
					out.append((String) b);
				} else {
					showSetContent(null, b, 3);
				}
			}
			if (suffix != null)
				out.append(suffix);
			if (c != null) {
				if (c instanceof String) {
					out.append((String) c);
				} else {
					showSetContent(null, c, 3);
				}
			}
			out.appendLine();
		} else {
			DecimalFormat format = new DecimalFormat("00");
			for (int i = 0; i < len; i++)
				str += format.format(i);
			out.appendLine(str);
			str = indent + "    ";
			for (int i = 0; i < len; i++) {
				char ch = parser.expr.charAt(i);
				if (ch <= 0xff)
					str += " ";
				str += ch;
			}
			out.appendLine(str);
		}
	}

	static public void addOutput(String s_name, String s_info) {
		if (out_table == null)
			return;
		s_name = s_name.toLowerCase();
		if (s_name.startsWith("{") && s_name.endsWith("}"))
			s_name = s_name.substring(1, s_name.length() - 1);
		if (s_info.startsWith("\"") && s_info.endsWith("\""))
			s_info = s_info.substring(1, s_info.length() - 1);
		out_table.put(s_name, s_info);
	}

	static public void addRule(String s_name, String s_cond) {
		if (eval_table == null)
			return;
		s_name = s_name.toLowerCase();
		boolean s_never, s_plus = s_name.charAt(0) == '+';
		String key;
		Hashtable table;
		if (s_name.charAt(0) == '=') {
			table = map_table;
			key = s_name.substring(1);
			s_never = false;
		} else {
			table = eval_table;
			s_never = s_name.charAt(1) == '&';
			key = s_name.substring(s_never ? 2 : 1);
		}
		String rank_str = getRankString(s_cond);
		boolean s_multi = rank_str.startsWith("^");
		int level_rank = getLevelRank(rank_str);
		int s_level = level_rank >> LEVEL_SHIFT;
		int s_rank = level_rank & RANK_MASK;
		String value = s_cond.substring(rank_str.length());
		int s = key.indexOf('{');
		if (s >= 0) { // expand sequence
			int e = key.indexOf('}');
			String prefix = key.substring(0, s);
			String suffix = key.substring(e + 1);
			String expr = key.substring(s + 1, e);
			StringTokenizer nst = new StringTokenizer(expr, ",");
			while (nst.hasMoreTokens()) {
				String n_name = nst.nextToken();
				String str;
				e = n_name.indexOf('=');
				if (e >= 0) {
					str = n_name.substring(e + 1);
					n_name = n_name.substring(0, e);
				} else {
					str = n_name;
				}
				if (table != map_table)
					str = "{" + str + "}";
				addSingleRule(table, prefix + n_name + suffix, value
						.replaceAll("\\{\\}", str), s_level, s_rank, s_plus,
						s_multi, s_never);
			}
		} else {
			addSingleRule(table, key, value, s_level, s_rank, s_plus, s_multi,
					s_never);
		}
	}

	static public int getLevelRank(String rank_str) {
		int s_level = 0, s_rank;
		if (rank_str.startsWith("^"))
			rank_str = rank_str.substring(1);
		boolean num_rank = rank_str.startsWith("[") && rank_str.endsWith("]");
		if (num_rank) {
			s_rank = 0;
			StringTokenizer nst = new StringTokenizer(rank_str.substring(1,
					rank_str.length() - 1), ".");
			if (nst.hasMoreTokens()) {
				s_level = FileIO.parseInt(nst.nextToken(), 0, true);
				if (nst.hasMoreTokens()) {
					s_rank = FileIO.parseInt(nst.nextToken(), 0, true) << RANK_SHIFT;
					if (nst.hasMoreTokens())
						s_rank += FileIO.parseInt(nst.nextToken(), 0, true);
					s_rank <<= RANK_SHIFT;
				}
			}
		} else {
			s_rank = NORMAL_RANK;
		}
		return (s_level << LEVEL_SHIFT) + s_rank;
	}

	static public String getRankString(String s_cond) {
		String rank = "";
		if (s_cond.startsWith("^")) {
			rank = "^";
			s_cond = s_cond.substring(1);
		}
		if (s_cond.length() > 2 && s_cond.charAt(0) == '[') {
			int val = s_cond.indexOf(']');
			if (val >= 0)
				return rank + s_cond.substring(0, val + 1);
		}
		return rank;
	}

	static private void addSingleRule(Hashtable table, String key,
			String value, int s_level, int s_rank, boolean s_plus,
			boolean s_multi, boolean s_never) {
		if (!use_alias && table == map_table) {
			if (key.startsWith("{") && key.endsWith("}"))
				key = key.substring(1, key.length() - 1);
			if (!value.startsWith("{") || !value.endsWith("}"))
				value = "{" + value + "}";
			String reverse_key = "__" + key;
			RuleEntry entry = new RuleEntry(reverse_key, value, s_level,
					s_rank, s_plus, s_multi, s_never);
			table.put(reverse_key, entry);
			String str = key;
			key = value;
			value = str;
			if (key.startsWith("{") && key.endsWith("}"))
				key = key.substring(1, key.length() - 1);
			if (!value.startsWith("{") || !value.endsWith("}"))
				value = "{" + value + "}";
		}
		RuleEntry entry = new RuleEntry(key, value, s_level, s_rank, s_plus,
				s_multi, s_never);
		table.put(key, entry);
	}

	static public void processRule() {
		// add auto reject
		for (Enumeration e = eval_table.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			RuleEntry entry = (RuleEntry) eval_table.get(key);
			String value = entry.conditions;
			for (int val = value.lastIndexOf("?{"); val >= 0; val = value
					.lastIndexOf("?{")) {
				String str = value.substring(val + 2);
				int l_val = str.indexOf('}');
				if (l_val >= 0) {
					str = str.substring(0, l_val);
					if (eval_table.get(str) != null) {
						if (entry.reject == null)
							entry.reject = new LinkedList();
						entry.reject.add("{" + str + "}");
					}
				}
				value = value.substring(0, val);
			}
		}
	}

	static private Object getFromStateTable(String key) {
		Object object = state_table.get(key);
		if (object == null && now_state_table != null)
			object = now_state_table.get(key);
		return object;
	}

	static private void putToStateTable(String key, Object object, boolean now)
			throws ArrayIndexOutOfBoundsException {
		try {
			if (now) {
				if (now_state_table != null)
					now_state_table.put(key, object);
			} else {
				state_table.put(key, object);
			}
		} catch (NullPointerException e) {
			throw new ArrayIndexOutOfBoundsException("cannot assign null value");
		}
		if (trace_variable != null && trace_variable.equalsIgnoreCase(key)) {
			out.append(indent + "--- " + key + " << ");
			showSetContent(null, object, 6);
			out.appendLine(" ---");
		}
	}

	static public void initBirth(Hashtable s_table, String[] zodiac,
			String[] stellar_signs, boolean sex) {
		multi_tag++;
		single_tag++;
		male = sex;
		state_table = s_table;
		now_state_table = null;
		full_zodiac = zodiac;
		full_stellar_signs = stellar_signs;
		trace_variable = null;
		reject_table = new Hashtable();
		separator = Resource.getString("separator_char");
		space = Resource.getString("non_white_space");
	}

	static public void initNow(Hashtable n_s_table) {
		multi_tag++;
		now_state_table = n_s_table;
	}

	static public void setRuleLevel(int level, int max_styles) {
		display_level = level;
		fill_max_styles = max_styles;
	}

	static public void setTrace(String key) {
		key = key.toLowerCase();
		RuleEntry entry = (RuleEntry) eval_table.get(key);
		if (entry == null) {
			entry = (RuleEntry) map_table.get(key);
			if (entry != null) {
				String new_key = entry.map(null);
				entry = (RuleEntry) eval_table.get(new_key);
			}
			if (entry == null && second_map_table != null) {
				entry = (RuleEntry) second_map_table.get("__" + key);
				if (entry != null)
					key = entry.map(null);
				entry = (RuleEntry) second_eval_table.get(key);
			}
		}
		if (entry != null)
			entry.trace = true;
	}

	static public void setTraceVariable(String key) {
		trace_variable = key;
	}

	static public void setTraceLevelRank(int level_rank) {
		int s_level = level_rank >> LEVEL_SHIFT;
		int s_rank = level_rank & RANK_MASK;
		for (int i = 0; i < 2; i++) {
			Hashtable table = (i == 0) ? eval_table : map_table;
			for (Enumeration e = table.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				RuleEntry entry = (RuleEntry) table.get(key);
				if (entry.level == s_level && entry.rank == s_rank)
					entry.trace = true;
			}
		}
	}

	static public void computeRules(LinkedList good_style, LinkedList bad_style) {
		boolean show_hidden_style = (Resource.hasKey("show_hidden_style")) ? (Resource
				.getPrefInt("show_hidden_style") != 0)
				: false;
		RuleParse parser = new RuleParse(TRACE_PARSER);
		indent = "";
		for (Enumeration e = eval_table.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			RuleEntry entry = (RuleEntry) eval_table.get(key);
			entry.evalRule(parser, true, false);
		}
		LinkedList list = new LinkedList();
		for (Enumeration e = eval_table.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			RuleEntry entry = (RuleEntry) eval_table.get(key);
			if (now_state_table != null && !entry.multi)
				continue;
			if (entry.valid && (!entry.hidden || show_hidden_style)
					&& entry.level >= display_level) {
				list.add(entry);
			}
		}
		if (list.isEmpty())
			return;
		RuleEntry[] array = (RuleEntry[]) list.toArray(new RuleEntry[1]);
		Arrays.sort(array, new Comparator() {
			public int compare(Object a, Object b) {
				RuleEntry e_a = (RuleEntry) a;
				RuleEntry e_b = (RuleEntry) b;
				int n = e_b.level - e_a.level;
				if (n != 0)
					return n;
				n = e_b.rank - e_a.rank;
				if (n != 0)
					return n;
				return e_a.name.compareTo(e_b.name);
			}
		});
		String remap = Resource.getString("sex_remap");
		String from = remap.substring(1);
		String to = remap.substring(0, 1);
		for (int i = 0; i < array.length; i++) {
			RuleEntry entry = array[i];
			String key = entry.name;
			RuleEntry map_entry = (RuleEntry) map_table.get(key);
			if (map_entry != null) {
				key = map_entry.map(parser);
				if (reject_table.get(key) != null)
					continue;
			} else if (entry.never)
				continue;
			if (!male)
				key = key.replaceAll(from, to);
			int e = key.lastIndexOf('*');
			if (e > 0)
				key = key.substring(0, e);
			if (entry.hidden)
				key = "*" + key;
			if (entry.plus) {
				if (!show_hidden_style && entry.level == display_level
						&& entry.rank < NORMAL_RANK
						&& good_style.size() >= fill_max_styles)
					continue;
				if (out_table != null) {
					String str = (String) out_table.get(key);
					if (str != null)
						key = processOutput(parser, "output", str, entry.trace,
								false);
				}
				good_style.add(key);
			} else {
				if (!show_hidden_style && entry.level == display_level
						&& entry.rank < NORMAL_RANK
						&& bad_style.size() >= fill_max_styles)
					continue;
				if (out_table != null) {
					String str = (String) out_table.get(key);
					if (str != null)
						key = processOutput(parser, "output", str, entry.trace,
								false);
				}
				bad_style.add(key);
			}
		}
	}

	static private String processOutput(RuleParse parser, String key,
			String mesg, boolean no_excepiton, boolean trace) {
		int s = mesg.indexOf('{');
		if (s < 0)
			return mesg;
		int e = mesg.lastIndexOf('}');
		if (e < 0)
			return mesg;
		int depth = 0, l = 0;
		String new_mesg = "";
		for (int i = s; i <= e; i++) {
			char c = mesg.charAt(i);
			if (c == '{') {
				if (depth == 0) {
					s = i;
					new_mesg += mesg.substring(l, s);
				}
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth < 0)
					return mesg;
				if (depth == 0) { // get condition
					l = i + 1;
					String str = mesg.substring(s, l);
					str = str.replaceAll(" |\t|\n", "");
					RuleEntry entry = new RuleEntry(key, str, 0, 0, false,
							now_state_table != null, true);
					entry.trace = trace;
					Object object = entry.evalRule(parser, false, no_excepiton);
					str = setToString(object);
					new_mesg += str;
				}
			}
		}
		new_mesg += mesg.substring(l);
		return new_mesg;
	}

	static public String getOutput(String key) {
		if (out_table == null)
			return null;
		else
			return (String) out_table.get(key.toLowerCase());
	}

	static public void reset(boolean alias) {
		eval_table = new Hashtable();
		map_table = new Hashtable();
		out_table = new Hashtable();
		use_alias = alias;
	}

	static public boolean hasRuleEntry(boolean rule) {
		if (rule) {
			return rule_table != null && rule_out_table != null
					&& !(rule_table.isEmpty() && rule_out_table.isEmpty());
		} else {
			return style_table != null && !style_table.isEmpty();
		}
	}

	static public void saveTable(boolean rule) {
		if (rule) {
			rule_table = eval_table;
			rule_map_table = map_table;
			rule_out_table = out_table;
			rule_use_alias = use_alias;
		} else {
			style_table = eval_table;
			style_map_table = map_table;
			style_use_alias = use_alias;
		}
		eval_table = map_table = out_table = null;
	}

	static public void restoreTable(boolean rule) {
		if (rule) {
			use_alias = rule_use_alias;
			eval_table = rule_table;
			map_table = rule_map_table;
			out_table = rule_out_table;
			second_eval_table = style_table;
			second_map_table = style_map_table;
		} else {
			use_alias = style_use_alias;
			eval_table = style_table;
			map_table = style_map_table;
			second_eval_table = second_map_table = out_table = null;
		}
	}

	static public void setOutputTab(BaseTab tab) {
		out = tab;
	}

	static public void setDebugOption(boolean show_failure) {
		show_exception = show_failure;
	}
}
