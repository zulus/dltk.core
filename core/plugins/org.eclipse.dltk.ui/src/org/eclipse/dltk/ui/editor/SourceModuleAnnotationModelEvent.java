/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * Event sent out by changes of the compilation unit annotation model.
 */
public class SourceModuleAnnotationModelEvent  extends AnnotationModelEvent {

	private boolean fIncludesProblemMarkerAnnotations;
	private IResource fUnderlyingResource;

	/**
	 * Constructor for CompilationUnitAnnotationModelEvent.
	 * @param model
	 * @param underlyingResource The annotation model's underlying resource
	 */
	public SourceModuleAnnotationModelEvent(IAnnotationModel model, IResource underlyingResource) {
		super(model);
		fUnderlyingResource= underlyingResource;
		fIncludesProblemMarkerAnnotations= false;
	}

	private void testIfProblemMarker(Annotation annotation) {
		if (fIncludesProblemMarkerAnnotations) {
			return;
		}
		if (annotation instanceof ScriptMarkerAnnotation) {
			fIncludesProblemMarkerAnnotations= ((ScriptMarkerAnnotation) annotation).isProblem();
		} else if (annotation instanceof MarkerAnnotation) {
			try {
				IMarker marker= ((MarkerAnnotation) annotation).getMarker();
				if (!marker.exists() || marker.isSubtypeOf(IMarker.PROBLEM)) {
					fIncludesProblemMarkerAnnotations= true;
				}
			} catch (CoreException e) {
				DLTKUIPlugin.log(e);
			}
		}
	}

	@Override
	public void annotationAdded(Annotation annotation) {
		super.annotationAdded(annotation);
		testIfProblemMarker(annotation);
	}


	@Override
	public void annotationRemoved(Annotation annotation) {
		super.annotationRemoved(annotation);
		testIfProblemMarker(annotation);
	}

	@Override
	public void annotationRemoved(Annotation annotation, Position position) {
		super.annotationRemoved(annotation, position);
		testIfProblemMarker(annotation);
	}

	@Override
	public void annotationChanged(Annotation annotation) {
		testIfProblemMarker(annotation);
		super.annotationChanged(annotation);
	}

	/**
	 * Returns whether the change included problem marker annotations.
	 *
	 * @return <code>true</code> if the change included marker annotations
	 */
	public boolean includesProblemMarkerAnnotationChanges() {
		return fIncludesProblemMarkerAnnotations;
	}

	/**
	 * Returns the annotation model's underlying resource
	 */
	public IResource getUnderlyingResource() {
		return fUnderlyingResource;
	}

}
