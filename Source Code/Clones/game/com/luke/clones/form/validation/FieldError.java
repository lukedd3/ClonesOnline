package com.luke.clones.form.validation;

import java.util.List;

/* The MIT License (MIT)

Copyright (c) 2016 £ukasz Dziak

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

public class FieldError <TC extends FieldType> {
	private TC fieldErrorType;
	private FieldErrorReasonType fieldErrorReasonType;
	
	//to delete
//	public static <T1 extends FieldType> void listGenericTest(List<FieldError<T1>> fieldErrors) {
//		
//	}
//	
//	public static <T1 extends FieldType> void fieldGenericTest(T1 fieldErrorType) {
//		
//	}
//	
//	public static <T1 extends FieldType, T2 extends FieldType> void listFieldGenericTest(List<FieldError<T1>> fieldErrors, T2 fieldErrorType) {
//		
//	}
	
	public static <T1 extends FieldType, T2 extends FieldType> boolean containsFieldErrorType(List<FieldError<T1>> fieldErrors,
			T2 fieldErrorType) {
		for (FieldErrorReasonType fieldErrorReasonType : FieldErrorReasonType
				.values()) {
			if (fieldErrors.contains(new FieldError<T2>(fieldErrorType,
					fieldErrorReasonType))) {
				return true;
			}
		}
		return false;
	}

	public static <T1 extends FieldType> FieldError<T1> getByFieldErrorType(List<FieldError<T1>> fieldErrors,
			T1 fieldErrorType) {
		int index = -1;
		for (FieldErrorReasonType fieldErrorReasonType : FieldErrorReasonType
				.values()) {
			index = fieldErrors.lastIndexOf(new FieldError<T1>(fieldErrorType,
					fieldErrorReasonType));
			if (index != -1) {
				break;
			}
		}
		if (index == -1) {
			return null;
		} else {
			return fieldErrors.get(index);
		}
	}

	public FieldError(TC fieldErrorType,
			FieldErrorReasonType fieldErrorReasonType) {
		this.fieldErrorType = fieldErrorType;
		this.fieldErrorReasonType = fieldErrorReasonType;
	}

	public TC getFieldErrorType() {
		return fieldErrorType;
	}

	public FieldErrorReasonType getFieldErrorReasonType() {
		return fieldErrorReasonType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((fieldErrorReasonType == null) ? 0 : fieldErrorReasonType
						.hashCode());
		result = prime * result
				+ ((fieldErrorType == null) ? 0 : fieldErrorType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldError other = (FieldError) obj;
		if (fieldErrorReasonType != other.fieldErrorReasonType)
			return false;
		if (fieldErrorType == null) {
			if (other.fieldErrorType != null)
				return false;
		} else if (!fieldErrorType.equals(other.fieldErrorType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FieldError [fieldErrorType=" + fieldErrorType
				+ ", fieldErrorReasonType=" + fieldErrorReasonType + "]";
	}
	
}