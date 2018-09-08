package php.plugin.pretty;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import php.plugin.pretty.dict.RegexOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@State(name = "PhpPrettyUsePlugin", storages = @Storage(file = "$APP_CONFIG$/php-pretty-use.xml"))
public class ApplicationSettings implements PersistentStateComponent<ApplicationSettings> {

    public List<RegexOption> regexOptions = new ArrayList<>();

    public boolean insertEmptyLineAfterCheckBox = true;

    @Nullable
    @Override
    public ApplicationSettings getState() {
        return this;
    }

    /**
     * First user change, so that can provide defaults
     */
    public boolean provideDefaults = true;

    @Override
    public void loadState(@NotNull ApplicationSettings insightApplicationSettings) {
        XmlSerializerUtil.copyBean(insightApplicationSettings, this);
    }

    public static ApplicationSettings getInstance() {
        return ServiceManager.getService(ApplicationSettings.class);
    }

    public static Collection<RegexOption> getDefaultRegexOption() {
        Collection<RegexOption> options = new ArrayList<>();

        options.add(new RegexOption("^use .?Symfony\\\\.*$", 100, true));
        options.add(new RegexOption("^use .?Shared\\\\.*$", 90, true));

        return options;
    }

    @NotNull
    public static Collection<RegexOption> getUseRegexOptionsWithDefaultFallback() {
        if (getInstance().provideDefaults && getInstance().regexOptions.size() == 0) {
            return getDefaultRegexOption();
        }

        return getInstance().regexOptions;
    }

    @NotNull
    public static Collection<RegexOption> getEnabledRegexOptions() {
        Collection<RegexOption> options = ApplicationSettings.getUseRegexOptionsWithDefaultFallback();

        return options.stream().filter(RegexOption::isEnabled).collect(Collectors.toList());
    }

    public static PriorityQueue<RegexOption> createRegexOptionQueue() {
        return new PriorityQueue<>(getEnabledRegexOptions());
    }
}
