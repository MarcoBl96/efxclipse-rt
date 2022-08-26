/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.fx.core.ThreadSynchronize;
import org.eclipse.fx.core.text.SourceTextEditActions;
import org.eclipse.fx.core.text.TextEditAction;
import org.eclipse.fx.text.ui.ITextViewer;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea.LineLocation;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.fx.ui.controls.styledtext.TriggerActionMapping.Context;
import org.eclipse.jface.text.IDocument;

import javafx.geometry.Point2D;

@SuppressWarnings("restriction")
public class ContentAssistant implements IContentAssistant {
	private final Function<ContentAssistContextData, List<ICompletionProposal>> proposalComputer;
	private ITextViewer fViewer;
//	private ContentProposalPopup fProposalPopup;
	private ContextInformationPopup fContextInfoPopup;
	private final ThreadSynchronize threadSynchnronize;
	private final IContextInformationValidator validator;

	private boolean directlyApplySingleMatch;

	private LazyInitReference<ContentProposalPopup> fProposalPopup = new LazyInitReference<>();

	public ContentAssistant(IContextInformationValidator validator, ThreadSynchronize threadSynchnronize, Function<ContentAssistContextData, List<ICompletionProposal>> proposalComputer) {
		this.validator = validator;
		this.threadSynchnronize = threadSynchnronize;
		this.proposalComputer = proposalComputer;
	}

	@Override
	public void configureWindowSize(Supplier<Point2D> windowSizeRetriever, Consumer<Point2D> windowSizePersister) {
		this.fProposalPopup.init(r -> r.configureWindowSize(windowSizeRetriever, windowSizePersister));
	}

	public void setDirectlyApplySingleMatch(boolean directlyApplySingleMatch) {
		this.directlyApplySingleMatch = directlyApplySingleMatch;
	}

	@Override
	public void install(ITextViewer textViewer) {
		if( this.fViewer == null ) {
			this.fViewer = textViewer;
			this.fProposalPopup.setFactory(()->new ContentProposalPopup(this.threadSynchnronize, this, textViewer, this.proposalComputer));
//			this.fProposalPopup = new ContentProposalPopup(this.threadSynchnronize, this, textViewer, this.proposalComputer);
			this.fViewer.subscribeAction(this::handleAction);
			this.fContextInfoPopup = new ContextInformationPopup(validator, textViewer);
		}
	}

	private boolean handleAction(TextEditAction action, Context context) {
		if (action == SourceTextEditActions.PROPOSAL_REQUEST) {
			final int offset = this.fViewer.getTextWidget().getCaretOffset();

			List<ICompletionProposal> proposals = this.proposalComputer.apply(new ContentAssistContextData(offset, this.fViewer.getDocument()/*,""*/));

			if( proposals.size() == 1 && this.directlyApplySingleMatch) {
				ICompletionProposal completionProposal = proposals.get(0);

				completionProposal.apply(this.fViewer.getDocument());

				showContextInformation(completionProposal.getContextInformation(), offset);

				//this.fViewer.getTextWidget().setSelection(proposals.get(0).getSelection(this.fViewer.getDocument()));
			} else if( ! proposals.isEmpty() ) {
				Point2D p = this.fViewer.getTextWidget().getLocationAtOffset(this.fViewer.getTextWidget().getCaretOffset(), LineLocation.BELOW);

				Point2D coords = this.fViewer.getTextWidget().localToScreen(p);

				CompletableFuture<Optional<ICompletionProposal>> chosenProposal = this.fProposalPopup.get().displayProposals(proposals, this.fViewer.getTextWidget().getCaretOffset(), coords);

				chosenProposal.thenAccept( v -> {
					v.ifPresent(proposal->{
						IDocument document = this.fViewer.getDocument();
						// apply the proposal
						proposal.apply(document);
						TextSelection selection = proposal.getSelection(document);
						if( selection.length > 0 ) {
							this.fViewer.getTextWidget().setCaretOffset(selection.offset +selection.length);
						}

						this.fViewer.getTextWidget().setSelection(selection);

						if (proposal.getContextInformation() != null) {
							showContextInformation(proposal.getContextInformation(), this.fViewer.getTextWidget().getCaretOffset());
						}
						this.fViewer.getTextWidget().layout();
					});
				});
			}

			return true;
		}
		return false;
	}

	void showContextInformation(IContextInformation contextInformation, int offset) {
		if (this.fContextInfoPopup != null) {
			this.fContextInfoPopup.showContextInformation(contextInformation, offset);
		}
	}

	@Override
	public void showContextInformation(IContextInformation info) {
		if (this.fContextInfoPopup != null) {
			this.fContextInfoPopup.showContextInformation(info, info.getContextInformationPosition());
		}
	}
}
