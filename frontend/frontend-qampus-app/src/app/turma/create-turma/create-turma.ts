import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { Disciplina, DisciplinaService } from '../../disciplina/disciplina-service';
import { FormsModule, ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { NewTurma, TurmaService } from '../turma-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-turma',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './create-turma.html',
  styleUrl: './create-turma.css',
})

export class CreateTurma implements OnInit{
  constructor(
    private disciplinaService: DisciplinaService,
    private turmaService: TurmaService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ){}
  disciplinas: Disciplina[] = [];

  async ngOnInit() {
    this.disciplinas = await this.disciplinaService.getAll();
    this.cdr.detectChanges();
  }

  formTurma = new FormGroup({
    periodo: new FormControl('', Validators.required),
    disciplina: new FormControl(0, Validators.required),
    emailProfessor: new FormControl('', [Validators.required, Validators.email])
  })

  async submit(){
    if(this.formTurma.valid){
      const novaTurma: NewTurma = {
        periodo: this.formTurma.value.periodo!,
        codigoDisciplina: this.formTurma.value.disciplina!,
        emailProfessor: this.formTurma.value.emailProfessor!
      }
      const response = await this.turmaService.create(novaTurma);
      if(response){
        this.router.navigate(['turmas']);
      }else{
        alert("Email deve pertencer a um professor cadastrado no sistema");
      }
    }else{
      if(this.formTurma.get('emailProfessor')?.hasError('email')){
        alert("Email inválido");
      }else{
        alert("Preencha todos os campos");
      }
    }
  }
}
