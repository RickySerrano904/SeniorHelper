describe('Appointment creation/deletion flow', () => {
  beforeEach(() => {
    cy.visit('/login', {
      onBeforeLoad: (win) => {
        win.localStorage.clear();
        win.sessionStorage.clear();
      }
    });
  });

  it('logs in as JohnSenior, creates an appointment, and logs out', () => {
    cy.get('[data-cy="username"]').type('JohnSenior');
    cy.get('[data-cy="password"]').type('password');
    cy.get('[data-cy="submit-login"]').click();

    cy.url().should('include', '/home');

    cy.get('a[routerlink="/calendar"]:visible').first().click();
    cy.url().should('include', '/calendar');

    cy.get('button.add-appointment-btn').click();

    cy.get('input[formcontrolname="start"]').invoke('val').should('match', /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/);
    cy.get('input[formcontrolname="end"]').invoke('val').should('match', /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/);

    cy.get('input[formcontrolname="title"]').type('Title');
    cy.get('textarea[formcontrolname="notes"]').type('Notes');
    cy.get('input[formcontrolname="location"]').type('Place');

    cy.get('button.btn-submit').click();

    cy.get('.event-title', { timeout: 10000 }).contains('Title').should('be.visible');

    cy.get('button.toggle-delete-btn').contains('Delete Appointment').click();
    cy.get('.event-title').contains('Title').first().click();
    cy.get('button.toggle-delete-btn').contains('Exit Delete Mode').click();

    cy.get('button.logout-button').click();
    cy.url().should('include', '/login');
    cy.get('[data-cy="submit-login"]').should('be.visible');
  });
});