/*******************************************************************************
 * Copyright (c) 2022 BestSolution.at and others.
 *  
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *     BestSolution.at - initial API and implementation
 *******************************************************************************/
///*******************************************************************************
// * Copyright (c) 2019 BestSolution.at and others.
// * 
// * This program and the accompanying materials are made available under the
// * terms of the Eclipse Public License v.2.0 which is available at
// * https://www.eclipse.org/legal/epl-2.0.
// * 
// * SPDX-License-Identifier: EPL-2.0
// *
// * Contributors:
// *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
// *******************************************************************************/
//package org.eclipse.fx.ui.controls.scroll;
//
//import javafx.geometry.Orientation;
//import javafx.scene.Node;
//import javafx.scene.control.ScrollBar;
//import javafx.scene.input.ScrollEvent;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.StackPane;
//
//public abstract class ScrollView extends Region {
//	private final ScrollBar vScrollbar;
//	private final ScrollBar hScrollbar;
//	private final ClipPane clipPane;
//	
//	public ScrollView() {
//		this.vScrollbar = new ScrollBar();
//		this.vScrollbar.setOrientation(Orientation.VERTICAL);
//		
//		this.hScrollbar = new ScrollBar();
//		this.hScrollbar.setOrientation(Orientation.HORIZONTAL);
//		
//		this.clipPane = new ClipPane();
////		this.clipPane.addEventHandler(ScrollEvent.SCROLL, eventHandler);
//		getChildren().addAll(this.clipPane, this.hScrollbar, this.vScrollbar);
//	}
//	
//	class ClipPane extends StackPane {
//		
//	}
//	
//	protected abstract <T extends Node & ScrolledContent> ScrolledContent createContent();
//	
//	public interface ScrolledContent {
//		public double computeContentHeight(double width);
//		public double computeContentWidth(double height);
//	}
//}
