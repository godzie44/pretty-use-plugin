package php.plugin.pretty.annotation;

import com.jetbrains.php.lang.psi.elements.PhpUseList;

public class SimpleDetectStrategy implements DetectStrategy {
    @Override
    public boolean pairIsPrettify(PhpUseList previousStatement, PhpUseList currentStatement, int previousStatementWeight, int currentStatementWeight) {
        return previousStatementWeight >= currentStatementWeight;
    }
}
