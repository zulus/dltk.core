/*******************************************************************************
 * Copyright (c) 2008, 2017 Jae Gangemi and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.dltk.ui.tests.core;

import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.core.tests.model.TestConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.junit.Assert;

public class DLTKUILanguageManagerTests extends AbstractModelTests {

	public DLTKUILanguageManagerTests(String name) {
		super(name);
	}

	public void testGetUILanguageToolkit() {
		IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
				.getLanguageToolkit(TestConstants.NATURE_ID);

		Assert.assertNotNull(toolkit);
	}

	public void testUILanguageToolkits() {
		IDLTKUILanguageToolkit[] toolkits = DLTKUILanguageManager
				.getLanguageToolkits();

		/*
		 * need at least 1, > 1 means multiple plugin implementations were 
		 * installed in the pde when the test ran 
		 */
		Assert.assertTrue(toolkits.length > 0);
	}

}
