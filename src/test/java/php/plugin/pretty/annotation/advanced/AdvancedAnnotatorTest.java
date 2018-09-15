package php.plugin.pretty.annotation.advanced;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.UseStatementGroupOptions;

public class AdvancedAnnotatorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ApplicationSettings.getInstance().useStatementGroupOptions.add(new UseStatementGroupOptions("^use .?Symfony\\\\.*$", 100, true));
        ApplicationSettings.getInstance().useStatementGroupOptions.add(new UseStatementGroupOptions("^use .?Shared\\\\.*$", 90, true));
        ApplicationSettings.getInstance().useAdvancedAnnotator = true;
    }

    @Override
    protected void tearDown() throws Exception {
        ApplicationSettings.getInstance().useStatementGroupOptions.clear();
        super.tearDown();
    }

    public String getTestDataPath() {
        return "src/test/java/php/plugin/pretty/annotation/advanced/fixture";
    }

    public void testHighlightingForNotPrettyStatement() {
        this.testFile("NotPrettyClass.php");
    }

    public void testNoHighlightingForPrettyStatement() {
        this.testFile("PrettyClassWithoutWhiteSpaces.php");
    }

    public void testNoHighlightingForPrettyStatementWithWhitespaces() {
        this.testFile("PrettyClassWithWhitespaces.php");
    }

    public void testHighlightingForNotPrettyStatementWithAnotherLibsSetting() {
        ApplicationSettings.getInstance().useStatementGroupOptions.add(new UseStatementGroupOptions("another_libs", 95, true));
        this.testFile("NotPrettyClassWithAnotherLibs.php");
    }

    public void testHighlightingIfNoAlphabeticalOrder() {
        this.testFile("PrettyClassWithoutAlphabeticalOrder.php");
    }

    public void testNoHighlightingIfAlphabeticalOrder() {
        this.testFile("PrettyClassWithAlphabeticalOrder.php");
    }

    private void testFile(String filepath) {
        myFixture.configureByFile(filepath);
        myFixture.checkHighlighting(false, false, true);
    }
}