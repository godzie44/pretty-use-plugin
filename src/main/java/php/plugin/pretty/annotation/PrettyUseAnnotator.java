package php.plugin.pretty.annotation;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.tool.UseStatementVisitor;

import java.util.List;

public class PrettyUseAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PhpFile)) {
            return;
        }

        UseStatementVisitor visitor = new UseStatementVisitor();

        element.accept(visitor);

        final List<PhpUseList> useStatementList = visitor.getStatementList();

        if (useStatementList.size() == 0) {
            return;
        }

        PrettifyDetector detector = PrettifyDetector.DetectorFactory.createFromSettings(ApplicationSettings.getInstance());

        if (!detector.isPretty(useStatementList)){
            this.annotateAllUseStatements(useStatementList, holder);
        }
    }

    private void annotateAllUseStatements(@NotNull List<PhpUseList> useStatementList, @NotNull AnnotationHolder holder) {
        TextRange range = new TextRange(useStatementList.get(0).getTextRange().getStartOffset(),
                useStatementList.get(useStatementList.size()-1).getTextRange().getEndOffset());

        Annotation annotation = holder.createWeakWarningAnnotation(range, "Use statement not pretty!");

        annotation.registerFix(new QuickFixToPretty());
    }
}
