package php.plugin.pretty.annotation;

import com.jetbrains.php.lang.psi.elements.PhpUseList;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.UseStatementGroupOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

class PrettifyDetector {
    private List<DetectStrategy> strategyList;
    private Collection<UseStatementGroupOptions> useStatementGroupOptions;

    private PrettifyDetector(List<DetectStrategy> strategyList, Collection<UseStatementGroupOptions> useStatementGroupOptions) {
        this.strategyList = strategyList;
        this.useStatementGroupOptions = useStatementGroupOptions;
    }

    boolean isPretty(@NotNull List<PhpUseList> useStatementList) {
        for (int i = 1; i < useStatementList.size(); i++) {
            PhpUseList previousStatement = useStatementList.get(i - 1);
            PhpUseList currentStatement = useStatementList.get(i);

            for (DetectStrategy strategy : this.strategyList) {
                if (!strategy.pairIsPrettify(
                        previousStatement,
                        currentStatement,
                        this.findWeight(previousStatement.getText()),
                        this.findWeight(currentStatement.getText())
                )) {
                    return false;
                }
            }
        }

        return true;
    }

    private int findWeight(@NotNull String text) {
        UseStatementGroupOptions regexDefinitionForAnotherLibs = null;

        for (UseStatementGroupOptions regexDefinition : this.useStatementGroupOptions) {
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

    static class DetectorFactory {
        static PrettifyDetector createFromSettings(ApplicationSettings applicationSettings) {
            List<DetectStrategy> strategyList = new ArrayList<>();

            strategyList.add(new SimpleDetectStrategy());

            if (applicationSettings.useAdvancedAnnotator) {
                strategyList.add(new AdvancedDetectStrategy());
            }

            return new PrettifyDetector(strategyList, ApplicationSettings.getEnabledStatementGroupOptionsList());
        }
    }
}

