package php.plugin.pretty.annotation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;

public class AdvancedDetectStrategy implements DetectStrategy {
    @Override
    public boolean pairIsPrettify(
            PhpUseList previousStatement, PhpUseList currentStatement, int previousStatementWeight, int currentStatementWeight
    ) {
        boolean currentStatementInNextGroup = previousStatementWeight != currentStatementWeight;
        if (currentStatementInNextGroup) {
            return this.hasLineBreakBetweenStatements(previousStatement, currentStatement);
        } else {
            return this.firstTextLessOrEqualSecond(previousStatement.getText(), currentStatement.getText());
        }
    }

    private boolean hasLineBreakBetweenStatements(PhpUseList previousStatement, PhpUseList statement) {
        int lineBreakCounter = 0;

        PsiElement nextPsi = previousStatement;

        do {
            nextPsi = nextPsi.getNextSibling();

            if (nextPsi instanceof PsiWhiteSpace) {
                lineBreakCounter += nextPsi.getText().length() - nextPsi.getText().replace("\n", "").length();
            }

        } while (nextPsi != statement && nextPsi != null);

        return lineBreakCounter > 1;
    }

    private boolean firstTextLessOrEqualSecond(@NotNull String text1, @NotNull String text2) {
        return text1.toUpperCase().compareTo(text2.toUpperCase()) <= 0;
    }
}
