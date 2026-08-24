import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { vi } from 'vitest';

import { Duvida } from './duvida';
import { Post, PostService, Answer } from '../post-service';

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

  const respostaMock: Answer = {
    id: 'answer-1',
    content: 'Essa é uma resposta válida.',
    postId: '1',
    createdAt: '2026-08-19T10:00:00',
    upVotes: 5,
    downVotes: 2
  };

  const postServiceMock = {
    findById: vi.fn(),
    getAnswersPost: vi.fn(),
    createAnswer: vi.fn(),
    editAnswer: vi.fn(),
    upvotePost: vi.fn(),
    downvotePost: vi.fn(),
    upvoteAnswer: vi.fn(),
    downvoteAnswer: vi.fn()
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    routerMock = {
      navigate: vi.fn()
    };

    vi.spyOn(window, 'alert').mockImplementation(() => {});

    postServiceMock.getAnswersPost.mockResolvedValue([]);

    postServiceMock.findById.mockResolvedValue({
      ...postMock,
      tags: postMock.tags.map(tag => ({ ...tag }))
    });

    postServiceMock.createAnswer.mockResolvedValue({
      ...respostaMock
    });

    postServiceMock.editAnswer.mockResolvedValue({
      ...respostaMock
    });

    postServiceMock.upvoteAnswer.mockResolvedValue({
      ...respostaMock,
      upVotes: 5,
      downVotes: 2
    });

    postServiceMock.downvoteAnswer.mockResolvedValue({
      ...respostaMock,
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
          useValue: routerMock
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

                  if (param === 'idComentario') {
                    return null;
                  }

                  return null;
                })
              }
            }
          }
        },
        {
          provide: PostService,
          useValue: postServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Duvida);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should load the post by id', () => {
    expect(postServiceMock.getAnswersPost).toHaveBeenCalledWith('1');
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

  it('should load the answers', async () => {
    const respostas: Answer[] = [
      {
        ...respostaMock
      }
    ];

    postServiceMock.getAnswersPost.mockResolvedValueOnce(respostas);

    await component.ngOnInit();

    expect(postServiceMock.getAnswersPost).toHaveBeenCalledWith('1');
    expect(component.respostas.length).toBe(1);
    expect(component.respostas[0].id).toBe('answer-1');
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

  it('should not vote when there is no post', async () => {
    component.post = null;

    await component.votar(1);

    expect(postServiceMock.upvotePost).not.toHaveBeenCalled();
    expect(postServiceMock.downvotePost).not.toHaveBeenCalled();
  });

  it('should not allow an empty response', async () => {
    component.novaResposta = '   ';

    await component.responder();

    expect(window.alert).toHaveBeenCalledWith(
      'O conteúdo da resposta é obrigatório.'
    );

    expect(postServiceMock.createAnswer).not.toHaveBeenCalled();
    expect(component.novaResposta).toBe('   ');
  });

  it('should not create a response when there is no post', async () => {
    component.post = null;
    component.novaResposta = 'Resposta válida.';

    await component.responder();

    expect(postServiceMock.createAnswer).not.toHaveBeenCalled();
    expect(component.novaResposta).toBe('Resposta válida.');
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

    expect(window.alert).toHaveBeenCalledWith(
      'Erro ao enviar resposta.'
    );

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
    const resposta: Answer = {
      ...respostaMock,
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
    const resposta: Answer = {
      ...respostaMock,
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

  it('should not vote on answer when there is no post', async () => {
    component.post = null;

    const resposta: Answer = {
      ...respostaMock
    };

    await component.votarResposta(resposta, 1);

    expect(postServiceMock.upvoteAnswer).not.toHaveBeenCalled();
    expect(postServiceMock.downvoteAnswer).not.toHaveBeenCalled();
  });

  it('should handle answer upvote error', async () => {
    const resposta: Answer = {
      ...respostaMock,
      upVotes: 4,
      downVotes: 2
    };

    component.respostas = [resposta];

    postServiceMock.upvoteAnswer.mockRejectedValueOnce(
      new Error('Erro ao votar na resposta')
    );

    await component.votarResposta(resposta, 1);

    expect(component.respostas[0].upVotes).toBe(4);
    expect(component.respostas[0].downVotes).toBe(2);
  });

  it('should handle answer downvote error', async () => {
    const resposta: Answer = {
      ...respostaMock,
      upVotes: 4,
      downVotes: 2
    };

    component.respostas = [resposta];

    postServiceMock.downvoteAnswer.mockRejectedValueOnce(
      new Error('Erro ao votar na resposta')
    );

    await component.votarResposta(resposta, -1);

    expect(component.respostas[0].upVotes).toBe(4);
    expect(component.respostas[0].downVotes).toBe(2);
  });

  it('should edit an answer successfully', async () => {
    component.editResposta = {
      ...respostaMock,
      content: 'Resposta editada.'
    };

    await component.editarResposta('answer-1');

    expect(postServiceMock.editAnswer).toHaveBeenCalledWith(
      '1',
      'answer-1',
      'Resposta editada.'
    );

    expect(routerMock.navigate).toHaveBeenCalledWith([
      'post/1'
    ]);
  });

  it('should show an alert when answer editing fails', async () => {
    postServiceMock.editAnswer.mockResolvedValueOnce(null);

    component.editResposta = {
      ...respostaMock,
      content: 'Resposta editada.'
    };

    await component.editarResposta('answer-1');

    expect(postServiceMock.editAnswer).toHaveBeenCalledWith(
      '1',
      'answer-1',
      'Resposta editada.'
    );

    expect(window.alert).toHaveBeenCalledWith(
      'Não foi possível editar o comentário'
    );

    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should not edit an answer when there is no post', async () => {
    component.post = null;

    component.editResposta = {
      ...respostaMock,
      content: 'Resposta editada.'
    };

    await component.editarResposta('answer-1');

    expect(postServiceMock.editAnswer).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});