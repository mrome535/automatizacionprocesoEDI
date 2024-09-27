package seleniumcode;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test850 {

    private EdgeDriver driver;
    EdgeOptions options = new EdgeOptions();
    String nameArchiveOkApple850= "apple_850_254125486";
    //public static String nameArchiveOkApple850 = JOptionPane.showInputDialog("Ingresa el nombre del archivo de apple 850 OK:");

    public static class SchedulerStatus {
        public static boolean schedulerExecuted = false;
    }
    public static class cidNumber {
        public static String cid = null;
        public static String lastLogText = null;
        public static WebElement lastLogElement = null;
    }


    @Given("^El usuario debe estar en la pagina principal de azure 850$")
    public void el_usuario_debe_estar_en_la_pagina_principal_de_azure_850() throws Throwable {

        System.setProperty("webdriver.edge.driver", "./src/test/resources/edgedrivers/msedgedriver.exe");
        // Añadir la opción para ejecutar en modo headless (segundo plano)
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        driver = new EdgeDriver(options);
        driver.get("https://anypoint.mulesoft.com/login/domain/intcomex-9");
        driver.manage().window().maximize();
        Thread.sleep(5000);
        //WebElement cookies = driver.findElement(By.id("onetrust-accept-btn-handler"));
        //cookies.click();
        WebElement login = driver.findElement(By.id("39effd49-2e9f-413f-800d-905792c5d18b"));
        login.click();
        Thread.sleep(5000);
        WebElement welcome = driver.findElement(By.className("Header--headerText__wGasg6"));
        Assert.assertTrue("Welcome to the #1 platform for APIs and integrations", welcome.isDisplayed());

    }

    @When("^Se ingresa a la api y se ejecuta el schedules del 850$")
    public void se_ingresa_a_la_api_y_se_ejecuta_el_schedules_del_850() throws Throwable {

        WebElement menu = driver.findElement(By.className("Toggle--toggle-button__pYsZ03"));
        menu.click();
        Thread.sleep(5000);
        WebElement runtime = driver.findElement(By.cssSelector(".Sidebar--sidebar-list-item__MZ_4hR:nth-child(3) .Sidebar--sidebar-list-item__MZ_4hR:nth-child(1) .Sidebar--sidebar-name__WK8lf8"));
        runtime.click();
        Thread.sleep(8000);
        WebElement shearch = driver.findElement(By.cssSelector(".sc-bxivhb"));
        shearch.click();
        driver.findElement(By.cssSelector("input:nth-child(2)")).sendKeys("edix12-msg-810");
        driver.findElement(By.linkText("edix12-msg-810-850-process-jobs-api-qa")).click();
        Thread.sleep(5000);
        WebElement scheduler = driver.findElement(By.linkText("Schedules"));
        scheduler.click();
        Thread.sleep(5000);
        driver.findElement(By.cssSelector(".schedules-row:nth-child(3) label")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
        Thread.sleep(90000);
        System.out.println();
        SchedulerStatus.schedulerExecuted = true;

    }

    @Then("^Validar logs de ejecucion 850$")
    public void validar_logs_de_ejecucion_850() throws Throwable {

        WebElement log = driver.findElement(By.linkText("Logs"));
        log.click();
        Thread.sleep(15000);

        List<WebElement> logElements = driver.findElements(By.xpath("//div[contains(@class, 'first-line') and contains(@class, 'ng-binding')]"));
        // Obtener el último registro de la lista
        cidNumber.lastLogElement = logElements.get(logElements.size() - 1);
        // Extraer el texto del último log
        cidNumber.lastLogText = cidNumber.lastLogElement.getText();
        // Extraer el valor de "cid" utilizando una expresión regular
        String regex = "\"cid\":\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cidNumber.lastLogText);

        if (matcher.find()) {
            // Guardar el valor de "cid" en una variable
            cidNumber.cid = matcher.group(1);
            System.out.println("El valor de 'cid' es 850: " + cidNumber.cid);
        } else {
            System.out.println("No se encontró el valor de 'cid' en el texto.");
        }
        driver.quit();

    }

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta processed apple 850$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_processed_apple_850() throws Throwable {
        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=processed/x12_refact_850/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveOkApple850;
        // Crear cliente HTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Realizar solicitud GET con la URL completa
            HttpGet request = new HttpGet(fullUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Obtener el código de estado HTTP correcto
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("El código de respuesta no es el esperado.", 200, statusCode);

                // Obtener el cuerpo de la respuesta
                String responseBody = EntityUtils.toString(response.getEntity());
                // Verificar si el cuerpo de la respuesta contiene el texto esperado
                boolean containsExpectedText = responseBody.contains(nameArchiveOkApple850 + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre correctamente apple 850$")
    public void validar_en_la_bd_que_se_registre_correctamente_apple_850() throws Throwable {
        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveOkApple850 + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();
            statement.close();
            connection.close();

        } catch (
        SQLException e) {
            e.printStackTrace();
        }

    }

    @Then("^validar en el SFTP que se muestren los archivos en las diferentes carpetas apple 850$")
    public void validar_en_el_SFTP_que_se_muestren_los_archivos_en_las_diferentes_carpetas_apple_850() throws Throwable {

        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/apple/archive/";


        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            // Crear una instancia de JSch
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            // Deshabilitar el chequeo de claves (opcional, depende de tu configuración)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // Conectar la sesión
            session.connect();
            // Abrir un canal SFTP
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            // Cambiar al directorio de trabajo remoto
            //Validar que exista el archivo en la carpeta raiz
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists = fileList.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveOkApple850 + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("El archivo 850 existe: " + fileExists);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Then("^Validar que se envie correo con la generacion de la orden y IWS$")
    public void Validar_que_se_envie_correo_con_la_generacion_de_la_orden_y_IWS() throws Throwable{

        String IwsNumber= null;

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=MuleSoftReports;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT request, response, * FROM LogIntegrationCRM WITH (NOLOCK) WHERE api_name = 'notifications-system-api' and cid = 'ade2d840-7ce3-11ef-ae57-0affd595860b'");
            while (resultSet.next()){
                // Obtener el campo "request" que está en formato JSON
                String requestJson = resultSet.getString("request");

                //System.out.println("Contenido del campo 'request':");
                //System.out.println(requestJson);  // Verificar el valor aquí

                // Validar si el campo es un JSON válido que empieza con '{'
                if (requestJson != null && requestJson.trim().startsWith("{")) {
                    // Parsear el campo como un objeto JSON
                    JSONObject jsonObject = new JSONObject(requestJson);

                    // Extraer el campo "subject" donde se encuentra la PO
                    String subject = jsonObject.getString("subject");

                    // Utilizar una expresión regular para extraer el número de la PO
                    Pattern pattern = Pattern.compile("PO:(\\d+)");
                    Matcher matcher = pattern.matcher(subject);


                    if (matcher.find()) {
                        IwsNumber = matcher.group(1);  // El número de iws
                        System.out.println("Número de PO extraído: " + IwsNumber);
                    } else {
                        System.out.println("No se encontró el número de PO en el subject.");
                    }
                }
            }
            resultSet.close();
            statement.close();
            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

        String baseUrl = "https://iwstest.intcomex.com/api/adminapi/SearchOrders?keywords="+ IwsNumber+"&customerId=&startDate=01/01/2024&endDate=12/31/2024&tag=&flattenResponse=false&locale=en&_=1727444318508";


        // Crear cliente HTTP
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Realizar solicitud GET con la URL completa
            HttpGet request = new HttpGet(baseUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Obtener el código de estado HTTP correcto
                int statusCode = response.getStatusLine().getStatusCode();
                Assert.assertEquals("El código de respuesta no es el esperado.", 200, statusCode);
                System.out.println("el codigo de respuesta es: " + statusCode);

                // Obtener el cuerpo de la respuesta
                String responseBody = EntityUtils.toString(response.getEntity());
                // Verificar si el cuerpo de la respuesta contiene el texto esperado
                boolean containsExpectedText = responseBody.contains(IwsNumber);
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);
                System.out.println("el resultado es ok"+ containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
