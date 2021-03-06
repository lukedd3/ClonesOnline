package com.luke.clones.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* The MIT License (MIT)

Copyright (c) 2016 �ukasz Dziak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

public class FormValidationUtil {
//	private static final String ONLY_ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9_//s//n]*$";
//	private static final String ONLY_ALPHANUMERIC_PATTERN = "^[^\\]*$";
//	private static final String ONLY_ALPHANUMERIC_PATTERN = "^[^<>`~!/@\\\\#}$%:;)(_^{&*=|'+]+$";
	private static final String ONLY_ALPHANUMERIC_PATTERN = "^[^\\\\]+$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static Pattern alphanumericPattern = Pattern.compile(ONLY_ALPHANUMERIC_PATTERN);
	private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
	private static Matcher matcher;

	public static boolean isFieldOnlyAlfanumeric(String fieldText) {
		matcher = alphanumericPattern.matcher(fieldText);
		return matcher.matches();
//		return true;
	}

	public static boolean isFieldEmpty(String fieldText) {
		return fieldText.isEmpty();
	}
	
	public static boolean isFieldEmail(String fieldText) {
		matcher = emailPattern.matcher(fieldText);
		return matcher.matches();
	}

}
