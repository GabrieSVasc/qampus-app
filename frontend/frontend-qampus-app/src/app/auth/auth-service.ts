import { Injectable } from '@angular/core';
import { User } from './user';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl+"/auth";
  
  getToken(): string | null{
    return localStorage.getItem('token');
  }

  async logout(): Promise<boolean> {
    try{
      const response = await fetch(this.apiUrl+"/logout", {
        method: 'POST',
        headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer '+localStorage.getItem('token')}
      });
      if(response.ok){
        localStorage.removeItem('token');
        return true;
      }else{
        return false;
      }
    }catch(error){
      console.error('Error logging out: ', error);
      return false;
    }
  }

  isAuthenticated(): boolean{
    return !!this.getToken();
  }

  async register(user: User): Promise<boolean>{
    try{
      const response = await fetch(this.apiUrl+"/register", {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(user)
      })
      if(!response.ok){
        return false;
      }
      const data = await response.json();
      localStorage.setItem('token', data.token);
      return true;
    }catch(error){
      console.error('Error registering user: ', error);
      return false;
    }
  }

  async login(email: string, senha: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email,
          password: senha
        })
      });

      if (!response.ok) {
        return false;
      }

      const data = await response.json();

      localStorage.setItem('token', data.token);

      return true;

    } catch (error) {
      console.error('Error logging in: ', error);
      return false;
    }
  }

  hasRole(requiredRole: string): boolean{
    const token = this.getToken();
    if(!token){
      return false;
    }
    try{
      const decodedToken: any = jwtDecode(token);
      return decodedToken.role == requiredRole;
    }catch(error){
      console.error('Token error: ', error);
      return false;
    }
  }

  getRole(): string | null {
    const token = this.getToken();

    if (!token) {
      return null;
    }

    try {
      const decodedToken: any = jwtDecode(token);
      return decodedToken.role ?? null;
    } catch (error) {
      console.error('Token error: ', error);
      return null;
    }
  }
}
