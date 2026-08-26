package utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.time.Duration;

public class OtpExtractor {

    public static void seleccionarUsuario(String nombreUsuario) {
        AndroidDriver driver = null;

        try {
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setAutomationName("UiAutomator2");
            options.setDeviceName("AndroidDevice");
            options.setAppPackage("org.fedorahosted.freeotp");
            options.setAppActivity("org.fedorahosted.freeotp.main.MainActivity");
            options.setNoReset(true);

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/"), options);

            // 1. Buscar y seleccionar al usuario en la lista
            By selectorCuenta = AppiumBy.androidUIAutomator(
                    "new UiSelector().textContains(\"" + nombreUsuario + "\")"
            );

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
            WebElement cuentaItem = wait.until(ExpectedConditions.elementToBeClickable(selectorCuenta));
            cuentaItem.click();
            System.out.println("⏳ ingresar el código manualmente...");

        } catch (Exception e) {
            System.err.println("❌ Error al intentar seleccionar el usuario [" + nombreUsuario + "]: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}