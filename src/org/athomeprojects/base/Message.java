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

public class Message {
	static private BaseMessage mesg;

	static public void setMessage(BaseMessage msg) {
		mesg = msg;
	}

	static public void info(String message) {
		mesg.info(message);
	}

	static public void warn(String message) {
		mesg.warn(message);
	}

	static public boolean question(String message) {
		return mesg.question(message);
	}

	static public void error(String message) {
		mesg.error(message);
	}
}