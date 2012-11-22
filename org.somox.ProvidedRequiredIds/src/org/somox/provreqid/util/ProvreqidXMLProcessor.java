/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.somox.provreqid.util;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

import org.somox.provreqid.ProvreqidPackage;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ProvreqidXMLProcessor extends XMLProcessor {

	/**
	 * Public constructor to instantiate the helper.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProvreqidXMLProcessor() {
		super((EPackage.Registry.INSTANCE));
		ProvreqidPackage.eINSTANCE.eClass();
	}
	
	/**
	 * Register for "*" and "xml" file extensions the ProvreqidResourceFactoryImpl factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected Map<String, Resource.Factory> getRegistrations() {
		if (registrations == null) {
			super.getRegistrations();
			registrations.put(XML_EXTENSION, new ProvreqidResourceFactoryImpl());
			registrations.put(STAR_EXTENSION, new ProvreqidResourceFactoryImpl());
		}
		return registrations;
	}

} //ProvreqidXMLProcessor
