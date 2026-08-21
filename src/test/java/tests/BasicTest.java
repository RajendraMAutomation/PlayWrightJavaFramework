package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicTest {
    Playwright playwright;
    Browser browser;
//    BrowserContext context;
    Page page;

    @BeforeMethod(alwaysRun = true)
    public void setUp(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));  //Headed Mode
//        context = browser.newContext();

//        // Start tracing before creating / navigating a page.
//        context.tracing().start(new Tracing.StartOptions()
//                .setScreenshots(true)
//                .setSnapshots(true)
//                .setSources(true));
//        page = context.newPage();

        page = browser.newPage();
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000); // By default, every Assertion has TimeOut 5 seconds in playwright, However if we want to increase this purposely , we will use this line of code. otherwise, this line of code not needed at all.
    }


    @Test(description = "Create Event - Book that event and verify if its booked")
    public void DemoTest(){
        System.out.println(page.title());
        assertThat(page).hasTitle("EventHub — Discover & Book Events");
        page.getByPlaceholder("you@email.com").fill("mauryar16@gmail.com");
        page.getByLabel("Password").fill("Rahul@123");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible(); // NOTE : assertThat() is a static method of PlaywrightAssertions interface. and Assert.asserTrue() is method in TestNG

        // Step 1 - Create Event from admin Page
        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        page.locator("#event-title-input").fill("QA Summit Rahul Shetty"); // when we don't find any right locator matching to find out the component on the web, then use page.locator and here we can provide either xpath or CSS in ""
        page.locator("#admin-event-form textarea").fill("Rahul Shetty QA Meetups");  // here we use custom CSS i.e from parent to child. syntax is : #ParentID childTag
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Test City");
        page.getByLabel("Venue").fill("Test Venue");
        page.getByLabel("Event Date & Time").fill("2026-12-18T19:30");
        //page.waitForTimeout(3000);  //applied wait purposely, however not needed here.
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("50");
        page.locator("#add-event-btn").click(); // purposely used CSS here however not needed, we can do same with '.getByRole' also.
        assertThat(page.getByText("Event created!")).isVisible();  // to validate Toast msg we use .getByText //By default Assertion will wait for 5 seconds in playwright, after that it will show timeOut Exception


        //step 2 - Find newly created event in the event page

        page.locator("#nav-events").click();  //used CSS
        Locator eventsCards = page.getByTestId("event-card");  //here event-card is COMMON locator for all events present on events page, so it returns Array of locators. same like driver.findElements in selenium
        eventsCards.first().waitFor();   // Force Playwright to wait until the first card is visible, before applying count otherwise it will return 0 count
        System.out.println("Total events are present are : "+eventsCards.count());  // will give total numbers of events on page

        // Validate Visibility of card that we have added
        Locator targetCard = eventsCards.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty"));  // if card is not present then here targetCard will return NULL
        assertThat(targetCard).isVisible();
       // assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000)); //if we know only this targetCard take 10 seconds to load in entire page, then we purposely define local timeout for this, else not needed.

        //book the Event
        String seatsText = targetCard.getByText("seats").innerText(); //take total number of seats available before booking event. // NOTE: we use here Locator.getByText() i.e "targetCard", instead of page.getBytext(), page will search in whole page.
        System.out.println(seatsText);
        int seatNumberBeforeBooking = Integer.parseInt(seatsText.split(" ")[0]);
        targetCard.getByTestId("book-now-btn").click();

        // Book the ticket for Event

        page.getByPlaceholder("Your full name").fill("Rajendraprasad");
        page.getByLabel("Email").fill("rajendra@gmail.com");
        page.locator("#phone").fill("9768778889");  //used CSS id
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();

        //validate Confirm booking
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();
        String bookingRef = page.locator(".booking-ref").innerText();        //NOTE: here there was 4 classname together,separated by space  i.e class="booking-ref font-mono font-bold text-indigo-600" and we use only one in CSS which is unique in page class
        page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("View My Bookings")).click(); // here we can use AriaRole.LINK also as in DOM it mentioned <a href="/bookings"> also


        //verify in booking History
        Locator bookingCards = page.locator("#booking-card"); //local 1, local2 , local3...
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        assertThat(targetBookingCard).isVisible();
        // check seat count reduction
        page.locator("#nav-events").click();
        page.waitForTimeout(1000); //applied wait purposely due to server slowness, however can be ignored, used here bcz wanted to give time before doing below actions
        Locator eventsCardsAfterBooking = page.getByTestId("event-card");
        Locator targetCardAfterBooking = eventsCardsAfterBooking.filter(new Locator.FilterOptions().setHasText("QA Summit Rahul Shetty"));  // if card is not present then here targetCard will return NULL
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText(); //take total number of seats available before booking event. // NOTE: we use here Locator.getByText() i.e "targetCard", instead of page.getBytext(), page will search in whole page.
        System.out.println(seatsTextAfterBooking);
        //validate if Afterbooking < BeforeBooking
        // 45 seats available
        int seatNumberAfterBooking = Integer.parseInt(seatsTextAfterBooking.split(" ")[0]); // took [0] i.e "45" String and converted into int. NOTE: split will return Array
        Assert.assertTrue(seatNumberBeforeBooking > seatNumberAfterBooking);


    }

    @AfterMethod
    public void tearDown(){
        // Stop tracing and export it into a zip archive.
//        context.tracing().stop(new Tracing.StopOptions()
//                .setPath(Paths.get("trace.zip")));
    }

}
