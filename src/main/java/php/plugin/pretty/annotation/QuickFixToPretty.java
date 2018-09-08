package php.plugin.pretty.annotation;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.PhpFile;
import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.action.SortAction;

public class QuickFixToPretty extends BaseIntentionAction {

    QuickFixToPretty(){}

    @NotNull
    @Override
    public String getText() {
        return "Prettify";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Use statement edit";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) file)), "RefactorToPretty", null);
    }
}
