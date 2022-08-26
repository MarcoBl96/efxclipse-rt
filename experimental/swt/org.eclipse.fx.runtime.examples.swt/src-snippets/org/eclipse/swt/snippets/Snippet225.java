/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
 * Tooltip example snippet: create a balloon tooltip for a tray item
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.2
 */
import org.eclipse.fx.runtime.swtutil.SWTUtil;
import org.eclipse.fx.runtime.swtutil.SWTUtil.BlockCondition;
import org.eclipse.fx.runtime.swtutil.SWTUtil.SWTAppStart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Snippet225 implements SWTAppStart {

	public BlockCondition createApp(Display display) {
		Shell shell = new Shell(display);
		Image image = null;
		final ToolTip tip = new ToolTip(shell, SWT.BALLOON
				| SWT.ICON_ERROR);
		tip.setMessage("Here is a message for the user. When the message is too long it wraps. I should say something cool but nothing comes to my mind.");
		Tray tray = display.getSystemTray();
		if (tray != null) {
			TrayItem item = new TrayItem(tray, SWT.NONE);
			image = display.getSystemImage(SWT.ICON_INFORMATION);
			item.setImage(image);
			tip.setText("Notification from a tray item");
			item.setToolTip(tip);
		} else {
			tip.setText("Notification from anywhere");
			tip.setLocation(400, 400);
		}
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Press for balloon tip");
		button.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				tip.setVisible(true);
			}
		});
		Rectangle clientArea = shell.getClientArea();
		button.setLocation(clientArea.x, clientArea.y);
		button.pack();
		shell.setBounds(50, 50, 300, 200);
		SWTUtil.getInstance().openBlocking(shell);
		if (image != null)
			image.dispose();
		return null;
	}
	
	public static void main(String[] args) {
		SWTUtil.getInstance().bootstrap(new Snippet225());
	}
}
