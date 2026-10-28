# Dinámica SQA: "Del comportamiento a la implementación"

**Caso:** registro de usuario en Van Lotus (e-commerce / sistema de gestión de una joyería)
**Prácticas:** ATDD → BDD → TDD, integrando DDD
**Repos:** `vanlotusbackend` (Spring Boot 3.5, Java 17) · `vanlotusfrontend` (React 19 + Vite)
**Rama de trabajo (en ambos repos):** `feature/registro-usuario-sqa`

---

## 0. Reparto del equipo (3 integrantes)

| Integrante | Explica | Rol en la dinámica |
|---|---|---|
| Integrante 1 | **ATDD** | Hace de **"rol cliente"** (la dueña de la joyería) y define con el equipo los 2 criterios de aceptación |
| Integrante 2 | **BDD** | Redacta los escenarios Gherkin y los conecta con código (step definitions de Cucumber) |
| Integrante 3 | **TDD** | Lleva el ciclo Rojo → Verde → Refactor y muestra la evidencia |
| Los 3 | **DDD** | Lenguaje ubicuo y elementos del dominio (sección 5) |

---

## 1. Conceptos (lo que cada integrante explica)

### TDD — Test-Driven Development (Beck, 2003)
Disciplina de desarrollo en la que **primero se escribe una prueba unitaria que falla**, luego el
**código mínimo** para que pase y al final se **refactoriza** sin cambiar el comportamiento.
- Ciclo: **Rojo → Verde → Refactor**.
- Pregunta que responde: *¿el código hace lo que el programador quiso?*
- Objetivo de calidad: cada pieza de código queda cubierta por una prueba, y el diseño sale modular y fácil de probar.
- En este caso: `NombreUsuarioTest`, `ContrasenaTest`, `RegistrarUsuarioTest` (backend) y `registroUsuario.test.js` (frontend).

### ATDD — Acceptance Test-Driven Development
El cliente/Product Owner, el desarrollador y el tester acuerdan **antes de codificar** los
**criterios de aceptación** de la historia de usuario, y esos criterios se vuelven pruebas.
- Pregunta que responde: *¿cuándo está "lista" la historia para el cliente?*
- Objetivo de calidad: aclarar el requisito antes de programar, con menos ambigüedad y menos retrabajo.
- Diferencia con BDD: ATDD se centra en **cumplir la historia** (criterios de aceptación); BDD en **describir el comportamiento** con lenguaje de negocio.

### BDD — Behavior-Driven Development (North, 2006)
Extensión de TDD centrada en la **comunicación**: el comportamiento esperado se describe en un
lenguaje que entienden negocio, QA y desarrollo, con la sintaxis **Gherkin**
(**Dado / Cuando / Entonces**).
- Pregunta que responde: *¿estamos construyendo el producto correcto?*
- Objetivo de calidad: **la especificación es la prueba ejecutable**. Si el escenario pasa, el software se comporta como pide el negocio.
- En este caso: `features/registro_usuario.feature` ejecutado con **Cucumber**.

### DDD — Domain-Driven Design (Evans, 2003)
Filosofía de **diseño** (no es una práctica de testing) que modela el software según el dominio
del negocio y usa un **lenguaje ubicuo** igual en las conversaciones, en los escenarios y en el código.
- Objetivo de calidad: integridad conceptual. Si el código refleja el negocio, es más fácil de entender y mantener y tiene menos errores de lógica.

### Cómo encajan
```
Cliente ──(ATDD)──> Criterios de aceptación ──(BDD)──> Escenarios Gherkin ejecutables
                                                             │
                                                     (TDD) pruebas unitarias
                                                             │
                                          Código del dominio (DDD: lenguaje ubicuo)
```

---

## 2. Fase ATDD: historia y criterios de aceptación

**Historia de usuario**
> **Como** dueña de la joyería Van Lotus
> **quiero** que una persona nueva pueda crear su propia cuenta
> **para** que pueda entrar al sistema sin que yo tenga que crearla a mano.

**Conversación con el "rol cliente"** (Integrante 1). Preguntas que aclararon el requisito:
- *¿Qué datos pide el registro?* Nombre de usuario y contraseña.
- *¿Qué contraseña es aceptable?* Al menos 8 caracteres, con letras y números.
- *¿Con qué permisos nace la cuenta?* TRABAJADOR. La primera cuenta del sistema es la de la dueña (ADMIN).
- *¿Y si el nombre ya existe?* Se rechaza con un mensaje claro y no se crea nada.

| ID | Criterio de aceptación |
|---|---|
| **CA-01** | Con un nombre de usuario libre y una contraseña segura (≥ 8 caracteres, con letras y números), el sistema **crea la cuenta con rol TRABAJADOR** y la contraseña se guarda **cifrada**, nunca en texto plano. |
| **CA-02** | Si el nombre de usuario ya está registrado, el sistema **rechaza el registro** con el mensaje *"El nombre de usuario ya está en uso"* y **no crea una segunda cuenta**. |

