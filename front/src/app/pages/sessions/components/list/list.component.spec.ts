import { HttpClientModule, provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect, jest } from '@jest/globals';
import { SessionService } from 'src/app/core/service/session.service';

import { ListComponent } from './list.component';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { routes } from 'src/app/app.routes';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Session } from 'src/app/core/models/session.interface';
import { SessionApiService } from 'src/app/core/service/session-api.service';
import { of } from 'rxjs';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;
  let debugElement: DebugElement;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
    },
  };
  const mockedSessions: Session[] = [
    {
      description: 'description',
      date: new Date(),
      name: 'name',
      teacher_id: 2,
      users: [],
      createdAt: new Date(),
      id: 2,
      updatedAt: new Date(),
    },
    {
      description: 'description',
      date: new Date(),
      name: 'name',
      teacher_id: 2,
      users: [],
      createdAt: new Date(),
      id: 2,
      updatedAt: new Date(),
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [MatCardModule, MatIconModule, ListComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(routes),
      ],
    }).compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);
    jest.spyOn(sessionApiService, 'all').mockReturnValue(of(mockedSessions));

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the session list', () => {
    const sessionCards = debugElement.queryAll(
      By.css('[data-testid="session-card"]'),
    );
    expect(sessionCards.length).toBe(2);
  });

  it('should display the session informations', () => {
    const sessionTitleElements = debugElement.queryAll(
      By.css('[data-testid="session-title"]'),
    );
    const sessionTitles = sessionTitleElements.map(
      (ste) => ste.nativeElement.textContent,
    );
    const expectedTitles = mockedSessions.map((s) => s.name);
    expect(sessionTitles).toEqual(expectedTitles);

    const sessionDescriptionElements = debugElement.queryAll(
      By.css('[data-testid="session-description"]'),
    );
    const sessionDescriptions = sessionDescriptionElements.map((ste) =>
      ste.nativeElement.textContent.trim(),
    );
    const expectedDescriptions = mockedSessions.map((s) => s.description);
    expect(sessionDescriptions).toEqual(expectedDescriptions);
  });

  it('should display only detail button for a non-admin user', () => {
    mockSessionService.sessionInformation.admin = false;
    fixture.detectChanges();

    const detailButton: HTMLButtonElement = debugElement.query(
      By.css('[data-testid="detail-button"]'),
    ).nativeElement;

    expect(detailButton).toBeDefined();

    const updateButton = debugElement.query(
      By.css('[data-testid="update-button"]'),
    );

    expect(updateButton).toBeNull();
  });

  it('should display detail and update button for an admin user', () => {
    mockSessionService.sessionInformation.admin = true;
    fixture.detectChanges();

    const detailButton: HTMLButtonElement = debugElement.query(
      By.css('[data-testid="detail-button"]'),
    ).nativeElement;

    expect(detailButton).toBeDefined();

    const updateButton: HTMLButtonElement = debugElement.query(
      By.css('[data-testid="update-button"]'),
    ).nativeElement;

    expect(updateButton).toBeDefined();
  });
});
