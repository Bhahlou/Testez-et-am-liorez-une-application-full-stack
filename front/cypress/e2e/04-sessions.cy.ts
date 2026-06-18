describe('template spec', () => {
  it('should display the create button and edit button for an admin', () => {
    cy.loginAsAdmin();

    cy.url().should('contain', '/sessions');

    cy.getSessionListCreateButton().should('be.visible');
    cy.getSessionListCards().each((card) => {
      cy.wrap(card).findSessionCardUpdateButton().should('be.visible');
    });
  });

  it('should not display the create button and edit button for an non admin user', () => {
    cy.loginAsUser();
    cy.url().should('contain', '/sessions');

    cy.getSessionListCreateButton().should('not.exist');
    cy.getSessionListCards().each((card) => {
      cy.wrap(card).findSessionCardUpdateButton().should('not.exist');
    });
  });

  it('should display the list of sessions with correct information', () => {
    cy.loginAsAdmin();

    cy.getSessionListCards().should('have.length', 2);

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardTitle().should('have.text', 'Session 1');
        cy.getSessionCardDate().should('contain.text', 'January 1, 2026');
        cy.getSessionCardDescription().should(
          'contain.text',
          'Ceci est la description de la session 1',
        );
        cy.getSessionCardDetailButton()
          .should('be.visible')
          .should('not.be.disabled');
      });

    cy.getSessionListCards()
      .eq(1)
      .within(() => {
        cy.getSessionCardTitle().should('have.text', 'Session 2');
        cy.getSessionCardDate().should('contain.text', 'February 1, 2026');
        cy.getSessionCardDescription().should(
          'contain.text',
          'Ceci est la description de la session 2',
        );
        cy.getSessionCardDetailButton()
          .should('be.visible')
          .should('not.be.disabled');
      });
  });
});
