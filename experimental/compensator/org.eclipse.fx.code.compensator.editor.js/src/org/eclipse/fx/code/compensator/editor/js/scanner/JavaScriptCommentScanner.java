/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
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
package org.eclipse.fx.code.compensator.editor.js.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.CombinedWordRule;
import org.eclipse.jface.text.rules.CombinedWordRule.CharacterBuffer;
import org.eclipse.jface.text.rules.CombinedWordRule.WordMatcher;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;

public class JavaScriptCommentScanner extends AbstractJavaScriptScanner {
	protected static final String TASK_TAG= IJavaScriptColorConstants.TASK_TAG;
	private TaskTagMatcher fTaskTagMatcher;
	private String fDefaultTokenProperty;
//	private String[] fTokenProperties;
	
	public JavaScriptCommentScanner(String defaultTokenProperty) {
		this(defaultTokenProperty, new String[] { defaultTokenProperty, TASK_TAG });
	}
	
	public JavaScriptCommentScanner(String defaultTokenProperty, String[] tokenProperties) {
		fDefaultTokenProperty= defaultTokenProperty;
//		fTokenProperties= tokenProperties;
		initialize();
	}

	@Override
	protected List<IRule> createRules() {
		List<IRule> list= new ArrayList<IRule>();
		Token defaultToken= getToken(fDefaultTokenProperty);

		List<WordMatcher> matchers= createMatchers();
		if (matchers.size() > 0) {
			CombinedWordRule combinedWordRule= new CombinedWordRule(new AtJavaIdentifierDetector(), defaultToken);
			for (int i= 0, n= matchers.size(); i < n; i++)
				combinedWordRule.addWordMatcher(matchers.get(i));
			list.add(combinedWordRule);
		}

		setDefaultReturnToken(defaultToken);

		return list;
	}
	
	protected List<WordMatcher> createMatchers() {
		List<WordMatcher> list= new ArrayList<WordMatcher>();

		// Add rule for Task Tags.
		boolean isCaseSensitive= true;
		String tasks= "TODO";
//		if (getPreferenceStore().contains(COMPILER_TASK_TAGS)) {
//			tasks= getPreferenceStore().getString(COMPILER_TASK_TAGS);
//			isCaseSensitive= ENABLED.equals(getPreferenceStore().getString(COMPILER_TASK_CASE_SENSITIVE));
//		} else if (fCorePreferenceStore != null) {
//			tasks= fCorePreferenceStore.getString(COMPILER_TASK_TAGS);
//			isCaseSensitive= ENABLED.equals(fCorePreferenceStore.getString(COMPILER_TASK_CASE_SENSITIVE));
//		}
		if (tasks != null) {
			fTaskTagMatcher= new TaskTagMatcher(getToken(TASK_TAG));
			fTaskTagMatcher.addTaskTags(tasks);
			fTaskTagMatcher.setCaseSensitive(isCaseSensitive);
			list.add(fTaskTagMatcher);
		}

		return list;
	}
	
	private class TaskTagMatcher extends CombinedWordRule.WordMatcher {

		private IToken fToken;
		/**
		 * Uppercase words
		 * @since 3.0
		 */
		private Map<CharacterBuffer, IToken> fUppercaseWords= new HashMap<CharacterBuffer, IToken>();
		/**
		 * <code>true</code> if task tag detection is case-sensitive.
		 * @since 3.0
		 */
		private boolean fCaseSensitive= true;
		/**
		 * Buffer for uppercase word
		 * @since 3.0
		 */
		private CombinedWordRule.CharacterBuffer fBuffer= new CombinedWordRule.CharacterBuffer(16);

		public TaskTagMatcher(IToken token) {
			fToken= token;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#clearWords()
		 * @since 3.0
		 */
		@Override
		public synchronized void clearWords() {
			super.clearWords();
			fUppercaseWords.clear();
		}

		public synchronized void addTaskTags(String value) {
			String[] tasks= split(value, ","); //$NON-NLS-1$
			for (int i= 0; i < tasks.length; i++) {
				if (tasks[i].length() > 0) {
					addWord(tasks[i], fToken);
				}
			}
		}

		private String[] split(String value, String delimiters) {
			StringTokenizer tokenizer= new StringTokenizer(value, delimiters);
			int size= tokenizer.countTokens();
			String[] tokens= new String[size];
			int i= 0;
			while (i < size)
				tokens[i++]= tokenizer.nextToken();
			return tokens;
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#addWord(java.lang.String, org.eclipse.jface.text.rules.IToken)
		 * @since 3.0
		 */
		@Override
		public synchronized void addWord(String word, IToken token) {
			Assert.isNotNull(word);
			Assert.isNotNull(token);

			super.addWord(word, token);
			fUppercaseWords.put(new CombinedWordRule.CharacterBuffer(word.toUpperCase()), token);
		}

		/*
		 * @see org.eclipse.jdt.internal.ui.text.CombinedWordRule.WordMatcher#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, org.eclipse.jdt.internal.ui.text.CombinedWordRule.CharacterBuffer)
		 * @since 3.0
		 */
		@Override
		public synchronized IToken evaluate(ICharacterScanner scanner, CombinedWordRule.CharacterBuffer word) {
			if (fCaseSensitive)
				return super.evaluate(scanner, word);

			fBuffer.clear();
			for (int i= 0, n= word.length(); i < n; i++)
				fBuffer.append(Character.toUpperCase(word.charAt(i)));

			IToken token= fUppercaseWords.get(fBuffer);
			if (token != null)
				return token;
			return Token.UNDEFINED;
		}

		/**
		 * Enables/disables the case-sensitivity of the task tag detection.
		 *
		 * @param caseSensitive <code>true</code> iff case-sensitivity should be enabled
		 * @since 3.0
		 */
		public void setCaseSensitive(boolean caseSensitive) {
			fCaseSensitive= caseSensitive;
		}
	}
	
	private static class AtJavaIdentifierDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return c == '@' || Character.isJavaIdentifierStart(c);
		}

		public boolean isWordPart(char c) {
			return c == '.' || Character.isJavaIdentifierPart(c);
		}
	}

}
