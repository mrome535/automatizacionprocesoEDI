Feature: Ejecutar proceso EDI para Apple del mensaje 810
  como usuario necesito ejecutar el proceso EDI para el vendor apple del mensaje 810, y validar que se inserte y se ejecute correctamente

  Scenario: Realizar la ejecucion del proceso para archivo exitoso apple
    Given El usuario debe estar en la pagina principal de azure
    When Se ingresa a la api y se ejecuta el schedules del 810
    Then Validar logs de ejecucion
    And Validar en el bloc storage que el archivo quede en la carpeta processed apple
    And Validar en la bd que se registre correctamente apple
    And validar en el SFTP que se muestren los archivos en las diferentes carpetas apple

  Scenario: Realizar la validacion del proceso para archivo con error apple
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta error apple
    And Validar en la bd que no se registre informacion apple
    And validar en el SFTP que no se generen archivos apple
    And Validar que se envie la notificacion al correo

  Scenario: Realizar la validacion del proceso para archivo en Retry apple
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta Retry apple
    And Validar en la bd que se registre en estado tres apple
    And Validar que se envie la notificacion al correo