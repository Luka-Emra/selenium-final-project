import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.io.File;
import java.util.*;
import java.util.List;

// Luka Emrashvili

public class Selenium_Project {
    WebDriver driver;
    @BeforeTest
    @Parameters("browser")
    public void setUp(String browser) throws Exception{
        if (browser.equalsIgnoreCase("Chrome")){
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        }
        else if (browser.equalsIgnoreCase("Edge")){
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        }
        else{
            throw new Exception("Invalid Browser");
        }
    }

    @Test
    public void test_website() {

        driver.navigate().to("http://automationpractice.com/index.php");
        driver.manage().window().maximize();
        Actions actions = new Actions(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Move to 'Women' and select 'T-shirts'

        actions.moveToElement(driver.findElement(By.xpath("//a[@title='Women' and @class='sf-with-ul']"))).perform();
        driver.findElement(By.xpath("//ul[starts-with(@class, 'sf-menu')]/li[1]//a[text()='T-shirts']")).click();
        WebElement image = driver.findElement(By.xpath("//a[contains(@itemprop, 'url')]//following-sibling::img"));
        js.executeScript("window.scrollBy(0,600)");
        actions.moveToElement(image).perform();

        // in 1/10 cases "Quick View" is disappearing before clicking and TimeoutException occurs, and if it happens
        // I am hovering on the image again, just to make sure Test won't be interrupted accidentally.

        try {
            WebElement quick_view = new WebDriverWait(driver, 5).until
                    (ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.quick-view")));
            quick_view.click();
        }catch(TimeoutException e){
            actions.moveToElement(image).perform();
            WebElement quick_view = new WebDriverWait(driver, 5).until
                    (ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.quick-view")));
            quick_view.click();
        }

        WebElement frame = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.cssSelector("iframe[name*='fancybox-frame']")));
        driver.switchTo().frame(frame);

        // check if big image changes appropriately while hovering on small images

        for (int i = 1; i <= 4; i++){
            WebElement small_img = driver.findElement(By.cssSelector("ul[id^='thumbs'] li:nth-child("+i+") img"));
            String src = small_img.getAttribute("src");
            actions.moveToElement(small_img).perform();
            String big_img_src = driver.findElement(By.cssSelector("span#view_full_size img#bigpic")).getAttribute("src");
            if (src.contains(""+i+"") && big_img_src.contains(""+i+"")){        //  if numbers in both image sources match each other:
                System.out.println("The hovered_image_"+i+" matches to the main_image.");
            } else {
                System.out.println("There is a mismatch in hovered_image_"+i+" and the main_image.");
            }
        }

        WebElement quantity = driver.findElement(By.id("quantity_wanted"));
        quantity.clear();
        quantity.sendKeys("2");

        WebElement size_dropdown = driver.findElement(By.name("group_1"));
        Select size = new Select(size_dropdown);
        size.selectByVisibleText("M");

        driver.findElement(By.className("exclusive")).click();  // Add to Cart

        WebElement Continue = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//i[contains(@class, 'chevron-left')]//parent::span")));

        // Total Price of first two items, including shipping. This is used to check the correctness of final order amount at the end of the test
        String Price_1 = driver.findElement(By.xpath("//span[@class='ajax_block_cart_total']")).getText();

        Continue.click();
        driver.switchTo().parentFrame();

        // move to 'Dresses' and select 'Casual Dresses'
        actions.moveToElement(driver.findElement(By.xpath("//ul[starts-with(@class, 'sf-menu')]" +
                "/li[2]//a[@title='Dresses']"))).perform();
        driver.findElement(By.xpath("//ul[starts-with(@class, 'submenu')]/li[1]//a[contains(text(), 'Casual')]")).click();
        WebElement img = new WebDriverWait(driver, 10).until(ExpectedConditions.

                visibilityOfElementLocated(By.xpath("//div[contains(@class, 'left')]//following-sibling::img")));
        js.executeScript("arguments[0].scrollIntoView();", img);

        // This is used to check the correctness of final order amount at the end of the test
        String Price_2 = driver.findElement(By.xpath("//div[@class='right-block']//span[@itemprop='price']")).getText();

        actions.moveToElement(img).perform();

        // in 1/10 cases "Add to Cart" is disappearing before clicking and TimeoutException occurs, and if it happens
        // I am hovering on the image again, just to make sure Test won't be interrupted accidentally.

        try {
            WebElement add = new WebDriverWait(driver, 5).until(ExpectedConditions.
                    visibilityOfElementLocated(By.xpath("//span[text()='Add to cart']")));
            add.click();
        }catch(TimeoutException e){
            actions.moveToElement(img).perform();
            WebElement add = new WebDriverWait(driver, 5).until(ExpectedConditions.
                    visibilityOfElementLocated(By.xpath("//span[text()='Add to cart']")));
            add.click();
        }

        // Continue Shopping

        WebElement Continue_2 = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//i[contains(@class, 'chevron-left')]//parent::span")));
        Continue_2.click();

        // Move To the Cart and Checkout

        WebElement Cart = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.tagName("b")));
        actions.moveToElement(Cart).perform();
        WebElement Check_out = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Check')]")));
        Check_out.click();

        // Check the description in the table dynamically and make sure all the specs of the items are correct.

        WebElement table = driver.findElement(By.id("cart_summary"));

        List<WebElement> Rows = table.findElements(By.xpath("//tbody/tr/td[@class='cart_product']"));
        List<WebElement> Columns = table.findElements(By.tagName("th"));

        // Creating Lists to store all the specs of items

        List<String> Items = new ArrayList<>();
        List<String> SKUs = new ArrayList<>();
        List<String> Colors_and_Sizes = new ArrayList<>();

