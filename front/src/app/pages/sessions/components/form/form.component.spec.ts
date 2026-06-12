import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { expect, jest } from '@jest/globals';
import { SessionService } from 'src/app/core/service/session.service';
import { SessionApiService } from '../../../../core/service/session-api.service';

import { FormComponent } from './form.component';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { routes } from 'src/app/app.routes';
import { MaterialModule } from 'src/app/shared/material.module';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let httpCtrl: HttpTestingController;
  let router: Router;
  let debugElement: DebugElement;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
    },
    isLogged: true,
  };

  // Mutable so each test can configure the id
  const mockActivatedRouteParams: { id: string | null } = { id: null };
  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) =>
          key === 'id' ? mockActivatedRouteParams.id : null,
      },
    },
  };

  beforeEach(async () => {
    mockActivatedRouteParams.id = null;

    await TestBed.configureTestingModule({
      imports: [MaterialModule, FormComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(routes),
        // Must come after provideRouter so it wins in DI resolution
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
      declarations: [],
    }).compileComponents();

    httpCtrl = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create the session when creating using a valid form', () => {
    fixture.detectChanges();
    httpCtrl.expectOne('api/teacher').flush([]);

    component.sessionForm?.setValue({
      name: 'Name',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: 1,
      description: 'Description de la nouvelle session',
    });

    const snackbarSpy = jest
      .spyOn(MatSnackBar.prototype, 'open')
      .mockReturnValue({} as any);
    const routerSpy = jest.spyOn(router, 'navigate');
    component.submit();
    httpCtrl.expectOne('api/session').flush({});

    expect(snackbarSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('should not be possible to submit if fields are missing in form', () => {
    fixture.detectChanges();
    httpCtrl.expectOne('api/teacher').flush([]);

    const submitButton: HTMLButtonElement = debugElement.query(
      By.css('[datatest-id = "submit-button"]'),
    ).nativeElement;

    component.sessionForm?.setValue({
      name: '',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: 1,
      description: 'Description de la nouvelle session',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: 'test',
      date: null,
      teacher_id: 1,
      description: 'Description de la nouvelle session',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: 'test',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: null,
      description: 'Description de la nouvelle session',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: 'test',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: 1,
      description: '',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: 'test',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: 1,
      description: 'Description de la nouvelle session',
    });
    fixture.detectChanges();
    expect(submitButton.disabled).toBeFalsy();
  });

  it('should update the session when updating using a valid form', () => {
    mockActivatedRouteParams.id = '9';
    // Spy on router.url so ngOnInit detects update mode without actual navigation
    jest.spyOn(router, 'url', 'get').mockReturnValue('sessions/update/9');
    fixture.detectChanges(); // ngOnInit runs: sees 'update' in URL, calls detail('9')

    // GET api/session/9 is now pending — flush it with valid data so initForm() works
    httpCtrl.expectOne('api/session/9').flush({
      id: 9,
      name: 'Test Session',
      date: new Date().toISOString(),
      teacher_id: 1,
      description: 'Test description',
      users: [],
    });

    fixture.detectChanges(); // sessionForm is now set, template re-renders and subscribes to teachers$
    httpCtrl.expectOne('api/teacher').flush([]);

    component.sessionForm?.setValue({
      name: 'Updated Name',
      date: new Date(2026, 11, 11, 10, 30),
      teacher_id: 1,
      description: 'Description mise à jour',
    });

    const snackbarSpy = jest
      .spyOn(MatSnackBar.prototype, 'open')
      .mockReturnValue({} as any);
    const routerSpy = jest.spyOn(router, 'navigate');
    component.submit();
    httpCtrl.expectOne({ method: 'PUT', url: 'api/session/9' }).flush({});

    expect(snackbarSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  });
});
