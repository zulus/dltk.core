/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.internal.core.search.IndexQueryRequestor;

public class OrPattern extends SearchPattern implements IIndexConstants {

	protected SearchPattern[] patterns;

	/*
	 * Whether this pattern is erasure match.
	 */
	// boolean isErasureMatch;

	/**
	 * One of {@link #R_ERASURE_MATCH}, {@link #R_EQUIVALENT_MATCH},
	 * {@link #R_FULL_MATCH}.
	 */
	int matchCompatibility;

	public OrPattern(SearchPattern leftPattern, SearchPattern rightPattern) {
		super(Math.max(leftPattern.getMatchRule(), rightPattern.getMatchRule()), leftPattern.getToolkit());
		((InternalSearchPattern) this).kind = OR_PATTERN;

		Assert.isNotNull(leftPattern.getToolkit());
		Assert.isTrue(leftPattern.getToolkit().equals(rightPattern.getToolkit()));

		SearchPattern[] leftPatterns = leftPattern instanceof OrPattern ? ((OrPattern) leftPattern).patterns : null;
		SearchPattern[] rightPatterns = rightPattern instanceof OrPattern ? ((OrPattern) rightPattern).patterns : null;
		int leftSize = leftPatterns == null ? 1 : leftPatterns.length;
		int rightSize = rightPatterns == null ? 1 : rightPatterns.length;
		this.patterns = new SearchPattern[leftSize + rightSize];

		if (leftPatterns == null)
			this.patterns[0] = leftPattern;
		else
			System.arraycopy(leftPatterns, 0, this.patterns, 0, leftSize);
		if (rightPatterns == null)
			this.patterns[leftSize] = rightPattern;
		else
			System.arraycopy(rightPatterns, 0, this.patterns, leftSize, rightSize);

		// Store erasure match
		matchCompatibility = 0;
		for (int i = 0, length = this.patterns.length; i < length; i++) {
			matchCompatibility |= ((DLTKSearchPattern) this.patterns[i]).matchCompatibility;
		}
	}

	@Override
	public void findIndexMatches(Index index, IndexQueryRequestor requestor, SearchParticipant participant,
			IDLTKSearchScope scope, IProgressMonitor progressMonitor) throws IOException {
		// per construction, OR pattern can only be used with a PathCollector
		// (which already gather results using a set)
		try {
			index.startQuery();
			for (int i = 0, length = this.patterns.length; i < length; i++)
				((InternalSearchPattern) this.patterns[i]).findIndexMatches(index, requestor, participant, scope,
						progressMonitor);
		} finally {
			index.stopQuery();
		}
	}

	@Override
	public SearchPattern getBlankPattern() {
		return null;
	}

	boolean isErasureMatch() {
		return (this.matchCompatibility & R_ERASURE_MATCH) != 0;
	}

	@Override
	public boolean isPolymorphicSearch() {
		for (int i = 0, length = this.patterns.length; i < length; i++)
			if (((InternalSearchPattern) this.patterns[i]).isPolymorphicSearch())
				return true;
		return false;
	}

	/**
	 * Returns whether the pattern has signatures or not.
	 *
	 * @return true if one at least of the stored pattern has signatures.
	 */
	public final boolean hasSignatures() {
		boolean isErasureMatch = isErasureMatch();
		for (int i = 0, length = this.patterns.length; i < length && !isErasureMatch; i++) {
			if (((DLTKSearchPattern) this.patterns[i]).hasSignatures())
				return true;
		}
		return false;
	}

	public SearchPattern[] getPatterns() {
		return patterns;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.patterns[0].toString());
		for (int i = 1, length = this.patterns.length; i < length; i++) {
			buffer.append("\n| "); //$NON-NLS-1$
			buffer.append(this.patterns[i].toString());
		}
		return buffer.toString();
	}
}
