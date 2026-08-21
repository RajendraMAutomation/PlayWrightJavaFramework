package tests;

import com.microsoft.playwright.Locator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.DataProviderUtil;

import java.io.IOException;
import java.util.HashMap;

public class FrameworkBuildDataDrivenTest extends TestBase {

    @DataProvider(name ="eventBookingData")
    public Object[][] eventBookingData() throws IOException {
       return DataProviderUtil.getJsonDataToMap("/src/test/resources/eventBookngData.json");
    }

    @Test(groups = {"framework"}, dataProvider = "eventBookingData", description = "Create Event - Book that event and verify if its booked")
    public void DemoTest(HashMap<String,String> data) {
        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage = loginPage.loginToApplication();
        dashboardPage.waitForEventsToLoad();
        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        // Step 1 - Create Event from admin Page
        adminEventsPage.createEvent(
                data.get("titlePrefix"),
                data.get("description"),
                data.get("category"),
                data.get("city"),
                data.get("venue"),
                data.get("dateTime"),
                data.get("price"),
                data.get("totalSeats")
        );
        //step 2 - Find newly created event in the event page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(data.get("titlePrefix"));
        int seatNumberBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage =eventsPage.proceedToBookingEvent(targetCard);

        // Book the ticket for Event
        bookingFormPage.fillAndConfirm(data.get("fullName"),data.get("email"),data.get("phone"));

    }

    @AfterMethod
    public void tearDown() {

    }

}
