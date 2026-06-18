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

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardDetailButton().click();
      });

    cy.getSessionDetailDeleteButton().click();
    cy.url().should('contain', '/sessions');

    cy.getSessionListCards().should('have.length', 2);
  });
});
