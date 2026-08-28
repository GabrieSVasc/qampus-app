import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Navbar } from '../../navbar/navbar';
import { Post, PostService, Answer } from '../post-service';

@Component({
  selector: 'app-duvida',
  imports: [Navbar, FormsModule],
  templateUrl: './duvida.html',
  styleUrl: './duvida.css',
})
export class Duvida implements OnInit {

  post: Post | null = null;

  novaResposta = '';

  relacionadas: Post[] = [];

  respostas: Answer[] = [];
  editResposta: Answer = {
    id: '',
    content: '',
    postId: '',
    createdAt: '',
    downVotes: 0,
    upVotes: 0
  };
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private postService: PostService,
    private cdr: ChangeDetectorRef
  ) { }

  async ngOnInit(): Promise<void> {
    this.route.paramMap.subscribe(async params => {
      const idPost = params.get('idPost');
      const idAnswer = params.get('idComentario');

      if (!idPost) {
        return;
      }

      this.post = null;
      this.respostas = [];

      this.editResposta = {
        id: idAnswer ?? '',
        content: '',
        postId: '',
        createdAt: '',
        downVotes: 0,
        upVotes: 0
      };

      try {
        const [post, resps] = await Promise.all([
          this.postService.findById(idPost),
          this.postService.getAnswersPost(idPost)
        ]);

        this.post = post;
        this.respostas = resps ?? [];

        if (idAnswer) {
          const respostaEncontrada = this.respostas.find(
            resposta => resposta.id === idAnswer
          );

          if (respostaEncontrada) {
            this.editResposta = respostaEncontrada;
          }
        }

        this.relacionadas = await this.postService.findAll();

        this.cdr.detectChanges();

      } catch (error) {
        console.error('Erro ao carregar dúvida:', error);
      }
    });
  }

  async votar(valor: number): Promise<void> {
    if (!this.post) {
      return;
    }

    try {
      const postAtualizado =
        valor > 0
          ? await this.postService.upvotePost(this.post.id)
          : await this.postService.downvotePost(this.post.id);

      this.post = postAtualizado;
      this.cdr.detectChanges();
    } catch (error) {
      console.error('Erro ao votar na dúvida:', error);
    }
  }

  async responder(): Promise<void> {
    if (!this.novaResposta.trim()) {
      alert('O conteúdo da resposta é obrigatório.');
      return;
    }

    if (!this.post) {
      return;
    }

    try {
      const resposta = await this.postService.createAnswer(
        this.post.id,
        this.novaResposta
      );

      this.respostas.push(resposta);
      this.novaResposta = '';
      this.cdr.detectChanges()
    } catch (error) {
      console.error('Erro ao responder dúvida:', error);
      alert('Erro ao enviar resposta.');
    }
  }

  async votarResposta(resposta: Answer, valor: number): Promise<void> {
    if (!this.post) {
      return;
    }

    try {
      const respostaAtualizada =
        valor > 0
          ? await this.postService.upvoteAnswer(this.post.id, resposta.id)
          : await this.postService.downvoteAnswer(this.post.id, resposta.id);

      const index = this.respostas.findIndex(
        r => r.id === resposta.id
      );

      if (index !== -1) {
        this.respostas[index] = respostaAtualizada;
      }
    } catch (error) {
      console.error('Erro ao votar na resposta:', error);
    }
    this.cdr.detectChanges();
  }

  async editarResposta(id: string) {
    if (this.post) {
      const response = await this.postService.editAnswer(this.post.id, id, this.editResposta.content)
      if (response) {
        this.router.navigate(['post/' + this.post.id]);
      } else {
        alert("Não foi possível editar o comentário")
      }
    }
  }

  visualizarRelacionada(id: string): void {
    this.router.navigate(['/post/' + id]);
  }
}