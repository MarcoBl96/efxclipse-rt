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
 * Table example snippet: find a table cell from mouse down (SWT.FULL_SELECTION)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

public class Snippet3 implements SWTAppStart {

	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet3());
	}

	public BlockCondition createApp(Display display) {
		Shell shell = new Shell(display);
		final Table table = new Table(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		final int rowCount = 64, columnCount = 4;
		for (int i = 0; i < columnCount; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText("Column " + i);
		}
		for (int i = 0; i < rowCount; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int j = 0; j < columnCount; j++) {
				item.setText(j, "Item " + i + "-" + j);
			}
		}
		for (int i = 0; i < columnCount; i++) {
			table.getColumn(i).pack();
		}

		Rectangle clientArea = shell.getClientArea();
		table.setLocation(clientArea.x, clientArea.y);
		Point size = table.computeSize(SWT.DEFAULT, 200);
		table.setSize(size);
		table.setSize(shell.getSize());
		shell.pack();
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				if (item == null)
					return;
				for (int i = 0; i < columnCount; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						int index = table.indexOf(item);
						System.out.println("Item " + index + "-" + i);
					}
				}
			}
		});
		SWTUtil.getInstance().openBlocking(shell);
		return null;
	}

}
