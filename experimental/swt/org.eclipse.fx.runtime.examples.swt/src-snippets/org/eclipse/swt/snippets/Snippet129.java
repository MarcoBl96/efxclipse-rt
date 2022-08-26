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
 * Table example snippet: color cells and rows in table
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.0
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class Snippet129 implements SWTAppStart {

	@Override
	public BlockCondition createApp(Display display) {
		Color red = display.getSystemColor(SWT.COLOR_RED);
		Color blue = display.getSystemColor(SWT.COLOR_BLUE);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color gray = display.getSystemColor(SWT.COLOR_GRAY);
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Table table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBackground(gray);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		TableColumn column3 = new TableColumn(table, SWT.NONE);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "entire", "row", "red foreground" });
		item.setForeground(red);
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "entire", "row", "red background" });
		item.setBackground(red);
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "entire", "row", "white fore/red back" });
		item.setForeground(white);
		item.setBackground(red);
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "normal", "blue foreground",
				"red foreground" });
		item.setForeground(1, blue);
		item.setForeground(2, red);
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "normal", "blue background",
				"red background" });
		item.setBackground(1, blue);
		item.setBackground(2, red);
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "white fore/blue back", "normal",
				"white fore/red back" });
		item.setForeground(0, white);
		item.setBackground(0, blue);
		item.setForeground(2, white);
		item.setBackground(2, red);

		column1.pack();
		column2.pack();
		column3.pack();

		shell.pack();
		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}
	
	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet129());
	}
}
