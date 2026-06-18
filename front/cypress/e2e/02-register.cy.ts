import { faker } from '@faker-js/faker';

let fakeFirstName: string;
let fakeLastName: string;
let fakeMail: string;
let fakePassword: string;

beforeEach(() => {
  fakeFirstName = faker.person.firstName();
  fakeLastName = faker.person.lastName();
  fakeMail = faker.internet.email();
  fakePassword = faker.internet.password();
});

describe('session list test suite', () => {
  it('should register user with correct form values', () => {
    cy.visit('/register');

    cy.getRegisterFirstNameInput().type(fakeFirstName);
    cy.getRegisterLastNameInput().type(fakeLastName);
    cy.getRegisterEmailInput().type(fakeMail);
    cy.gerRegisterPasswordInput().type(fakePassword);

    cy.getRegisterSubmitButton().should('not.be.disabled').click();

    cy.url().should('include', '/login');

    // Then login should work
    cy.getRegisterEmailInput().type(fakeMail);
    cy.gerRegisterPasswordInput().type(fakePassword);
    cy.getRegisterSubmitButton().should('not.be.disabled').click();

    cy.url().should('include', '/sessions');
  });

  it('should be impossible to submit without filling the form with valid values', () => {
    cy.visit('/register');

    cy.getRegisterFirstNameInput().type(fakeFirstName);
    cy.getRegisterLastNameInput().type(fakeLastName);
    cy.getRegisterEmailInput().type(fakeMail);
    cy.gerRegisterPasswordInput().type(fakePassword);
    cy.getRegisterFirstNameInput().clear().should('have.class', 'ng-invalid');
    cy.getRegisterSubmitButton().should('be.disabled');

    cy.getRegisterFirstNameInput().type(fakeFirstName);
    cy.getRegisterLastNameInput().clear().should('have.class', 'ng-invalid');
    cy.getRegisterSubmitButton().should('be.disabled');

    cy.getRegisterLastNameInput().type(fakeLastName);
    cy.getRegisterEmailInput().clear().should('have.class', 'ng-invalid');
    cy.getRegisterSubmitButton().should('be.disabled');

    cy.getRegisterEmailInput().type(fakeMail);
    cy.gerRegisterPasswordInput().clear().should('have.class', 'ng-invalid');
    cy.getRegisterSubmitButton().should('be.disabled');
  });
});
