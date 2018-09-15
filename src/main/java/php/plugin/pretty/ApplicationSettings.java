package php.plugin.pretty;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import php.plugin.pretty.dict.UseStatementGroupOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

@State(name = "PhpPrettyUsePlugin", storages = @Storage(file = "$APP_CONFIG$/php-pretty-use.xml"))
public class ApplicationSettings implements PersistentStateComponent<ApplicationSettings> {

    public List<UseStatementGroupOptions> useStatementGroupOptions = new ArrayList<>();

    public boolean insertEmptyLineAfterCheckBox = true;
    public boolean useAdvancedAnnotator = false;


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

    public static Collection<UseStatementGroupOptions> getDefaultUseStatementGroupOption() {
        Collection<UseStatementGroupOptions> options = new ArrayList<>();

        options.add(new UseStatementGroupOptions("^use .?Symfony\\\\.*$", 100, true));
        options.add(new UseStatementGroupOptions("^use .?Shared\\\\.*$", 90, true));

        return options;
    }

    @NotNull
    public static Collection<UseStatementGroupOptions> getStatementGroupOptionsWithDefaultFallback() {
        if (getInstance().provideDefaults && getInstance().useStatementGroupOptions.size() == 0) {
            return getDefaultUseStatementGroupOption();
        }

        return getInstance().useStatementGroupOptions;
    }

    @NotNull
    public static Collection<UseStatementGroupOptions> getEnabledStatementGroupOptionsList() {
        Collection<UseStatementGroupOptions> options = ApplicationSettings.getStatementGroupOptionsWithDefaultFallback();

        return options.stream().filter(UseStatementGroupOptions::isEnabled).collect(Collectors.toList());
    }

    public static PriorityQueue<UseStatementGroupOptions> createStatementGroupOptionsQueue() {
        return new PriorityQueue<>(getEnabledStatementGroupOptionsList());
    }
}
