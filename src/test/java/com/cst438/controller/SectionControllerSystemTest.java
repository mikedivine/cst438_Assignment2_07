package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectionControllerSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver_win64/chromedriver.exe";

    //public static final String CHROME_DRIVER_FILE_LOCATION =
    //        "~/chromedriver_macOS/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.


    // add selenium dependency to pom.xml

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        // set properties required by Chrome Driver
        System.setProperty(
                "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(URL);
        // must have a short wait to allow time for the page to download
        Thread.sleep(SLEEP_DURATION);

    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void systemTestAddSection() throws Exception {
        // add a section for cst499 Spring 2024 term
        // verify section shows on the list of sections for Spring 2024
        // delete the section
        // verify the section is gone


        // click link to navigate to Sections
        WebElement we = driver.findElement(By.id("sections"));
        we.click();
        Thread.sleep(SLEEP_DURATION);

        // enter cst499, 2024, Spring and click search sections
        driver.findElement(By.id("scourseId")).sendKeys("cst499");
        driver.findElement(By.id("syear")).sendKeys("2024");
        driver.findElement(By.id("ssemester")).sendKeys("Spring");
        driver.findElement(By.id("search")).click();
        Thread.sleep(SLEEP_DURATION);

        // verify that cst499 is not in the list of sections
        // if it exists, then delete it
        // Selenium throws NoSuchElementException when the element is not found
        try {
            while (true) {
                WebElement row499 = driver.findElement(By.xpath("//tr[td='cst499']"));
                List<WebElement> buttons = row499.findElements(By.tagName("button"));
                // delete is the second button
                assertEquals(2, buttons.size());
                buttons.get(1).click();
                Thread.sleep(SLEEP_DURATION);
                // find the YES to confirm button
                List<WebElement> confirmButtons = driver
                  .findElement(By.className("react-confirm-alert-button-group"))
                  .findElements(By.tagName("button"));
                assertEquals(2, confirmButtons.size());
                confirmButtons.get(0).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
           // do nothing, continue with test
        }

        // find and click button to add a section
        driver.findElement(By.id("addSection")).click();
        Thread.sleep(SLEEP_DURATION);

        // enter data
        //  courseId: cst499,
        driver.findElement(By.id("ecourseId")).sendKeys("cst499");
        //  secId: 1,
        driver.findElement(By.id("esecId")).sendKeys("1");
        //  year:2024,
        driver.findElement(By.id("eyear")).sendKeys("2024");
        //  semester:Spring,
        driver.findElement(By.id("esemester")).sendKeys("Spring");
        //  building:052,
        driver.findElement(By.id("ebuilding")).sendKeys("052");
        //  room:104,
        driver.findElement(By.id("eroom")).sendKeys("104");
        //  times:W F 1:00-2:50 pm,
        driver.findElement(By.id("etimes")).sendKeys("W F 1:00-2:50 pm");
        //  instructorEmail jgross@csumb.edu
        driver.findElement(By.id("einstructorEmail")).sendKeys("jgross@csumb.edu");
        // click Save
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("addMessage")).getText();
        assertTrue(message.startsWith("section added"));

        // close the dialog
        driver.findElement(By.id("close")).click();

        // verify that new Section shows up on Sections list
        // find the row for cst499
        WebElement row499 = driver.findElement(By.xpath("//tr[td='cst499']"));
        List<WebElement> buttons = row499.findElements(By.tagName("button"));
        // delete is the second button
        assertEquals(2, buttons.size());
        buttons.get(1).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        // verify that Section list is now empty
        assertThrows(NoSuchElementException.class, () ->
                driver.findElement(By.xpath("//tr[td='cst499']")));

    }

    @Test
    public void systemTestAddSectionBadCourse() throws Exception {
        // attempt to add a section to course cst599 2024, Spring
        // fails because course does not exist
        // change courseId to cst499 and try again
        // verify success
        // delete the section

        // click link to navigate to Sections
        WebElement we = driver.findElement(By.id("sections"));
        we.click();
        Thread.sleep(SLEEP_DURATION);

        // enter cst, 2024, Spring and click search sections
        driver.findElement(By.id("scourseId")).sendKeys("cst");
        driver.findElement(By.id("syear")).sendKeys("2024");
        driver.findElement(By.id("ssemester")).sendKeys("Spring");
        driver.findElement(By.id("search")).click();
        Thread.sleep(SLEEP_DURATION);

        // verify that cst499 is not in the list of sections
        // Selenium throws NoSuchElementException when the element is not found
        try {
           while (true) {
               WebElement row499 = driver.findElement(By.xpath("//tr[td='cst499']"));
               List<WebElement> buttons = row499.findElements(By.tagName("button"));
               // delete is the second button
               assertEquals(2, buttons.size());
               buttons.get(1).click();
               Thread.sleep(SLEEP_DURATION);
               // find the YES to confirm button
               List<WebElement> confirmButtons = driver
                       .findElement(By.className("react-confirm-alert-button-group"))
                       .findElements(By.tagName("button"));
               assertEquals(2, confirmButtons.size());
               confirmButtons.get(0).click();
               Thread.sleep(SLEEP_DURATION);
           }
        } catch (NoSuchElementException e) {
           // do nothing, continue with test
        }

        // find and click button to add a section
        driver.findElement(By.id("addSection")).click();
        Thread.sleep(SLEEP_DURATION);

        // enter data
        //  courseId: cst599
        driver.findElement(By.id("ecourseId")).sendKeys("cst599");
        //  secId: 1,
        driver.findElement(By.id("esecId")).sendKeys("1");
        //  year:2024,
        driver.findElement(By.id("eyear")).sendKeys("2024");
        //  semester:Spring,
        driver.findElement(By.id("esemester")).sendKeys("Spring");
        //  building:052,
        driver.findElement(By.id("ebuilding")).sendKeys("052");
        //  room:104,
        driver.findElement(By.id("eroom")).sendKeys("104");
        //  times:W F 1:00-2:50 pm,
        driver.findElement(By.id("etimes")).sendKeys("W F 1:00-2:50 pm");
        //  instructorEmail jgross@csumb.edu
        driver.findElement(By.id("einstructorEmail")).sendKeys("jgross@csumb.edu");
        // click Save
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement msg = driver.findElement(By.id("addMessage"));
        String message = msg.getText();
        assertEquals("course not found cst599", message);

        // clear the courseId field and enter cst499
        WebElement courseId = driver.findElement(By.id("ecourseId"));
        courseId.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        Thread.sleep(SLEEP_DURATION);
        courseId.sendKeys("cst499");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        message = driver.findElement(By.id("addMessage")).getText();
        assertTrue(message.startsWith("section added"));

        // close the dialog
        driver.findElement(By.id("close")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement row = driver.findElement(By.xpath("//tr[td='cst499']"));
        assertNotNull(row);
        // find the delete button on the row from prior statement.
        List<WebElement> deleteButtons = row.findElements(By.tagName("button"));
        // delete is the second button
        assertEquals(2, deleteButtons.size());
        deleteButtons.get(1).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
               .findElement(By.className("react-confirm-alert-button-group"))
               .findElements(By.tagName("button"));
        assertEquals(2,confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        // verify that Section list is empty
        assertThrows(NoSuchElementException.class, () ->
               driver.findElement(By.xpath("//tr[td='cst499']")));
    }
}
