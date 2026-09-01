import { ComponentFixture, TestBed } from "@angular/core/testing";
import { Register } from "./register";
import { AuthService } from "../auth-service";
import { Router } from "@angular/router";
import { User } from "../user";


describe('Register + AuthService Integration', () => {
    let component: Register;
    let fixture: ComponentFixture<Register>;
    let routerMock: {
        navigate: ReturnType<typeof vi.fn>;
    }

    beforeEach(async () => {
        routerMock = {
            navigate: vi.fn()
        }

        await TestBed.configureTestingModule({
            imports: [Register],
            providers: [
                AuthService,
                {
                    provide: Router,
                    useValue: routerMock,
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(Register);
        component = fixture.componentInstance;

        await fixture.whenStable();

        localStorage.clear();
        vi.resetAllMocks();
    });

    it('should register through AuthService and navigate to home', async () => {
        const payload = btoa(JSON.stringify({
            sub: '123',
            role: 'STUDENT'
        }));

        const tokenIntegracao = `header.${payload}.signature`;

        const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
            ok: true,
            json: async () => ({
                token: tokenIntegracao
            })
        } as Response);


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
        expect(fetchMock).toHaveBeenCalledWith(
            expect.stringContaining('/register'),
            expect.objectContaining({
                method: 'POST',
            })
        );

        expect(localStorage.getItem('token')).toBe(tokenIntegracao);
        expect(routerMock.navigate).toHaveBeenCalledWith(['home']);
    })

    it('should not navigate to home when AuthService register fails', async () => {
        vi.spyOn(globalThis, 'fetch').mockResolvedValue({
            ok: false,
        } as Response);

        const alertMock = vi
            .spyOn(window, 'alert')
            .mockImplementation(() => { });

        component.registerForm.setValue({
            name: "Nome de Teste",
            email: "repetido@email.com",
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

        expect(localStorage.getItem('token')).toBeNull();

        expect(routerMock.navigate).not.toHaveBeenCalledWith(['home']);

        expect(alertMock).toHaveBeenCalledWith("Já existe uma conta cadastrada com este email.")
        alertMock.mockRestore();
    })
})