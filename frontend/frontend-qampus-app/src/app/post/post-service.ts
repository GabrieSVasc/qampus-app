import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Tag {
  id: string;
  name: string;
}

export interface Post {
  id: string;
  title: string;
  content: string;
  upVotes: number;
  downVotes: number;
  tags: Tag[];
  createdAt: string;
}

export interface NewPost {
  title: string,
  content: string,
  tags: string[]
}

export interface EditPostI {
  id: string,
  title: string,
  content: string,
  tags: string[]
}

export interface Answer {
  id: string;
  content: string;
  postId: string;
  createdAt: string;
  upVotes: number;
  downVotes: number;
}

export interface PostRecomend {
  id: string;
  title: string;
  voteBalance: number;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiUrl = environment.apiUrl + "/post";

  async findAll(): Promise<Post[]> {
    const response = await fetch(this.apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao buscar dúvidas');
    }

    return await response.json();
  }

  async searchPosts(busca: string): Promise<Post[]> {
    const response = await fetch(
      `${this.apiUrl}/search?busca=${encodeURIComponent(busca)}`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
      }
    );

    if (!response.ok) {
      throw new Error('Erro ao buscar dúvidas');
    }

    return await response.json();
  }

  async createPost(post: NewPost) {
    try {
      const response = await fetch(this.apiUrl + "/create", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(post)
      })
      return response.ok;
    } catch (error) {
      console.error("Error creating new Post: ", error);
      return false;
    }
  }

  async findById(id: string): Promise<Post> {
    const response = await fetch(`${this.apiUrl}/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao buscar dúvida');
    }

    return await response.json();
  }

  async upvotePost(id: string): Promise<Post> {
    const response = await fetch(`${this.apiUrl}/${id}/upvote`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao votar positivamente na dúvida');
    }

    return await response.json();
  }

  async downvotePost(id: string): Promise<Post> {
    const response = await fetch(`${this.apiUrl}/${id}/downvote`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao votar negativamente na dúvida');
    }

    return await response.json();
  }

  async editPost(post: EditPostI, id: string) {
    const response = await fetch(`${this.apiUrl}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      body: JSON.stringify(post),
    });
    return response.ok;
  }

  async createAnswer(postId: string, content: string): Promise<Answer> {
    const response = await fetch(`${this.apiUrl}/${postId}/answer`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify({
        postId,
        content
      })
    });

    const data = await response;
    console.log(data);
    if (!response.ok) {
      throw new Error('Erro ao criar resposta');
    }

    return data.json();
  }

  async upvoteAnswer(postId: string, answerId: string): Promise<Answer> {
    const response = await fetch(
      `${this.apiUrl}/${postId}/answer/${answerId}/upvote`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
      }
    );

    if (!response.ok) {
      throw new Error('Erro ao votar positivamente na resposta');
    }

    return await response.json();
  }

  async downvoteAnswer(postId: string, answerId: string): Promise<Answer> {
    const response = await fetch(
      `${this.apiUrl}/${postId}/answer/${answerId}/downvote`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
      }
    );

    if (!response.ok) {
      throw new Error('Erro ao votar negativamente na resposta');
    }

    return await response.json();
  }

  async getAnswersPost(id: string): Promise<Answer[]> {
    const response = await fetch(this.apiUrl + "/" + id + "/answers", {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });
    const data = await response.json();
    return data;
  }

  async editAnswer(idPost: string, idAnswer: string, content: string) {
    const response = await fetch(this.apiUrl + "/" + idPost + "/answer/" + idAnswer, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
      body: JSON.stringify({
        content: content
      })
    })

    return response.ok;
  }

  async recommendation(id: string): Promise<PostRecomend[]> {
    const response = await fetch(this.apiUrl + "/" + id + "/recommend", {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      },
    })
    const data = await response.json();
    return data;
  }
}