Feature: Ejecutar proceso EDI para WD del mensaje 810
  como usuario necesito ejecutar el proceso EDI para el vendor WD del mensaje 810, y validar que se inserte y se ejecute correctamente

  Scenario: Realizar la ejecucion del proceso para archivo exitoso WD
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta processed WD
    And Validar en la bd que se registre correctamente WD
    And validar en el SFTP que se muestren los archivos en las diferentes carpetas WD

  Scenario: Realizar la validacion del proceso para archivo con error WD
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta error WD
    And Validar en la bd que no se registre informacion WD
    And validar en el SFTP que no se generen archivos WD
    And Validar que se envie la notificacion al correo

  Scenario: Realizar la validacion del proceso para archivo en Retry WD
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta Retry WD
    And Validar en la bd que se registre en estado tres WD
    And Validar que se envie la notificacion al correo