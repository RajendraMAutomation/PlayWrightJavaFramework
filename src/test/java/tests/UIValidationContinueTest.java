package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class UIValidationContinueTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://rahulshettyacademy.com/AutomationPractice/");
    }

    @Test(groups = {"smoke"})
    public void popUpValidations(){
        //visibility/display handle
        assertThat(page.getByPlaceholder("Hide/Show Example")).isVisible();
        page.locator("#hide-textbox").click(); // used CSS id
        assertThat(page.getByPlaceholder("Hide/Show Example")).isHidden();  // NOTE: PlayWright has direct isHidden() method unLike Selenium. IN selenium we have to use: " !webElement.isDisplay() "

        // Alert Handeling
        page.onDialog(dialog -> dialog.accept()); // here "OnDialog" is Listener in PLayWright, this must be use before any Alert/popup
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Alert")).click();

        // mouse Hover
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mouse Hover")).hover(); // we have hover() method in Playwright
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Top")).click();
        page.waitForTimeout(3000);

        //Handling Frames

        FrameLocator frameLocator = page.frameLocator("#courses-iframe");
        frameLocator.getByRole(AriaRole.LINK, new FrameLocator.GetByRoleOptions().setName("Learning paths")).click();
        String textCheck = frameLocator.locator(".inner-box h1").innerText();
        System.out.println(textCheck);
    }

    @Test
    public void screenShotTest(){
        //take SS as page level
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pagescreenShotBefore.png")));

        //take SS as Locator level
        Locator displayEditBox = page.getByPlaceholder("Hide/Show Example");
        displayEditBox.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get("editBox.png"))); //only in parameter we have to write "new Locator." instead of "new Page." rest are same syntax

        page.locator("#hide-textbox").click();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("pagescreenShotAfter.png")));


    }

}
