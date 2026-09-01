import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { User } from '../user';
import { AuthService } from '../auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  constructor(
    private authService: AuthService,
    private router: Router
  ) { }
  submitClicked: boolean = false;
  registerForm = new FormGroup({
    name: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
    confirmPassword: new FormControl('', Validators.required),
    role: new FormControl('', Validators.required)
  })
  goTo(rota: string) {
    this.router.navigate([rota])
  }
  async submit() {
    this.submitClicked = true;
    if (this.registerForm.valid) {
      const user: User = {
        name: this.registerForm.value.name!,
        email: this.registerForm.value.email!,
        password: this.registerForm.value.password!,
        role: this.registerForm.value.role!
      }
      const response = await this.authService.register(user);
      if (response) {
        if (this.authService.getRole() == 'STUDENT') {
          this.router.navigate(["home"]);
        } else if (this.authService.getRole() == 'PROFESSOR') {
          this.router.navigate(["anuncios"]);
        }
      } else {
        alert("Já existe uma conta cadastrada com este email.");
      }
    }
  }
}
