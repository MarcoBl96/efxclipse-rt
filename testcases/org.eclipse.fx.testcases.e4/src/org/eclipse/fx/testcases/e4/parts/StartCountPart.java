/*******************************************************************************
 * Copyright (c) 2014 EM-SOFTWARE and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Christoph Keimel <c.keimel@emsw.de> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.testcases.e4.parts;

import java.util.Map;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class StartCountPart {

	private Integer startCount;

	@PostConstruct
	void postContruct(MPart part, BorderPane p) {
		Map<String, String> persistedState = part.getPersistedState();
		String startCountValue = persistedState.get("startCount");
		if (startCountValue == null) { 
			startCount = 0;
		} else {
			startCount = Integer.valueOf(startCountValue);
		}
		startCount++;
		persistedState.put("startCount", startCount.toString());
		
		Label textField = new Label("Number of times started without cleaning the persisted state: " + startCount);
		textField.setWrapText(true);
		p.setCenter(textField);
	}
}
