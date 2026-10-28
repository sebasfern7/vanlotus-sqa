# language: es
Característica: Registro de usuario en Van Lotus
  Como dueña de la joyería Van Lotus
  Quiero que una persona nueva pueda crear su propia cuenta
  Para que pueda entrar al sistema sin que yo tenga que crearla a mano

  Antecedentes:
    Dado que el registro público está habilitado
    Y que ya existe la cuenta "admin"

  # CA-01: datos válidos -> cuenta creada con rol TRABAJADOR y contraseña cifrada
  Escenario: Registro exitoso con datos válidos
    Cuando una persona se registra con el usuario "lucia.vargas" y la contraseña "Joyas2026"
    Entonces la cuenta "lucia.vargas" queda registrada con el rol "TRABAJADOR"
    Y la contraseña de "lucia.vargas" no se guarda en texto plano

  # CA-02: nombre de usuario duplicado -> rechazo con mensaje claro, sin crear otra cuenta
  Escenario: Rechazo de un nombre de usuario ya registrado
    Dado que ya existe la cuenta "lucia.vargas"
    Cuando una persona se registra con el usuario "lucia.vargas" y la contraseña "OtraClave99"
    Entonces el registro es rechazado con el mensaje "El nombre de usuario ya está en uso"
    Y en el sistema existe una sola cuenta "lucia.vargas"

  # Extra: reglas de la política de contraseñas que forman parte de "datos válidos" (CA-01)
  Esquema del escenario: Rechazo de contraseñas inseguras
    Cuando una persona se registra con el usuario "mateo" y la contraseña "<contrasena>"
    Entonces el registro es rechazado con el mensaje "<mensaje>"
    Y la cuenta "mateo" no queda registrada

    Ejemplos:
      | contrasena      | mensaje                                                   |
      | abc12           | La contraseña debe tener al menos 8 caracteres            |
      | solamenteletras | La contraseña debe contener al menos una letra y un número |
      | 12345678        | La contraseña debe contener al menos una letra y un número |
