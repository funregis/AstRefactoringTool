package fr.altari.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * 
 * @author rlimare
 *
 */
public class AstRefactoringFieldAnnotation {
    static boolean overwriteFile;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
	Iterator<File> filesIter = FileUtils.iterateFiles(new File(
		"C:\\Developpement\\workspaces\\argos-etoil-trunk\\ArgosEtoileWS\\src\\main\\java\\fr\\stime\\argosetoile"),
		FileFilterUtils.suffixFileFilter("java"), FileFilterUtils.trueFileFilter());
	while (filesIter.hasNext()) {
	    // parse the file
	    File source = filesIter.next();
	    CompilationUnit cu = JavaParser.parse(source, "UTF-8");
	    overwriteFile = false;
	    new MethodVisitor().visit(cu, null);
	    new FieldVisitor().visit(cu, null);
	    if (overwriteFile) {
		System.out.println(cu.toString());
		FileUtils.writeStringToFile(source, cu.toString());
	    }
	}
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    @SuppressWarnings("rawtypes")
    private static class MethodVisitor extends VoidVisitorAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void visit(MethodDeclaration n, Object arg) {
	    new ParameterVisitor().visit(n, null);
	}
    }

    @SuppressWarnings("rawtypes")
    private static class FieldVisitor extends VoidVisitorAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void visit(FieldDeclaration n, Object arg) {
	    new AnnotationVisitor().visit(n, n.getVariables()
		    .get(0));
	}
    }

    @SuppressWarnings("rawtypes")
    private static class ParameterVisitor extends VoidVisitorAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public void visit(Parameter n, Object arg) {
	    new AnnotationVisitor().visit(n, n.getId());
	}
    }

    @SuppressWarnings("rawtypes")
    private static class AnnotationVisitor extends VoidVisitorAdapter {

	@Override
	public void visit(NormalAnnotationExpr n, Object arg) {
	    if (n.getName().getName().equals("FieldValidation")) {
		List<MemberValuePair> pairs = new ArrayList<MemberValuePair>();
		boolean isRequired = false;
		for (MemberValuePair pair : n.getPairs()) {
		    if (pair.getName()
			    .equals("required")) {
			String value = pair.getValue().toString();
			pairs.add(new MemberValuePair("name", ASTHelper.createNameExpr(value)));
			pair.setValue(ASTHelper.createNameExpr("true"));
			isRequired = true;
		    }
		    pairs.add(pair);

		}
		if (!isRequired) {
		    pairs.add(new MemberValuePair("name", ASTHelper.createNameExpr("\"" + arg.toString() + "\"")));
		}
		n.setPairs(pairs);
		overwriteFile = true;
	    }

	}
    }

}
