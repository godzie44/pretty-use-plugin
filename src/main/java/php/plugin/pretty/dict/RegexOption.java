package php.plugin.pretty.dict;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RegexOption implements Comparable<RegexOption> {
    private static final String ANOTHER_LIBS_PLACEHOLDER = "another_libs";

    @NotNull
    private String regex = "";

    private int weight;
    private boolean enabled;

    private Pattern pattern;

    public RegexOption(){
    }

    public RegexOption(@NotNull String regex, int weight, boolean enabled)
    {
        this.setRegex(regex);
        this.setWeight(weight);
        this.setEnabled(enabled);
    }

    @NotNull
    public String getRegex() {
        return regex;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public void setRegex(@NotNull String regex)
    {
        this.regex = regex;

        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public int compareTo(@NotNull RegexOption o) {
        if (this.getWeight() < o.getWeight()) {
            return 1;
        }
        if (this.getWeight() > o.getWeight()) {
            return -1;
        }

        return 0;
    }

    public boolean forAnotherLibs() {
        return this.getRegex().equals(ANOTHER_LIBS_PLACEHOLDER);
    }
}
