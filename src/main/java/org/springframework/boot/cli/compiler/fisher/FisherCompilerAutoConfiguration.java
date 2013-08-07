/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.cli.compiler.fisher;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.springframework.boot.cli.compiler.CompilerAutoConfiguration;
import org.springframework.boot.cli.compiler.GroovyCompilerConfiguration;

/**
 * {@link CompilerAutoConfiguration} for adding <code>@ResponseBody</code>.
 * 
 * @author Dave Syer
 */
public class FisherCompilerAutoConfiguration extends CompilerAutoConfiguration {
	
	private static ClassNode responseBody = ClassHelper.make("ResponseBody");
	private static ClassNode requestMapping = ClassHelper.make("RequestMapping");
	private static ClassNode noResponseBody = ClassHelper.make("NoResponseBody");

	@Override
	public void applyImports(ImportCustomizer imports) {
		imports.addImports(NoResponseBody.class.getName());
	}

	@Override
	public void apply(GroovyClassLoader loader,
			GroovyCompilerConfiguration configuration, GeneratorContext generatorContext,
			SourceUnit source, ClassNode classNode) throws CompilationFailedException {
		for (MethodNode methodNode : classNode.getAllDeclaredMethods()) {
			if (hasAnnotation(methodNode, requestMapping) && !hasAnnotation(methodNode, responseBody) && !hasAnnotation(methodNode, noResponseBody)) {
				methodNode.addAnnotation(new AnnotationNode(responseBody));
			}
		}
	}

	private boolean hasAnnotation(MethodNode node, ClassNode annotation) {
		return !node.getAnnotations(annotation).isEmpty();
	}

	@Override
	public void applyToMainClass(GroovyClassLoader loader,
			GroovyCompilerConfiguration configuration, GeneratorContext generatorContext,
			SourceUnit source, ClassNode classNode) throws CompilationFailedException {
		// Could add switch for auto config, but it seems like it wouldn't get used much
		addEnableAutoConfigurationAnnotation(source, classNode);
	}
	
	public static @interface NoResponseBody {
		
	}

	private void addEnableAutoConfigurationAnnotation(SourceUnit source,
			ClassNode classNode) {
		if (!hasEnableAutoConfigureAnnotation(classNode)) {
			try {
				Class<?> annotationClass = source.getClassLoader().loadClass(
						"org.springframework.boot.autoconfigure.EnableAutoConfiguration");
				AnnotationNode annotationNode = new AnnotationNode(new ClassNode(
						annotationClass));
				classNode.addAnnotation(annotationNode);
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

	private boolean hasEnableAutoConfigureAnnotation(ClassNode classNode) {
		for (AnnotationNode node : classNode.getAnnotations()) {
			if ("EnableAutoConfiguration".equals(node.getClassNode()
					.getNameWithoutPackage())) {
				return true;
			}
		}
		return false;
	}
}
