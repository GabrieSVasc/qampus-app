import { Component, OnInit } from '@angular/core';
import { Navbar } from '../../navbar/navbar';
import { AuthService } from '../../auth/auth-service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { TurmaService, Turma } from '../turma-service';

@Component({
  selector: 'app-list-turma',
  imports: [Navbar],
  templateUrl: './list-turma.html',
  styleUrl: './list-turma.css',
})

export class ListTurma implements OnInit{
  canCreate: boolean = false;
  turmas: Turma[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private turmaService: TurmaService,
    private cdr: ChangeDetectorRef
  ){}
  async ngOnInit() {
    this.canCreate = this.authService.hasRole("PROFESSOR");
    this.turmas = await this.turmaService.getAll();
    this.cdr.detectChanges();
  }

  criar(){
    this.router.navigate(['turmas/criar']);
  }

  async remove(id: number){
    const response = await this.turmaService.remove(id);
    if(response){
      this.turmas = await this.turmaService.getAll();
      this.cdr.detectChanges();
    }
  }
  edit(id: number){
    this.router.navigate(['turmas/editar/'+id]);
  }
}
