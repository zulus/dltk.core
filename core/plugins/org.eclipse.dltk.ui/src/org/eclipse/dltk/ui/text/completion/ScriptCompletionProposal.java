/*******************************************************************************
 * Copyright (c) 2000, 20018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/

package org.eclipse.dltk.ui.text.completion;

import java.util.function.Supplier;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

public class ScriptCompletionProposal extends AbstractScriptCompletionProposal {

	/**
	 * Creates a new completion proposal. All fields are initialized based on the
	 * provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param image             the image to display for this proposal
	 * @param displayString     the string to be displayed for the proposal If set
	 *                          to <code>null</code>, the replacement string will be
	 *                          taken as display string.
	 */
	public ScriptCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		this(replacementString, replacementOffset, replacementLength, image, displayString, relevance, false);
	}

	/**
	 * Creates a new completion proposal. All fields are initialized based on the
	 * provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param image             the image to display for this proposal
	 * @param displayString     the string to be displayed for the proposal If set
	 *                          to <code>null</code>, the replacement string will be
	 *                          taken as display string.
	 * @param relevance         the relevance
	 * @param indoc             <code>true</code> for a javadoc proposal
	 *
	 */
	public ScriptCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance, boolean indoc) {
		this(replacementString, replacementOffset, replacementLength, image, new StyledString(displayString), relevance,
				false);
	}

	/**
	 * Creates a new completion proposal. All fields are initialized based on the
	 * provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param image             the image to display for this proposal
	 * @param displayString     the StyledString to be displayed for the proposal If
	 *                          set to <code>null</code>, the replacement string
	 *                          will be taken as display string.
	 * @param relevance         the relevance
	 * @param indoc             <code>true</code> for a javadoc proposal
	 * @since 5.2
	 *
	 */
	public ScriptCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image,
			StyledString displayString, int relevance, boolean indoc) {
		this(replacementString, replacementOffset, replacementLength, displayString, relevance, indoc);
		setImage(image);
	}

	/**
	 * Creates a new completion proposal. All fields are initialized based on the
	 * provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param image             the image to display for this proposal
	 * @param displayString     the StyledString to be displayed for the proposal If
	 *                          set to <code>null</code>, the replacement string
	 *                          will be taken as display string.
	 * @param relevance         the relevance
	 * @param indoc             <code>true</code> for a javadoc proposal
	 * @since 5.9
	 *
	 */
	public ScriptCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			Supplier<Image> image, StyledString displayString, int relevance, boolean indoc) {
		this(replacementString, replacementOffset, replacementLength, displayString, relevance, indoc);
		setImageFactory(image);
	}

	private ScriptCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			StyledString displayString, int relevance, boolean indoc) {
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);

		setReplacementString(replacementString);
		setReplacementOffset(replacementOffset);
		setReplacementLength(replacementLength);
		setStyledDisplayString(displayString == null ? new StyledString(replacementString) : displayString);
		setRelevance(relevance);
		setCursorPosition(replacementString.length());
		setInDoc(indoc);
		setSortString(displayString == null ? replacementString : displayString.toString());
	}

	@Override
	protected boolean isValidPrefix(String prefix) {
		String word = getDisplayString();
		if (isInDoc()) {
			int idx = word.indexOf("{@link "); //$NON-NLS-1$
			if (idx == 0) {
				word = word.substring(7);
			} else {
				idx = word.indexOf("{@value "); //$NON-NLS-1$
				if (idx == 0) {
					word = word.substring(8);
				}
			}
		}
		return isPrefix(prefix, word);
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		String string = getReplacementString();
		int pos = string.indexOf('(');
		if (pos > 0) {
			return string.subSequence(0, pos);
		}
		return string;
	}

}
