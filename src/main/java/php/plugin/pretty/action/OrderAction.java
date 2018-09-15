package php.plugin.pretty.action;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.UseStatementGroupOptions;
import php.plugin.pretty.dict.WeighedUseStatementGroup;
import php.plugin.pretty.tool.UseStatementVisitor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderAction implements Runnable {

    @NotNull
    private PhpFile phpFile;

    public OrderAction(@NotNull PhpFile psiFile) {
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
        PriorityQueue<UseStatementGroupOptions> orderedUseStatementGroupOptionsQueue = ApplicationSettings.createStatementGroupOptionsQueue();

        UseStatementGroupSimpleContainer groups = Stream.generate(orderedUseStatementGroupOptionsQueue::poll)
                .filter(Objects::nonNull).map(WeighedUseStatementGroup::new).limit(orderedUseStatementGroupOptionsQueue.size())
                .collect(Collectors.toCollection(UseStatementGroupSimpleContainer::new));

        List<PhpUseList> copiedUseStatementList = useStatementList.stream()
                .map(phpUseList -> (PhpUseList) phpUseList.copy()).collect(Collectors.toList());

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

        WeighedUseStatementGroup appendUseStatementGroupForAllStatements() {
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