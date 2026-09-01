import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-unauthorized',
  imports: [],
  templateUrl: './unauthorized.html',
  styleUrl: './unauthorized.css',
})
export class Unauthorized {
  constructor(
    private router: Router,
    private authService: AuthService
  ){}
  goTo(rota: string){
    this.router.navigate([rota]);
  }
  home(){
    if(this.authService.getRole()=='PROFESSOR'){
      this.router.navigate(['anuncios']);
    }else if(this.authService.getRole()=='STUDENT'){
      this.router.navigate(['home'])
    }
  }
}
