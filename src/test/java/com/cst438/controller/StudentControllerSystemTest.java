package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;


public class StudentControllerSystemTest {

    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver_win64/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

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
    public void enrollInSection() throws Exception {

        driver.findElement(By.id("classEnroll")).click();
        Thread.sleep(SLEEP_DURATION);
        // Find all rows in the table
        List<WebElement> rows = driver.findElements(By.xpath("//table[@class='Center']/tbody/tr"));

        for (WebElement row : rows) {
            // Find the "Add" button in each row and click it
            WebElement addButton = row.findElement(By.id("addClassButton"));
            addButton.click();
            Thread.sleep(SLEEP_DURATION);

            // Find the output element and get the text
            WebElement outputElement = driver.findElement(By.xpath("//h4"));
            String outputText = outputElement.getText();

            // Assert that the output contains the expected text
            assertTrue(outputText.contains("Class added") || outputText.contains("You have attempted to add a course the student is already enrolled in"));
        }
    }
}
