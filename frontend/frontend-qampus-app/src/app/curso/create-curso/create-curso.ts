import { Component } from '@angular/core';
import { Navbar } from "../../navbar/navbar";
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CursoService, NewCurso } from '../curso-service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-create-curso',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './create-curso.html',
  styleUrl: './create-curso.css',
})
export class CreateCurso {
  constructor(
    private cursoService: CursoService,
    private router: Router
  ){}

  formCurso = new FormGroup({
    bloco: new FormControl('', Validators.required),
    nome: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email])
  })

  async submit(){
    if(this.formCurso.valid){
      const novoCurso: NewCurso = {
        bloco: this.formCurso.value.bloco!,
        nome: this.formCurso.value.nome!,
        email: this.formCurso.value.email!
      }

      const response = await this.cursoService.createCurso(novoCurso);
      if(response){
        this.router.navigate(['cursos'])
      }else{
        alert("Email deve ser de um professor cadastrado");
      }
    }else{
      if(this.formCurso.get('email')?.hasError('email')){
        alert("Email inválido");
      }else{
        alert("Preencha todos os campos");
      }
    }
  }
}
