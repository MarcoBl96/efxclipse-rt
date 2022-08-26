/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.code.compensator.editor.hsl;

import org.eclipse.fx.code.compensator.editor.hsl.internal.JavaScriptHelper;
import org.eclipse.fx.code.compensator.hsl.hSL.ParitionRule;
import org.eclipse.fx.code.compensator.hsl.hSL.PartitionJSRule;
import org.eclipse.fx.code.compensator.hsl.hSL.PartitionMultiLineRule;
import org.eclipse.fx.code.compensator.hsl.hSL.PartitionSingleLineRule;
import org.eclipse.fx.code.compensator.hsl.hSL.RulePartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class HSLRuleBasedPartitionScanner extends RuleBasedPartitionScanner {
	public HSLRuleBasedPartitionScanner(ClassLoader cl, RulePartitioner paritioner) {
		IPredicateRule[] pr = new IPredicateRule[paritioner.getRules().size()];
		int i = 0;
		
		for( ParitionRule r : paritioner.getRules() ) {
			if( r instanceof PartitionSingleLineRule ) {
				PartitionSingleLineRule sr = (PartitionSingleLineRule) r;
				pr[i] = new SingleLineRule(sr.getStartSeq(), sr.getEndSeq(), new Token(sr.getParition().getName()), sr.getEscapeSeq() != null ? sr.getEndSeq().charAt(0) : 0, false);
			} else if( r instanceof PartitionMultiLineRule ) {
				PartitionMultiLineRule mr = (PartitionMultiLineRule) r;
				pr[i] = new MultiLineRule(mr.getStartSeq(), mr.getEndSeq(), new Token(mr.getParition().getName()), mr.getEscapeSeq() != null ? mr.getEscapeSeq().charAt(0) : 0, false);
			} else if( r instanceof PartitionJSRule ) {
				pr[i] = JavaScriptHelper.loadScript(cl, r, ((PartitionJSRule) r).getFileURI());
			}
			i++;
		}
		setPredicateRules(pr);
	}
}
