import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { Disciplina, DisciplinaService } from '../../disciplina/disciplina-service';
import { NewTurma, Turma, TurmaService } from '../turma-service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-edit-turma',
  imports: [Navbar, FormsModule, ReactiveFormsModule],
  templateUrl: './edit-turma.html',
  styleUrl: './edit-turma.css',
})
export class EditTurma implements OnInit{
  constructor(
    private disciplinaService: DisciplinaService,
    private turmaService: TurmaService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ){}
  disciplinas: Disciplina[] = [];

  turma: Turma = {
    id: 0,
    periodo: '',
    nomeDisciplina: '',
    nomeProfessor: ''
  }

  formTurma = new FormGroup({
    periodo: new FormControl('', Validators.required),
    disciplina: new FormControl(0, Validators.required),
    emailProfessor: new FormControl('', [Validators.required, Validators.email])
  })

  async ngOnInit() {
    this.disciplinas = await this.disciplinaService.getAll();
    this.cdr.detectChanges();

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if(id){
      const turmaEncontrada = await this.turmaService.getById(id);
      if(turmaEncontrada){
        this.turma = turmaEncontrada;
        let idDisciplina = 0;
        for(let index=0; index<this.disciplinas.length; index++){
          if(this.disciplinas[index].nome==this.turma.nomeDisciplina){
            idDisciplina = this.disciplinas[index].codigo;
          }
        }
        this.formTurma.setValue({
          periodo: this.turma.periodo,
          disciplina: idDisciplina,
          emailProfessor: this.turma.nomeProfessor
        })
      }
    }
    this.cdr.detectChanges();
  }

  async submit(){
    if(this.formTurma.valid){
      const editar: NewTurma = {
        periodo: this.formTurma.value.periodo!,
        codigoDisciplina: this.formTurma.value.disciplina!,
        emailProfessor: this.formTurma.value.emailProfessor!
      }

      const response = await this.turmaService.edit(editar, this.turma.id);
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
