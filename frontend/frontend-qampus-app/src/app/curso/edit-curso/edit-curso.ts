import { Component, OnInit } from '@angular/core';
import { Navbar } from "../../navbar/navbar";
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CursoService, Curso } from '../curso-service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-edit-curso',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-curso.html',
  styleUrl: './edit-curso.css',
})
export class EditCurso implements OnInit{
  constructor(
    private route: ActivatedRoute,
    private cursoService: CursoService,
    private router: Router
  ){}

  curso: Curso = {
    id: 0,
    nome: '',
    bloco: '',
    email: ''
  }

  async ngOnInit(){
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if(id){
      const cursoEncontrado = await this.cursoService.getCurso(id);
      if(cursoEncontrado){
        this.curso = cursoEncontrado
        this.formCurso.setValue({
          bloco: this.curso.bloco,
          email: this.curso.email,
          nome: this.curso.nome
        })
      }
    }
  }

  formCurso = new FormGroup({
    bloco: new FormControl('', Validators.required),
    nome: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email])
  })

  async submit(){
    if(this.formCurso.valid){
      this.curso.bloco = this.formCurso.value.bloco!;
      this.curso.email = this.formCurso.value.email!;
      this.curso.nome = this.formCurso.value.nome!;

      const response = await this.cursoService.editCurso(this.curso);
      if(response){
        this.router.navigate(['cursos'])
      }else{
        alert("Erro ao registrar um novo curso");
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
