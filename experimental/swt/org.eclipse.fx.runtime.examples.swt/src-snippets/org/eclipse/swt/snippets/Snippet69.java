/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
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
package org.eclipse.swt.snippets;

/*
 * FormLayout example snippet: center a label and single line text using a form layout
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;

public class Snippet69 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		Shell shell = new Shell(display);
		Label label = new Label(shell, SWT.NONE | SWT.BORDER);
		label.setText("Name");
		Text text = new Text(shell, SWT.NONE);

		FormLayout layout = new FormLayout();
		layout.marginWidth = layout.marginHeight = 5;
		shell.setLayout(layout);

		GC gc = new GC(text);
		FontMetrics fm = gc.getFontMetrics();
		int charWidth = fm.getAverageCharWidth();
		int width = text.computeSize(charWidth * 8, SWT.DEFAULT).x;
		gc.dispose();
		FormData data = new FormData(width, SWT.DEFAULT);
		text.setLayoutData(data);
		data.left = new FormAttachment(label, 5);
		data.top = new FormAttachment(label, 0, SWT.CENTER);

		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}

	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet69());
	}
}
