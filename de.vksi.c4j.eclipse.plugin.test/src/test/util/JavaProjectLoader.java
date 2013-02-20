package test.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;

public class JavaProjectLoader {
	
	public static IJavaProject loadProject(String projectName, String relativPathToProjectFile) {
		IPath pathToDotProjectFile = getAbsolutPathTo(relativPathToProjectFile);
		IJavaProject javaProject = null;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		try {
			IProjectDescription projectDescription = workspace.loadProjectDescription(pathToDotProjectFile);
			IProject project = workspace.getRoot().getProject(projectDescription.getName());
			JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);

			project = root.getProject(projectName);

			// set the Java nature
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });

			// create the project
			project.setDescription(description, null);
			javaProject = JavaCore.create(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return javaProject;
	}
	
	public static IPath getAbsolutPathTo(String relativFilePath) {
		File file = new File(relativFilePath);
		return new Path(file.getAbsolutePath());
	}

	public static ICompilationUnit getCompilationUnit(IJavaProject javaProject, String nameWithFileExtension)
			throws JavaModelException {
		List<IPackageFragment> sourcePackages = getSourcePackages(javaProject);

		for (IPackageFragment pkg : sourcePackages) {
			for (ICompilationUnit unit : pkg.getCompilationUnits()) {
				if (unit.getElementName().equals(nameWithFileExtension))
					return unit;
			}
		}

		return null;
	}

	private static List<IPackageFragment> getSourcePackages(IJavaProject javaProject)
			throws JavaModelException {
		List<IPackageFragment> sourcePackages = new ArrayList<IPackageFragment>();
		if (javaProject != null) {
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment currPackage : packages) {
				if (currPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					sourcePackages.add(currPackage);
				}
			}
		}
		return sourcePackages;
	}

	public static IMethod getMethod(ICompilationUnit unit, String name, String signature)
			throws JavaModelException {
		IType[] allTypes = unit.getAllTypes();
		IMethod method = null;

		for (IType type : allTypes) {
			method = findMethod(type, name, signature);
			if (method != null)
				return method;
		}
		return null;
	}

	private static IMethod findMethod(IType type, String name, String signature) throws JavaModelException {
		IMethod[] methods = type.getMethods();
		for (IMethod method : methods) {
			if (method.getElementName().equals(name) && method.getSignature().equals(signature))
				return method;
		}
		return null;
	}

	public static IType getType(ICompilationUnit targetCompUnit) throws JavaModelException {
		IJavaElement[] children = targetCompUnit.getChildren();
		for (IJavaElement child : children) {
			if (child instanceof IType)
				return (IType) child;
		}

		return null;
	}
}
