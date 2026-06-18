describe('session detail test suite', () => {
  it('should display delete button for an admin', () => {
    cy.loginAsAdmin();

    cy.url().should('contains', '/sessions');

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardDetailButton().should('not.be.disabled').click();
      });

    cy.url().should('contain', '/sessions/detail/1');

    cy.getSessionDetailDeleteButton()
      .should('exist')
      .should('be.visible')
      .should('not.be.disabled');
  });

  it('should not display delete button for an non admin user', () => {
    cy.loginAsUser();

    cy.url().should('contains', '/sessions');

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardDetailButton().should('not.be.disabled').click();
      });

    cy.url().should('contain', '/sessions/detail/1');

    cy.getSessionDetailDeleteButton().should('not.exist');
  });

  it('(un)participate button for an non admin user should update attendees', () => {
    cy.loginAsUser();

    cy.url().should('contains', '/sessions');

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardDetailButton().should('not.be.disabled').click();
      });

    cy.url().should('contain', '/sessions/detail/1');
    cy.getSessionDetailUnParticipateButton().should('not.exist');
    cy.getSessionDetailAttendees().should('contain', '0 attendees');

    cy.getSessionDetailParticipateButton()
      .should('exist')
      .should('be.visible')
      .should('not.be.disabled')
      .click();

    cy.getSessionDetailAttendees().should('contain', '1 attendees');
    cy.getSessionDetailUnParticipateButton()
      .should('exist')
      .should('be.visible')
      .should('not.be.disabled')
      .click();

    cy.getSessionDetailAttendees().should('contain', '0 attendees');
    cy.getSessionDetailUnParticipateButton().should('not.exist');
    cy.getSessionDetailParticipateButton()
      .should('exist')
      .should('be.visible')
      .should('not.be.disabled');
  });

  it('should display session detail', () => {
    cy.loginAsUser();

    cy.url().should('contains', '/sessions');

    cy.getSessionListCards()
      .eq(0)
      .within(() => {
        cy.getSessionCardDetailButton().should('not.be.disabled').click();
      });

    cy.url().should('contain', '/sessions/detail/1');

    cy.getSessionDetailTeacherName().should('contain.text', 'Doe JOHN');
    cy.getSessionDetailDate().should('contain.text', 'January 1, 2026');
    cy.getSessionDetailDescription().should(
      'contain.text',
      'Ceci est la description de la session 1',
    );
  });
});
