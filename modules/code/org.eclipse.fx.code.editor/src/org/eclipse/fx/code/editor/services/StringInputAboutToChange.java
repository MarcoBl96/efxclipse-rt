/*******************************************************************************
* Copyright (c) 2016 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
*
* Contributors:
* 	Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
*******************************************************************************/
package org.eclipse.fx.code.editor.services;

import org.eclipse.fx.code.editor.StringInput;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;

public class StringInputAboutToChange {
	private final DocumentEvent event;
	private final StringInput input;

	public StringInputAboutToChange(StringInput input, DocumentEvent event) {
		this.event = event;
		this.input = input;
	}

	public StringInput input() {
		return input;
	}

	public int offset() {
		return event.getOffset();
	}

	public int length() {
		return event.getLength();
	}

	public String text() {
		return event.getText();
	}

	public int startLine() {
		try {
			return event.fDocument.getLineOfOffset(offset());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	public int startOffsetInLine() {
		try {
			return offset() - event.fDocument.getLineOffset(startLine());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	public int endLine() {
		try {
			return event.fDocument.getLineOfOffset(offset() + length());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	public int endOffsetInLine() {
		try {
			return offset() + length() - event.fDocument.getLineOffset(endLine());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return "PreStringInputChangeData [input()=" + input() + ", offset()=" + offset() + ", length()=" + length() + ", text()=" + text() + ", startLine()=" + startLine() + ", startOffsetInLine()=" + startOffsetInLine() + ", endLine()=" + endLine() + ", endOffsetInLine()=" + endOffsetInLine()
				+ "]";
	}
}