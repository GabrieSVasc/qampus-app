import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { vi } from 'vitest';

import { Duvida } from './duvida';
import { Post, PostService } from '../post-service';

describe('Duvida', () => {
  let component: Duvida;
  let fixture: ComponentFixture<Duvida>;

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  const postMock: Post = {
    id: '1',
    title: 'Como funciona a matrícula nas disciplinas?',
    content: 'Conteúdo da dúvida',
    upVotes: 12,
    downVotes: 3,
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
  };

  const postServiceMock = {
    findById: vi.fn(),
    getAnswersPost: vi.fn(),
    createAnswer: vi.fn(),
    upvotePost: vi.fn(),
    downvotePost: vi.fn(),
    upvoteAnswer: vi.fn(),
    downvoteAnswer: vi.fn(),
    editAnswer: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    routerMock = {
      navigate: vi.fn(),
    };

    postServiceMock.findById.mockResolvedValue({
      ...postMock,
      tags: postMock.tags.map(tag => ({ ...tag }))
    });

    postServiceMock.getAnswersPost.mockResolvedValue([]);

    postServiceMock.createAnswer.mockResolvedValue({
      id: 'answer-1',
      content: 'Essa é uma resposta válida.',
      userId: 'user-1',
      postId: '1',
      createdAt: '2026-08-19T10:00:00',
      upVotes: 5,
      downVotes: 2
    });

    postServiceMock.upvoteAnswer.mockResolvedValue({
      id: 'answer-1',
      content: 'Essa é uma resposta válida.',
      userId: 'user-1',
      postId: '1',
      createdAt: '2026-08-19T10:00:00',
      upVotes: 5,
      downVotes: 2
    });

    postServiceMock.downvoteAnswer.mockResolvedValue({
      id: 'answer-1',
      content: 'Essa é uma resposta válida.',
      userId: 'user-1',
      postId: '1',
      createdAt: '2026-08-19T10:00:00',
      upVotes: 4,
      downVotes: 3
    });

    postServiceMock.upvotePost.mockResolvedValue({
      ...postMock,
      upVotes: 13,
      downVotes: 3
    });

    postServiceMock.downvotePost.mockResolvedValue({
      ...postMock,
      upVotes: 12,
      downVotes: 4
    });

    await TestBed.configureTestingModule({
      imports: [Duvida],
      providers: [
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: vi.fn((param: string) => {
                  if (param === 'idPost') {
                    return '1';
                  }

                  return null;
                }),
              },
            },
          },
        },
        {
          provide: PostService,
          useValue: postServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Duvida);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the post by id', () => {
    expect(postServiceMock.findById).toHaveBeenCalledWith('1');

    expect(component.post).toBeTruthy();

    expect(component.post?.id).toBe('1');
    expect(component.post?.title).toBe(
      'Como funciona a matrícula nas disciplinas?'
    );
    expect(component.post?.content).toBe('Conteúdo da dúvida');
  });

  it('should contain the post votes', () => {
    expect(component.post?.upVotes).toBe(12);
    expect(component.post?.downVotes).toBe(3);
  });

  it('should contain the post tags', () => {
    expect(component.post?.tags.length).toBe(2);
    expect(component.post?.tags[0].name).toBe('CURSO');
    expect(component.post?.tags[1].name).toBe('TURMA 1');
  });

  it('should increase the up votes', async () => {
    await component.votar(1);

    expect(postServiceMock.upvotePost).toHaveBeenCalledWith('1');
    expect(component.post?.upVotes).toBe(13);
    expect(component.post?.downVotes).toBe(3);
  });

  it('should increase the down votes', async () => {
    await component.votar(-1);

    expect(postServiceMock.downvotePost).toHaveBeenCalledWith('1');
    expect(component.post?.upVotes).toBe(12);
    expect(component.post?.downVotes).toBe(4);
  });

  it('should navigate to the selected related question', () => {
    component.visualizarRelacionada('3');

    expect(routerMock.navigate).toHaveBeenCalledWith([
      '/post',
      '3'
    ]);
  });

  it('should not allow an empty response', async () => {
    component.novaResposta = '   ';

    await component.responder();

    expect(postServiceMock.createAnswer).not.toHaveBeenCalled();
    expect(component.novaResposta).toBe('   ');
  });

  it('should create a response successfully', async () => {
    component.novaResposta = 'Essa é uma resposta válida.';

    await component.responder();

    expect(postServiceMock.createAnswer).toHaveBeenCalledWith(
      '1',
      'Essa é uma resposta válida.'
    );

    expect(component.novaResposta).toBe('');
  });

  it('should add the created response to the discussion', async () => {
    component.novaResposta = 'Essa é uma resposta válida.';

    await component.responder();

    expect(component.respostas.length).toBe(1);
    expect(component.respostas[0].id).toBe('answer-1');
    expect(component.respostas[0].content).toBe(
      'Essa é uma resposta válida.'
    );
  });

  it('should handle response creation error', async () => {
    postServiceMock.createAnswer.mockRejectedValueOnce(
      new Error('Erro ao criar resposta')
    );

    component.novaResposta = 'Resposta válida.';

    await component.responder();

    expect(component.novaResposta).toBe('Resposta válida.');
  });

  it('should handle upvote error', async () => {
    postServiceMock.upvotePost.mockRejectedValueOnce(
      new Error('Erro ao votar')
    );

    await component.votar(1);

    expect(component.post?.upVotes).toBe(12);
    expect(component.post?.downVotes).toBe(3);
  });

  it('should handle downvote error', async () => {
    postServiceMock.downvotePost.mockRejectedValueOnce(
      new Error('Erro ao votar')
    );

    await component.votar(-1);

    expect(component.post?.upVotes).toBe(12);
    expect(component.post?.downVotes).toBe(3);
  });

  it('should upvote an answer', async () => {
    const resposta = {
      id: 'answer-1',
      content: 'Essa é uma resposta válida.',
      userId: 'user-1',
      postId: '1',
      createdAt: '2026-08-19T10:00:00',
      upVotes: 4,
      downVotes: 2
    };

    component.respostas = [resposta];

    await component.votarResposta(resposta, 1);

    expect(postServiceMock.upvoteAnswer).toHaveBeenCalledWith(
      '1',
      'answer-1'
    );

    expect(component.respostas[0].upVotes).toBe(5);
    expect(component.respostas[0].downVotes).toBe(2);
  });

  it('should downvote an answer', async () => {
    const resposta = {
      id: 'answer-1',
      content: 'Essa é uma resposta válida.',
      userId: 'user-1',
      postId: '1',
      createdAt: '2026-08-19T10:00:00',
      upVotes: 4,
      downVotes: 2
    };

    component.respostas = [resposta];

    await component.votarResposta(resposta, -1);

    expect(postServiceMock.downvoteAnswer).toHaveBeenCalledWith(
      '1',
      'answer-1'
    );

    expect(component.respostas[0].upVotes).toBe(4);
    expect(component.respostas[0].downVotes).toBe(3);
  });
});