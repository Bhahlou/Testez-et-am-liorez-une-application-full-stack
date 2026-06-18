declare global {
  namespace Cypress {
    interface Chainable {
      getRegisterFirstNameInput(): Chainable<JQuery<HTMLElement>>;
      getRegisterLastNameInput(): Chainable<JQuery<HTMLElement>>;
      getRegisterEmailInput(): Chainable<JQuery<HTMLElement>>;
      gerRegisterPasswordInput(): Chainable<JQuery<HTMLElement>>;
      getRegisterSubmitButton(): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export const firstNameInput = '[data-cy="firstName-input"]';
export const lastNameInput = '[data-cy="lastName-input"]';
export const emailInput = '[data-cy="email-input"]';
export const passwordInput = '[data-cy="password-input"]';
export const submitButton = '[data-cy="submit-button"]';

Cypress.Commands.add('getRegisterFirstNameInput', () => {
  return cy.get(firstNameInput);
});

Cypress.Commands.add('getRegisterLastNameInput', () => {
  return cy.get(lastNameInput);
});
Cypress.Commands.add('getRegisterEmailInput', () => {
  return cy.get(emailInput);
});

Cypress.Commands.add('gerRegisterPasswordInput', () => {
  return cy.get(passwordInput);
});

Cypress.Commands.add('getRegisterSubmitButton', () => {
  return cy.get(submitButton);
});

function login(email: string, password: string): void {
  cy.visit('/login');

  cy.getRegisterEmailInput().type(email);
  cy.gerRegisterPasswordInput().type(password);

  cy.getRegisterSubmitButton().should('not.be.disabled').click();
}

Cypress.Commands.add('loginAsAdmin', () => {
  login('yoga@studio.com', 'test!1234');
});

Cypress.Commands.add('loginAsUser', () => {
  login('john@doe.com', 'test!1234');
});
