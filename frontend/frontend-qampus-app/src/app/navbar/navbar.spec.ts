import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthService } from '../auth/auth-service';
import { Router } from '@angular/router';
import { Navbar } from './navbar';
import { vi } from 'vitest';

describe('Navbar', () => {
  let component: Navbar;
  let fixture: ComponentFixture<Navbar>;

  let authServiceMock: {
    logout: ReturnType<typeof vi.fn>;
    hasRole: ReturnType<typeof vi.fn>;
  };

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authServiceMock = {
      logout: vi.fn(),
      hasRole: vi.fn().mockReturnValue(false),
    };

    routerMock = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Navbar],
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

    fixture = TestBed.createComponent(Navbar);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should logout and navigate to login', async () => {
    authServiceMock.logout.mockResolvedValue(true);

    await component.logout();

    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to the route', () => {
    component.goTo('home');

    expect(routerMock.navigate).toHaveBeenCalledWith(['home']);
  });

  it('should show alert when logout fails', async () => {
    authServiceMock.logout.mockResolvedValue(false);

    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});

    await component.logout();

    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(alertSpy).toHaveBeenCalledWith(
      'Erro ao realizar o logout'
    );

    alertSpy.mockRestore();
  });
});