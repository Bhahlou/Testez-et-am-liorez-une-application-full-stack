describe('Login spec', () => {
  it('should login successfully with correct credentials', () => {
    cy.loginAsUser();
    cy.url().should('include', '/sessions');
  });

  it('should disable the submit button if field is missing', () => {
    cy.visit('/login');

    cy.getLoginSubmitButton().should('be.disabled');

    cy.getLoginEmailInput().type('yoga@studio.com');
    cy.getLoginSubmitButton().should('be.disabled');

    cy.getLoginEmailInput().clear();
    cy.getLoginPasswordInput().type('test!1234');
    cy.getLoginEmailInput().should('have.class', 'ng-invalid');
    cy.getLoginSubmitButton().should('be.disabled');
  });

  it('should display an error with incorrect credentials', () => {
    cy.visit('/login');

    cy.getLoginEmailInput().type('incorrectUser@studio.com');
    cy.getLoginPasswordInput().type('incorrectPassword');

    cy.getLoginSubmitButton().should('not.be.disabled').click();

    cy.get('.error')
      .should('be.visible')
      .should('contain.text', 'An error occurred');
  });
});
