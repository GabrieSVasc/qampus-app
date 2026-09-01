import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';
import { vi } from 'vitest';

describe('Login + AuthService Integration', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    routerMock = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        AuthService,
        {
          provide: Router,
          useValue: routerMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;

    await fixture.whenStable();

    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('should login through AuthService and navigate to home', async () => {
    const payload = btoa(JSON.stringify({
            sub: '123',
            role: 'STUDENT'
        }));

    const tokenIntegracao = `header.${payload}.signature`;
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ({
        token: tokenIntegracao
      }),
    } as Response);

    component.email = 'usuario@email.com';
    component.password = '123456';

    await component.onSubmit();

    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/login'),
      expect.objectContaining({
        method: 'POST',
      })
    );

    expect(localStorage.getItem('token')).toBe(tokenIntegracao);

    expect(routerMock.navigate).toHaveBeenCalledWith(['home']);
  });

  it('should not navigate to home when AuthService login fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: false,
    } as Response);

    const alertMock = vi
      .spyOn(window, 'alert')
      .mockImplementation(() => {});

    component.email = 'usuario@email.com';
    component.password = 'senha-errada';

    await component.onSubmit();

    expect(localStorage.getItem('token')).toBeNull();

    expect(routerMock.navigate).not.toHaveBeenCalledWith(['home']);

    expect(alertMock).toHaveBeenCalledWith('Email ou Senha Inválidos');

    alertMock.mockRestore();
  });
});