---

## 3. Fase BDD: un escenario Gherkin por criterio

Archivo: [`src/test/resources/features/registro_usuario.feature`](../src/test/resources/features/registro_usuario.feature)

```gherkin
# language: es
Característica: Registro de usuario en Van Lotus

  Antecedentes:
    Dado que el registro público está habilitado
    Y que ya existe la cuenta "admin"

  # CA-01
  Escenario: Registro exitoso con datos válidos
    Cuando una persona se registra con el usuario "lucia.vargas" y la contraseña "Joyas2026"
    Entonces la cuenta "lucia.vargas" queda registrada con el rol "TRABAJADOR"
    Y la contraseña de "lucia.vargas" no se guarda en texto plano

  # CA-02
  Escenario: Rechazo de un nombre de usuario ya registrado
    Dado que ya existe la cuenta "lucia.vargas"
    Cuando una persona se registra con el usuario "lucia.vargas" y la contraseña "OtraClave99"
    Entonces el registro es rechazado con el mensaje "El nombre de usuario ya está en uso"
    Y en el sistema existe una sola cuenta "lucia.vargas"
```

Extra: un **Esquema del escenario** con 3 ejemplos de contraseñas inseguras (`abc12`,
`solamenteletras`, `12345678`). Detalla la regla "contraseña segura" de CA-01.

Los escenarios **se ejecutan de verdad**:
- `RegistroUsuarioAceptacionTest`: suite JUnit 5 que lanza Cucumber.
- `RegistroUsuarioSteps`: traduce cada frase Gherkin a una llamada al caso de uso `RegistrarUsuario`. Usa el cifrador **BCrypt real** y un repositorio en memoria, sin base de datos.

---

## 4. Fase TDD: Rojo → Verde → Refactor (con evidencia real)

Escenario implementado con TDD: **CA-01 (registro exitoso)**, con sus reglas de dominio, y luego CA-02.

### 🔴 Rojo: primero las pruebas
Se escribieron `NombreUsuarioTest`, `ContrasenaTest` y `RegistrarUsuarioTest` **antes** de que
existiera cualquier clase del dominio. Resultado: **no compila**; las clases `NombreUsuario`,
`CuentaUsuario`, `RegistrarUsuario`, etc. no existen.
Evidencia: [`docs/evidencias/01-rojo.txt`](evidencias/01-rojo.txt)

```java
@Test
@DisplayName("CA-01: registra una cuenta nueva con rol TRABAJADOR")
void registraCuentaConRolTrabajador() {
    casoDeUso(true).ejecutar("admin", "Admin2026");
    CuentaUsuario cuenta = casoDeUso(true).ejecutar("lucia.vargas", "Joyas2026");

    assertThat(cuenta.rol()).isEqualTo(Rol.TRABAJADOR);
    assertThat(repositorio.buscar("lucia.vargas")).isPresent();
}
```

### 🟢 Verde: el código mínimo
Se crearon los Value Objects, la entidad `CuentaUsuario`, los puertos y el caso de uso
`RegistrarUsuario` con lo justo para pasar. **21 pruebas en verde.**
Evidencia: [`docs/evidencias/02-verde.txt`](evidencias/02-verde.txt)

> **Hallazgo de calidad (vale la pena contarlo):** en esta corrida Surefire mostraba
> `Tests run: 0` para la suite de Cucumber. Para comprobarlo, **rompimos a propósito un escenario**
> y el build **seguía en verde**: era un *falso verde*. La causa es un bug de
> `maven-surefire-plugin 3.5.3` (la versión por defecto de Spring Boot 3.5.4). Se fijó la
> versión **3.5.4** en el `pom.xml` y el escenario roto volvió a fallar el build, como debe ser.
> Lección: una prueba que nunca vimos fallar no prueba nada.

### 🔵 Refactor: mejorar sin cambiar el comportamiento
- `AuthService.register` tenía las reglas mezcladas con HTTP y JWT (conteo de usuarios, duplicados, rol ADMIN). Ahora **delega en el caso de uso** `RegistrarUsuario` y solo genera el token.
- Las reglas viven en el dominio: la longitud y los caracteres en `NombreUsuario`, la política en `Contrasena` y el "primer usuario = ADMIN" en `CuentaUsuario.nueva(...)`.
- `Contrasena.toString()` oculta el valor para que no se filtre en logs.
- Las pruebas siguen en verde: **21/21, ahora con los 5 escenarios contados por Surefire.**
  Evidencia: [`docs/evidencias/03-refactor.txt`](evidencias/03-refactor.txt)