        for (int i = 0; i < Rows.size(); i++){
            for(int j = 0; j < Columns.size(); j++){
                if (Columns.get(j).getText().contains("Description")) {
                    Items.add(driver.findElement(By.xpath("//tbody/tr["+i+"+1]/td["+j+"+1]/p[@class='product-name']/a")).getText());   // adding item names
                    SKUs.add(driver.findElement(By.xpath("//tbody/tr["+i+"+1]/td["+j+"+1]//small[@class='cart_ref']")).getText());    // adding SKUs
                    Colors_and_Sizes.add(driver.findElement(By.xpath("//tbody/tr["+i+"+1]/td["+j+"+1]//a[contains(text(), 'Color')]")).getText());  // adding Colors and Sizes
                }
            }
        }

        // Check the Quantity of types of items

        if (Items.size()==2){
            System.out.println("\nQuantity of types of the items is correct!");
        }

        // Check the correctness of all the specs of items

        for (int i = 0; i < Items.size(); i++){
            if (Items.get(i).equals("Faded Short Sleeve T-shirts")){

                if (SKUs.get(i).contains("demo_1") && Colors_and_Sizes.get(i).contains("Orange")
                        && Colors_and_Sizes.get(i).contains("M")) {
                    System.out.printf("Name, SKU, color and size of the item_%d are correct!\n", i+1);
                }else{
                    System.out.printf("SKU, color or size of the item_%d is incorrect!\n", i+1);
                }
            }
            else if (Items.get(i).equals("Printed Dress")){
                if (SKUs.get(i).contains("demo_3") && Colors_and_Sizes.get(i).contains("Orange")
                        && Colors_and_Sizes.get(i).contains("S")) {
                    System.out.printf("Name, SKU, color and size of the item_%d are correct!\n", i+1);
                }else{
                    System.out.printf("SKU, color or size of the item_%d is incorrect!\n", i+1);
                }
            }else{
                System.out.println("The name of one of the items is incorrect!");
            }
        }

        // Sign up

        driver.findElement(By.xpath("//p[contains(@class, 'clearfix')]//span")).click();
        driver.findElement(By.id("email_create")).sendKeys("luka" + new Date().getTime() + "@gmail.com");
        driver.findElement(By.xpath("//i[@class='icon-user left']//parent::span")).click();

        WebElement gender = new WebDriverWait(driver, 10).
                until(ExpectedConditions.visibilityOfElementLocated(By.id("id_gender1")));

        gender.click();
        driver.findElement(By.name("customer_firstname")).sendKeys("Luka");
        driver.findElement(By.cssSelector("input#customer_lastname")).sendKeys("Emrashvili");
        js.executeScript("document.getElementById('passwd').value='lemra4444';");
        new Select(driver.findElement(By.name("days"))).selectByValue("12");
        new Select(driver.findElement(By.name("months"))).selectByValue("7");
        new Select(driver.findElement(By.name("years"))).selectByValue("2000");
        driver.findElement(By.id("newsletter")).click();
        driver.findElement(By.id("optin")).click();
        driver.findElement(By.id("address1")).sendKeys("Street #3");
        js.executeScript("document.getElementById('city').value='Atlanta';");
        new Select(driver.findElement(By.name("id_state"))).selectByVisibleText("Georgia");
        driver.findElement(By.id("postcode")).sendKeys("30305");
        driver.findElement(By.id("phone_mobile")).sendKeys("555123456");
        driver.findElement(By.id("submitAccount")).click();
        driver.findElement(By.name("processAddress")).click();
        driver.findElement(By.name("processCarrier")).click();

        // handling the error window

        if (driver.findElement(By.className("fancybox-error")).isDisplayed()){
            actions.click().perform();
            driver.findElement(By.id("cgv")).click();
            driver.findElement(By.name("processCarrier")).click();
        }

        driver.findElement(By.cssSelector("a.cheque")).click();

        // checking the final amount of order using previously grabbed prices
        // Getting rid of Dollar signs and converting strings into double values

        double Price_1_new = Double.parseDouble(Price_1.replaceAll("[$]", ""));
        double Price_2_new = Double.parseDouble(Price_2.replaceAll("[$]", ""));
        double Final_Price = Double.parseDouble(js.executeScript("return document." +
                "getElementById('amount').innerText.replace(/\\$/g, '');").toString());

        if (Price_1_new + Price_2_new == Final_Price) {
            System.out.println("The total amount of the order is correct!");
        }

        WebElement confirm = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//span[text()='I confirm my order']")));
        js.executeScript("arguments[0].click()", confirm);

        // Moving to the 'customer service department' link

        WebElement link = new WebDriverWait(driver, 10).until(ExpectedConditions.
                visibilityOfElementLocated(By.xpath("//a[contains(text(),'customer')]")));
        js.executeScript("arguments[0].click()", link);

        new Select(driver.findElement(By.name("id_contact"))).selectByValue("2");
        new Select(driver.findElement(By.name("id_order"))).selectByIndex(1);
        new Select(driver.findElement(By.name("id_product"))).selectByValue("3");

        // uploading file

        File file = new File(System.getProperty("user.dir") + "/src/tbc.png");
        String path = file.getAbsolutePath();
        driver.findElement(By.id("fileUpload")).sendKeys(path);

        // adding message

        driver.findElement(By.id("message")).sendKeys("Dear Sir/Madam, I want to change the color of the item," +
                " I have ordered a few hours ago, if it is possible of course. Thanks in advance.");
        driver.findElement(By.id("submitMessage")).click();
        driver.quit();
    }
}
