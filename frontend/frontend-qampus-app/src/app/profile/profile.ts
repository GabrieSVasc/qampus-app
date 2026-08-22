import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from "../navbar/navbar";
import { Post, PostService } from '../post/post-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [Navbar],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit{
  
  nomeUser = "Nome do Usuário"
  email = "Email@gmail.com"

  qtVotos = 20
  qtComentarios = 30
  qtPublicacoes = 50
  reputacao = 20

  duvidas: Post[] = [];

  constructor(
    private postService: PostService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ){}

  async ngOnInit() {
    try{
      this.duvidas = await this.postService.findAll();
      this.cdr.detectChanges();
    }catch(error){
      alert("Erro ao carregar dúvidas publicadas por você");
    }
  }

  editar(id: string){
    this.router.navigate(['post/editar/'+id]);
  }
}
