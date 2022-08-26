/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
package org.eclipse.fx.text.ui.contentassist;

import org.eclipse.fx.text.hover.HtmlString;
import org.eclipse.fx.text.ui.ITextViewer;
import org.eclipse.fx.text.ui.internal.SimpleHtmlViewer;
import org.eclipse.fx.ui.controls.Util;
import org.eclipse.fx.ui.controls.styledtext.StyledString;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea.LineLocation;
import org.eclipse.fx.ui.controls.styledtext.VerifyEvent;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.PopupWindow;


/**
 * This class is used to present context information to the user.
 * If multiple contexts are valid at the current cursor location,
 * a list is presented from which the user may choose one context.
 * Once the user makes their choice, or if there was only a single
 * possible context, the context information is shown in a tool tip like popup. <p>
 * If the tool tip is visible and the user wants to see context information of
 * a context embedded into the one for which context information is displayed,
 * context information for the embedded context is shown. As soon as the
 * cursor leaves the embedded context area, the context information for
 * the embedding context is shown again.
 *
 * @see IContextInformation
 * @see IContextInformationValidator
 */
@SuppressWarnings("restriction")
class ContextInformationPopup implements IContentAssistListener {


//	/**
//	 * Represents the state necessary for embedding contexts.
//	 *
//	 * @since 2.0
//	 */
//	static class ContextFrame {
//
//		final int fBeginOffset;
//		final int fOffset;
//		final int fVisibleOffset;
//		final IContextInformation fInformation;
//		final IContextInformationValidator fValidator;
//		final IContextInformationPresenter fPresenter;
//
//		/*
//		 * @since 3.1
//		 */
//		public ContextFrame(IContextInformation information, int beginOffset, int offset, int visibleOffset, IContextInformationValidator validator, IContextInformationPresenter presenter) {
//			this.fInformation = information;
//			this.fBeginOffset = beginOffset;
//			this.fOffset = offset;
//			this.fVisibleOffset = visibleOffset;
//			this.fValidator = validator;
//			this.fPresenter = presenter;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (obj instanceof ContextFrame) {
//				ContextFrame frame= (ContextFrame) obj;
//				return this.fInformation.equals(frame.fInformation) && this.fBeginOffset == frame.fBeginOffset;
//			}
//			return super.equals(obj);
//		}
//
//		@Override
//		public int hashCode() {
//			return (this.fInformation.hashCode() << 16) | this.fBeginOffset;
//		}
//	}

	private ITextViewer fViewer;

	private PopupWindow fContextInfoPopup;
	private BorderPane fRoot;

	private Node fContent;

//	/**
//	 * The last removed context frame is remembered in order to not re-query the
//	 * user about which context should be used.
//	 *
//	 * @since 3.0
//	 */
//	private ContextFrame fLastContext= null;

	private IContextInformationValidator contextInfoValidator;

	private ChangeListener<Number> selectionChange;

	/**
	 * Creates a new context information popup.
	 *
	 * @param viewer the viewer on top of which the context information is shown
	 */
	public ContextInformationPopup(IContextInformationValidator contextInfoValidator, ITextViewer viewer) {
		this.selectionChange = this::onSelectionChange;
		this.contextInfoValidator = contextInfoValidator;
		this.fViewer= viewer;

		this.fContextInfoPopup = new PopupWindow() {
		};

		this.fContextInfoPopup.setAutoFix(false);
		// we set autohide to false, because it prevents click events on the parent window
		this.fContextInfoPopup.setAutoHide(false);
		viewer.getTextWidget().sceneProperty().addListener( e -> {
			if( viewer.getTextWidget().getScene() != null ) {
				this.fContextInfoPopup.getScene().getStylesheets().setAll(viewer.getTextWidget().getScene().getStylesheets());
			}
		});
		if( viewer.getTextWidget().getScene() != null ) {
			this.fContextInfoPopup.getScene().getStylesheets().setAll(viewer.getTextWidget().getScene().getStylesheets());
		}
		this.fRoot = new BorderPane();
		this.fRoot.getStyleClass().add("styled-text-hover"); //$NON-NLS-1$

		this.fContextInfoPopup.getScene().setRoot(this.fRoot);

		this.fContextInfoPopup.setOnShowing(this::subscribe);
		this.fContextInfoPopup.setOnHidden(this::unsubscribe);
		
	}

