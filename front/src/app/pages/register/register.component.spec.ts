import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect, jest } from '@jest/globals';

import { RegisterComponent } from './register.component';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { routes } from 'src/app/app.routes';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { AuthService } from 'src/app/core/service/auth.service';
import { throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let router: Router;
  let debugElement: DebugElement;
  let httpReq: HttpTestingController;
  let authService: AuthService;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [RegisterComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(routes),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
    httpReq = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpReq.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('valid form test suite', () => {
    const getSubmitButton = (): HTMLButtonElement =>
      debugElement.query(By.css('button[type=submit]')).nativeElement;

    it('should disable submit button if all fields are missing', () => {
      expect(getSubmitButton().disabled).toBeTruthy();
    });

    it('should disable the submit button if firstName is missing', () => {
      component.form.setValue({
        email: 'toto@gmail.com',
        firstName: null,
        lastName: 'toto',
        password: 'toto',
      });
      fixture.detectChanges();
      expect(getSubmitButton().disabled).toBeTruthy();
    });

    it('should disable the submit button if lastName is missing', () => {
      component.form.setValue({
        email: 'toto@gmail.com',
        firstName: 'toto',
        lastName: null,
        password: 'toto',
      });
      fixture.detectChanges();
      expect(getSubmitButton().disabled).toBeTruthy();
    });

    it('should disable the submit button if password is missing', () => {
      component.form.setValue({
        email: 'toto@gmail.com',
        firstName: 'toto',
        lastName: 'toto',
        password: null,
      });
      fixture.detectChanges();
      expect(getSubmitButton().disabled).toBeTruthy();
    });

    it('should disable the submit button if mail is invalid', () => {
      component.form.setValue({
        email: 'toto',
        firstName: 'toto',
        lastName: 'toto',
        password: 'toto',
      });
      fixture.detectChanges();
      expect(getSubmitButton().disabled).toBeTruthy();
    });

    it('should enable the submit button if form is valid', () => {
      component.form.setValue({
        email: 'toto@gmail.com',
        firstName: 'toto',
        lastName: 'toto',
        password: 'toto',
      });
      fixture.detectChanges();
      expect(getSubmitButton().disabled).toBeFalsy();
    });
  });

  describe('submit test suite', () => {
    beforeEach(() => {
      component.form.setValue({
        email: 'toto@gmail.com',
        firstName: 'toto',
        lastName: 'toto',
        password: 'toto',
      });
    });
    it('should submit registration and navigate to /login with valid form', () => {
      const spy = jest.spyOn(router, 'navigate');

      component.submit();

      const req = httpReq.expectOne('/api/auth/register');
      req.flush({});

      expect(spy).toHaveBeenCalledWith(['/login']);
    });

    it('shoud show an error if register fails', () => {
      jest
        .spyOn(authService, 'register')
        .mockReturnValue(throwError(() => new Error('any')));

      component.submit();
      fixture.detectChanges();

      expect(component.onError).toBeTruthy();
      const errorMessage: HTMLParagraphElement = debugElement.query(
        By.css('.error'),
      ).nativeElement;
      expect(errorMessage).toBeTruthy();
      expect(errorMessage.textContent).toContain('An error occurred');
    });
  });
});
