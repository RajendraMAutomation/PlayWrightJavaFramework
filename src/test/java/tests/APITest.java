package tests;

import com.jayway.jsonpath.JsonPath;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

public class APITest {
    @Test
    public void e2eApiTest() {
        //Login
        HashMap<Object, Object> loginPayload = new HashMap<>();
        loginPayload.put("email", "mauryar16@gmail.com");
        loginPayload.put("password", "Rahul@123");

        Playwright playwright = Playwright.create();
        APIRequestContext apiRequest = playwright.request().newContext();
        APIResponse loginResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/auth/login",
                RequestOptions.create().setData(loginPayload));
        Assert.assertTrue(loginResponse.ok()); // return True, if response code is between 200-299
        System.out.println(loginResponse.text());
        String token = JsonPath.read(loginResponse.text(), "$.token");
        System.out.println("Login Success : " + token);

        //Create Event
        String eventTitle = "PlayWright API Testing";
        HashMap<Object, Object> creatEventPayload = new HashMap<>();
        creatEventPayload.put("title", eventTitle);
        creatEventPayload.put("description", "api");
        creatEventPayload.put("category", "Conference");
        creatEventPayload.put("venue", "Main Road");
        creatEventPayload.put("city", "Bangalore");
        creatEventPayload.put("eventDate", "2026-09-09T07:19:00.000Z");
        creatEventPayload.put("price", "100");
        creatEventPayload.put("totalSeats", "500");

        APIResponse eventResponse = apiRequest.post("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setHeader("Authorization", "Bearer " + token).setData(creatEventPayload));
        Assert.assertTrue(eventResponse.ok(), "Create Event API should succeed"); // we can give msg also for assert FAIL.
        int eventId = JsonPath.read(eventResponse.text(), "$.data.id"); // Extracting id from api response
        System.out.println("Event Created and its ID is : "+eventId);


        //Get Event
        APIResponse retrieveEvents = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create().setQueryParam("page","1")
                        .setQueryParam("limit","12")
                        .setHeader("Authorization","Bearer "+token));
        System.out.println(retrieveEvents.text());
            Assert.assertTrue(retrieveEvents.ok(),"Event Retrieval API should successful");
            List<Integer> allEventIds = JsonPath.read(retrieveEvents.text(),"$.data[*].id"); //we have to retrieve all id so used data[*]
            Assert.assertTrue(allEventIds.contains(eventId), "Created event should appear in Event Lists");

        //DELETE Event
        APIResponse deleteResponse = apiRequest.delete("https://api.eventhub.rahulshettyacademy.com/api/events/"+eventId,
                RequestOptions.create().setHeader("Authorization","Bearer "+token));
        Assert.assertTrue(deleteResponse.ok());

        Assert.assertTrue(deleteResponse.text().contains("Event deleted successfully"),"Even should be deleted");

        // Verify Deletion is successful ..-> getEvent and confirm that event is not exist anymore
        APIResponse retrieveEventsAfterDeletion = apiRequest.get("https://api.eventhub.rahulshettyacademy.com/api/events",
                RequestOptions.create()
                        .setQueryParam("page","1")
                        .setQueryParam("limit","12")
                        .setHeader("Authorization","Bearer "+token));
        System.out.println(retrieveEventsAfterDeletion.text());
        Assert.assertTrue(retrieveEventsAfterDeletion.ok(),"Event Retrieval API should successful after Detetion");
        List<Integer> allEventIdsAfterDeletion = JsonPath.read(retrieveEventsAfterDeletion.text(),"$.data[*].id"); //we have to retrieve all id so used data[*]
        Assert.assertFalse(allEventIdsAfterDeletion.contains(eventId), "Deleted event should not appear in Event Lists"); //NOTE: used assertFalse here
        System.out.println("Deletion verified : event no longer in the list");

    }



}
