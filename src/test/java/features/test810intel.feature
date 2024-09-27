Feature: Ejecutar proceso EDI para Intel del mensaje 810
  como usuario necesito ejecutar el proceso EDI para el vendor Intel del mensaje 810, y validar que se inserte y se ejecute correctamente

  Scenario: Realizar la ejecucion del proceso para archivo exitoso Intel
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta processed Intel
    And Validar en la bd que se registre correctamente Intel
    And validar en el SFTP que se muestren los archivos en las diferentes carpetas Intel

  Scenario: Realizar la validacion del proceso para archivo con error intel
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta error intel
    And Validar en la bd que no se registre informacion intel
    And validar en el SFTP que no se generen archivos intel
    And Validar que se envie la notificacion al correo

  Scenario: Realizar la validacion del proceso para archivo en Retry Intel
    Given Se confirme la ejecucion del schedules
    When Se obtiene el cid
    Then Validar en el bloc storage que el archivo quede en la carpeta Retry Intel
    And Validar en la bd que se registre en estado tres Intel
    And Validar que se envie la notificacion al correo