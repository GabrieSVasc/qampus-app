import { ComponentFixture, TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { Register } from './register';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';
import { User } from '../user';

describe('Register', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let authServiceMock: {
    register: ReturnType<typeof vi.fn>;
  }
  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  }

  beforeEach(async () => {
    authServiceMock = {
      register: vi.fn(),
    };

    routerMock = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock
        },
        {
          provide: Router,
          useValue: routerMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
  });

  it('should call AuthService register with the new user', async () => {
    authServiceMock.register.mockResolvedValue(true);
    component.registerForm.setValue({
      name: "Nome de Teste",
      email: "teste@email.com",
      password: "senha123",
      confirmPassword: "senha123",
      role: "STUDENT"
    })
    await component.submit();
    const user: User = {
      name: "Nome de Teste",
      email: "teste@email.com",
      password: "senha123",
      role: "STUDENT"
    }
    expect(authServiceMock.register).toHaveBeenCalledWith(user);
  })

  it('should show an alert when register fails', async () => {
    authServiceMock.register.mockResolvedValue(false);
    const alertMock = vi
      .spyOn(window, 'alert')
      .mockImplementation(() => { });

    component.registerForm.setValue({
      name: "Nome de Teste",
      email: "emailRepetido@email.com",
      password: "senha123",
      confirmPassword: "senha123",
      role: "STUDENT"
    })

    await component.submit();
    expect(alertMock).toHaveBeenCalledWith('Já existe uma conta cadastrada com este email.');
    alertMock.mockRestore();
  })

  it('should navigate to the route', () => {
    component.goTo("login");
    expect(routerMock.navigate).toHaveBeenCalledWith(['login']);
  })

  it('should show name error when name is empty and submit was clicked', () => {
    component.submitClicked = true;
    component.registerForm.controls.name.setValue('');

    fixture.detectChanges();

    const alert = fixture.nativeElement.querySelector(
      '.alert'
    ) as HTMLElement;

    expect(alert).not.toBeNull();
    expect(alert.textContent).toContain('Digite o nome');
  });
});