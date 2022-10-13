package jp.co.gutingjun.rpa.model.action.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import jp.co.gutingjun.rpa.common.RPAConst;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriver;

import java.util.concurrent.TimeUnit;

public abstract class WebDriverActionModel extends WebActionModel {

  /** selenium网络连接 */
  private RemoteWebDriver webDriver;
  /** 采用selenium网络连接时，是否将窗口移出屏幕可视范围 */
  private boolean moveOutOfScreen;

  public RemoteWebDriver getWebDriver() {
    if (webDriver == null) {
      if (getContext().containsKey(RPAConst.WEBDRIVER)
          && getContext().get(RPAConst.WEBDRIVER) != null) {
        webDriver = (RemoteWebDriver) getContext().get(RPAConst.WEBDRIVER);
      } else {
        RemoteWebDriver driver = null;
        DriverService browseService = null;

        try {
          WebDriverManager.edgedriver().setup();
          browseService = EdgeDriverService.createDefaultService();
          driver = new EdgeDriver((EdgeDriverService) browseService, new EdgeOptions());
        } catch (Exception ex) {
        }

        if (driver == null) {
          try {
            WebDriverManager.chromedriver().setup();
            browseService = ChromeDriverService.createDefaultService();
            driver = new ChromeDriver((ChromeDriverService) browseService, new ChromeOptions());
          } catch (Exception ex) {
          }
        }

        if (driver == null) {
          try {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
          } catch (Exception ex) {
          }
        }

        if (driver == null) {
          try {
            WebDriverManager.safaridriver().setup();
            driver = new SafariDriver();
          } catch (Exception ex) {
          }
        }

        if (driver == null) {
          throw new RuntimeException("系统中未发现可用的浏览器应用。");
        }

        if (isMoveOutOfScreen()) {
          driver.manage().window().setSize(new Dimension(1, 1));
          driver.manage().window().setPosition(new Point(-100000, -100000));
          driver.manage().window().setSize(new Dimension(1920, 1024));
        }
        setWebDriverDefaultValue(driver);
        webDriver = driver;
      }
    }
    return webDriver;
  }

  public void setWebDriver(RemoteWebDriver webDriver) {
    this.webDriver = webDriver;
  }

  private void setWebDriverDefaultValue(RemoteWebDriver driver) {
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
  }

  public boolean isMoveOutOfScreen() {
    return moveOutOfScreen;
  }

  public void setMoveOutOfScreen(boolean moveOutOfScreen) {
    this.moveOutOfScreen = moveOutOfScreen;
  }
}
