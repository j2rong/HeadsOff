/**
   Copyright (c) 2016-2017, j2Rong     
     
   Licensed under the Apache License, Version 2.0 (the "License");     
   you may not use this file except in compliance with the License.     
   You may obtain a copy of the License at     
     
       http://www.apache.org/licenses/LICENSE-2.0     
     
   Unless required by applicable law or agreed to in writing, software     
   distributed under the License is distributed on an "AS IS" BASIS,     
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     
   See the License for the specific language governing permissions and     
   limitations under the License.   
*/
package com.rong.lib.utils;

import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;

/**
 * Changelogs:
 * 	2016-03-20:
 * 		+ {@link HtmlString#tab()}
 * 		+ {@link HtmlString#blank()}, {@link HtmlString#blank(int)}
 * 		* Constraining param of {@link HtmlString#big(String, int)}
 */
@SuppressWarnings("unused")
public class HtmlString {

	public static final int MAX_BIG_SIZE = 7;

	public StringBuilder internalBuilder;

	public HtmlString() {
		internalBuilder = new StringBuilder();
	}

	/**
	 * Append plain text or formatted html string
	 */
	public HtmlString append(String str) {
		internalBuilder.append(str);
		return this;
	}

	/**
	 * Add a new line
	 */
	public HtmlString nl() {
		internalBuilder.append("<br/>");
		return this;
	}

	public HtmlString nl(int n) {
		for (int i = 0; i < n; i++) {
			this.nl();
		}

		return this;
	}

	/**
	 * Add a bold string, "<b>" tag
	 */
	public HtmlString bold(String str) {
		internalBuilder.append("<b>").append(str).append("</b>");
		return this;
	}

	/**
	 * Add a paragraph, "<p>" tag
	 * @param str	string
	 * @return		a {@link HtmlString} object
	 */
	public HtmlString paragraph(String str) {
		internalBuilder.append("<p>").append(str).append("</p>");
		return this;
	}

	/**
	 * Add a italic string, "<i>" tag
	 */
	public HtmlString italic(String str) {
		internalBuilder.append("<i>").append(str).append("</i>");
		return this;
	}

	/**
	 * Add a colored string
	 */
	public HtmlString color(String str, int color) {
		internalBuilder.append("<font color='#");
		internalBuilder.append(String.format("%02X", Color.red(color)));
		internalBuilder.append(String.format("%02X", Color.green(color)));
		internalBuilder.append(String.format("%02X", Color.blue(color)));
		internalBuilder.append("'>");
		internalBuilder.append(str);
		internalBuilder.append("</font>");
		return this;
	}

	/**
	 * tag <font color="..." face="...">
	 * size do not work on font tag, use <small> or <big> instead
	 * @param color		-1 to ignore
	 * @param face		empty to ignore
	 */
	public HtmlString font(String str, int color, String face) {
		internalBuilder.append("<font ");

		if(color != -1) {
			internalBuilder.append("color='#");
			internalBuilder.append(String.format("%02X", Color.red(color)));
			internalBuilder.append(String.format("%02X", Color.green(color)));
			internalBuilder.append(String.format("%02X", Color.blue(color)));
			internalBuilder.append("' ");
		}

		if(!face.isEmpty()) {
			internalBuilder.append("face='").append(face).append("'");
		}

		internalBuilder.append(">");
		internalBuilder.append(str);
		internalBuilder.append("</font>");

		return this;
	}

	private int clapSize(int nest) {
		return (nest < 1) ? 1 : ((nest > MAX_BIG_SIZE) ? MAX_BIG_SIZE : nest);
	}

	/**
	 * size up to 7
	 */
	public HtmlString big(String str, int nest) {
		int size = clapSize(nest);

		for (int i = 0; i < size; i++) {
			internalBuilder.append("<big>");
		}

		internalBuilder.append(str);

		for(int i = 0; i < size; i++) {
			internalBuilder.append("</big>");
		}

		return this;
	}

	public HtmlString small(String str, int nest) {
		if (nest < 1)
			nest = 1;

		for (int i = 0; i < nest; i++) {
			internalBuilder.append("<small>");
		}

		internalBuilder.append(str);

		for(int i = 0; i < nest; i++) {
			internalBuilder.append("</small>");
		}

		return this;
	}

	/**
	 * Add a string with heading format
	 * @param level		from 1 ~ 6, 6 is the smallest heading
	 */
	public HtmlString heading(String str, int level) {
		internalBuilder.append("<h").append(String.valueOf(level)).append(">");
		internalBuilder.append(str);
		internalBuilder.append("</h").append(String.valueOf(level)).append(">");

		return this;
	}

	/**
	 * Add a tab character
	 */
	public HtmlString tab() {
		internalBuilder.append("<pre>&#9;</pre>");	// tab ASCII=9

		return this;
	}

	public HtmlString blank() {
		return blank(1);
	}

	/**
	 * Add a blank space
	 * @param c		how many blank spaces to add
	 */
	public HtmlString blank(int c) {
		for (int i = 0; i < c; i++)
			internalBuilder.append("&nbsp;");

		return this;
	}

	public Spanned fromHtml() {
		return Html.fromHtml(internalBuilder.toString());
	}

}
