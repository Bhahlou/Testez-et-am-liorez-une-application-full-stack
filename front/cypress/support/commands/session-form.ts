declare global {
  namespace Cypress {
    interface Chainable {
      getSessionFormNameInput(): Chainable<JQuery<HTMLElement>>;
      getSessionFormDateInput(): Chainable<JQuery<HTMLElement>>;
      getSessionFormTeacherInput(): Chainable<JQuery<HTMLElement>>;
      getSessionFormDescriptionInput(): Chainable<JQuery<HTMLElement>>;
      getSessionFormSaveButton(): Chainable<JQuery<HTMLElement>>;
    }
  }
}

export const nameInput = '[data-cy="name-input"]';
export const dateInput = '[data-cy="date-input"]';
export const teacherInput = '[data-cy="teacher-input"]';
export const descriptionInput = '[data-cy="desc-input"]';
export const saveButton = '[data-cy="save-button"]';

Cypress.Commands.add('getSessionFormNameInput', () => {
  return cy.get(nameInput);
});

Cypress.Commands.add('getSessionFormDateInput', () => {
  return cy.get(dateInput);
});

Cypress.Commands.add('getSessionFormTeacherInput', () => {
  return cy.get(teacherInput);
});

Cypress.Commands.add('getSessionFormDescriptionInput', () => {
  return cy.get(descriptionInput);
});

Cypress.Commands.add('getSessionFormSaveButton', () => {
  return cy.get(saveButton);
});
