Feature: Ejecutar proceso EDI para Apple del mensaje 850
  como usuario necesito ejecutar el proceso EDI para el vendor apple del mensaje 850, y validar que se inserte y se ejecute correctamente

  Scenario: Realizar la ejecucion del proceso para archivo exitoso apple
    Given El usuario debe estar en la pagina principal de azure 850
    When Se ingresa a la api y se ejecuta el schedules del 850
    Then Validar logs de ejecucion 850
    And Validar en el bloc storage que el archivo quede en la carpeta processed apple 850
    And Validar en la bd que se registre correctamente apple 850
    And validar en el SFTP que se muestren los archivos en las diferentes carpetas apple 850
    And Validar que se envie correo con la generacion de la orden y IWS