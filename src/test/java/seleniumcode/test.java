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
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    private EdgeDriver driver;
    EdgeOptions options = new EdgeOptions();

    public static String nameArchiveOkApple = JOptionPane.showInputDialog("Ingresa el nombre del archivo de apple 810 ok:");
    public static String nameArchiveErrorApple = JOptionPane.showInputDialog("Ingresa el nombre del archivo de apple 810 error:");
    public static String nameArchiveRetryApple = JOptionPane.showInputDialog("Ingresa el nombre del archivo de apple 810 Retry:");
    //String nameArchiveOkApple = "apple_810_485288458";
    //String nameArchiveErrorApple = "apple_810_4154130_errors";
    //String nameArchiveRetryApple = "apple_810_45012-retrys";
    String nameArchiveOkIntel = "intel_810_000018336_AR";
    String nameArchiveErrorIntel = "intel_810_4100017338_err";
    String nameArchiveRetryIntel = "intel_810_000017338_retry";
    String nameArchiveOkWd = "wd_810_000018668";
    String nameArchiveErrorWd = "";
    String nameArchiveRetryWd = "";


    public static class SchedulerStatus {
        public static boolean schedulerExecuted = false;
    }
    public static class cidNumber {
        public static String cid = null;
        public static String lastLogText = null;
        public static WebElement lastLogElement = null;
    }

    //Apple
    @Given("^El usuario debe estar en la pagina principal de azure$")
    public void el_usuario_debe_estar_en_la_pagina_principal_de_azure() throws Throwable {
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

    @When("^Se ingresa a la api y se ejecuta el schedules del 810$")
    public void se_ingresa_a_la_api_y_se_ejecuta_el_schedules_del_810() throws Throwable {
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
        driver.findElement(By.cssSelector(".schedules-row:nth-child(1) label")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
        Thread.sleep(90000);
        System.out.println();
        SchedulerStatus.schedulerExecuted = true;

    }

    @Then("^Validar logs de ejecucion$")
    public void validar_logs_de_ejecucion() throws Throwable {
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
            System.out.println("El valor de 'cid' es: " + cidNumber.cid);
        } else {
            System.out.println("No se encontró el valor de 'cid' en el texto.");
        }
        driver.quit();

    }

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta processed apple$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_processed_apple() throws Throwable {
        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=processed/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveOkApple;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveOkApple + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Then("^Validar en la bd que se registre correctamente apple$")
    public void validar_en_la_bd_que_se_registre_correctamente_apple() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveOkApple + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT*FROM ICMXEDI.dbo.EdiProcessFiles where ediProcessId = '" + edi + "'");
            List<String> poNumbers = new ArrayList<>();

            while (resultSet2.next()) {
                String poNumber = resultSet2.getString("MessageId");
                System.out.println("el campo guardado es:" + poNumber);
                poNumbers.add(poNumber);
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("ediProcessId") + " | " + resultSet2.getString("MessageId") + " | " + resultSet2.getString("SftpPathArchive") + " | " + resultSet2.getString("SftpPathIn"));
            }
            resultSet2.close();
            for (String poNumber : poNumbers) {
                ResultSet resultSet3 = statement.executeQuery("SELECT * FROM X12810Invoice WHERE po_number = '" + poNumber + "'");
                while (resultSet3.next()) {
                    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("invoice_number"));
                }
                resultSet3.close();

                ResultSet resultSet4 = statement.executeQuery("SELECT * FROM X12810InvoiceLine WHERE po_number = '" + poNumber + "'");
                while (resultSet4.next()) {
                    System.out.println(resultSet4.getString("po_number") + " | " + resultSet4.getString("suppid") + " | " + resultSet4.getString("line_recid"));
                }
                resultSet4.close();

                ResultSet resultSet5 = statement.executeQuery("SELECT * FROM X12810InvoiceRawdata WHERE po_number = '" + poNumber + "'");
                while (resultSet5.next()) {
                    System.out.println(resultSet5.getString("po_number") + " | " + resultSet5.getString("suppid") + " | " + resultSet5.getString("as2_status"));
                }
                resultSet5.close();
            }

            //boolean hasData = resultSet3.next();
            //System.out.println("estoy aca " + hasData);
            //Assert.assertTrue("No se encontraron datos para la consulta", hasData);
            //if (hasData) {
            //do {
            // Imprimir los datos del ResultSet
            //    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("as2_status"));
            //} while (resultSet3.next());
            //}

            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Then("^validar en el SFTP que se muestren los archivos en las diferentes carpetas apple$")
    public void validar_en_el_SFTP_que_se_muestren_los_archivos_en_las_diferentes_carpetas_apple() throws Throwable {

        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/apple/archive/";
        String SFTPWORKINGDIR2 = "/edinfs/home/traxedi/test/apple/archive/810/";
        String SFTPWORKINGDIR3 = "/edinfs/home/traxedi/test/apple/in/";
        String SFTPWORKINGDIR4 = "/edinfs/home/traxedi/test/XUS/Apple/810/";

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
            //Validar que exista el mensaje 997
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists = fileList.stream()
                    .anyMatch(entry -> entry.getFilename().equals("997_" + nameArchiveOkApple + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("El archivo de mensaje 997 existe: " + fileExists);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists);

            // Validar que existe en la factura en pdf
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList2 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists2 = fileList2.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkApple + ".pdf"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo .pdf existe: " + fileExists2);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists2);
            //Validar que exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR2);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR2);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveOkApple + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists3);
            //Validar que exista en la carpeta in
            channelSftp.cd(SFTPWORKINGDIR3);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList4 = channelSftp.ls(SFTPWORKINGDIR3);
            // Verificar si el archivo existe
            boolean fileExists4 = fileList4.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkApple + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta in: " + fileExists4);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists4);
            //Validar que exista en la carpeta del pais, vendor y 810
            channelSftp.cd(SFTPWORKINGDIR4);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList5 = channelSftp.ls(SFTPWORKINGDIR4);
            // Verificar si el archivo existe
            boolean fileExists5 = fileList5.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkApple + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta del pais, vendor y 810: " + fileExists5);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists5);

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


    @Given("^Se confirme la ejecucion del schedules$")
    public void se_confirme_la_ejecucion_del_schedules() throws Throwable {

        Assert.assertTrue("El scheduler no se ejecutó correctamente.", SchedulerStatus.schedulerExecuted);

    }

    @When("^Se obtiene el cid$")
    public void se_obtiene_el_cid() throws Throwable{
        System.out.println("El valor de 'cid' es: " + cidNumber.cid);
        Assert.assertTrue("La variable está vacía o nula", cidNumber.cid != null && !cidNumber.cid.isEmpty());


    }

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta error apple$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_error_apple() throws Throwable {

        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=error/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveErrorApple;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveErrorApple + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que no se registre informacion apple$")
    public void validar_en_la_bd_que_no_se_registre_informacion_apple() throws Throwable {
        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

            try {
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveErrorApple + ".txt'");
                boolean hasData = resultSet.next();
                Assert.assertFalse("Se encontraron datos para la consulta", hasData);

                resultSet.close();
                statement.close();
                connection.close();

            }catch (SQLException e) {
                    e.printStackTrace();
                }

    }

    @Then("^validar en el SFTP que no se generen archivos apple$")
    public void validar_en_el_SFTP_que_no_se_generen_archivos_apple() throws Throwable {
        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/apple/archive/810/";

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
            //Validar que No exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveErrorApple + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertFalse("El archivo existe en el servidor SFTP", fileExists3);
        }catch (Exception ex) {
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

    @Then("^Validar que se envie la notificacion al correo$")
    public void validar_que_se_envie_la_notificacion_al_correo() throws Throwable {
        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=MuleSoftReports;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT request, response, * FROM LogIntegrationCRM WITH (NOLOCK) WHERE api_name = 'notifications-system-api' and cid = '" + cidNumber.cid + "'");
            boolean hasData = resultSet.next();
            System.out.println("Se envio correctamente el correo: " + hasData);
            Assert.assertTrue("No se encontraron datos para la consulta", hasData);
            if (hasData) {
                do {
                // Imprimir los datos del ResultSet
                  //  System.out.println(resultSet.getString("request") + " | " + resultSet.getString("cid"));
                } while (resultSet.next());
                }
            resultSet.close();
            statement.close();
            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Then("^Validar en el bloc storage que el archivo quede en la carpeta Retry apple$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_Retry_apple() throws Throwable {

        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=retry/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveRetryApple;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveRetryApple + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre en estado tres apple$")
    public void validar_en_la_bd_que_se_registre_en_estado_tres_apple() throws Throwable {
        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveRetryApple + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT id, Status, retries, filename, * FROM ICMXEDI.dbo.EdiProcess where Id = '" + edi + "'");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("status") + " | " + resultSet2.getString("retries") + " | " + resultSet2.getString("filename"));
            }
            resultSet2.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Intel
    @Then("^Validar en el bloc storage que el archivo quede en la carpeta processed Intel$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_processed_Intel() throws Throwable {
        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=processed/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveOkIntel;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveOkIntel + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre correctamente Intel$")
    public void validar_en_la_bd_que_se_registre_correctamente_Intel() throws Throwable {
        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveOkIntel + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT*FROM ICMXEDI.dbo.EdiProcessFiles where ediProcessId = '" + edi + "'");
            List<String> poNumbers = new ArrayList<>();

            while (resultSet2.next()) {
                String poNumber = resultSet2.getString("MessageId");
                System.out.println("el campo guardado es:" + poNumber);
                poNumbers.add(poNumber);
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("ediProcessId") + " | " + resultSet2.getString("MessageId") + " | " + resultSet2.getString("SftpPathArchive") + " | " + resultSet2.getString("SftpPathIn"));
            }
            resultSet2.close();
            for (String poNumber : poNumbers) {
                ResultSet resultSet3 = statement.executeQuery("SELECT * FROM X12810Invoice WHERE po_number = '" + poNumber + "'");
                while (resultSet3.next()) {
                    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("invoice_number"));
                }
                resultSet3.close();

                ResultSet resultSet4 = statement.executeQuery("SELECT * FROM X12810InvoiceLine WHERE po_number = '" + poNumber + "'");
                while (resultSet4.next()) {
                    System.out.println(resultSet4.getString("po_number") + " | " + resultSet4.getString("suppid") + " | " + resultSet4.getString("line_recid"));
                }
                resultSet4.close();

                ResultSet resultSet5 = statement.executeQuery("SELECT * FROM X12810InvoiceRawdata WHERE po_number = '" + poNumber + "'");
                while (resultSet5.next()) {
                    System.out.println(resultSet5.getString("po_number") + " | " + resultSet5.getString("suppid") + " | " + resultSet5.getString("as2_status"));
                }
                resultSet5.close();
            }

            //boolean hasData = resultSet3.next();
            //System.out.println("estoy aca " + hasData);
            //Assert.assertTrue("No se encontraron datos para la consulta", hasData);
            //if (hasData) {
            //do {
            // Imprimir los datos del ResultSet
            //    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("as2_status"));
            //} while (resultSet3.next());
            //}

            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Then("^validar en el SFTP que se muestren los archivos en las diferentes carpetas Intel$")
    public void validar_en_el_SFTP_que_se_muestren_los_archivos_en_las_diferentes_carpetas_Intel() throws Throwable {

        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/intel/archive/";
        String SFTPWORKINGDIR2 = "/edinfs/home/traxedi/test/intel/archive/810/";
        String SFTPWORKINGDIR3 = "/edinfs/home/traxedi/test/intel/in/";
        String SFTPWORKINGDIR4 = "/edinfs/home/traxedi/test/XUS/Intel/810/";

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
            //Validar que exista el mensaje 997
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists = fileList.stream()
                    .anyMatch(entry -> entry.getFilename().equals("997_" + nameArchiveOkIntel + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("El archivo de mensaje 997 existe: " + fileExists);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists);

            // Validar que existe en la factura en pdf
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList2 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists2 = fileList2.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkIntel + ".pdf"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo .pdf existe: " + fileExists2);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists2);
            //Validar que exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR2);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR2);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveOkIntel + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists3);
            //Validar que exista en la carpeta in
            channelSftp.cd(SFTPWORKINGDIR3);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList4 = channelSftp.ls(SFTPWORKINGDIR3);
            // Verificar si el archivo existe
            boolean fileExists4 = fileList4.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkIntel + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta in: " + fileExists4);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists4);
            //Validar que exista en la carpeta del pais, vendor y 810
            channelSftp.cd(SFTPWORKINGDIR4);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList5 = channelSftp.ls(SFTPWORKINGDIR4);
            // Verificar si el archivo existe
            boolean fileExists5 = fileList5.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkIntel + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta del pais, vendor y 810: " + fileExists5);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists5);

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

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta error intel$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_error_intel() throws Throwable {

        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=error/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveErrorIntel;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveErrorIntel + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que no se registre informacion intel$")
    public void validar_en_la_bd_que_no_se_registre_informacion_intel() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveErrorIntel + ".txt'");
            boolean hasData = resultSet.next();
            Assert.assertFalse("Se encontraron datos para la consulta", hasData);

            resultSet.close();
            statement.close();
            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Then("^validar en el SFTP que no se generen archivos intel$")
    public void validar_en_el_SFTP_que_no_se_generen_archivos_intel() throws Throwable {

        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/intel/archive/810/";

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
            //Validar que No exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveErrorIntel + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertFalse("El archivo existe en el servidor SFTP", fileExists3);
        }catch (Exception ex) {
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

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta Retry Intel$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_Retry_Intel() throws Throwable {

        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=retry/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveRetryIntel;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveRetryIntel + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre en estado tres Intel$")
    public void validar_en_la_bd_que_se_registre_en_estado_tres_Intel() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveRetryIntel + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT id, Status, retries, filename, * FROM ICMXEDI.dbo.EdiProcess where Id = '" + edi + "'");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("status") + " | " + resultSet2.getString("retries") + " | " + resultSet2.getString("filename"));
            }
            resultSet2.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //WD
    @Then("^Validar en el bloc storage que el archivo quede en la carpeta processed WD$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_processed_WD() throws Throwable {
        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=processed/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveOkWd;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveOkWd + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre correctamente WD$")
    public void validar_en_la_bd_que_se_registre_correctamente_WD() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveOkWd + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT*FROM ICMXEDI.dbo.EdiProcessFiles where ediProcessId = '" + edi + "'");
            List<String> poNumbers = new ArrayList<>();

            while (resultSet2.next()) {
                String poNumber = resultSet2.getString("MessageId");
                System.out.println("el campo guardado es:" + poNumber);
                poNumbers.add(poNumber);
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("ediProcessId") + " | " + resultSet2.getString("MessageId") + " | " + resultSet2.getString("SftpPathArchive") + " | " + resultSet2.getString("SftpPathIn"));
            }
            resultSet2.close();
            for (String poNumber : poNumbers) {
                ResultSet resultSet3 = statement.executeQuery("SELECT * FROM X12810Invoice WHERE po_number = '" + poNumber + "'");
                while (resultSet3.next()) {
                    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("invoice_number"));
                }
                resultSet3.close();

                ResultSet resultSet4 = statement.executeQuery("SELECT * FROM X12810InvoiceLine WHERE po_number = '" + poNumber + "'");
                while (resultSet4.next()) {
                    System.out.println(resultSet4.getString("po_number") + " | " + resultSet4.getString("suppid") + " | " + resultSet4.getString("line_recid"));
                }
                resultSet4.close();

                ResultSet resultSet5 = statement.executeQuery("SELECT * FROM X12810InvoiceRawdata WHERE po_number = '" + poNumber + "'");
                while (resultSet5.next()) {
                    System.out.println(resultSet5.getString("po_number") + " | " + resultSet5.getString("suppid") + " | " + resultSet5.getString("as2_status"));
                }
                resultSet5.close();
            }

            //boolean hasData = resultSet3.next();
            //System.out.println("estoy aca " + hasData);
            //Assert.assertTrue("No se encontraron datos para la consulta", hasData);
            //if (hasData) {
            //do {
            // Imprimir los datos del ResultSet
            //    System.out.println(resultSet3.getString("po_number") + " | " + resultSet3.getString("suppid") + " | " + resultSet3.getString("as2_status"));
            //} while (resultSet3.next());
            //}

            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Then("^validar en el SFTP que se muestren los archivos en las diferentes carpetas WD$")
    public void validar_en_el_SFTP_que_se_muestren_los_archivos_en_las_diferentes_carpetas_WD() throws Throwable {
        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/wd/archive/";
        String SFTPWORKINGDIR2 = "/edinfs/home/traxedi/test/wd/archive/810/";
        String SFTPWORKINGDIR3 = "/edinfs/home/traxedi/test/wd/in/";
        String SFTPWORKINGDIR4 = "/edinfs/home/traxedi/test/XUS/WD/810/";

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
            //Validar que exista el mensaje 997
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists = fileList.stream()
                    .anyMatch(entry -> entry.getFilename().equals("997_" + nameArchiveOkWd + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("El archivo de mensaje 997 existe: " + fileExists);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists);

            // Validar que existe en la factura en pdf
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList2 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists2 = fileList2.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkWd + ".pdf"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo .pdf existe: " + fileExists2);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists2);
            //Validar que exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR2);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR2);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveOkWd + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists3);
            //Validar que exista en la carpeta in
            channelSftp.cd(SFTPWORKINGDIR3);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList4 = channelSftp.ls(SFTPWORKINGDIR3);
            // Verificar si el archivo existe
            boolean fileExists4 = fileList4.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkWd + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta in: " + fileExists4);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists4);
            //Validar que exista en la carpeta del pais, vendor y 810
            channelSftp.cd(SFTPWORKINGDIR4);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList5 = channelSftp.ls(SFTPWORKINGDIR4);
            // Verificar si el archivo existe
            boolean fileExists5 = fileList5.stream()
                    .anyMatch(entry -> entry.getFilename().equals("1_" + nameArchiveOkWd + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta del pais, vendor y 810: " + fileExists5);
            Assert.assertTrue("El archivo no existe en el servidor SFTP", fileExists5);

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

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta error WD$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_error_WD() throws Throwable {
        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=error/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveErrorWd;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveErrorWd + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que no se registre informacion WD$")
    public void validar_en_la_bd_que_no_se_registre_informacion_WD() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveErrorWd + ".txt'");
            boolean hasData = resultSet.next();
            Assert.assertFalse("Se encontraron datos para la consulta", hasData);

            resultSet.close();
            statement.close();
            connection.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Then("^validar en el SFTP que no se generen archivos WD$")
    public void validar_en_el_SFTP_que_no_se_generen_archivos_WD() throws Throwable {

        String SFTPHOST = "xnapscalenfs.intcomex.com";
        int SFTPPORT = 22;
        String SFTPUSER = "ediuser";
        String SFTPPASS = "bJc93?f*5G3A$Q#";
        String SFTPWORKINGDIR = "/edinfs/home/traxedi/test/intel/archive/810/";

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
            //Validar que No exista en la carpeta 810
            channelSftp.cd(SFTPWORKINGDIR);
            // Listar los archivos en el directorio remoto
            Vector<ChannelSftp.LsEntry> fileList3 = channelSftp.ls(SFTPWORKINGDIR);
            // Verificar si el archivo existe
            boolean fileExists3 = fileList3.stream()
                    .anyMatch(entry -> entry.getFilename().equals(nameArchiveErrorWd + ".txt"));
            // Imprimir el resultado y realizar el assert
            System.out.println("el archivo existe en la carpeta 810: " + fileExists3);
            Assert.assertFalse("El archivo existe en el servidor SFTP", fileExists3);
        }catch (Exception ex) {
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

    @Then("^Validar en el bloc storage que el archivo quede en la carpeta Retry WD$")
    public void validar_en_el_bloc_storage_que_el_archivo_quede_en_la_carpeta_Retry_WD() throws Throwable {

        String baseUrl = "https://saintcedirepo.blob.core.windows.net/dvlp?restype=container&comp=list&sv=2023-01-03&st=2024-09-12T20%3A45%3A44Z&se=2024-12-31T20%3A45%3A00Z&sr=c&sp=rwl&sig=gsWmpb1KzqETgXun7cMjpkaTfscPZI2oWnIgN4zsEhU%3D&prefix=retry/x12_refact_810/";

        // Combinar las dos partes de la URL
        String fullUrl = baseUrl + nameArchiveRetryWd;
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
                boolean containsExpectedText = responseBody.contains(nameArchiveRetryWd + ".txt");
                // Aserción para verificar que el texto esperado está presente
                Assert.assertTrue("El cuerpo de la respuesta no contiene el texto esperado.", containsExpectedText);

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Then("^Validar en la bd que se registre en estado tres WD$")
    public void validar_en_la_bd_que_se_registre_en_estado_tres_WD() throws Throwable {

        String url = "jdbc:sqlserver://XNAPDBD01:1433;databaseName=ICMXEDI;instance=MSSQLSERVER;encrypt=false;trustCertificate=true;";
        String username = "MuleSoftDBUSer";
        String password = "Xn@p*Mu13$0f+2o2o";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ICMXEDI.dbo.EdiProcess where filename = '" + nameArchiveRetryWd + ".txt'");
            String edi = null;
            while (resultSet.next()) {
                edi = resultSet.getString("Id");
                System.out.println("Dato obtenido: " + edi);
                System.out.println(resultSet.getString("Id") + " | " + resultSet.getString("Vendor") + " | " + resultSet.getString("MessageType") + " | " + resultSet.getString("Status"));
            }
            resultSet.close();

            ResultSet resultSet2 = statement.executeQuery("SELECT id, Status, retries, filename, * FROM ICMXEDI.dbo.EdiProcess where Id = '" + edi + "'");
            while (resultSet2.next()) {
                System.out.println(resultSet2.getString("Id") + " | " + resultSet2.getString("status") + " | " + resultSet2.getString("retries") + " | " + resultSet2.getString("filename"));
            }
            resultSet2.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}


