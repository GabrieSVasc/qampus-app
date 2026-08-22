import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DisciplinaService, NewDisciplina } from '../disciplina-service';
import { Router } from '@angular/router';
import { Curso, CursoService } from '../../curso/curso-service';

@Component({
  selector: 'app-create-disciplina',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './create-disciplina.html',
  styleUrl: './create-disciplina.css',
})
export class CreateDisciplina implements OnInit{
  constructor(
    private cursoService: CursoService,
    private disciplinaService: DisciplinaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ){}

  cursos: Curso[] = [];

  async ngOnInit() {
    this.cursos = await this.cursoService.getCursos();
    this.cdr.detectChanges();
  }

  formDisciplina = new FormGroup({
    nome: new FormControl('', Validators.required),
    curso: new FormControl(0, Validators.required)
  })

  async submit(){
    if(this.formDisciplina.valid){
      const novaDisciplina: NewDisciplina = {
        nome: this.formDisciplina.value.nome!,
        idCurso: this.formDisciplina.value.curso!
      }
      const response = await this.disciplinaService.create(novaDisciplina);
      if(response){
        this.router.navigate(['disciplinas']);
      }else{
        alert("Erro ao registrar uma nova disciplina");
      }
    }else{
      alert("Preencha todos os campos");
    }
  }
}
