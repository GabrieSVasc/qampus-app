import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { By } from '@angular/platform-browser';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;

  let authServiceMock: {
    login: ReturnType<typeof vi.fn>;
  };

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authServiceMock = {
      login: vi.fn(),
    };

    routerMock = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: Router,
          useValue: routerMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should call AuthService login with email and password', async () => {
    authServiceMock.login.mockResolvedValue(true);

    component.email = 'teste@email.com';
    component.password = '123456';

    await component.onSubmit();

    expect(authServiceMock.login).toHaveBeenCalledWith(
      'teste@email.com',
      '123456'
    );
  });

  it('should navigate to home when login is successful', async () => {
    authServiceMock.login.mockResolvedValue(true);

    component.email = 'teste@email.com';
    component.password = '123456';

    await component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['home']);
  });

  it('should show an alert when login fails', async () => {
    authServiceMock.login.mockResolvedValue(false);

    const alertMock = vi
      .spyOn(window, 'alert')
      .mockImplementation(() => { });

    component.email = 'teste@email.com';
    component.password = 'senha-errada';

    await component.onSubmit();

    expect(alertMock).toHaveBeenCalledWith('Email ou Senha Inválidos');

    alertMock.mockRestore();
  });

  it('should navigate to the specified route', () => {
    component.goTo('registrar');

    expect(routerMock.navigate).toHaveBeenCalledWith(['registrar']);
  });

  it('should call onSubmit when form is submitted', () => {
    const onSubmitSpy = vi.spyOn(component, 'onSubmit');

    const form = fixture.nativeElement.querySelector('form');

    form.dispatchEvent(new Event('submit'));

    expect(onSubmitSpy).toHaveBeenCalled();
  });

  it('should navigate to registrar when register button is clicked', () => {
  const goToSpy = vi.spyOn(component, 'goTo');

  const element = fixture.debugElement.query(
    By.css('a')
  );

  element.triggerEventHandler('click');

  expect(goToSpy).toHaveBeenCalledWith('registrar');
});

  it('should update email when input changes', () => {
    const input = fixture.debugElement.query(
      By.css('input[name="email"]')
    ).nativeElement;

    input.value = 'teste@email.com';
    input.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.email).toBe('teste@email.com');
  });

  it('should update email when input changes', () => {
  const input = fixture.debugElement.query(
    By.css('input[name="email"]')
  ).nativeElement;

  input.value = 'teste@email.com';
  input.dispatchEvent(new Event('input'));

  fixture.detectChanges();

  expect(component.email).toBe('teste@email.com');
});

  it('should update password when input changes', () => {
    const input = fixture.debugElement.query(
      By.css('input[name="password"]')
    ).nativeElement;

    input.value = '123456';
    input.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.password).toBe('123456');
  });
});