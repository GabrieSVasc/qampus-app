import { Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { ActivatedRoute } from '@angular/router';
import { DisciplinaService, Disciplina, NewDisciplina } from '../disciplina-service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Curso, CursoService } from '../../curso/curso-service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-edit-disciplina',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-disciplina.html',
  styleUrl: './edit-disciplina.css',
})
export class EditDisciplina implements OnInit{
  constructor(
    private route: ActivatedRoute,
    private disciplinaService: DisciplinaService,
    private router: Router,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef
  ){}

  disciplina: Disciplina = {
    codigo: 0,
    nome: '',
    nomeCurso: ''
  }

  cursos: Curso[] = [];

  async ngOnInit(){
    this.cursos = await this.cursoService.getCursos();
    this.cdr.detectChanges();

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if(id){
      const discEncontrada = await this.disciplinaService.getById(id);
      if(discEncontrada){
        this.disciplina = discEncontrada;
        let idCurso = 0;
        for (let index = 0; index < this.cursos.length; index++) {
          if(this.cursos[index].nome == this.disciplina.nomeCurso){
            idCurso= this.cursos[index].id
          }
          
        }
        this.formDisciplina.setValue({
          nome: this.disciplina.nome,
          curso: idCurso
        })
      }
    }
    this.cdr.detectChanges();
    
  }
  formDisciplina = new FormGroup({
    nome: new FormControl('', Validators.required),
    curso: new FormControl(0, Validators.required)
  })

  async submit(){
    const editar: NewDisciplina = {
      nome: this.formDisciplina.value.nome!,
      idCurso: this.formDisciplina.value.curso!
    }
    const response = await this.disciplinaService.edit(editar, this.disciplina.codigo);

    if(response){
      this.router.navigate(['disciplinas']);
    }else{
      alert("Erro ao editar a disciplina")
    }
  }
}
