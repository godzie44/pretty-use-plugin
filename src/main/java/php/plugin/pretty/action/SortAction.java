package php.plugin.pretty.action;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.RegexOption;
import php.plugin.pretty.dict.WeighedUseStatementGroup;
import php.plugin.pretty.tool.UseStatementVisitor;

import java.util.*;

public class SortAction implements Runnable {

    @NotNull
    private PhpFile phpFile;

    public SortAction(@NotNull PhpFile psiFile) {
        this.phpFile = psiFile;
    }

    @Override
    public void run() {
        UseStatementVisitor visitor = new UseStatementVisitor();

        this.phpFile.accept(visitor);

        final List<PhpUseList> useStatementList = visitor.getStatementList();

        if (useStatementList.size() == 0) {
            return;
        }

        final PriorityQueue<WeighedUseStatementGroup> useStatementGroupQueue =
                this.createUseStatementGroupSortedQueue(useStatementList);

        this.replaceStatements(useStatementList, useStatementGroupQueue);

    }

    private PriorityQueue<WeighedUseStatementGroup> createUseStatementGroupSortedQueue(final List<PhpUseList> useStatementList) {
        UseStatementGroupSimpleContainer groups = new UseStatementGroupSimpleContainer();

        PriorityQueue<RegexOption> sortedRegexOptionQueue = ApplicationSettings.createRegexOptionQueue();

        while (sortedRegexOptionQueue.peek() != null) {
            groups.add(new WeighedUseStatementGroup(sortedRegexOptionQueue.poll()));
        }

        List<PhpUseList> copiedUseStatementList = new ArrayList<>();

        for (PhpUseList phpUseList : useStatementList) {
            copiedUseStatementList.add((PhpUseList) phpUseList.copy());
        }

        for (WeighedUseStatementGroup useStatementGroup : groups) {
            for (Iterator<PhpUseList> iterator = copiedUseStatementList.iterator(); iterator.hasNext(); ) {
                PhpUseList useStatement = iterator.next();

                boolean statementInserted = useStatementGroup.insertIfValid(useStatement);

                if (statementInserted) {
                    iterator.remove();
                }
            }
        }

        if (copiedUseStatementList.size() != 0) {
            if (groups.hasStatementGroupForAnotherLibs()) {
                groups.getAnotherLibsStatementGroup().insertFromCollection(copiedUseStatementList);
            } else {
                groups.appendUseStatementGroupForAllStatements().insertFromCollectionOnlyValid(copiedUseStatementList);
            }
        }

        return new PriorityQueue<>(groups);
    }


    private class UseStatementGroupSimpleContainer extends ArrayList<WeighedUseStatementGroup> {
        boolean hasStatementGroupForAnotherLibs() {
            for (WeighedUseStatementGroup useStatementGroup : this) {
                if (useStatementGroup.forAnotherLibs()) {
                    return true;
                }
            }

            return false;
        }

        WeighedUseStatementGroup getAnotherLibsStatementGroup() {
            for (WeighedUseStatementGroup useStatementGroup : this) {
                if (useStatementGroup.forAnotherLibs()) {
                    return useStatementGroup;
                }
            }

            return null;
        }

        WeighedUseStatementGroup appendUseStatementGroupForAllStatements()
        {
            WeighedUseStatementGroup groupForAllStatement = WeighedUseStatementGroup.createForAllStatements(0);
            this.add(groupForAllStatement);

            return groupForAllStatement;
        }
    }

    private void replaceStatements(List<PhpUseList> oldUseStatementList, PriorityQueue<WeighedUseStatementGroup> newUseStatementGroupQueue) {
        int replacedCounter = 0;
        while (newUseStatementGroupQueue.peek() != null) {

            WeighedUseStatementGroup useStatementGroup = newUseStatementGroupQueue.poll();

            PhpUseList lastInserted = null;

            PriorityQueue<PhpUseList> sortedUseStatementsQueue = useStatementGroup.createSortedUseStatementsQueue();
            while (sortedUseStatementsQueue.peek() != null) {
                PhpUseList newUseStatement = sortedUseStatementsQueue.poll();
                PhpUseList oldUseStatement = oldUseStatementList.get(replacedCounter++);

                lastInserted = (PhpUseList) oldUseStatement.replace(newUseStatement);

                if (replacedCounter != (oldUseStatementList.size())) {
                    this.deleteUnnecessaryWhiteSpaces(lastInserted);
                }

            }

            if (lastInserted != null) {
                this.insertEmptyLineIfNeeded(lastInserted);
            }
        }
    }

    private void deleteUnnecessaryWhiteSpaces(PhpUseList useList) {
        PsiElement nextSibling = useList.getNextSibling();
        if (nextSibling instanceof PsiWhiteSpace && !nextSibling.getText().equals("\n")) {
            nextSibling.replace(PsiParserFacade.SERVICE.getInstance(this.phpFile.getProject()).createWhiteSpaceFromText("\n"));
        }
    }

    private void insertEmptyLineIfNeeded(PhpUseList insertAfter) {
        if (!ApplicationSettings.getInstance().insertEmptyLineAfterCheckBox) {
            return;
        }

        PsiElement nextSibling = insertAfter.getNextSibling();
        PsiElement nextNextSibling = nextSibling.getNextSibling();

        if (nextSibling instanceof PsiWhiteSpace && nextSibling.textMatches("\n") && nextNextSibling instanceof PhpUseList) {
            PsiElement ws = PsiParserFacade.SERVICE.getInstance(this.phpFile.getProject()).createWhiteSpaceFromText("\n");
            insertAfter.getParent().addAfter(ws, insertAfter);
        }
    }
}