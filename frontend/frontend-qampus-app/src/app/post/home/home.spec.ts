import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Home } from './home';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth-service';
import { PostService, Post } from '../post-service';
import { vi } from 'vitest';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  const postsMock: Post[] = [
    {
      id: '1',
      title: 'Como funciona a matrícula nas disciplinas?',
      content: 'Tenho dúvidas sobre o processo de matrícula.',
      upVotes: 12,
      downVotes: 0,
      tags: [
        {
          id: '1',
          name: 'CURSO'
        },
        {
          id: '2',
          name: 'TURMA 1'
        }
      ],
      createdAt: '2026-08-11T10:00:00'
    },
    {
      id: '2',
      title: 'Quando começam as aulas do próximo período?',
      content: 'Gostaria de saber a data de início das aulas.',
      upVotes: 8,
      downVotes: 0,
      tags: [
        {
          id: '3',
          name: 'CURSO'
        }
      ],
      createdAt: '2026-08-11T11:00:00'
    },
    {
      id: '3',
      title: 'Como acessar o material das disciplinas?',
      content: 'Não consigo encontrar os arquivos disponibilizados pelos professores.',
      upVotes: 15,
      downVotes: 0,
      tags: [
        {
          id: '4',
          name: 'TURMA 2'
        }
      ],
      createdAt: '2026-08-11T12:00:00'
    }
  ];

  const postServiceMock = {
    findAll: vi.fn(),
    searchPosts: vi.fn()
  };

  const authServiceMock = {
    logout: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    routerMock = {
      navigate: vi.fn()
    };
    
    postServiceMock.findAll.mockResolvedValue(
      postsMock.map(post => ({
        ...post,
        tags: post.tags.map(tag => ({ ...tag }))
      }))
    );

    // Remove qualquer retorno deixado por testes anteriores
    postServiceMock.searchPosts.mockReset();

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        {
          provide: Router,
          useValue: routerMock
        },
        {
          provide: PostService,
          useValue: postServiceMock
        },
        {
          provide: AuthService,
          useValue: authServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should load the posts from PostService', () => {
    expect(postServiceMock.findAll).toHaveBeenCalled();

    expect(component.duvidas.length).toBe(3);

    const primeiraDuvida = component.duvidas.find(
      duvida => duvida.id === '1'
    );

    expect(primeiraDuvida?.title).toBe(
      'Como funciona a matrícula nas disciplinas?'
    );

    expect(primeiraDuvida?.upVotes).toBe(12);
    expect(primeiraDuvida?.downVotes).toBe(0);
  });

  it('should order posts from newest to oldest', () => {
    expect(component.duvidasFiltradas[0].id).toBe('3');
    expect(component.duvidasFiltradas[1].id).toBe('2');
    expect(component.duvidasFiltradas[2].id).toBe('1');
  });

  it('should navigate to create question page', () => {
    component.fazerPergunta();

    expect(routerMock.navigate).toHaveBeenCalledWith([
      '/post/criar'
    ]);
  });

  it('should navigate to the selected question', () => {
    component.visualizarDuvida('2');

    expect(routerMock.navigate).toHaveBeenCalledWith([
      '/post',
      '2'
    ]);
  });

  it('should logout and navigate to login', () => {
    component.logout();

    expect(authServiceMock.logout).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith([
      '/login'
    ]);
  });

  it('should search posts by title', async () => {
    postServiceMock.searchPosts.mockResolvedValue([
      postsMock[0]
    ]);

    component.termoBusca = 'matrícula';

    await component.aplicarFiltros();

    expect(postServiceMock.searchPosts)
      .toHaveBeenCalledWith('matrícula');

    expect(component.duvidasFiltradas.length).toBe(1);
    expect(component.duvidasFiltradas[0].id).toBe('1');
  });

  it('should search posts by content', async () => {
    postServiceMock.searchPosts.mockResolvedValue([
      postsMock[2]
    ]);

    component.termoBusca = 'arquivos';

    await component.aplicarFiltros();

    expect(postServiceMock.searchPosts)
      .toHaveBeenCalledWith('arquivos');

    expect(component.duvidasFiltradas.length).toBe(1);
    expect(component.duvidasFiltradas[0].id).toBe('3');
  });

  it('should return no posts when search has no matches', async () => {
    postServiceMock.searchPosts.mockResolvedValue([]);

    component.termoBusca = 'computador quântico';

    await component.aplicarFiltros();

    expect(component.duvidasFiltradas.length).toBe(0);
  });

  it('should filter posts by selected tag', async () => {
    component.tagsSelecionadas = ['TURMA 1'];

    await component.aplicarFiltros();

    expect(component.duvidasFiltradas.length).toBe(1);
    expect(component.duvidasFiltradas[0].id).toBe('1');
  });

  it('should filter posts by search text and selected tag', async () => {
    postServiceMock.searchPosts.mockResolvedValue([
      postsMock[2]
    ]);

    component.termoBusca = 'disciplinas';
    component.tagsSelecionadas = ['TURMA 2'];

    await component.aplicarFiltros();

    expect(postServiceMock.searchPosts)
      .toHaveBeenCalledWith('disciplinas');

    expect(component.duvidasFiltradas.length).toBe(1);
    expect(component.duvidasFiltradas[0].id).toBe('3');
  });

  it('should add a tag when the checkbox is selected', async () => {
    const event = {
      target: {
        checked: true
      }
    } as unknown as Event;

    component.alterarFiltroTag('TURMA 1', event);

    expect(component.tagsSelecionadas).toContain('TURMA 1');

    await fixture.whenStable();
  });

  it('should remove a tag when the checkbox is unchecked', async () => {
    component.tagsSelecionadas = ['TURMA 1'];

    const event = {
      target: {
        checked: false
      }
    } as unknown as Event;

    component.alterarFiltroTag('TURMA 1', event);

    expect(component.tagsSelecionadas).not.toContain('TURMA 1');

    await fixture.whenStable();
  });

  it('should return all available tags without duplicates', () => {
    const tags = component.getTagsDisponiveis();

    expect(tags).toEqual([
      'TURMA 2',
      'CURSO',
      'TURMA 1'
    ]);
  });

  it('should keep filtered posts ordered from newest to oldest', async () => {
    component.tagsSelecionadas = ['CURSO'];

    await component.aplicarFiltros();

    expect(component.duvidasFiltradas.length).toBe(2);
    expect(component.duvidasFiltradas[0].id).toBe('2');
    expect(component.duvidasFiltradas[1].id).toBe('1');
  });

  it('should order search results from newest to oldest', async () => {
    postServiceMock.searchPosts.mockResolvedValue([
      postsMock[0],
      postsMock[2],
      postsMock[1]
    ]);

    component.termoBusca = 'disciplinas';

    await component.aplicarFiltros();

    expect(component.duvidasFiltradas[0].id).toBe('3');
    expect(component.duvidasFiltradas[1].id).toBe('2');
    expect(component.duvidasFiltradas[2].id).toBe('1');
  });
});