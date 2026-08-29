import { Component, HostListener } from '@angular/core';
import { AuthService } from '../auth/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  menuAberto = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  toggleMenu(): void {
    this.menuAberto = !this.menuAberto;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    if (!target.closest('.user-menu')) {
      this.menuAberto = false;
    }
  }

  isProfessor(): boolean {
    return this.authService.hasRole('PROFESSOR');
  }

  async logout(): Promise<void> {
    const resposta = await this.authService.logout();

    if (resposta) {
      this.router.navigate(['/login']);
    } else {
      alert('Erro ao realizar o logout');
    }
  }

  goTo(rota: string): void {
    this.router.navigate([rota]);
  }
}