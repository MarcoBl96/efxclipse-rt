/*******************************************************************************
* Copyright (c) 2014 BestSolution.at and others.
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
package org.eclipse.fx.code.editor.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.fx.code.editor.Constants;
import org.eclipse.fx.code.editor.Input;
import org.eclipse.fx.code.editor.SourceSelection;
import org.eclipse.fx.code.editor.fx.services.ContextInformationPresenter;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider;
import org.eclipse.fx.code.editor.fx.services.EditorContextMenuProvider.Type;
import org.eclipse.fx.code.editor.services.ContextInformation;
import org.eclipse.fx.code.editor.services.DelegatingEditingContext;
import org.eclipse.fx.code.editor.services.EditingContext;
import org.eclipse.fx.code.editor.services.URIProvider;
import org.eclipse.fx.core.Subscription;
import org.eclipse.fx.core.di.ContextValue;
import org.eclipse.fx.core.event.EventBus;
import org.eclipse.fx.core.preferences.Preference;
import org.eclipse.fx.core.text.TextEditAction;
import org.eclipse.fx.text.ui.contentassist.IContextInformation;
import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.text.ui.source.SourceViewerConfiguration;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.BorderPane;

/**
 * Component setting up a JavaFX text editor
 */
@SuppressWarnings("restriction")
public class TextEditor {

	private IDocument document;

	private SourceViewerConfiguration configuration;

	private IDocumentPartitioner partitioner;

	private Input<?> input;

	private Property<Input<?>> activeInput;

	private SourceViewer viewer;

	private Integer tabAdvance;

	private Boolean spacesForTab;

	private EditingContext editingContext;

	private ContextInformationPresenter contextInformationPresenter;

	private EditorContextMenuProvider contextMenuProvider;

	private List<Subscription> toDispose = new ArrayList<>();

	private <T> void installListener(Property<T> property, ChangeListener<? super T> listener) {
		property.addListener(listener);
		toDispose.add(() -> property.removeListener(listener));
	}

	private void disposeListeners() {
		toDispose.forEach(s -> s.dispose());
		toDispose.clear();
	}

