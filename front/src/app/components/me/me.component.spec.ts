import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionService } from 'src/app/core/service/session.service';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { MaterialModule } from 'src/app/shared/material.module';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let debugElement: DebugElement;
  let httpCtrl: HttpTestingController;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [],
      imports: [MaterialModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    httpCtrl = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MeComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the user details', () => {
    httpCtrl.expectOne('api/user/1').flush({
      firstName: 'firstName',
      lastName: 'lastName',
      email: 'email@test.com',
    });

    fixture.detectChanges();

    const nameTextArea: HTMLParagraphElement = debugElement.query(
      By.css('[datatest-id="name"]'),
    ).nativeElement;
    expect(nameTextArea.textContent?.trim()).toEqual(
      'Name: firstName LASTNAME',
    );

    const mailTextArea: HTMLParagraphElement = debugElement.query(
      By.css('[datatest-id="mail"]'),
    ).nativeElement;
    expect(mailTextArea.textContent?.trim()).toEqual('Email: email@test.com');
  });
});
