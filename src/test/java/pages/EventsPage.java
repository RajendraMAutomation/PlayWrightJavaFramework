package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class EventsPage {
    Page page;

    public EventsPage(Page page) {
        this.page = page;
    }

    public void goTo() {
        page.locator("#nav-events").click();
    }

    public Locator waitForEventsToLoad() {
        Locator eventsCards = page.getByTestId("event-card");  //here event-card is COMMON locator for all events present on events page, so it returns Array of locators. same like driver.findElements in selenium
        assertThat(eventsCards.first()).isVisible();   // Force Playwright to wait until the first card is visible, before applying count otherwise it will return 0 count
        return eventsCards;
    }

    public Locator findEventCard(String titleCard) {
        Locator eventsCards = waitForEventsToLoad();
        Locator targetCard = eventsCards.filter(new Locator.FilterOptions().setHasText(titleCard)); // it returns targetCard . if card is not present then here 'targetCard' will return NULL
        assertThat(targetCard).isVisible();
        return targetCard;
    }

    public int getSeatsCount(Locator targetCard) {
        String seatsText = targetCard.getByText("seats").innerText(); //take total number of seats available before booking event. // NOTE: we use here Locator.getByText() i.e "targetCard", instead of page.getBytext(), page will search in whole page.
        System.out.println(seatsText);
        return Integer.parseInt(seatsText.split(" ")[0]); // it will return 'seatNumberBeforeBooking'

    }

    public BookingFormPage proceedToBookingEvent(Locator targetCard) {
        targetCard.getByTestId("book-now-btn").click();
        return new BookingFormPage(page);
    }

}
