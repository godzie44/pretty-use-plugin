package php.plugin.pretty.annotation;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.RegexOption;
import php.plugin.pretty.tool.UseStatementVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

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

        int previousWeight = this.findWeight(useStatementList.get(0).getText());

        for (int i = 1; i < useStatementList.size(); i++) {
            int weight = this.findWeight(useStatementList.get(i).getText());
            if (weight > previousWeight) {
                this.annotateAllUseStatements(useStatementList, holder);
                return;
            }

            previousWeight = weight;
        }
    }

    private void annotateAllUseStatements(@NotNull List<PhpUseList> useStatementList, @NotNull AnnotationHolder holder) {
        TextRange range = new TextRange(useStatementList.get(0).getTextRange().getStartOffset(),
                useStatementList.get(useStatementList.size()-1).getTextRange().getEndOffset());

        Annotation annotation = holder.createWeakWarningAnnotation(range, "Use statement not pretty! :(");

        annotation.registerFix(new QuickFixToPretty());
    }

    private int findWeight(@NotNull String text)
    {
        Collection<RegexOption> options = ApplicationSettings.getEnabledRegexOptions();

        RegexOption regexDefinitionForAnotherLibs = null;

        for (RegexOption regexDefinition : options) {
            Matcher matcher = regexDefinition.getPattern().matcher(text);

            if (regexDefinition.forAnotherLibs()) {
                regexDefinitionForAnotherLibs = regexDefinition;
            }

            if (matcher.find()) {
                return regexDefinition.getWeight();
            }
        }

        if (regexDefinitionForAnotherLibs != null) {
            return regexDefinitionForAnotherLibs.getWeight();
        }

        return 0;
    }

}
