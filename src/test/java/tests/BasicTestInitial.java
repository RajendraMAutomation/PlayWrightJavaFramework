package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicTestInitial {

//Invoke Browser -> Invoke Tab/page -> type url
    // by default Playwright run on HeadLess Mode
    // to run in headed mode we have to pass 'new BrowserType.LaunchOptions().setHeadless(false)' inside launch() method.
    @Test
    public void DemoTest(){
        //1. create PlayWright object using Interface 'PlayWright' and it's method 'create'
        Playwright playwright = Playwright.create();
        //2. now launch the browser using 'launch' method
//        Browser browser = playwright.chromium().launch();  //Headless Mode by default
          Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));  //Headed Mode
//        Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));  //Headed Mode fpr firefox
//        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));  //Headed Mode for local chrome browser
        //3. now create page using 'newPage' method
        Page page = browser.newPage();
        //4. now open url using 'navigate' method
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        System.out.println(page.title());
        // to perform Assertion we have static assertThat()
        assertThat(page).hasTitle("EventHub — Discover & Book Events");

   //     page.getByLabel("email").fill("mauryar16@gmail.com");  // OR
        page.getByPlaceholder("you@email.com").fill("mauryar16@gmail.com");
        page.getByLabel("Password").fill("Rahul@123");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

    }

}