	public void setContextMenuProvider(EditorContextMenuProvider contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void setContextInformationPresenter(ContextInformationPresenter contextInformationPresenter) {
		this.contextInformationPresenter = contextInformationPresenter;
	}

	public void setEditingContext(EditingContext editingContext) {
		if( viewer != null ) {
			throw new IllegalArgumentException("The EditingContext has to be set before the editor is initialized");
		}
		this.editingContext = editingContext;
	}

	public void setDocument(IDocument document) {
		if( viewer != null ) {
			throw new IllegalArgumentException("The document has to be set before the editor is initialized");
		}
		this.document = document;
	}

	public void setSourceViewerConfiguration(SourceViewerConfiguration configuration) {
		if( viewer != null ) {
			throw new IllegalArgumentException("The configuration has to be set before the editor is initialized");
		}
		this.configuration = configuration;
	}

	public void setPartitioner(IDocumentPartitioner partitioner) {
		if( viewer != null ) {
			throw new IllegalArgumentException("The partitioner has to be set before the editor is initialized");
		}

		this.partitioner = partitioner;
	}

	@Inject
	public void setTabAdvance(@Preference(nodePath=Constants.PREFERENCE_NODE_PATH, key=Constants.PREFERENCE_TAB_ADVANCE) Integer tabAdvance ) {
		if( tabAdvance != null && tabAdvance.intValue() > 0 ) {
			this.tabAdvance = tabAdvance;
		}

		if( viewer != null && tabAdvance != null ) {
			viewer.getTextWidget().setTabAdvance(tabAdvance.intValue());
		}
	}

	@Inject
	public void setInsertSpacesForTab(@Preference(nodePath=Constants.PREFERENCE_NODE_PATH, key=Constants.PREFERENCE_SPACES_FOR_TAB) Boolean spacesForTab ) {
		this.spacesForTab = spacesForTab;
		if( viewer != null && spacesForTab != null ) {
			viewer.getTextWidget().setInsertSpacesForTab(spacesForTab.booleanValue());
		}
	}

	@Inject
	public void setZoomFactor(@Preference(nodePath=Constants.PREFERENCE_NODE_PATH, key=Constants.PREFERENCE_ZOOMFACTOR, defaultValue="1.0") double zoomFactor) {
		if( viewer != null ) {
			viewer.getTextWidget().setFontZoomFactor(zoomFactor);
		}
	}

	public void setInput(Input<?> input) {
		if( viewer != null ) {
			throw new IllegalArgumentException("The input has to be set before the editor is initialized");
		}

		this.input = input;
	}

	public void setActiveInputTracker(@ContextValue("activeInput") Property<Input<?>> activeInput) {
		this.activeInput = activeInput;
	}

	@PostConstruct
	public void initUI(BorderPane pane,
			EventBus eventBus,
			EditorContextMenuProvider contextMenuProvider,
			ContextInformationPresenter contextInformationPresenter,
			EditingContext editingContext,
			IDocument document,
			SourceViewerConfiguration configuration,
			IDocumentPartitioner partitioner,
			Input<?> input,
			@Optional @ContextValue("activeInput") Property<Input<?>> activeInput,
			@Preference(nodePath=Constants.PREFERENCE_NODE_PATH, key=Constants.PREFERENCE_ZOOMFACTOR, defaultValue="1.0") Property<Double> zoomFactor) {
		setContextMenuProvider(contextMenuProvider);
		setContextInformationPresenter(contextInformationPresenter);
		setEditingContext(editingContext);
		setDocument(document);
		setSourceViewerConfiguration(configuration);
		setPartitioner(partitioner);
		setInput(input);
		setActiveInputTracker(activeInput);

		viewer = createSourceViewer();
		if( tabAdvance != null ) {
			viewer.getTextWidget().setTabAdvance(tabAdvance.intValue());
		}
		if( spacesForTab != null ) {
			viewer.getTextWidget().setInsertSpacesForTab(spacesForTab.booleanValue());
		}

		if( document instanceof IDocumentExtension3 ) {
			((IDocumentExtension3)document).setDocumentPartitioner(configuration.getConfiguredDocumentPartitioning(viewer),partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		viewer.configure(configuration);
		viewer.setDocument(document, configuration.getAnnotationModel());
		pane.setCenter(viewer);
		if( activeInput != null ) {
			activeInput.setValue(input);
		}
		viewer.getTextWidget().setFontZoomFactor(zoomFactor.getValue() != null ? zoomFactor.getValue() : 1.0);

		installListener(viewer.getTextWidget().fontZoomFactorProperty(), (o,ol,ne) -> {
			zoomFactor.setValue(ne.doubleValue());
		});

		toDispose.add(
				eventBus.subscribe(Constants.TOPIC_SELECT_SOURCE, EventBus.data(this::onSourceSelect))
		);

		if (editingContext instanceof DelegatingEditingContext) {
			DelegatingEditingContext c = (DelegatingEditingContext) editingContext;
			c.setDelegate(new EditingContext() {
				private TextSelection convert(IRegion selection) {
					return new TextSelection(selection.getOffset(), selection.getLength());
				}
				private IRegion convert(TextSelection selection) {
					return new Region(selection.offset, selection.length);
				}

				@Override
				public void showContextInformation(ContextInformation info) {
					if (contextInformationPresenter != null) {
						IContextInformation ci = contextInformationPresenter.createInformation(info);
						viewer.showContextInformation(ci);
					}
				}

				@Override
				public void triggerAction(TextEditAction action) {
					viewer.getTextWidget().triggerAction(action);
				}

				@Override
				public void setSelection(IRegion selection) {
					viewer.getTextWidget().setSelection(convert(selection));
				}

				@Override
				public void setCaretOffset(int offset, boolean keepSelection) {
					viewer.getTextWidget().impl_setCaretOffset(offset, keepSelection);
				}

				@Override
				public void setCaretOffset(int offset) {
					viewer.getTextWidget().setCaretOffset(offset);
				}

				@Override
				public void revealCaret() {
					viewer.getTextWidget().revealCaret();
				}

				@Override
				public Subscription registerOnSelectionChanged(Consumer<IRegion> listener) {
					ChangeListener<TextSelection> l = (x, o, n) -> {
						if( n != null ) {
							listener.accept(convert(n));
						} else {
							listener.accept(null);
						}
					};
					viewer.getTextWidget().selectionProperty().addListener(l);
					return new Subscription() {
						@Override
						public void dispose() {
							viewer.getTextWidget().selectionProperty().removeListener(l);
						}
					};
				}

				@Override
				public Subscription registerOnCaretOffsetChanged(Consumer<Integer> listener) {
					ChangeListener<? super Number> l = (x, o, n) -> {
						listener.accept(n.intValue());
					};
					viewer.getTextWidget().caretOffsetProperty().addListener(l);
					return new Subscription() {
						@Override
						public void dispose() {
							viewer.getTextWidget().caretOffsetProperty().removeListener(l);
						}
					};
				}

				@Override
				public IRegion getSelection() {
					TextSelection selection = viewer.getTextWidget().getSelection();
					return convert(selection);
				}

				@Override
				public int getCaretOffset() {
					return viewer.getTextWidget().getCaretOffset();
				}
			});
		}

		contextMenuProvider.attacheMenu(viewer.getTextWidget(), Type.CONTENT);
	}

	private void onSourceSelect(SourceSelection data) {
		if (((URIProvider)input).getURI().equals(data.uri)) {
			getSourceViewer().getTextWidget().setCaretOffset(data.selection.getOffset() + data.selection.getLength());
			getSourceViewer().getTextWidget().setSelection(new TextSelection(data.selection.getOffset(), data.selection.getLength()));
			getSourceViewer().getTextWidget().revealCaret();
		}
	}

	protected SourceViewer createSourceViewer() {
		return new SourceViewer();
	}

	public SourceViewer getSourceViewer() {
		return viewer;
	}

	@Persist
	public void save() {
		input.persist();
		documentSaved();
	}

	protected void documentSaved() {

	}

	@Focus
	void focused() {
		if( activeInput != null ) {
			activeInput.setValue(input);
		}

		viewer.getTextWidget().requestFocus();
		//TODO We should remember the caret offset
		if( viewer.getTextWidget().getCaretOffset() == -1 && viewer.getTextWidget().getContent().getCharCount() > 0 ) {
			viewer.getTextWidget().setCaretOffset(0);
		}
	}

	@PreDestroy
	void destroy() {
		if( activeInput != null && activeInput.getValue() == input ) {
			activeInput.setValue(null);
		}
		this.input = null;

		if (editingContext instanceof DelegatingEditingContext) {
			// cleanup delegate
			DelegatingEditingContext c = (DelegatingEditingContext) editingContext;
			c.dispose();
		}

		disposeListeners();

		viewer.dispose();
	}
}
