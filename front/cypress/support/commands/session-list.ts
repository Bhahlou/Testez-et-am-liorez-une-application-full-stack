declare global {
  namespace Cypress {
    interface Chainable {
      getSessionListCreateButton(): Chainable<JQuery<HTMLElement>>;
      getSessionListCards(): Chainable<JQuery<HTMLElement>>;
      findSessionCardUpdateButton(): Chainable<JQuery<HTMLElement>>;
      getSessionCardTitle(): Chainable<JQuery<HTMLElement>>;
      getSessionCardDate(): Chainable<JQuery<HTMLElement>>;
      getSessionCardDescription(): Chainable<JQuery<HTMLElement>>;
      getSessionCardDetailButton(): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export const createButton = '[data-cy="create-button"]';
export const sessionCards = '[data-testid="session-card"]';
export const updateButton = '[data-testid="update-button"]';
export const sessionTitle = '[data-testid="session-title"]';
export const sessionDate = '[data-testid="session-date"]';
export const sessionDescription = '[data-testid="session-description"]';
export const detailButton = '[data-testid="detail-button"]';

Cypress.Commands.add('getSessionListCreateButton', () => {
  return cy.get(createButton);
});

Cypress.Commands.add('getSessionListCards', () => {
  return cy.get(sessionCards);
});
Cypress.Commands.add(
  'findSessionCardUpdateButton',
  { prevSubject: true },
  (subject) => {
    return cy.wrap(subject).find(updateButton);
  },
);

Cypress.Commands.add('getSessionCardTitle', () => {
  return cy.get(sessionTitle);
});

Cypress.Commands.add('getSessionCardDate', () => {
  return cy.get(sessionDate);
});

Cypress.Commands.add('getSessionCardDescription', () => {
  return cy.get(sessionDescription);
});

Cypress.Commands.add('getSessionCardDetailButton', () => {
  return cy.get(detailButton);
});
