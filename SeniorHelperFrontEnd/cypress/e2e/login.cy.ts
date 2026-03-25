describe('Login', () => {
  beforeEach(() => {
    cy.visit('/login', {
      onBeforeLoad: (win) => {
        win.localStorage.clear();
        win.sessionStorage.clear();
      }
    });
  });

  it('logs in with any non-empty username/password (mock login)', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: {
        token: 'mock-token-123',
        message: 'Signed in successfully.'
      }
    }).as('loginRequest');

    cy.intercept('GET', '**/api/appointments/me', {
      statusCode: 200,
      body: []
    });

    cy.get('[data-cy="login-title"]').should('contain', 'Senior Helper');
    cy.get('[data-cy="username"]').type('student');
    cy.get('[data-cy="password"]').type('password123');
    cy.get('[data-cy="submit-login"]').click();

    cy.wait('@loginRequest');
    cy.url().should('not.include', '/login');
  });

  it('shows an error if username/password are empty', () => {
    cy.get('form.login-form').submit();
    cy.get('.error').should('be.visible');
  });
});
