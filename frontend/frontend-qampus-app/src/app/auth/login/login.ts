import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onSubmit() {
    const sucesso = await this.authService.login(this.email, this.password);
    if(sucesso){
      if(this.authService.getRole() == 'STUDENT'){
        this.router.navigate(["home"]);
      }else if(this.authService.getRole()=='PROFESSOR'){
        this.router.navigate(["anuncios"]);
      }
    }else{
      alert("Email ou Senha Inválidos");
    }
  }
  goTo(rota: string){
    this.router.navigate([rota]);
  }
}