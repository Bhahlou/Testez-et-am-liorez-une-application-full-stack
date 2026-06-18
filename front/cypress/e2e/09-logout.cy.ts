describe('session create test suite', () => {
  it('should display account information without delete button for an admin', () => {
    cy.loginAsAdmin();

    cy.get('[data-cy="logout"]').click();
    cy.url().should('contain', '/login');
  });
});