	// we manually hide whenever a mouse pressed somewhere outside of the editor control occurs
	private EventHandler<MouseEvent> mousePressed = me -> {
		Bounds b = this.fViewer.getTextWidget().getBoundsInLocal();
		Bounds bScreen = this.fViewer.getTextWidget().localToScreen(b);
		if (bScreen.contains(me.getScreenX(), me.getScreenY())) {
			// click within text widget -> no hide
		}
		else {
			this.fContextInfoPopup.hide();
		}
	};
	
	// we manually hide whenever the stage loses its focus
	private ChangeListener<Boolean> stageFocusChanged = (x, o, n) -> {
		if (!n) {
			this.fContextInfoPopup.hide();
		}
	};
	
	private void subscribe(Event e) {
		this.fViewer.getTextWidget().caretOffsetProperty().addListener(this.selectionChange);
		this.fViewer.getTextWidget().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressed);
		this.fViewer.getTextWidget().getScene().getWindow().focusedProperty().addListener(this.stageFocusChanged);
	}
	private void unsubscribe(Event e) {
		this.fViewer.getTextWidget().caretOffsetProperty().removeListener(this.selectionChange);
		this.fViewer.getTextWidget().getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, this.mousePressed);
		this.fViewer.getTextWidget().getScene().getWindow().focusedProperty().removeListener(this.stageFocusChanged);
	}

	private void onSelectionChange(Observable x, Number oldSelection, Number newSelection) {
		int offset = this.fViewer.getTextWidget().caretOffsetProperty().get();
		if( this.contextInfoValidator != null ) {
			if( ! this.contextInfoValidator.isContextInformationValid(offset) ) {
				this.fContextInfoPopup.hide();
			} else {
				CharSequence infoText = this.contextInfoValidator.getInformationDisplayString(offset);
				if( infoText != null ) {
					updateInfoText(infoText);
				}
			}
		}
	}


	/**
	 * Shows all possible contexts for the given cursor position of the viewer.
	 *
	 * @param autoActivated <code>true</code>  if auto activated
	 * @return  a potential error message or <code>null</code> in case of no error
	 */
	@SuppressWarnings("static-method")
	public String showContextProposals(final boolean autoActivated) {
		return "TODO";
	}

	private void updateInfoText(CharSequence infoText) {
		if( infoText instanceof StyledString ) {
			StackPane wrap = new StackPane();
			wrap.getStyleClass().add("context-info"); //$NON-NLS-1$
			Node content = Util.toNode((StyledString) infoText);
			wrap.getChildren().add(content);
			this.fRoot.setCenter(wrap);
			this.fContent = wrap;
		} else if ( infoText instanceof HtmlString) {
			StackPane wrap = new StackPane();
			wrap.getStyleClass().add("context-info"); //$NON-NLS-1$
			wrap.setPrefHeight(this.fViewer.getTextWidget().getLineHeight(0) + 4);
			SimpleHtmlViewer content = new SimpleHtmlViewer(true, true);
			content.setContent((HtmlString) infoText);
			wrap.getChildren().add(content);
			this.fRoot.setCenter(wrap);
			this.fContent = wrap;
		} else {
			Label content = new Label();
			content.getStyleClass().add("context-info"); //$NON-NLS-1$
			content.setText(infoText.toString());
			this.fRoot.setCenter(content);
			this.fContent = content;
		}
	}

	/**
	 * Displays the given context information for the given offset.
	 *
	 * @param info the context information
	 * @param offset the offset
	 * @since 2.0
	 */
	public void showContextInformation(final IContextInformation info, final int offset) {
			CharSequence infoText = info == null ? null : info.getInformationDisplayString();
			if (infoText != null && infoText.length() > 0) {
				if( this.contextInfoValidator != null ) {
					this.contextInfoValidator.install(info, this.fViewer, offset);
				}
				updateInfoText(infoText);
				Point2D locationAtOffset = this.fViewer.getTextWidget().getLocationAtOffset(offset, LineLocation.ABOVE);
				locationAtOffset = this.fViewer.getTextWidget().localToScreen(locationAtOffset);
				if (locationAtOffset != null) {
					this.fContent.applyCss();
					double y = locationAtOffset.getY() - this.fContent.prefHeight(-1);
					this.fContextInfoPopup.show(this.fViewer.getTextWidget().getScene().getWindow(), locationAtOffset.getX(), y);
					this.fContextInfoPopup.sizeToScene();
				}
			}
			else {
				this.fContextInfoPopup.hide();
			}
	}

	@Override
	public boolean verifyKey(VerifyEvent event) {

		return false;
	}

}
