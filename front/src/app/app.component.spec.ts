import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { expect, jest } from '@jest/globals';

import { AppComponent } from './app.component';
import { provideRouter, Router } from '@angular/router';
import { routes } from './app.routes';
import { SessionService } from './core/service/session.service';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatToolbarModule, AppComponent],
      declarations: [],
      providers: [provideHttpClient(), provideRouter(routes)],
    }).compileComponents();
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should check if authenticated', () => {
    const sessionService = TestBed.inject(SessionService);
    const isLoggedSpy = jest.spyOn(sessionService, '$isLogged');

    component.$isLogged();
    expect(isLoggedSpy).toHaveBeenCalled();
  });

  it('should navigate to home when logging out', () => {
    const sessionService = TestBed.inject(SessionService);
    const router = TestBed.inject(Router);

    const logOutSpy = jest.spyOn(sessionService, 'logOut');
    const routerSpy = jest.spyOn(router, 'navigate');

    component.logout();

    expect(logOutSpy).toHaveBeenCalled();
    expect(sessionService.isLogged).toBeFalsy();
    expect(routerSpy).toHaveBeenCalledWith(['']);
  });
});
