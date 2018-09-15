package php.plugin.pretty.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import php.plugin.pretty.ApplicationSettings;

import javax.swing.*;

public class MainForm implements Configurable {
    private JCheckBox insertEmptyLineAfterCheckBox;
    private JPanel panel;
    private JCheckBox useAdvanceHighlighterCheckBox;

    @Nls
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        return insertEmptyLineAfterCheckBox.isSelected() != ApplicationSettings.getInstance().insertEmptyLineAfterCheckBox
                ||
                useAdvanceHighlighterCheckBox.isSelected() != ApplicationSettings.getInstance().useAdvancedAnnotator;
    }

    @Override
    public void apply() throws ConfigurationException {
        ApplicationSettings.getInstance().insertEmptyLineAfterCheckBox = insertEmptyLineAfterCheckBox.isSelected();
        ApplicationSettings.getInstance().useAdvancedAnnotator = useAdvanceHighlighterCheckBox.isSelected();
    }

    @Override
    public void reset() {
        insertEmptyLineAfterCheckBox.setSelected(ApplicationSettings.getInstance().insertEmptyLineAfterCheckBox);
        useAdvanceHighlighterCheckBox.setSelected(ApplicationSettings.getInstance().useAdvancedAnnotator);
    }
}
