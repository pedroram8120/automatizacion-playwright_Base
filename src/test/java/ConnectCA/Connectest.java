package ConnectCA;

import Pages.HomePage.Connect_CA;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.IOException;

public class Connectest {

    @Test
    @DisplayName("Validacion")

    public void Prueba1Angel() throws IOException, InterruptedException {
        Connect_CA angelapptest = new Connect_CA();
        angelapptest.loginTest("ram", "Password%001password010203");
    }

    @Test
    @DisplayName("Validacion2")
    public void Prueba2Angel() throws IOException, InterruptedException {
        Connect_CA angelapptest = new Connect_CA();
        angelapptest.loginTest("admi2", "Password%001password010203");
    }

    @Test
    @DisplayName("Validacion3")
    public void Prueba3Angel() throws IOException, InterruptedException {
        Connect_CA angelapptest = new Connect_CA();
        angelapptest.loginTest("admin", "Password%001password010203");
    }

}