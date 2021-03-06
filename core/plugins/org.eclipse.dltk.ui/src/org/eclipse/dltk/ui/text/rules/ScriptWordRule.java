/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.dltk.ui.text.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * An implementation of <code>IRule</code> capable of detecting script words.
 *
 * <p>
 * This class is a direct copy of {@link WordRule} capable of tracking the 'last
 * seen' word and returning a token that differs from the default token for this
 * rule when the next word is encountered. For instance, this could be used to
 * provide syntax hightlighting for method declarations.
 * </p>
 *
 * <p>
 * If an <code>IScriptWordDetector</code> implementation is used, the rule will
 * also check that character prior to the word start character is valid for the
 * start of the word to occur. For instance, this could be used to prevent
 * method names that also match builtin keywords from being hightlighted as
 * such.
 * </p>
 *
 * @see IWordDetector
 * @see IScriptWordDetector
 */
public class ScriptWordRule implements IRule {

	/** Internal setting for the un-initialized column constraint */
	protected static final int UNDEFINED = -1;

	/** The word detector used by this rule */
	protected IWordDetector fDetector;
	/**
	 * The default token to be returned on success and if nothing else has been
	 * specified.
	 */
	protected IToken fDefaultToken;
	/** The column constraint */
	protected int fColumn = UNDEFINED;
	/** The table of predefined words and token for this rule */
	protected Map<String, IToken> fWords = new HashMap<>();
	/** Buffer used for pattern detection */
	private StringBuffer fBuffer = new StringBuffer();

	private boolean fIgnoreCase = false;

	private int fLastSeenEnd = 0;
	private String fLastSeen = Util.EMPTY_STRING;
	private Map<String, IToken> fNext = new HashMap<>();

	/**
	 * Creates a rule which, with the help of an word detector, will return the
	 * token associated with the detected word. If no token has been associated,
	 * the scanner will be rolled back and an undefined token will be returned
	 * in order to allow any subsequent rules to analyze the characters.
	 *
	 * @param detector
	 *            the word detector to be used by this rule, may not be
	 *            <code>null</code>
	 * @see #addWord(String, IToken)
	 */
	public ScriptWordRule(IWordDetector detector) {
		this(detector, Token.UNDEFINED, false);
	}

	/**
	 * Creates a rule which, with the help of a word detector, will return the
	 * token associated with the detected word. If no token has been associated,
	 * the specified default token will be returned.
	 *
	 * @param detector
	 *            the word detector to be used by this rule, may not be
	 *            <code>null</code>
	 * @param defaultToken
	 *            the default token to be returned on success if nothing else is
	 *            specified, may not be <code>null</code>
	 * @see #addWord(String, IToken)
	 */
	public ScriptWordRule(IWordDetector detector, IToken defaultToken) {
		this(detector, defaultToken, false);
	}

	/**
	 * Creates a rule which, with the help of a word detector, will return the
	 * token associated with the detected word. If no token has been associated,
	 * the specified default token will be returned.
	 *
	 * @param detector
	 *            the word detector to be used by this rule, may not be
	 *            <code>null</code>
	 * @param defaultToken
	 *            the default token to be returned on success if nothing else is
	 *            specified, may not be <code>null</code>
	 * @param ignoreCase
	 *            the case sensitivity associated with this rule
	 * @see #addWord(String, IToken)
	 */
	public ScriptWordRule(IWordDetector detector, IToken defaultToken,
			boolean ignoreCase) {
		Assert.isNotNull(detector);
		Assert.isNotNull(defaultToken);

		fDetector = detector;
		fDefaultToken = defaultToken;
		fIgnoreCase = ignoreCase;
	}

	/**
	 * Adds a word and the token to be returned if it is detected.
	 *
	 * @param word
	 *            the word this rule will search for, may not be
	 *            <code>null</code>
	 * @param token
	 *            the token to be returned if the word has been found, may not
	 *            be <code>null</code>
	 */
	public void addWord(String word, IToken token) {
		Assert.isNotNull(word);
		Assert.isNotNull(token);

		fWords.put(word, token);
	}

	/**
	 * Add a word to be treated in a 'last seen' context.
	 *
	 * <p>
	 * If the specified word was the 'last seen', the specified token will be
	 * returned as the token for the next detected word.
	 * </p>
	 *
	 * @param word
	 *            'last seen' word look for, may not be <code>null</code>
	 * @param token
	 *            the token to be returned if the 'last seen' word is detected
	 *
	 */
	public void addNextTokenAfterSeen(String word, IToken token) {
		Assert.isNotNull(word);
		Assert.isNotNull(token);

		fNext.put(word, token);
	}

	/**
	 * Sets a column constraint for this rule. If set, the rule's token will
	 * only be returned if the pattern is detected starting at the specified
	 * column. If the column is smaller then 0, the column constraint is
	 * considered removed.
	 *
	 * @param column
	 *            the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		if (column < 0)
			column = UNDEFINED;
		fColumn = column;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		// don't unwind the scanner if we're at the beginning
		if (fDetector instanceof IScriptWordDetector
				&& scanner.getColumn() > 0) {
			scanner.unread();
			int c = scanner.read();
			if (!((IScriptWordDetector) fDetector).isPriorCharValid((char) c)) {
				return Token.UNDEFINED;
			}
		}

		int c = scanner.read();
		if (fDetector.isWordStart((char) c)) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {

				fBuffer.setLength(0);
				do {
					fBuffer.append((char) c);
					c = scanner.read();
				} while (c != ICharacterScanner.EOF
						&& fDetector.isWordPart((char) c));
				scanner.unread();

				String buffer = fBuffer.toString();

				//
				// our swap criteria:
				//
				// 1) we have a mapping for the 'lastSeen' word
				// 2) the current word doesn't start w/ the 'lastSeen'
				// 3) the current position in the scanner is > the end column of
				// 'lastSeen'
				//
				if (fNext.containsKey(fLastSeen)
						&& !buffer.startsWith(fLastSeen)
						&& scanner.getColumn() > fLastSeenEnd) {
					IToken replace = fNext.get(fLastSeen);
					fLastSeen = buffer;
					fLastSeenEnd = scanner.getColumn();
					return replace;
				}

				IToken token = fWords.get(buffer);
				if (fIgnoreCase) {
					Iterator<String> iter = fWords.keySet().iterator();
					while (iter.hasNext()) {
						String key = iter.next();
						if (buffer.equalsIgnoreCase(key)) {
							token = fWords.get(key);
							break;
						}
					}
				}

				if (token != null) {
					fLastSeen = buffer;
					fLastSeenEnd = scanner.getColumn();
					return token;
				}

				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);

				return fDefaultToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	/**
	 * Returns the characters in the buffer to the scanner.
	 *
	 * @param scanner
	 *            the scanner to be used
	 */
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i = fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}
}
