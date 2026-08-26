package Pages.Loginpage;

import Pages.BasePage.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.BrowserFactory;

public class Login extends BasePage {

    public Login(Page page) {
        super(page);
    }

    public void Sign_OAuth_Keycloak() {
        try {
            Locator loginButton = page.getByText("Sign in with OAuth Keycloak");
            loginButton.click();
        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo hacer clic en OAuth Keycloak. Causa: " + e.getMessage());
        }
    }

    public void Ingreso_usuario(String usuario) {
        try {
            page.fill("//input[@id='username']", usuario);
            Thread.sleep(2000);
        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo ingresar el usuario. Causa: " + e.getMessage());
        }
    }

    public void escribirPassword(String password) {
        try {

            page.fill("//input[@id='password']", password);
            Thread.sleep(2000);
        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo escribir en el campo contraseña. Causa: " + e.getMessage());
        }
    }

    public void UsuarioOTP(String nombreUsuario) {
        try {
            // 1. Abrir FreeOTP y seleccionar al usuario para que la clave sea visible en pantalla
            utils.OtpExtractor.seleccionarUsuario(nombreUsuario);

            // 2. Ubicar el campo OTP en la aplicación web
            Locator campoOtp = page.locator("//input[@id='otp' or @name='otp' or @autocomplete='one-time-code']").first();
            campoOtp.focus();
            Thread.sleep(5000);

        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo procesar el flujo de OTP. Causa: " + e.getMessage());
        }
    }
    public void iniciar_sesion() throws InterruptedException {
        try {
            page.click("//button[@id='kc-login']");
            Thread.sleep(1000);
            // Al hacer clic, esperamos a que el servidor responda si las credenciales son incorrectas
            String errorPantalla = obtenerMensajeErrorDinamico();
            if (errorPantalla != null) {
                BrowserFactory.validarOAlertar(page, true, "Respuesta de la aplicación: " + errorPantalla);
            }

        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo hacer clic en el botón de login. Causa: " + e.getMessage());
        }
    }

    public void seleccionar_Btp_user() {
        try {
            page.click("//*[@id='nav-item-users']");
            System.out.println("Seleccionando usuario BTP");
        } catch (Exception e) {
            BrowserFactory.validarOAlertar(page, true, "No se pudo seleccionar BTP user. Causa: " + e.getMessage());
        }
    }

    public String obtenerMensajeErrorDinamico() {
        // Busca mensajes de error de credenciales, de OTP inválido o clases estándar de alerta
        Locator alerta = page.locator(
                "span:has-text('UsuarioOTP o contraseña incorrectos'), " +
                        "span:has-text('El código de autenticación no es válido'), " +
                        "span:has-text('incorrectos'), " +
                        "span:has-text('no es válido'), " +
                        "span:has-text('inválid'), " +
                        ".alert-danger, .error-message"
        ).first();

        try {
            // Espera hasta 3 segundos a que la aplicación pinte la alerta en pantalla
            alerta.waitFor(new Locator.WaitForOptions().setTimeout(3000));

            if (alerta.isVisible()) {
                String texto = alerta.innerText().trim();
                if (!texto.isEmpty()) {
                    return texto;
                }
            }
        } catch (Exception e) {
            // Si no aparece ninguna alerta en ese tiempo, la ejecución continúa limpiamente
        }
        return null;
    }
}