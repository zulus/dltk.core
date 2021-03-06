/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.changes;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.core.refactoring.Change;

public class MoveScriptFolderChange extends ScriptFolderReorgChange {

	public MoveScriptFolderChange(IScriptFolder pack, IProjectFragment dest) {
		super(pack, dest, null);
	}

	@Override
	protected Change doPerformReorg(IProgressMonitor pm) throws ModelException {
		getPackage().move(getDestination(), null, getNewName(), true, pm);
		return null;
	}

	@Override
	public String getName() {
		return MessageFormat.format(RefactoringCoreMessages.MovePackageChange_move, getPackage().getElementName(),
				getDestination().getElementName());
	}
}
