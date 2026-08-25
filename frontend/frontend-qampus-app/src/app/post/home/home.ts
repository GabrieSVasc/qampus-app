import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth-service';
import { PostService, Post } from '../post-service';
import { Navbar } from "../../navbar/navbar";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  imports: [Navbar, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  duvidas: Post[] = [];
  duvidasFiltradas: Post[] = [];

  termoBusca = '';

  tagsSelecionadas: string[] = [];

  constructor(
    private authService: AuthService,
    private router: Router,
    private postService: PostService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    try {
      this.duvidas = await this.postService.findAll();

      this.duvidas = this.ordenarPorMaisRecente(this.duvidas);

      this.duvidasFiltradas = [...this.duvidas];

      this.cdr.detectChanges();
    } catch (error) {
      console.error('Erro ao carregar dúvidas:', error);
      alert('Erro ao carregar dúvidas');
    }
  }

  async aplicarFiltros(): Promise<void> {
    const termo = this.termoBusca.trim();

    try {
      let resultado: Post[];

      if (termo) {
        resultado = await this.postService.searchPosts(termo);
      } else {
        resultado = [...this.duvidas];
      }

      if (this.tagsSelecionadas.length > 0) {
        resultado = resultado.filter(duvida =>
          duvida.tags.some(tag =>
            this.tagsSelecionadas.includes(tag.name)
          )
        );
      }

      resultado = this.ordenarPorMaisRecente(resultado);

      this.duvidasFiltradas = resultado;

      this.cdr.detectChanges();
    } catch (error) {
      console.error('Erro ao buscar dúvidas:', error);
    }
  }

  alterarFiltroTag(tag: string, event: Event): void {
    const checkbox = event.target as HTMLInputElement;

    if (checkbox.checked) {
      this.tagsSelecionadas.push(tag);
    } else {
      this.tagsSelecionadas = this.tagsSelecionadas.filter(
        tagSelecionada => tagSelecionada !== tag
      );
    }

    this.aplicarFiltros();
  }

  getTagsDisponiveis(): string[] {
    const tags = this.duvidas.flatMap(duvida =>
      duvida.tags.map(tag => tag.name)
    );

    return [...new Set(tags)];
  }

  ordenarPorMaisRecente(posts: Post[]): Post[] {
    return [...posts].sort((a, b) =>
      new Date(b.createdAt).getTime() -
      new Date(a.createdAt).getTime()
    );
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  fazerPergunta(): void {
    this.router.navigate(['/post/criar']);
  }

  visualizarDuvida(id: string): void {
    this.router.navigate(['/post', id]);
  }
}