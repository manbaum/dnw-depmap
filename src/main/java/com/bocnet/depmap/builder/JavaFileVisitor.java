/**
 * !(#) JavaFileVisitor.java
 * Copyright (c) 2014 Bank of China Co. Ltd.
 * All rights reserved.
 *
 * Create by manbaum.
 * On Sep 29, 2014.
 */
package com.bocnet.depmap.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Class/Interface JavaFileVisitor.
 * 
 * @author manbaum
 * @since Sep 29, 2014
 * 
 */
public class JavaFileVisitor implements IResourceVisitor {

	/**
	 * Overrider method visit.
	 * 
	 * @author manbaum
	 * @since Sep 29, 2014
	 * 
	 * @param resource
	 * @return
	 * @throws CoreException
	 * 
	 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
	 */
	public boolean visit(IResource resource) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}
}
