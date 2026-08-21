package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildTest extends TestBase {


    @Test(groups = {"framework"}, description = "Create Event - Book that event and verify if its booked")
    public void DemoTest() {
        String eventTitle = "PlayWright Framework Test";
        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();
        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        // Step 1 - Create Event from admin Page
        adminEventsPage.createEvent(
                eventTitle,
                "PlayWright test event",
                "Concert",
                "Test City",
                "Test Venue",
                "2026-12-18T19:30",
                "100",
                "50"
        );
        //step 2 - Find newly created event in the event page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(eventTitle);
        int seatNumberBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage =eventsPage.proceedToBookingEvent(targetCard);

        // Book the ticket for Event
        bookingFormPage.fillAndConfirm("Test Student","test.student@example.com","9768554442");

    }

    @AfterMethod
    public void tearDown() {

    }

}
