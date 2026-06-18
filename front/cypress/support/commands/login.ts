declare global {
  namespace Cypress {
    interface Chainable {
      loginAsAdmin(): Chainable<void>;
      loginAsUser(): Chainable<void>;
      getLoginEmailInput(): Chainable<JQuery<HTMLElement>>;
      getLoginPasswordInput(): Chainable<JQuery<HTMLElement>>;
      getLoginSubmitButton(): Chainable<JQuery<HTMLElement>>;
    }
  }
}
// Login
export const emailInput = '[data-cy="email-input"]';
export const passwordInput = '[data-cy="password-input"]';
export const submitButton = '[data-cy="submit-button"]';

Cypress.Commands.add('getLoginEmailInput', () => {
  return cy.get(emailInput);
});

Cypress.Commands.add('getLoginPasswordInput', () => {
  return cy.get(passwordInput);
});

Cypress.Commands.add('getLoginSubmitButton', () => {
  return cy.get(submitButton);
});

function login(email: string, password: string): void {
  cy.visit('/login');

  cy.getLoginEmailInput().type(email);
  cy.getLoginPasswordInput().type(password);

  cy.getLoginSubmitButton().should('not.be.disabled').click();
}

Cypress.Commands.add('loginAsAdmin', () => {
  login('yoga@studio.com', 'test!1234');
});

Cypress.Commands.add('loginAsUser', () => {
  login('john@doe.com', 'test!1234');
});
