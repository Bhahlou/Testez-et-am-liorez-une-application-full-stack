import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { expect, jest } from '@jest/globals';
import { SessionService } from '../../../../core/service/session.service';

import { DetailComponent } from './detail.component';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { routes } from 'src/app/app.routes';
import { Session } from 'src/app/core/models/session.interface';
import { Teacher } from 'src/app/core/models/teacher.interface';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let debugElement: DebugElement;
  let httpReq: HttpTestingController;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
    isLogged: true,
  };

  const mockSession: Session = {
    date: new Date(2026, 6, 11, 20, 30),
    description: 'description',
    name: 'name',
    teacher_id: 1,
    users: [1],
    createdAt: new Date(2026, 6, 9, 20, 30),
    id: 2,
    updatedAt: new Date(2026, 6, 10, 20, 30),
  };

  const mockTeacher: Teacher = {
    firstName: 'firstName',
    lastName: 'lastName',
    id: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('2'),
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatSnackBarModule, ReactiveFormsModule, DetailComponent],
      declarations: [],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(routes),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();
    httpReq = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display session detail', () => {
    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    httpReq.verify();

    fixture.detectChanges();

    const titleElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-name"]'),
    ).nativeElement;
    expect(titleElement.textContent).toEqual('Name');

    const teacherElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="teacher-name"]'),
    ).nativeElement;
    expect(teacherElement.textContent).toEqual('firstName LASTNAME');

    const descriptionElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-description"]'),
    ).nativeElement;
    expect(descriptionElement.textContent).toEqual('Description: description ');

    const attendeesElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-attendees"]'),
    ).nativeElement;
    expect(attendeesElement.textContent).toEqual('1 attendees');

    const sessionDateElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-date"]'),
    ).nativeElement;
    expect(sessionDateElement.textContent).toEqual('July 11, 2026');

    const creationDateElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-creation-date"]'),
    ).nativeElement;
    expect(creationDateElement.textContent).toContain('July 9, 2026');

    const lastUpdateElement: HTMLTextAreaElement = debugElement.query(
      By.css('[data-testid="session-last-update"]'),
    ).nativeElement;
    expect(lastUpdateElement.textContent).toContain('July 10, 2026');
  });

  it('should display delete button if user is admin', () => {
    mockSessionService.sessionInformation.admin = true;

    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    fixture.detectChanges();

    const deleteButton: HTMLButtonElement = debugElement.query(
      By.css('[data-testid="delete-button"]'),
    ).nativeElement;
    expect(deleteButton).toBeDefined();
  });

  it('should not display delete button if user is not admin', () => {
    component.isAdmin = false;

    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    fixture.detectChanges();

    const deleteButton = debugElement.query(
      By.css('[data-testid="delete-button"]'),
    );
    expect(deleteButton).toBeNull();
  });

  it('should delete session and navigate to session list on delete', () => {
    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    const snackbarSpy = jest.spyOn(MatSnackBar.prototype, 'open');
    const routerSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.delete();
    const deleteReq = httpReq.expectOne('api/session/2');
    deleteReq.flush('2');
    fixture.detectChanges();

    expect(snackbarSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
    httpReq.verify();
  });

  it('should remove participation and refetch session on unparticipate click', () => {
    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    component.unParticipate();
    const unparticipateReq = httpReq.expectOne('api/session/2/participate/1');
    unparticipateReq.flush({});
    fixture.detectChanges();

    const fetchReq = httpReq.expectOne('api/session/2');
    fetchReq.flush({});

    httpReq.verify();
  });

  it('should add participation and refetch session on participate click', () => {
    mockSession.users = [];

    const sessionReq = httpReq.expectOne('api/session/2');
    sessionReq.flush(mockSession);

    const teacherReq = httpReq.expectOne('api/teacher/1');
    teacherReq.flush(mockTeacher);

    component.participate();
    const unparticipateReq = httpReq.expectOne('api/session/2/participate/1');
    unparticipateReq.flush({});
    fixture.detectChanges();

    const fetchReq = httpReq.expectOne('api/session/2');
    fetchReq.flush({});

    httpReq.verify();
  });
});
