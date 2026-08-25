import { TestBed } from '@angular/core/testing';
import { NewPost, PostService } from './post-service';

describe('PostService', () => {
  let service: PostService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostService);
  });

  it('should create new post succesfully', async () => {
    const post: NewPost = {
      title: 'TESTE',
      content: 'TESTES',
      tags: []
    }
    const token = 'token-teste';
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(
        JSON.stringify(post),
        {
          status: 200,
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
          }
        }
      )
    )
    const result = await service.createPost(post);
    expect(result).toBe(true);
  });

  it('should return false when creating a new post fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, { status: 401 })
    );
    const post: NewPost = {
      title: 'TESTE',
      content: 'TESTES',
      tags: []
    }

    const result = await service.createPost(post);
    expect(result).toBe(false);
  })

  it('should return false when creating a new post throws an error', async () => {
    vi.spyOn(globalThis, 'fetch').mockRejectedValue(
      new Error('Erro de conexão')
    );
    const post: NewPost = {
      title: 'TESTE',
      content: 'TESTES',
      tags: []
    }
    const result = await service.createPost(post);
    expect(result).toBe(false);
  })

  it('should edit an answer successfully', async () => {
    const token = 'token-teste';

    localStorage.setItem('token', token);

    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 200
      })
    );

    const result = await service.editAnswer(
      '1',
      'answer-1',
      'Resposta editada.'
    );

    expect(result).toBe(true);

    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining('/post/1/answer/answer-1'),
      expect.objectContaining({
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer token-teste'
        },
        body: JSON.stringify({
          content: 'Resposta editada.'
        })
      })
    );
  });

  it('should return false when editing an answer fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    const result = await service.editAnswer(
      '1',
      'answer-1',
      'Resposta editada.'
    );

    expect(result).toBe(false);
  });

  it('should send the correct data when editing an answer', async () => {
    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 200
      })
    );

    await service.editAnswer(
      '1',
      'answer-1',
      'Novo conteúdo da resposta'
    );

    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/post/1/answer/answer-1'),
      expect.objectContaining({
        method: 'PUT',
        body: JSON.stringify({
          content: 'Novo conteúdo da resposta'
        })
      })
    );
  });

  it('should throw when editing an answer has a network error', async () => {
    vi.spyOn(globalThis, 'fetch').mockRejectedValue(
      new Error('Erro de conexão')
    );

    await expect(
      service.editAnswer(
        '1',
        'answer-1',
        'Resposta editada.'
      )
    ).rejects.toThrow('Erro de conexão');
  });

  it('should return all posts', async () => {
    const posts = [
      {
        id: '1',
        title: 'Teste',
        content: 'Conteúdo',
        upVotes: 10,
        downVotes: 2,
        tags: [],
        createdAt: '2026-08-25'
      }
    ];

    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(posts), {
        status: 200
      })
    );

    const result = await service.findAll();

    expect(result).toEqual(posts);
  });
  it('should throw when finding all posts fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 500
      })
    );

    await expect(service.findAll())
      .rejects
      .toThrow('Erro ao buscar dúvidas');
  });

  it('should throw when finding all posts fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 500
      })
    );

    await expect(service.findAll())
      .rejects
      .toThrow('Erro ao buscar dúvidas');
  });

  it('should search posts', async () => {
    const posts = [
      {
        id: '1',
        title: 'Matrícula',
        content: 'Teste',
        upVotes: 1,
        downVotes: 0,
        tags: [],
        createdAt: '2026-08-25'
      }
    ];

    const fetchMock = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify(posts), {
        status: 200
      })
    );

    const result = await service.searchPosts('matrícula');

    expect(result).toEqual(posts);
    expect(fetchMock).toHaveBeenCalled();
  });

  it('should throw when searching posts fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 500
      })
    );

    await expect(
      service.searchPosts('teste')
    ).rejects.toThrow('Erro ao buscar dúvidas');
  });

  it('should edit a post successfully', async () => {
    const post = {
      id: '1',
      title: 'Novo título',
      content: 'Novo conteúdo',
      tags: []
    };

    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 200
      })
    );

    const result = await service.editPost(post, '1');

    expect(result).toBe(true);
  });

  it('should return false when editing a post fails', async () => {
    const post = {
      id: '1',
      title: 'Novo título',
      content: 'Novo conteúdo',
      tags: []
    };

    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    const result = await service.editPost(post, '1');

    expect(result).toBe(false);
  });

  it('should throw an error when findAll fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 500
      })
    );

    await expect(service.findAll())
      .rejects
      .toThrow('Erro ao buscar dúvidas');
  });

  it('should throw an error when searchPosts fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 500
      })
    );

    await expect(service.searchPosts('teste'))
      .rejects
      .toThrow('Erro ao buscar dúvidas');
  });

  it('should throw an error when findById fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 404
      })
    );

    await expect(service.findById('1'))
      .rejects
      .toThrow('Erro ao buscar dúvida');
  });

  it('should throw an error when upvotePost fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    await expect(service.upvotePost('1'))
      .rejects
      .toThrow('Erro ao votar positivamente na dúvida');
  });

  it('should throw an error when downvotePost fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    await expect(service.downvotePost('1'))
      .rejects
      .toThrow('Erro ao votar negativamente na dúvida');
  });

  it('should throw an error when createAnswer fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    await expect(
      service.createAnswer('1', 'Resposta teste')
    ).rejects.toThrow('Erro ao criar resposta');
  });

  it('should throw an error when upvoteAnswer fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    await expect(
      service.upvoteAnswer('1', 'answer-1')
    ).rejects.toThrow(
      'Erro ao votar positivamente na resposta'
    );
  });

  it('should throw an error when downvoteAnswer fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, {
        status: 400
      })
    );

    await expect(
      service.downvoteAnswer('1', 'answer-1')
    ).rejects.toThrow(
      'Erro ao votar negativamente na resposta'
    );
  });

  it('should return false when editing an answer fails', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, { status: 400 })
    );

    const result = await service.editAnswer(
      '1',
      'answer-1',
      'Resposta editada.'
    );

    expect(result).toBe(false);
  });
});
