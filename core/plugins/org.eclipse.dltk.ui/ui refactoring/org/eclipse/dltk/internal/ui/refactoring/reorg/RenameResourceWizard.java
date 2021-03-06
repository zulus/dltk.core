/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.refactoring.reorg;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;


public class RenameResourceWizard extends RenameRefactoringWizard {
	
	public RenameResourceWizard(Refactoring refactoring) {
		super(refactoring,
			RefactoringMessages.RenameResourceWizard_defaultPageTitle, 
			RefactoringMessages.RenameResourceWizard_inputPage_description, 
			DLTKPluginImages.DESC_WIZBAN_REFACTOR,
			""/*IScriptHelpContextIds.RENAME_RESOURCE_WIZARD_PAGE*/); //$NON-NLS-1$
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
		
	}
}
