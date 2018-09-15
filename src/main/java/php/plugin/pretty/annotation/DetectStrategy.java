package php.plugin.pretty.annotation;

import com.jetbrains.php.lang.psi.elements.PhpUseList;

interface DetectStrategy {
    boolean pairIsPrettify(PhpUseList previousStatement, PhpUseList currentStatement, int previousStatementWeight, int currentStatementWeight);
}
