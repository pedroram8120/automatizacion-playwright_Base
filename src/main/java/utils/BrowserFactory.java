package utils;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class BrowserFactory {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    public Page iniciarNavegador(String url) throws IOException {
        if (true) {
            playwright = Playwright.create();

            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setChannel("msedge")
                            .setHeadless(false)
                            .setArgs(Arrays.asList("--start-maximized"))
            );

            context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(null)
            );

            page = context.newPage();
            page.navigate(url);
            System.out.println("Navegador iniciado de manera correcta");

        } else {
            throw new RuntimeException("Error al iniciar el navegador");
        }

        return page;
    }

    public void cerrarNavegador() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        System.out.println("Cerrando navegador");
    }

    public void tomarCaptura(String nombreCaptura) {
        try {
            Path carpetaCapturas = Paths.get("capturas");
            Files.createDirectories(carpetaCapturas);

            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            Path archivoCaptura = carpetaCapturas.resolve(nombreCaptura + "_" + fecha + ".png");

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(archivoCaptura)
                            .setFullPage(true)
            );

            System.out.println("Captura guardada en: " + archivoCaptura.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la captura", e);
        }
    }

    public void guardarCapturaEnExcel(String nombreCaptura, String rutaExcel) {
        try {
            Path carpetaCapturas = Paths.get("capturas");
            Files.createDirectories(carpetaCapturas);

            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            Path archivoCaptura = carpetaCapturas.resolve(nombreCaptura + "_" + fecha + ".png");

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(archivoCaptura)
                            .setFullPage(true)
            );

            File archivo = new File(rutaExcel);
            FileInputStream fis = new FileInputStream(archivo);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            fis.close();

            int fila = sheet.getLastRowNum() + 1;
            Row row = sheet.getRow(fila);
            if (row == null) {
                row = sheet.createRow(fila);
            }

            Cell cell = row.createCell(7); // Columna H
            cell.setCellValue(archivoCaptura.toString());

            FileOutputStream fos = new FileOutputStream(archivo);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("Captura registrada en Excel: " + archivoCaptura.toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar captura y registrar en Excel", e);
        }
    }

    public void guardarImagenEnExcel(String nombreCaptura, String rutaExcel) {
        try {
            Path carpetaCapturas = Paths.get("capturas");
            Files.createDirectories(carpetaCapturas);

            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            Path archivoCaptura = carpetaCapturas.resolve(nombreCaptura + "_" + fecha + ".png");

            page.screenshot(
                    new Page.ScreenshotOptions()
                            .setPath(archivoCaptura)
                            .setFullPage(true)
            );

            FileInputStream fis = new FileInputStream(rutaExcel);
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheetAt(0);
            fis.close();

            InputStream inputStream = Files.newInputStream(archivoCaptura);
            byte[] imageBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();

            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            int fila = sheet.getLastRowNum() + 1;
            Row row = sheet.getRow(fila);
            if (row == null) {
                row = sheet.createRow(fila);
            }

            int col = 13; // Columna N
            Cell cell = row.createCell(col);
            cell.setCellValue("Screenshot");

            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(col);
            anchor.setRow1(fila);
            anchor.setCol2(col + 1);
            anchor.setRow2(fila + 1);

            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(1.0);

            sheet.setColumnWidth(col, 7000);
            row.setHeight((short) 4000);

            FileOutputStream fos = new FileOutputStream(rutaExcel);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("Captura insertada y ajustada a celda correctamente: " + archivoCaptura.toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar captura en Excel con ajuste de celda", e);
        }
    }

    public void adjuntarCapturaAllure(String nombreCaptura) {
        if (page == null) {
            throw new RuntimeException("No existe una instancia de Page para tomar la captura.");
        }

        try {
            byte[] screenshot = page.screenshot(
                    new Page.ScreenshotOptions()
                            .setFullPage(true)
            );

            Allure.addAttachment(
                    nombreCaptura,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );

            System.out.println("Captura agregada a Allure: " + nombreCaptura);

        } catch (Exception e) {
            throw new RuntimeException("Error al adjuntar captura a Allure: " + nombreCaptura, e);
        }
    }

    /**
     * Método estático de alerta y fallo automático para Allure.
     * Puede llamarse como BrowserFactory.validarOAlertar(...)
     */
    public static void validarOAlertar(Page targetPage, boolean hayError, String mensajeError) {
        if (hayError) {
            if (targetPage != null) {
                byte[] screenshot = targetPage.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment("Evidencia_Error", "image/png", new ByteArrayInputStream(screenshot), ".png");
            }

            System.err.println("❌ ALERTA DE FALLO: " + mensajeError);
            throw new AssertionError("Prueba Fallida: " + mensajeError);
        }
    }

    public String obtenerMensajeErrorDinamico() {
        // Selector dirigido específicamente al texto de credenciales incorrectas o contenedores de error
        Locator alerta = page.locator("span:has-text('incorrectos'), span:has-text('inválid'), .alert-danger, .error-message").first();

        try {
            // Le damos hasta 4 segundos a la interfaz para que procese el login y muestre el mensaje en rojo
            alerta.waitFor(new Locator.WaitForOptions().setTimeout(4000));

            if (alerta.isVisible()) {
                String texto = alerta.innerText().trim();
                if (!texto.isEmpty()) {
                    return texto;
                }
            }
        } catch (Exception e) {
            // Si pasaron 4 segundos y no apareció la alerta, significa que el login fue exitoso
        }
        return null;
    }
}