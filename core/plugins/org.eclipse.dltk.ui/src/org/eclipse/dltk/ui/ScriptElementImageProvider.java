/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.DLTKUIMessages;
import org.eclipse.dltk.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ScriptElementImageProvider {
	/**
	 * Flags for the ScriptImageLabelProvider: Generate images with overlays.
	 */
	public final static int OVERLAY_ICONS = 0x1;

	/**
	 * Generate small sized images.
	 */
	public final static int SMALL_ICONS = 0x2;

	/**
	 * Use the 'light' style for rendering types.
	 */
	public final static int LIGHT_TYPE_ICONS = 0x4;

	public static final Point SMALL_SIZE = new Point(16, 16);

	public static final Point BIG_SIZE = new Point(22, 16);

	private static ImageDescriptor DESC_OBJ_PROJECT_CLOSED;
	private static ImageDescriptor DESC_OBJ_PROJECT;
	{
		ISharedImages images = DLTKUIPlugin.getDefault().getWorkbench()
				.getSharedImages();
		DESC_OBJ_PROJECT_CLOSED = images
				.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		DESC_OBJ_PROJECT = images
				.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}
	private ImageDescriptorRegistry fRegistry;

	public ScriptElementImageProvider() {
		fRegistry = null; // lazy initialization
	}

	public void dispose() {
	}

	/**
	 * Returns the icon for a given element. The icon depends on the element
	 * type and element properties. If configured, overlay icons are constructed
	 * for <code>ISourceReference</code>s.
	 *
	 * @param flags
	 *                  Flags as defined by the ScriptImageLabelProvider
	 */
	public Image getImageLabel(Object element, int flags) {
		return getImageLabel(computeDescriptor(element, flags));
	}

	private Image getImageLabel(ImageDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}
		return getRegistry().get(descriptor);
	}

	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry = DLTKUIPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}

	private static boolean showOverlayIcons(int flags) {
		return (flags & OVERLAY_ICONS) != 0;
	}

	/**
	 * @since 2.0
	 */
	public static boolean useSmallSize(int flags) {
		return (flags & SMALL_ICONS) != 0;
	}

	private static boolean useLightIcons(int flags) {
		return (flags & LIGHT_TYPE_ICONS) != 0;
	}

	private ImageDescriptor computeDescriptor(Object element, int flags) {
		if (element instanceof IModelElement) {
			return getScriptImageDescriptor((IModelElement) element, flags);
		} else if (element instanceof IAdaptable) {
			return getWorkbenchImageDescriptor((IAdaptable) element, flags);
		}
		return null;
	}

	private static final String LABELPROVIDERS_EXTENSION_POINT = "org.eclipse.dltk.ui.scriptElementLabelProviders"; //$NON-NLS-1$

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$

	private static Map<String, ILabelProvider> labelProviders = null;

	/**
	 * Creates {@link ILabelProvider} objects from configuration elements.
	 */
	private static void createProviders(IConfigurationElement[] elements) {
		labelProviders = new HashMap<>();
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			try {
				ILabelProvider pr = (ILabelProvider) element
						.createExecutableExtension(ATTR_CLASS);
				String nature = element.getAttribute(ATTR_NATURE);
				labelProviders.put(nature, pr);
			} catch (CoreException e) {
				DLTKUIPlugin.log(e);
			}
		}
	}

	private ILabelProvider getContributedLabelProvider(IModelElement element) {
		if (labelProviders == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry
					.getConfigurationElementsFor(
							LABELPROVIDERS_EXTENSION_POINT);
			createProviders(elements);
		}
		IDLTKLanguageToolkit languageToolkit = DLTKLanguageManager
				.getLanguageToolkit(element);
		if (languageToolkit == null) {
			return null;
		}
		return labelProviders.get(languageToolkit.getNatureId());
	}

	public ImageDescriptor getWorkbenchImageDescriptor(IAdaptable adaptable,
			int flags) {
		IWorkbenchAdapter wbAdapter = adaptable
				.getAdapter(IWorkbenchAdapter.class);
		if (wbAdapter == null) {
			return null;
		}
		ImageDescriptor descriptor = wbAdapter.getImageDescriptor(adaptable);
		if (descriptor == null) {
			return null;
		}
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		return new ScriptElementImageDescriptor(descriptor, 0, size);
	}

	public ImageDescriptor getScriptImageDescriptor(IModelElement element,
			int flags) {
		int adornmentFlags = computeAdornmentFlags(element, flags);
		Point size = useSmallSize(flags) ? SMALL_SIZE : BIG_SIZE;
		ImageDescriptor descr = getBaseImageDescriptor(element, flags);
		if (descr != null) {
			return new ScriptElementImageDescriptor(descr, adornmentFlags,
					size);
		}
		return null;
	}

	/**
	 * Returns an image descriptor for ascriptelement. This is the base image,
	 * no overlays.
	 */
	public ImageDescriptor getBaseImageDescriptor(IModelElement element,
			int renderFlags) {

		// if (true) {
		// return DLTKPluginImages.DESC_OBJS_UNKNOWN;
		// }

		ILabelProvider provider = getContributedLabelProvider(element);
		if (provider != null) {
			Image img = provider.getImage(element);
			if (img != null) {
				return ImageDescriptor.createFromImage(img);
			}
		}

		try {
			switch (element.getElementType()) {
			case IModelElement.IMPORT_CONTAINER:
				return DLTKPluginImages.DESC_OBJS_IMPCONT;
			case IModelElement.IMPORT_DECLARATION:
				return DLTKPluginImages.DESC_OBJS_IMPDECL;
			case IModelElement.METHOD: {
				IMethod method = (IMethod) element;
				int flags = method.getFlags();
				return getMethodImageDescriptor(flags);
			}

			case IModelElement.FIELD: {
				IField member = (IField) element;
				return getFieldImageDescriptor(member.getFlags());
			}

			case IModelElement.TYPE: {
				IType type = (IType) element;
				return getTypeImageDescriptor(type.getFlags(),
						useLightIcons(renderFlags));
			}

			case IModelElement.LOCAL_VARIABLE:
				return DLTKPluginImages.DESC_OBJS_LOCAL_VARIABLE;

			case IModelElement.PACKAGE_DECLARATION:
				return DLTKPluginImages.DESC_OBJS_PACKDECL;

			case IModelElement.PROJECT_FRAGMENT: {
				IProjectFragment root = (IProjectFragment) element;
				if (root.isExternal()) {
					if (root.isArchive()) {
						return DLTKPluginImages.DESC_OBJS_JAR_WSRC;
					}
					return DLTKPluginImages.DESC_OBJS_PACKFRAG_ROOT;
				}
				return DLTKPluginImages.DESC_OBJS_PACKFRAG_ROOT;
			}

			case IModelElement.SCRIPT_FOLDER:
				return getScriptFolderIcon(element, renderFlags);

			case IModelElement.SOURCE_MODULE:
				if (((ISourceModule) element).isBinary()) {
					return getBinaryModuleIcon(element, renderFlags);
				}
				boolean external = element instanceof IExternalSourceModule;
				return getSourceModuleIcon(element, external, renderFlags);

			case IModelElement.SCRIPT_PROJECT:
				IScriptProject jp = (IScriptProject) element;
				if (jp.getProject().isOpen()) {
					IProject project = jp.getProject();
					IWorkbenchAdapter adapter = project
							.getAdapter(IWorkbenchAdapter.class);
					if (adapter != null) {
						ImageDescriptor result = adapter
								.getImageDescriptor(project);
						if (result != null)
							return result;
					}
					return DESC_OBJ_PROJECT;
				}
				return DESC_OBJ_PROJECT_CLOSED;
			// return DESC_OBJ_PROJECT;

			case IModelElement.SCRIPT_MODEL:
				// return DLTKPluginImages.DESC_OBJS_JAVA_MODEL;
				return null;
			}
			Assert.isTrue(false,
					DLTKUIMessages.ScriptImageLabelprovider_assert_wrongImage
							+ "(" + element.getElementType() + ","
							+ element.getElementName() + ")");

			return DLTKPluginImages.DESC_OBJS_GHOST;
		} catch (ModelException e) {
			if (e.isDoesNotExist())
				return DLTKPluginImages.DESC_OBJS_UNKNOWN;

			DLTKUIPlugin.log(e);
			return DLTKPluginImages.DESC_OBJS_GHOST;
		}
	}

	private ImageDescriptor getBinaryModuleIcon(IModelElement element,
			int renderFlags) {
		return DLTKPluginImages.DESC_OBJS_CFILE;
	}

	private ImageDescriptor getSourceModuleIcon(IModelElement element,
			boolean external, int renderFlags) {
		return null;
	}

	private ImageDescriptor getScriptFolderIcon(IModelElement element,
			int renderFlags) {
		IScriptFolder fragment = (IScriptFolder) element;

		boolean containsScriptElements = false;
		try {
			containsScriptElements = fragment.hasChildren();
		} catch (ModelException e) {
			// assuming no children;
		}

		try {
			if (!containsScriptElements
					&& (fragment.getForeignResources().length > 0)) {
				return DLTKPluginImages.DESC_OBJS_EMPTY_PACKAGE_RESOURCES;
			}

			if (!containsScriptElements) {
				return DLTKPluginImages.DESC_OBJS_EMPTY_PACKAGE;
			}
		} catch (ModelException e) {
			// TODO: handle situation
		}
		return DLTKPluginImages.DESC_OBJS_PACKAGE;
	}

	public static ImageDescriptor getFieldImageDescriptor(int flags) {
		if (Flags.isPrivate(flags)) {
			return DLTKPluginImages.DESC_FIELD_PRIVATE;
		}
		if (Flags.isProtected(flags)) {
			return DLTKPluginImages.DESC_FIELD_PROTECTED;
		}
		if (Flags.isPublic(flags)) {
			return DLTKPluginImages.DESC_FIELD_PUBLIC;
		}
		return DLTKPluginImages.DESC_FIELD_DEFAULT;
	}

	public static ImageDescriptor getTypeImageDescriptor(int flags,
			boolean useLightIcons) {
		if (Flags.isInterface(flags)) {
			if (useLightIcons) {
				return DLTKPluginImages.DESC_OBJS_INTERFACEALT;
			}
		} else if (useLightIcons) {
			return DLTKPluginImages.DESC_OBJS_CLASSALT;
		}
		return getClassImageDescriptor(flags);
	}

	private static ImageDescriptor getClassImageDescriptor(int flags) {
		if ((flags & Modifiers.AccTest) != 0) {
			return DLTKPluginImages.DESC_OBJS_TEST;
		}
		if ((flags & Modifiers.AccTestCase) != 0) {
			return DLTKPluginImages.DESC_OBJS_TESTCASE;
		}
		if ((flags & Modifiers.AccNameSpace) != 0) {
			return DLTKPluginImages.DESC_OBJS_NAMESPACE;
		}

		if ((flags & Modifiers.AccModule) != 0) {
			return DLTKPluginImages.DESC_OBJS_MODULE;
		}
		if ((flags & Modifiers.AccInterface) != 0) {
			return DLTKPluginImages.DESC_OBJS_INTERFACE;
		}
		return DLTKPluginImages.DESC_OBJS_CLASS;
	}

	public static ImageDescriptor getMethodImageDescriptor(int flags) {
		if (Flags.isPrivate(flags)) {
			return DLTKPluginImages.DESC_METHOD_PRIVATE;
		}
		if (Flags.isProtected(flags)) {
			return DLTKPluginImages.DESC_METHOD_PROTECTED;
		}
		if (Flags.isPublic(flags)) {
			return DLTKPluginImages.DESC_METHOD_PUBLIC;
		}
		return DLTKPluginImages.DESC_METHOD_DEFAULT;
	}

	// ---- Methods to compute the adornments flags ----------------------------
	// -----

	public static int computeAdornmentFlags(IModelElement element,
			int renderFlags) {
		int flags = 0;
		if (showOverlayIcons(renderFlags) && element instanceof IMember) {
			try {
				IMember member = (IMember) element;
				if (element.getElementType() == IModelElement.METHOD
						&& ((IMethod) element).isConstructor()) {
					flags |= ScriptElementImageDescriptor.CONSTRUCTOR;
				}

				IType declaringType = member.getDeclaringType();
				boolean isInterface = declaringType != null
						&& Flags.isInterface(declaringType.getFlags());
				int modifiers = member.getFlags();

				if (Flags.isAbstract(modifiers) && !isInterface)
					flags |= ScriptElementImageDescriptor.ABSTRACT;
				if (Flags.isFinal(modifiers))
					flags |= ScriptElementImageDescriptor.FINAL;
				if (Flags.isStatic(modifiers))
					flags |= ScriptElementImageDescriptor.STATIC;

			} catch (ModelException e) {
				// do nothing. Can't compute runnable adornment or get flags
			}
		}
		return flags;
	}
}
