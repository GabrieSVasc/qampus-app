import { Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { Disciplina, DisciplinaService } from '../disciplina-service';
import { AuthService } from '../../auth/auth-service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-list-disciplina',
  imports: [Navbar],
  templateUrl: './list-disciplina.html',
  styleUrl: './list-disciplina.css',
})
export class ListDisciplina implements OnInit{
  canCreate: boolean = false;
  disciplinas: Disciplina[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private disciplinaService: DisciplinaService,
    private cdr: ChangeDetectorRef
  ){}

  async ngOnInit(){
    this.canCreate = this.authService.hasRole("PROFESSOR");
    this.disciplinas = await this.disciplinaService.getAll();
    this.cdr.detectChanges()
  }

  criar(){
    this.router.navigate(['disciplinas/criar']);
  }

  async remove(id: number){
    const response = await this.disciplinaService.remove(id);
    if(response){
      this.disciplinas = await this.disciplinaService.getAll();
      this.cdr.detectChanges();
    }
  }
  edit(id: number){
    this.router.navigate(['disciplinas/editar/'+id]);
  }
}
