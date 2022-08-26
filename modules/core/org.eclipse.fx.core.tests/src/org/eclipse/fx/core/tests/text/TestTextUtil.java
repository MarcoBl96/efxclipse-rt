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
package org.eclipse.fx.core.tests.text;

import org.eclipse.fx.core.Triple;
import org.eclipse.fx.core.text.TextUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestTextUtil {
	@Test
	public void test_replaceTabBySpace() {
		Triple<char[], int[], int[]> rv = TextUtil.replaceTabBySpace("h	ello	world".toCharArray(), 4);
		Assert.assertNotNull(rv);
		Assert.assertEquals("h    ello    world", new String(rv.value1));
		Assert.assertEquals(1, rv.value2[0]);
		Assert.assertEquals(6, rv.value2[1]);
		Assert.assertEquals(1, rv.value3[0]);
		Assert.assertEquals(9, rv.value3[1]);

		char[] source = "hello world".toCharArray();
		rv = TextUtil.replaceTabBySpace(source, 4);
		Assert.assertNotNull(rv);
		Assert.assertNotEquals(source, rv);

	}
}
