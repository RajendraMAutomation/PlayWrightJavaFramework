package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class MoreUIValidationsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
// Scenario: here we will take email from childwindow and enter into parent window
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Chrome Browser - incognito, browser - OTP
        context = browser.newContext(); // here we are creating context i.e profile for browser, it is used in case we wants to open multiple browser profile like normal or incognito

        // Start tracing before creating / navigating a page.
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();  // NOTE: generally we use browser.newPage() line after chromium launch, but here as we wanted multiple browser profile so used above one and then used context to open new page.
        page.navigate("https://rahulshettyacademy.com/loginpagePractise/");
    }
    @AfterMethod
    public void tearDown(){
        // Stop tracing and export it into a zip archive.
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace.zip")));
    }

    @Test
    public void childWindowHandle() {
        Locator blinkingTexts = page.locator(".blinkingText"); // locat1, locat2.
        //   blinkingTexts.first().click();  // since here blickingTexts has two lacator and we have to click on first
        Page newPage = context.waitForPage(() -> blinkingTexts.first().click()); // before clicking link we are asking 'context' to wait for another page to open, so above line for direct clicking, we will not use here.. Here context.waitForPage() acts as Listener
        newPage.waitForLoadState(); // waiting for new page i.e link to be loaded properly as we are directly opening link here , not by using page.navigate(). earlier in setup() method we used page.navigate and in navigate the Load is already in-built.
        String childText = newPage.locator(".red").textContent(); // textContent() will grab complete content/text for the given Locator, whether it is separated into multiple block or not. Earlier we used .innerText() as there was text given in single block.

        // Please email us at mentor@rahulshettyacademy.com with below template to receive response
        //String midText = childText.split("at ")[1];   // mentor@rahulshettyacademy.com with below template to receive response
        //String emailID = midText.split(" ")[0];        // mentor@rahulshettyacademy.com

        String emailId = childText.split("at ")[1].split(" ")[0]; // merged above two line in one.
        page.getByLabel("Username:").fill(emailId); // NOTE: we used page. here instead of newPage., as we are now enter the email grab from newpage i.e child window and entered into page i.e parent/main page
        String abc = page.getByLabel("Username:").inputValue();  // here we use .inputValue() instead of innerText() or textContext(), because this text/emailId is entered by Playwright while script execution so it won't present in DOM.
        System.out.println(abc);
        page.waitForTimeout(3000);
    }

    @Test(groups = {"smoke"})
    public void UIControls(){
        Locator userRdBtn = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName("User"));
        userRdBtn.click();
        page.getByRole(AriaRole.BUTTON , new Page.GetByRoleOptions().setName("Okay")).click();
        Assert.assertTrue(userRdBtn.isChecked());

        Locator checkBoxTerms =page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("I Agree to the terms and conditions"));
        checkBoxTerms.check(); // we can use .click() also
        Assert.assertTrue(checkBoxTerms.isChecked());

        page.getByRole(AriaRole.COMBOBOX).selectOption("Teacher"); // here for Dropdown, the Label in missing in UI. NO worry PlayWright provide inbuilt option "COMBOBOX" and for this we do not need to write 'new Page.GetByRoleOptions().setName("")'
        page.waitForTimeout(3000);

    }


}
