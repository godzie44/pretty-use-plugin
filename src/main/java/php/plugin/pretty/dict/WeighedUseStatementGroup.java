package php.plugin.pretty.dict;

import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeighedUseStatementGroup implements Comparable<WeighedUseStatementGroup> {
    private List<PhpUseList> useStatements;

    private final int weight;

    private final Pattern pattern;

    private boolean forAnotherLibs = false;

    public static WeighedUseStatementGroup createForAllStatements(int weight) {
        return new WeighedUseStatementGroup(new UseStatementGroupOptions(
                ".*?",
                weight,
                true
        ));
    }

    public WeighedUseStatementGroup(UseStatementGroupOptions useStatementGroupOptions) {
        this(useStatementGroupOptions.getPattern(), useStatementGroupOptions.getWeight());

        if (useStatementGroupOptions.forAnotherLibs()) {
            forAnotherLibs = true;
        }
    }

    private WeighedUseStatementGroup(Pattern pattern, int weight) {
        this.pattern = pattern;
        this.weight = weight;
        useStatements = new ArrayList<>();
    }

    public boolean forAnotherLibs() {
        return forAnotherLibs;
    }

    public PriorityQueue<PhpUseList> createSortedUseStatementsQueue() {
        if (this.useStatements.size() == 0) {
            return new PriorityQueue<>();
        }

        PriorityQueue<PhpUseList> queue = new PriorityQueue<>(this.useStatements.size(), new Comparator<PhpUseList>() {
            @Override
            public int compare(PhpUseList o1, PhpUseList o2) {
                return o1.getText().toUpperCase().compareTo(o2.getText().toUpperCase());
            }
        });

        queue.addAll(this.useStatements);

        return queue;
    }

    private boolean canInsert(PhpUseList phpUseList) {
        Matcher matcher = this.pattern.matcher(phpUseList.getText());
        return matcher.find();
    }

    private void insert(PhpUseList phpUseList) {
        this.useStatements.add(phpUseList);
    }

    public void insertFromCollection(Collection<PhpUseList> collection) {
        for (PhpUseList useStatement : collection) {
            this.insert(useStatement);
        }
    }

    public boolean insertIfValid(PhpUseList phpUseList) {
        boolean insertAccepted = this.canInsert(phpUseList);

        if (insertAccepted) {
            this.insert(phpUseList);
        }

        return insertAccepted;
    }

    public void insertFromCollectionOnlyValid(Collection<PhpUseList> collection)
    {
        for (PhpUseList useStatement : collection) {
            this.insertIfValid(useStatement);
        }
    }

    @Override
    public int compareTo(@NotNull WeighedUseStatementGroup o) {
        return Integer.compare(o.weight, this.weight);
    }
}
