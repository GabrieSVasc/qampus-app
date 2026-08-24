import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Home } from './home';
import { Router } from '@angular/router';
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
      content: 'Conteúdo da dúvida',
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
      content: 'Conteúdo da dúvida',
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
      content: 'Conteúdo da dúvida',
      upVotes: 15,
      downVotes: 0,
      tags: [
        {
          id: '4',
          name: 'CURSO'
        }
      ],
      createdAt: '2026-08-11T12:00:00'
    }
  ];

  const postServiceMock = {
    findAll: vi.fn()
  };

  beforeEach(async () => {
    routerMock = {
      navigate: vi.fn(),
    };

    postServiceMock.findAll.mockResolvedValue(postsMock);

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: PostService,
          useValue: postServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should load the posts from PostService', () => {
    expect(postServiceMock.findAll).toHaveBeenCalled();

    expect(component.duvidas.length).toBe(3);

    expect(component.duvidas[0].title).toBe(
      'Como funciona a matrícula nas disciplinas?'
    );

    expect(component.duvidas[0].upVotes).toBe(12);
    expect(component.duvidas[0].downVotes).toBe(0);
  });

  it('should navigate to create question page', () => {
    component.fazerPergunta();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/post/criar']);
  });

  it('should navigate to the selected question', () => {
    component.visualizarDuvida('2');

    expect(routerMock.navigate).toHaveBeenCalledWith(['/post', '2']);
  });
});