/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.code;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.tagging.ICommentProvider;
import org.eclipse.dltk.internal.corext.refactoring.tagging.IScriptableRefactoring;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * Partial implementation of a scriptable refactoring which provides a comment
 * for the history.
 *
 */
public abstract class ScriptableRefactoring extends Refactoring implements IScriptableRefactoring, ICommentProvider {

	/**
	 * Creates a fatal error status telling that the input element does not exist.
	 *
	 * @param element the input element, or <code>null</code>
	 * @param name    the name of the refactoring
	 * @param id      the id of the refactoring
	 * @return the refactoring status
	 */
	public static RefactoringStatus createInputFatalStatus(final Object element, final String name, final String id) {
		Assert.isNotNull(name);
		Assert.isNotNull(id);
		return RefactoringStatus.createFatalErrorStatus(
				MessageFormat.format(RefactoringCoreMessages.InitializableRefactoring_inputs_do_not_exist, name, id));
	}

	/**
	 * Creates a warning status telling that the input element does not exist.
	 *
	 * @param element the input element, or <code>null</code>
	 * @param name    the name of the refactoring
	 * @param id      the id of the refactoring
	 * @return the refactoring status
	 */
	public static RefactoringStatus createInputWarningStatus(final Object element, final String name, final String id) {
		Assert.isNotNull(name);
		Assert.isNotNull(id);
		if (element != null)
			return RefactoringStatus.createWarningStatus(
					MessageFormat.format(RefactoringCoreMessages.InitializableRefactoring_input_not_exists, name, id));
		else
			return RefactoringStatus.createWarningStatus(MessageFormat
					.format(RefactoringCoreMessages.InitializableRefactoring_inputs_do_not_exist, name, id));
	}

	/** The comment */
	private String fComment;

	@Override
	public boolean canEnableComment() {
		return true;
	}

	/**
	 * Creates a fatal error status telling that the input element does not exist.
	 *
	 * @param element the input element, or <code>null</code>
	 * @param id      the id of the refactoring
	 * @return the refactoring status
	 */
	public final RefactoringStatus createInputFatalStatus(final Object element, final String id) {
		return createInputFatalStatus(element, getName(), id);
	}

	/**
	 * Creates a warning status telling that the input element does not exist.
	 *
	 * @param element the input element, or <code>null</code>
	 * @param id      the id of the refactoring
	 * @return the refactoring status
	 */
	public final RefactoringStatus createInputWarningStatus(final Object element, final String id) {
		return createInputWarningStatus(element, getName(), id);
	}

	@Override
	public String getComment() {
		return fComment;
	}

	@Override
	public void setComment(String comment) {
		fComment = comment;
	}
}
