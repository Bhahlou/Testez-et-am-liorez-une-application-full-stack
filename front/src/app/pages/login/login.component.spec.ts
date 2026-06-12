import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { expect, jest } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from 'src/app/core/service/auth.service';
import { SessionInformation } from 'src/app/core/models/sessionInformation.interface';
import { SessionService } from 'src/app/core/service/session.service';
import { routes } from 'src/app/app.routes';
import { LoginComponent } from './login.component';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

const mockSessionInfo: SessionInformation = {
  token: 'fake-token',
  type: 'Bearer',
  id: 1,
  username: 'testuser',
  firstName: 'John',
  lastName: 'Doe',
  admin: false,
};

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;
  let sessionService: SessionService;
  let debugElement: DebugElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, NoopAnimationsModule],
      providers: [
        SessionService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(routes),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    debugElement = fixture.debugElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('form validation', () => {
    const submitButtonSelector = 'button[type=submit]';

    it('should disable submit button when both fields are empty', () => {
      const submitButton: HTMLButtonElement = debugElement.query(
        By.css(submitButtonSelector),
      ).nativeElement;
      expect(submitButton.disabled).toBeTruthy();
    });

    it('should disable submit button when only email is filled', () => {
      component.form.setValue({ email: 'test@test.com', password: '' });
      fixture.detectChanges();
      const button: HTMLButtonElement = debugElement.query(
        By.css(submitButtonSelector),
      ).nativeElement;
      expect(button.disabled).toBeTruthy();
    });

    it('should disable submit button when only password is filled', () => {
      component.form.setValue({ email: '', password: 'password123' });
      fixture.detectChanges();
      const button: HTMLButtonElement = debugElement.query(
        By.css(submitButtonSelector),
      ).nativeElement;
      expect(button.disabled).toBeTruthy();
    });

    it('should enable submit button when form is valid', () => {
      component.form.setValue({
        email: 'test@test.com',
        password: 'password123',
      });
      fixture.detectChanges();
      const button: HTMLButtonElement = debugElement.query(
        By.css(submitButtonSelector),
      ).nativeElement;
      expect(button.disabled).toBeFalsy();
    });
  });

  describe('submit', () => {
    it('should navigate to /sessions and login to session on successful login', () => {
      jest.spyOn(authService, 'login').mockReturnValue(of(mockSessionInfo));
      const navigateSpy = jest.spyOn(router, 'navigate');
      const sessionSpy = jest.spyOn(sessionService, 'logIn');

      component.form.setValue({
        email: 'test@test.com',
        password: 'password123',
      });
      component.submit();

      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
      expect(sessionSpy).toHaveBeenCalledWith(mockSessionInfo);
    });

    it('should set onError to true on failed login', () => {
      jest
        .spyOn(authService, 'login')
        .mockReturnValue(throwError(() => new Error('Unauthorized')));

      component.form.setValue({
        email: 'test@test.com',
        password: 'wrongpassword',
      });
      component.submit();

      expect(component.onError).toBe(true);
    });

    it('should display error message when login fails', () => {
      jest
        .spyOn(authService, 'login')
        .mockReturnValue(throwError(() => new Error('Unauthorized')));

      component.form.setValue({
        email: 'test@test.com',
        password: 'wrongpassword',
      });
      component.submit();
      fixture.detectChanges();

      const errorMessage = debugElement.query(By.css('.error')).nativeElement;
      expect(errorMessage).toBeTruthy();
      expect(errorMessage.textContent).toContain('An error occurred');
    });
  });
});
