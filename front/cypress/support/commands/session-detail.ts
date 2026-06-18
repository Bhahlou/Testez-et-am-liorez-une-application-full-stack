declare global {
  namespace Cypress {
    interface Chainable {
      getSessionDetailDeleteButton(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailUnParticipateButton(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailParticipateButton(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailAttendees(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailTeacherName(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailDate(): Chainable<JQuery<HTMLElement>>;
      getSessionDetailDescription(): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export const deleteButton = '[data-cy="delete-button"]';
export const unparticipateButton = '[data-cy="unparticipate-button"]';
export const participateButton = '[data-cy="participate-button"]';
export const sessionAttendees = '[data-testid="session-attendees"]';
export const sessionTeacherName = '[data-testid="teacher-name"]';
export const sessionDate = '[data-testid="session-date"]';
export const sessionDescription = '[data-testid="session-description"]';

Cypress.Commands.add('getSessionDetailDeleteButton', () => {
  return cy.get(deleteButton);
});

Cypress.Commands.add('getSessionDetailUnParticipateButton', () => {
  return cy.get(unparticipateButton);
});

Cypress.Commands.add('getSessionDetailParticipateButton', () => {
  return cy.get(participateButton);
});

Cypress.Commands.add('getSessionDetailAttendees', () => {
  return cy.get(sessionAttendees);
});

Cypress.Commands.add('getSessionDetailTeacherName', () => {
  return cy.get(sessionTeacherName);
});

Cypress.Commands.add('getSessionDetailDate', () => {
  return cy.get(sessionDate);
});

Cypress.Commands.add('getSessionDetailDescription', () => {
  return cy.get(sessionDescription);
});
