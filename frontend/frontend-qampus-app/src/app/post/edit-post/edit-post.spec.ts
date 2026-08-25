import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { vi } from 'vitest';

import { EditPost } from './edit-post';
import { PostService, Post } from '../post-service';

describe('EditPost', () => {
  let component: EditPost;
  let fixture: ComponentFixture<EditPost>;

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  const postMock: Post = {
    id: '1',
    title: 'Título do post',
    content: 'Conteúdo do post',
    upVotes: 10,
    downVotes: 2,
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
    createdAt: '2026-08-25T10:00:00'
  };

  const postServiceMock = {
    findById: vi.fn(),
    editPost: vi.fn()
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: vi.fn()
      }
    }
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    routerMock = {
      navigate: vi.fn()
    };

    vi.spyOn(window, 'alert').mockImplementation(() => {});

    activatedRouteMock.snapshot.paramMap.get.mockImplementation(
      (param: string) => {
        if (param === 'id') {
          return '1';
        }

        return null;
      }
    );

    postServiceMock.findById.mockResolvedValue({
      ...postMock,
      tags: postMock.tags.map(tag => ({ ...tag }))
    });

    postServiceMock.editPost.mockResolvedValue(true);

    await TestBed.configureTestingModule({
      imports: [EditPost],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock
        },
        {
          provide: Router,
          useValue: routerMock
        },
        {
          provide: PostService,
          useValue: postServiceMock
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EditPost);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should load the post by id', () => {
    expect(postServiceMock.findById).toHaveBeenCalledWith('1');

    expect(component.post.id).toBe('1');
    expect(component.post.title).toBe('Título do post');
    expect(component.post.content).toBe('Conteúdo do post');
  });

  it('should fill the form with the post data', () => {
    expect(component.postForm.value).toEqual({
      title: 'Título do post',
      content: 'Conteúdo do post'
    });
  });

  it('should load the post tags', () => {
    expect(component.tagsCriadas).toEqual([
      'CURSO',
      'TURMA 1'
    ]);
  });

  it('should not update the component when the post is not found', async () => {
    postServiceMock.findById.mockResolvedValueOnce(null);

    component.post = {
      ...postMock
    };

    await component.ngOnInit();

    expect(component.post.id).toBe('1');
  });

  it('should open and close the tag input', () => {
    expect(component.novaTag).toBe(false);

    component.abrirFecharTag();

    expect(component.novaTag).toBe(true);

    component.abrirFecharTag();

    expect(component.novaTag).toBe(false);
  });

  it('should add a tag', () => {
    component.nomeTag = '  NOVA TAG  ';
    component.novaTag = true;

    component.adicionarTag();

    expect(component.tagsCriadas).toContain('NOVA TAG');
    expect(component.nomeTag).toBe('');
    expect(component.novaTag).toBe(false);
  });

  it('should remove a tag', () => {
    component.tagsCriadas = [
      'CURSO',
      'TURMA 1',
      'COMPUTAÇÃO'
    ];

    component.removeTag('TURMA 1');

    expect(component.tagsCriadas).toEqual([
      'CURSO',
      'COMPUTAÇÃO'
    ]);
  });

  it('should navigate to the given route', () => {
    component.goTo('home');

    expect(routerMock.navigate).toHaveBeenCalledWith([
      'home'
    ]);
  });

  it('should edit the post successfully', async () => {
    component.post = {
      ...postMock
    };

    component.postForm.setValue({
      title: 'Novo título',
      content: 'Novo conteúdo'
    });

    component.tagsCriadas = [
      'CURSO',
      'NOVA TAG'
    ];

    postServiceMock.editPost.mockResolvedValueOnce(true);

    await component.submit();

    expect(postServiceMock.editPost).toHaveBeenCalledWith(
      {
        id: '1',
        title: 'Novo título',
        content: 'Novo conteúdo',
        tags: [
          'CURSO',
          'NOVA TAG'
        ]
      },
      '1'
    );

    expect(routerMock.navigate).toHaveBeenCalledWith([
      'home'
    ]);
  });

  it('should show an alert when editing the post fails', async () => {
    component.post = {
      ...postMock
    };

    component.postForm.setValue({
      title: 'Novo título',
      content: 'Novo conteúdo'
    });

    component.tagsCriadas = [
      'CURSO'
    ];

    postServiceMock.editPost.mockResolvedValueOnce(false);

    await component.submit();

    expect(postServiceMock.editPost).toHaveBeenCalledWith(
      {
        id: '1',
        title: 'Novo título',
        content: 'Novo conteúdo',
        tags: [
          'CURSO'
        ]
      },
      '1'
    );

    expect(window.alert).toHaveBeenCalledWith(
      'Erro ao editar a dúvida'
    );

    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});