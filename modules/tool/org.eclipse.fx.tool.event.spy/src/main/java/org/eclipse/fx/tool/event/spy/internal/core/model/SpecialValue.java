/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.tool.event.spy.internal.core.model;

/**
 * Special values for search criteria.
 */
public enum SpecialValue {
	Null("null"), EmptyString("empty"), NameAndValue("%s=%s");

	private String text;

	private SpecialValue(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
