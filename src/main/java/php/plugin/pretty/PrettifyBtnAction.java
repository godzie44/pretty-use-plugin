package php.plugin.pretty;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpFile;
import php.plugin.pretty.action.OrderAction;

public class PrettifyBtnAction extends AnAction {
    public PrettifyBtnAction() {
        super("Prettify");
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        final PsiFile psi = event.getData(LangDataKeys.PSI_FILE);

        if(!(psi instanceof PhpFile)) {
            return;
        }

        CommandProcessor.getInstance().executeCommand(project,
                () -> ApplicationManager.getApplication().runWriteAction(new OrderAction((PhpFile)psi)), "RefactorToPretty", null);
    }
}