### Frontend (mismo ciclo)
`src/domain/registroUsuario.test.js` se escribió primero (falló porque el módulo no existía).
Después se implementó `src/domain/registroUsuario.js` → **6/6 en verde** con Vitest. La página
`/registro` usa esas validaciones para avisar al usuario antes de llamar a la API.

---

## 5. DDD: elementos del dominio

**Bounded context:** *Registro* (identidad de usuarios), en el paquete
`com.ems.vanlotusbackend.registro`, separado de Ventas, Inventario, Marketing, etc.

**Lenguaje ubicuo (glosario)**

| Término del negocio | En el código |
|---|---|
| Cuenta de usuario | `CuentaUsuario` |
| Nombre de usuario | `NombreUsuario` |
| Contraseña segura | `Contrasena` |
| Contraseña cifrada | `ContrasenaCifrada` |
| Dueña / Trabajador | `Rol.ADMIN` / `Rol.TRABAJADOR` |
| Registrar una cuenta | `RegistrarUsuario.ejecutar(...)` |
| "Ya está en uso" | `UsuarioYaRegistradoException` |

**Bloques tácticos**

| Elemento DDD | Clase | Responsabilidad |
|---|---|---|
| **Entidad / raíz de agregado** | `domain/CuentaUsuario` | Identidad por nombre de usuario; regla "la primera cuenta es ADMIN" |
| **Value Object** | `domain/NombreUsuario` | Inmutable, igualdad por valor; 3–50 caracteres, sin espacios |
| **Value Object** | `domain/Contrasena` | Política: ≥ 8 caracteres, letras y números |
| **Value Object** | `domain/ContrasenaCifrada` | Lo único que se persiste |
| **Repositorio (puerto)** | `domain/RepositorioCuentas` | El dominio dice *qué* necesita (existe, contar, guardar) |
| **Servicio de dominio (puerto)** | `domain/CifradorContrasenas` | Cifrar sin acoplarse a BCrypt |
| **Servicio de aplicación** | `application/RegistrarUsuario` | Orquesta el caso de uso |
| **Excepciones de dominio** | `NombreUsuarioInvalido`, `ContrasenaInsegura`, `UsuarioYaRegistrado`, `RegistroDeshabilitado` | Reglas violadas, con mensajes del lenguaje ubicuo |
| **Adaptadores (infraestructura)** | `infrastructure/RepositorioCuentasJpa`, `CifradorContrasenasBCrypt`, `RegistroConfig` | Conectan el dominio con JPA/MySQL y Spring Security |

Como el dominio no depende de Spring ni de la base de datos, las pruebas TDD y BDD corren
en milisegundos sin MySQL.

```
registro/
├── domain/          ← reglas del negocio (sin Spring)
├── application/     ← caso de uso RegistrarUsuario
└── infrastructure/  ← JPA, BCrypt, configuración Spring
```

---

## 6. Cómo ejecutarlo

**Backend.** Requiere JDK 17 o 21. Con JDK 23+ Lombok no se procesa y el proyecto no compila.
```bash
mvn test -Dtest='com.ems.vanlotusbackend.registro.**' -Dsurefire.failIfNoSpecifiedTests=false
```
- Reporte HTML de Cucumber: `target/cucumber-report.html`
- `VanlotusbackendApplicationTests` (prueba que ya existía) necesita MySQL en `localhost:3307`; por eso se filtra arriba.

**Frontend**
```bash
npm install
npm test
npm run dev   # luego abrir http://localhost:5173/registro
```

**Endpoint** (sin cambios en el contrato): `POST /api/v1/auth/register` con `{ "username", "password" }`.
- 200 → `{ token, username, message }`
- 400 → nombre inválido, contraseña insegura o usuario duplicado
- 409 → registro público deshabilitado

---

## 7. Observaciones encontradas en los repos

1. **`api.js` del frontend** usa `baseURL: 'http://localhost:8080/api/api'`, pero el backend no
   define `context-path`; las rutas reales son `/api/v1/...`. Así, el login y el registro no llegan
   al backend en local. No se cambió porque afecta a todo el frontend. Conviene revisarlo con el equipo.
2. El frontend está en la versión V0.1.1 y el backend en V1.0. Puede haber más desfases de contrato (ver `SCD.md`).
3. Spring Boot 3.5.4 trae Surefire 3.5.3, que oculta los resultados de Cucumber (sección 4).

---

## Bibliografía
- Beck, K. (2003). *Test-Driven Development: By Example*. Addison-Wesley.
- Evans, E. (2003). *Domain-Driven Design*. Addison-Wesley.
- North, D. (2006). *Introducing BDD*.
- Freeman, S. & Pryce, N. (2009). *Growing Object-Oriented Software, Guided by Tests*.
- Ambler, S. (2012). *Agile Modeling and Development*.
