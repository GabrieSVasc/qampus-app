import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Navbar } from "../navbar/navbar";
import { Post, PostService, Answer } from '../post/post-service';
import { Router } from '@angular/router';
import { UserService, User } from '../user-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  imports: [Navbar, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit{
  user: User = {
    id: '',
    name: '',
    email: ''
  }

  visualizar: 'publicacoes' | 'comentarios' = 'publicacoes';

  duvidas: Post[] = [];
  respostas: Answer[] = [];

  constructor(
    private postService: PostService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private userService: UserService
  ){}

  async ngOnInit() {
    try{
      this.user = await this.userService.getUser();
      this.duvidas = await this.postService.findAll();
      this.cdr.detectChanges();
      this.respostas = await this.userService.getAnswers(this.user.id);
      this.cdr.detectChanges();
    }catch(error){
      alert("Erro ao carregar dúvidas publicadas por você");
    }
  }

  editar(id: string){
    this.router.navigate(['post/editar/'+id]);
  }

  editarResposta(idPost: string, idComentario: string){
    this.router.navigate(['post/editar/'+idPost+'/comentario/'+idComentario]);
  }
}
