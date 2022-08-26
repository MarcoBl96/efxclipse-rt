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
package application.text;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.AnnotationPresenter;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class JavaSourceConfiguration extends SourceViewerConfiguration {

	private JavaCodeScanner fCodeScanner;
	private JavaDocScanner fJavaDocScanner;
	private JavaCommentScanner fMultilineCommentScanner;
	private JavaCommentScanner fSinglelineCommentScanner;
	private SingleTokenJavaScanner fStringScanner;
	private IAnnotationModel annotationModel;
	private List<AnnotationPresenter> annotationPresenters;

	public JavaSourceConfiguration() {
		initializeScanners();
	}

	@Override
	public IAnnotationModel getAnnotationModel() {
		return annotationModel;
	}

	private void initializeScanners() {
		fCodeScanner= new JavaCodeScanner();
		fMultilineCommentScanner= new JavaCommentScanner(IJavaColorConstants.JAVA_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner= new JavaCommentScanner(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
		fStringScanner= new SingleTokenJavaScanner(IJavaColorConstants.JAVA_STRING);
		fJavaDocScanner= new JavaDocScanner();
	}

	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return IJavaPartitions.JAVA_PARTITIONING;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler= new /*JavaPresentationReconciler*/ PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr= new DefaultDamagerRepairer(getJavaDocScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_DOC);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_DOC);

		dr= new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_STRING);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_STRING);

		dr= new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, IJavaPartitions.JAVA_CHARACTER);
		reconciler.setRepairer(dr, IJavaPartitions.JAVA_CHARACTER);

		return reconciler;
	}

	private ITokenScanner getStringScanner() {
		return fStringScanner;
	}

	private ITokenScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}

	private ITokenScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}

	private ITokenScanner getJavaDocScanner() {
		return fJavaDocScanner;
	}

	private ITokenScanner getCodeScanner() {
		return fCodeScanner;
	}

	@Override
	public String getStyleclassName() {
		return "java";
	}
	
	@Override
	public List<AnnotationPresenter> getAnnotationPresenters() {
		return annotationPresenters;
	}
}