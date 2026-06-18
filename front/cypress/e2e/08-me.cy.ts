describe('session create test suite', () => {
  it('should display account information without delete button for an admin', () => {
    cy.loginAsAdmin();

    cy.getMeRouterLink().click();
    cy.url().should('contain', '/me');

    cy.getMeName().should('contain', 'Admin ADMIN');
    cy.getMeMail().should('contain', 'yoga@studio.com');

    cy.getMeDeleteButton().should('not.exist');
  });

  it('should display account information with delete button for a non admin', () => {
    cy.loginAsUser();

    cy.getMeRouterLink().click();
    cy.url().should('contain', '/me');

    cy.getMeName().should('contain', 'Doe JOHN');
    cy.getMeMail().should('contain', 'john@doe.com');

    cy.getMeDeleteButton().should('exist').click();

    cy.url().should('contain', '/login');
  });
});
