/**
 * !(#) NeoWriter.java
 * Copyright (c) 2014 DNW Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     DNW Technologies - initial API and implementation
 *
 * Create by manbaum since Oct 10, 2014.
 */
package com.dnw.depmap.neo;

import java.util.List;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * Class/Interface NeoWriter.
 * 
 * @author manbaum
 * @since Oct 10, 2014
 * 
 */
public class NeoWriter {

	private final NeoAccessor accessor;

	public NeoWriter(NeoAccessor accessor) {
		this.accessor = accessor;
	}

	public static final String CREATECLASS = "merge (t:Class {name:{name}}) on create set t.displayname={dname}, t.implements={impls}, t.extends={parent}";
	public static final String CREATEINTERFACE = "merge (t:Interface {name:{name}}) on create set t.displayname={dname}, t.extends={parent}";
	public static final String CREATEIMPLEMENTS = "match (t:Type {name:{name}}) match (b:Type {name:{nameb}}) merge (t)-[:Implements]->(b)";
	public static final String CREATEEXTENDS = "match (t:Type {name:{name}}) match (b:Type {name:{nameb}}) merge (t)-[:Extends]->(b)";
	public static final String CREATEMETHOD = "match (t:Type {name:{type}})  merge (t)-[:Declare]->(m:Method {name:{name}}) on create set m.displayname={dname}";
	public static final String CREATEINVOKE = "match (f:Method {name:{namef}}) match (t:Method {name:{namet}}) merge (f)-[:Invoke {args:{args}}]->(t)";

	private static String nameOf(ITypeBinding type) {
		return type.getQualifiedName();
	}

	private static String displayNameOf(ITypeBinding type) {
		return type.getName();
	}

	private static String nameOf(IMethodBinding method) {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass().getQualifiedName());
		sb.append(Modifier.isStatic(method.getModifiers()) ? '/' : '#');
		sb.append(method.isConstructor() ? "<ctor>" : method.getName());
		sb.append('(');
		boolean first = true;
		for (ITypeBinding t : method.getParameterTypes()) {
			if (first)
				first = false;
			else
				sb.append(',');
			sb.append(t.getQualifiedName());
		}
		sb.append(')');
		return sb.toString();
	}

	private static String displayNameOf(IMethodBinding method) {
		StringBuffer sb = new StringBuffer();
		// sb.append(method.getDeclaringClass().getName());
		// sb.append(Modifier.isStatic(method.getModifiers()) ? '/' : '#');
		sb.append(method.isConstructor() ? "<ctor>" : method.getName());
		sb.append("()");
		return sb.toString();
	}

	private final static class ITypeBindingK extends M.K {
		@Override
		public Object make(Object value) {
			return nameOf(((ITypeBinding) value));
		}

		public final static ITypeBindingK K = new ITypeBindingK();
	}

	public void createTypeNoCheck(ITypeBinding type) {
		M p = M.m().e("name", nameOf(type)).e("dname", displayNameOf(type));
		if (type.isInterface()) {
			p.e(ITypeBindingK.K, "parent", type.getInterfaces());
			accessor.execute(CREATEINTERFACE, p);
		} else {
			p.e(ITypeBindingK.K, "impls", type.getInterfaces());
			p.e(ITypeBindingK.K, "parent", type.getSuperclass());
			accessor.execute(CREATECLASS, p);
		}
	}

	public void createImplementsNoCheck(ITypeBinding type, ITypeBinding base) {
		M p = M.m().e("name", nameOf(type)).e("nameb", nameOf(base));
		accessor.execute(CREATEIMPLEMENTS, p);
	}

	public void createExtendsNoCheck(ITypeBinding type, ITypeBinding base) {
		M p = M.m().e("name", nameOf(type)).e("nameb", nameOf(base));
		accessor.execute(CREATEEXTENDS, p);
	}

	public void createMethodNoCheck(IMethodBinding method) {
		ITypeBinding type = method.getDeclaringClass();
		// createTypeNoCheck(type);
		M p = M.m().e("type", nameOf(type)).e("name", nameOf(method))
				.e("dname", displayNameOf(method));
		accessor.execute(CREATEMETHOD, p);
	}

	public void createInvocationNoCheck(IMethodBinding from, IMethodBinding to,
			List<?> args) {
		// createMethodNoCheck(from);
		createMethodNoCheck(to);
		M p = M.m().e("namef", nameOf(from)).e("namet", nameOf(to))
				.e("args", args);
		accessor.execute(CREATEINVOKE, p);
	}

	public void createType(ITypeBinding type) {
		if (type != null && BlackOrWhite.allowed(nameOf(type))) {
			createTypeNoCheck(type);
			if (type.isInterface()) {
				for (ITypeBinding t : type.getInterfaces()) {
					if (BlackOrWhite.allowed(nameOf(t))) {
						createTypeNoCheck(t);
						createExtendsNoCheck(type, t);
					}
				}
			} else {
				for (ITypeBinding t : type.getInterfaces()) {
					if (BlackOrWhite.allowed(nameOf(t))) {
						createTypeNoCheck(t);
						createImplementsNoCheck(type, t);
					}
				}
				if (BlackOrWhite.allowed(nameOf(type.getSuperclass()))) {
					createExtendsNoCheck(type, type.getSuperclass());
				}
			}
		}
	}

	public void createMethod(IMethodBinding method) {
		ITypeBinding type = method.getDeclaringClass();
		String typeName = nameOf(type);
		if (method != null && BlackOrWhite.allowed(typeName)) {
			createMethodNoCheck(method);
		}
	}

	public void createInvocation(IMethodBinding from, IMethodBinding to,
			List<?> args) {
		if (from != null && to != null) {
			String typeNameF = nameOf(from.getDeclaringClass());
			boolean allowF = BlackOrWhite.allowed(typeNameF);
			String typeNameT = nameOf(to.getDeclaringClass());
			boolean allowT = BlackOrWhite.allowed(typeNameT);
			if (allowF && allowT)
				createInvocationNoCheck(from, to, args);
		}
	}
}