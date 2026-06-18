declare global {
  namespace Cypress {
    interface Chainable {
      getMeRouterLink(): Chainable<JQuery<HTMLElement>>;
      getMeName(): Chainable<JQuery<HTMLElement>>;
      getMeMail(): Chainable<JQuery<HTMLElement>>;
      getMeDeleteButton(): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export const meRouterLink = '[routerLink="me"]';
export const meName = '[datatest-id="name"]';
export const meMail = '[datatest-id="mail"]';
export const meDeleteButton = '[datatest-id="delete-button"]';

Cypress.Commands.add('getMeRouterLink', () => {
  return cy.get(meRouterLink);
});

Cypress.Commands.add('getMeName', () => {
  return cy.get(meName);
});

Cypress.Commands.add('getMeMail', () => {
  cy.get(meMail);
});

Cypress.Commands.add('getMeDeleteButton', () => {
  cy.get(meDeleteButton);
});
