import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from "../../navbar/navbar";
import { AuthService } from '../../auth/auth-service';
import { Router } from '@angular/router';
import { Curso, CursoService } from '../curso-service';

@Component({
  selector: 'app-list-curso',
  imports: [Navbar],
  templateUrl: './list-curso.html',
  styleUrl: './list-curso.css',
})
export class ListCurso implements OnInit{
  canCreate: boolean = false;
  cursos: Curso[] = [];
  constructor(
    private authService: AuthService,
    private router: Router,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef
  ){}
  
  async ngOnInit() {
    this.canCreate = this.authService.hasRole("PROFESSOR");
    this.cursos = await this.cursoService.getCursos();
    this.cdr.detectChanges()
  }

  criarCurso(){
    this.router.navigate(['cursos/criar'])
  }

  async remove(id: number){
    const response = await this.cursoService.removeCurso(id);
    if(response){
      this.cursos = await this.cursoService.getCursos();
      this.cdr.detectChanges()
    }else{
      alert("Erro ao remover este curso");
    }
  }

  edit(id: number){
    this.router.navigate(["cursos/editar/"+id])
  }
}
