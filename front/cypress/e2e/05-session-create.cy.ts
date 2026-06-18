import { faker } from '@faker-js/faker';

let sessionFakeName: string;
let sessionFakeDate: string;
let sessionFakeDescription: string;

beforeEach(() => {
  sessionFakeDate = faker.date.future().toISOString().split('T')[0];
  sessionFakeDescription = faker.lorem.paragraph();
  sessionFakeName = faker.commerce.productName();
});

describe('session create test suite', () => {
  it('should create a new session with a valid form', () => {
    cy.loginAsAdmin();
    cy.url().should('contains', '/sessions');

    cy.getSessionListCreateButton().should('be.visible').click();
    cy.url().should('contain', '/sessions/create');

    cy.getSessionFormNameInput().type(sessionFakeName);
    cy.getSessionFormDateInput().type(sessionFakeDate);
    cy.getSessionFormTeacherInput().click();
    cy.get('mat-option').eq(2).click();
    cy.getSessionFormDescriptionInput().type(sessionFakeDescription);

    cy.getSessionFormSaveButton().should('not.be.disabled').click();

    cy.url().should('contains', '/sessions');

    cy.getSessionListCards().should('have.length', 3);
  });

  it('should not be possible to submit with a missing field', () => {
    cy.loginAsAdmin();
    cy.url().should('contains', '/sessions');

    cy.getSessionListCreateButton().should('be.visible').click();
    cy.url().should('contain', '/sessions/create');

    cy.getSessionFormNameInput().type(sessionFakeName);
    cy.getSessionFormDateInput().type(sessionFakeDate);
    cy.getSessionFormDescriptionInput().type(sessionFakeDescription);

    cy.getSessionFormSaveButton().should('be.disabled');

    cy.getSessionFormTeacherInput().click();
    cy.get('mat-option').eq(2).click();
    cy.getSessionFormNameInput().clear();
    cy.getSessionFormSaveButton().should('be.disabled');
    cy.getSessionFormNameInput().type(sessionFakeName);

    cy.getSessionFormDateInput().clear();
    cy.getSessionFormSaveButton().should('be.disabled');
    cy.getSessionFormDateInput().type(sessionFakeDate);

    cy.getSessionFormDescriptionInput().clear();
    cy.getSessionFormSaveButton().should('be.disabled');
    cy.getSessionFormDescriptionInput().type(sessionFakeDescription);

    cy.getSessionFormSaveButton().should('not.be.disabled');
  });
});
