package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {

  //change this directory to where chromedriver.exe is saved on your computer
  public static final String CHROME_DRIVER_FILE_LOCATION =
    "/Users/russsell/Documents/School/1 - CSUMB/CST 438 Software Engineering/chromedriver-mac-x64/chromedriver";

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

  // instructor adds a new assignment successfully
  @Test
  public void systemTestAddAssignment() throws Exception {
    // add an assignment for Section Number 8
    // verify assignment shows on the list of assignments for Section Number 8
    // delete the assignment
    // verify the assignment is gone

    // enter 2024, Spring and click search sections
    driver.findElement(By.id("year")).sendKeys("2024");
    driver.findElement(By.id("semester")).sendKeys("Spring");
    driver.findElement(By.id("showSections")).click();
    Thread.sleep(SLEEP_DURATION);

    // find and click button to view section assignments
    List<WebElement> links = driver.findElements(By.tagName("a"));
    links.get(1).click();
    Thread.sleep(SLEEP_DURATION);

    // find and click on the Add Assignment button
    driver.findElement(By.id("addAssignment")).click();
    Thread.sleep(SLEEP_DURATION);

    // name assignment and set date and save
    driver.findElement(By.id("assignmentTitle")).sendKeys("Test Assignment");
    driver.findElement(By.id("assignmentDate")).sendKeys("03152024");
    driver.findElement(By.id("saveButton")).click();
    Thread.sleep(SLEEP_DURATION);

    String message = driver.findElement(By.id("message")).getText();
    assertTrue(message.startsWith("Assignment added"));

    // verify that new Assignment shows up on Assignments list
    // find the row for Test Assignment
    WebElement testAssignmentRow = driver.findElement(By.xpath("//tr[td='Test Assignment']"));
    List<WebElement> buttons = testAssignmentRow.findElements(By.tagName("button"));

    Thread.sleep(SLEEP_DURATION);
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

    // verify that the Test Assignment was deleted from the list
    assertThrows(NoSuchElementException.class, () ->
      driver.findElement(By.xpath("//tr[td='Test Assignment']")));
  }

  //instructor grades an assignment and enters scores for all enrolled students and uploads scores
  @Test
  public void enterScores() throws Exception {
    //Spring2024 cst363 sectionNo=8

    //grade an assignment
    driver.findElement(By.id("year")).sendKeys("2024");
    driver.findElement(By.id("semester")).sendKeys("Spring");
    driver.findElement(By.id("showSections")).click();
    Thread.sleep(SLEEP_DURATION);
    Thread.sleep(SLEEP_DURATION);
    driver.findElement(By.xpath("//tr[1]/td[8]/a")).click();
    Thread.sleep(SLEEP_DURATION);
    List<WebElement> weList = driver.findElements(By.xpath("//a[contains(text(), 'Grade')]"));
    weList.get(0).click();
    Thread.sleep(SLEEP_DURATION);
    WebElement input = driver.findElement(By.tagName("input"));
    //change Keys.COMMAND to Keys.CONTROL if on Windows
    input.sendKeys(Keys.chord(Keys.COMMAND,"a",Keys.DELETE));
    input.sendKeys("50");
    driver.findElement(By.tagName("button")).click();
    WebElement messageElement = driver.findElement(By.tagName("h4"));

    //check save grades was successful
    WebElement gradeBox = driver.findElement(By.tagName("input"));
    assertEquals("50", gradeBox.getAttribute("value"));
    assertEquals("Grades saved", messageElement.getText());

    Thread.sleep(SLEEP_DURATION);
  }
}
