package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnrollmentControllerSystemTest {

  //change this directory to where chromedriver.exe is saved on your computer
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

  //instructor enters final grades for all enrolled students
  @Test
  public void enterFinalGradesForAllStudents() throws Exception{
    //Year: 2024
    //Semester: Spring

    //give grade to all enrollments
    driver.findElement(By.tagName("a")).click();
    driver.findElement(By.id("year")).sendKeys("2024");
    driver.findElement(By.id("semester")).sendKeys("Spring");
    driver.findElement(By.id("showSections")).click();
    Thread.sleep(SLEEP_DURATION);
    List<WebElement> weList = driver.findElements(By.xpath("//a[contains(text(), 'View Enrollments')]"));
    weList.get(0).click();
    Thread.sleep(SLEEP_DURATION);
    weList = driver.findElements(By.tagName("input"));
    for (WebElement inputElement : weList) {
      // MAC = Keys.COMMAND : Windows = Keys.CONTROL
      inputElement.sendKeys(Keys.chord(Keys.CONTROL,"a",Keys.DELETE));
      //inputElement.sendKeys(Keys.chord(Keys.COMMAND,"a",Keys.DELETE));
      inputElement.sendKeys("F");
      assertEquals("F", inputElement.getAttribute("value"));
    }
    driver.findElement(By.tagName("button")).click();
    Thread.sleep(SLEEP_DURATION);
    WebElement messageElement = driver.findElement(By.tagName("h4"));

    //check save enrollments grades was successful
    assertEquals("Enrollment Saved", messageElement.getText());
  }
}
