package php.plugin.pretty.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.jetbrains.php.lang.psi.PhpFile;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.RegexOption;


public class SortActionTest extends LightCodeInsightFixtureTestCase {
    public String getTestDataPath() {
        return "src/test/java/php/plugin/pretty/action/fixture";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ApplicationSettings.getInstance().regexOptions.add(new RegexOption("^use .?Symfony\\\\.*$", 100, true));
        ApplicationSettings.getInstance().regexOptions.add(new RegexOption("^use .?Shared\\\\.*$", 90, true));
    }

    @Override
    protected void tearDown() throws Exception {
        ApplicationSettings.getInstance().regexOptions.clear();
        super.tearDown();
    }

    public void testSortWorkAsExpected() {
        myFixture.configureByFile("NotPrettyClass.php");

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("NotPrettyClassAfterApply.php");
    }

    public void testSortDropUnnecessaryWhitespaces() {
        myFixture.configureByFile("NotPrettyClassWithWhiteSpaces.php");

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("NotPrettyClassAfterApply.php");
    }

    public void testSortAddWhitespacesBetweenUseStatementGroupsEvenItsPretty() {
        myFixture.configureByFile("PrettyClassWithoutWhiteSpaces.php");

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("PrettyClassWithoutWhiteSpacesAfterApply.php");

    }

    public void testNoSortForPrettyUse() {
        myFixture.configureByFile("NotPrettyClassAfterApply.php");

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("NotPrettyClassAfterApply.php");
    }

    public void testAlphabeticalSortWorkEvenForPrettyUseList()
    {
        myFixture.configureByFile("PrettyClassWithoutAlphabetSort.php");

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("PrettyClassWithoutAlphabetSortAfterApply.php");

    }

    public void testAnotherLibsFunctional()
    {
        myFixture.configureByFile("NotPrettyClassWithAnotherLibs.php");

        ApplicationSettings.getInstance().regexOptions.add(new RegexOption("another_libs", 95, true));

        CommandProcessor.getInstance().executeCommand(getProject(),
                () -> ApplicationManager.getApplication().runWriteAction(new SortAction((PhpFile) myFixture.getFile())), "TestRefactorToPretty", null);

        myFixture.checkResultByFile("NotPrettyClassWithAnotherLibsAfterApply.php");

    }
}
