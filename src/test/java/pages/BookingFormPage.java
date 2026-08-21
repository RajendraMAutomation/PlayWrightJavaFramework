package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingFormPage {
    Page page;

    public BookingFormPage(Page page){
        this.page =page;
    }
    private static final String FULL_NAME_PLACEHOLDER = "Your full name";
    private static final String EMAIL_LABEL = "Email";
    private static final String PHONE_LOCATOR = "#phone";
    private static final String CONFIRM_BTN = "Confirm Booking";

    public void fillAndConfirm(String fullName, String email, String phone) {
        page.getByPlaceholder(FULL_NAME_PLACEHOLDER).fill(fullName);
        page.getByLabel(EMAIL_LABEL).fill(email);
        page.locator(PHONE_LOCATOR).fill(phone);  //used CSS id
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONFIRM_BTN)).click();

        //validate Confirm booking
        assertThat(page.getByText("Your tickets are reserved.")).isVisible();

    }


}
