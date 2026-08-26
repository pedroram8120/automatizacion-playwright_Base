package Pages.HomePage;

import Pages.Loginpage.Login;
import com.microsoft.playwright.Page;
import utils.BrowserFactory;
import java.io.IOException;
public class Connect_CA {


    public void loginTest(String Usuario, String password) throws InterruptedException, IOException {

        BrowserFactory browser = new BrowserFactory();
        var Ruta_excel= "C:\\Users\\Usuario\\IdeaProjects\\otros\\automatizacionplaywrigth\\Reporte\\Matriz_V3.xlsx";

        Page page = browser.iniciarNavegador("http://172.25.3.2:8088");


        Login login = new Login(page);
        //browser.guardarImagenEnExcel("Ingreso a la aplicacion", Ruta_excel);
        login.Ingreso_usuario(Usuario);
        browser.adjuntarCapturaAllure("ingresar usuario");
        login.escribirPassword(password);
        browser.adjuntarCapturaAllure("Ingreso la contraseña");
        login.iniciar_sesion();
        login.UsuarioOTP("ram");
        login.iniciar_sesion();
        /*
        login.seleccionar_Btp_user();
        browser.guardarImagenEnExcel("Seleeciono USER", Ruta_excel);
        browser.cerrarNavegador();

        */
    }


}