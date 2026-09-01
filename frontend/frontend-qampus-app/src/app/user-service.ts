import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../environments/environment';
import { Answer } from './post/post-service';
import { Post } from './post/post-service';

export interface User {
  id: string,
  name: string,
  email: string,
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  apiUrl = environment.apiUrl + "/user";

  async getUser(): Promise<User> {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken: any = jwtDecode(token);
      const email = decodedToken.sub;

      const response = await fetch(this.apiUrl + "/" + email, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
      });
      const data = await response.json();
      return data;
    } else {
      throw new Error('Ninguem está logado');
    }
  }

  async getAnswers(id: string): Promise<Answer[]> {
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

  async getPosts(id: string): Promise<Post[]> {
    const response = await fetch(this.apiUrl + "/" + id + "/posts", {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });
    const data = await response.json();
    return data;
  }
}
