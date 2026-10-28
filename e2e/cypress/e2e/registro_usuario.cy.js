// E2E — Historia: registro de usuario (mismos criterios CA-01 y CA-02 del Gherkin del backend).
// El backend se simula con cy.intercept: la prueba verifica la pantalla real del front sin necesitar base de datos.

const URL_REGISTRO = '**/v1/auth/register';

const llenarFormulario = (usuario, contrasena, confirmacion = contrasena) => {
  cy.get('#username').type(usuario);
  cy.get('#password').type(contrasena);
  cy.get('#confirmacion').type(confirmacion);
  cy.contains('button', 'Registrarme').click();
};

describe('Registro de usuario', () => {
  beforeEach(() => {
    cy.visit('/registro');
  });

  it('CA-01: con datos válidos crea la cuenta y entra al sistema', () => {
    cy.intercept('POST', URL_REGISTRO, {
      statusCode: 200,
      body: { token: 'token-de-prueba', username: 'lucia.vargas', message: 'Usuario registrado exitosamente' },
    }).as('registro');
    // El dashboard pide datos al cargar: se responden vacíos para aislar la prueba del registro
    cy.intercept('GET', '**/v1/**', { statusCode: 200, body: [] });

    llenarFormulario('lucia.vargas', 'Joyas2026');

    cy.wait('@registro').its('request.body').should('deep.equal', {
      username: 'lucia.vargas',
      password: 'Joyas2026',
    });
    cy.contains('Cuenta creada').should('be.visible');
    cy.contains('button', 'OK').click();
    cy.location('pathname').should('eq', '/dashboard');
  });

  it('CA-02: si el nombre de usuario ya existe, muestra el mensaje y no entra', () => {
    cy.intercept('POST', URL_REGISTRO, {
      statusCode: 400,
      body: { code: 'BAD_REQUEST', message: 'El nombre de usuario ya está en uso' },
    }).as('registro');

    llenarFormulario('admin', 'Joyas2026');

    cy.wait('@registro');
    cy.contains('El nombre de usuario ya está en uso').should('be.visible');
    cy.location('pathname').should('eq', '/registro');
  });

  it('Contraseña sin números: muestra el error y no llama al servidor', () => {
    cy.intercept('POST', URL_REGISTRO).as('registro');

    llenarFormulario('lucia.vargas', 'soloLetras');

    cy.contains('La contraseña debe contener al menos una letra y un número').should('be.visible');
    cy.get('@registro.all').should('have.length', 0);
  });

  it('Confirmación distinta: muestra el error y no llama al servidor', () => {
    cy.intercept('POST', URL_REGISTRO).as('registro');

    llenarFormulario('lucia.vargas', 'Joyas2026', 'Joyas2027');

    cy.contains('Las contraseñas no coinciden').should('be.visible');
    cy.get('@registro.all').should('have.length', 0);
  });
});
