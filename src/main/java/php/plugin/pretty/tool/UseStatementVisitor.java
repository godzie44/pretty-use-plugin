package php.plugin.pretty.tool;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.lang.psi.elements.PhpUseList;

import java.util.ArrayList;
import java.util.List;

public class UseStatementVisitor extends PsiRecursiveElementWalkingVisitor {
    private List<PhpUseList> useStatementList = new ArrayList<>();

    public UseStatementVisitor(){
        super();
    }

    @Override
    public void visitElement(PsiElement element) {
        if ((element instanceof PhpUseList)) {
            visitUseList((PhpUseList) element);
        }
        super.visitElement(element);
    }

    private void visitUseList(PhpUseList phpUseList) {
        if (phpUseList.isTraitImport()) {
            return;
        }

        if (phpUseList.getDeclarations().length == 0) {
            return;
        }

        useStatementList.add(phpUseList);
    }

    public List<PhpUseList> getStatementList()
    {
        return useStatementList;
    }
}
