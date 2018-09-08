package php.plugin.pretty.annotation;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.RegexOption;

public class PrettyUseStatementsAnnotatorTest extends LightCodeInsightFixtureTestCase {

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

    public String getTestDataPath() {
        return "src/test/java/php/plugin/pretty/annotation/fixture";
    }

    public void testHighlightingForNotPrettyStatement() {
        myFixture.configureByFile("NotPrettyClass.php");

        myFixture.checkHighlighting(true, false, false);
    }

    public void testNoHighlightingForPrettyStatement() {
        myFixture.configureByFile("PrettyClassWithoutWhiteSpaces.php");
        myFixture.checkHighlighting(true, true, true, true);
    }

    public void testNoHighlightingForPrettyStatementWithWhitespaces() {
        myFixture.configureByFile("PrettyClassWithWhitespaces.php");
        myFixture.checkHighlighting(true, true, true, true);
    }

    public void testHighlightingForNotPrettyStatementWithAnotherLibsSetting() {
        myFixture.configureByFile("NotPrettyClassWithAnotherLibs.php");

        ApplicationSettings.getInstance().regexOptions.add(new RegexOption("another_libs", 95, true));

        myFixture.checkHighlighting(true, false, false);
    }

}
