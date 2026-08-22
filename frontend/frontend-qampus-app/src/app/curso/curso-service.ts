import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface NewCurso{
  bloco: string,
  nome: string,
  email: string
}

export interface Curso{
    id: number,
    bloco: string,
    nome: string,
    email: string
}

@Injectable({
  providedIn: 'root',
})
export class CursoService {
  private apiUrl = environment.apiUrl + "/cursos";

  async getCursos(): Promise<Curso[]>{
    const response = await fetch(this.apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      }
    });
    if(!response.ok){
      throw new Error('Erro ao buscar os cursos');
    }

    return await response.json();
  }

  async createCurso(curso: NewCurso){
    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
      body: JSON.stringify(curso)
    });
    return response.ok;
  }

  async editCurso(curso: Curso){
    const response = await fetch(this.apiUrl+"/"+curso.id, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
      body: JSON.stringify(curso)
    });
    return response.ok;
  }

  async removeCurso(id: number){
    const response = await fetch(this.apiUrl+"/"+id, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
    });

    return response.ok;
  }

  async getCurso(id: number): Promise<Curso>{
    const response = await fetch(this.apiUrl+"/"+id, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
    });
    if(!response.ok){
      throw new Error('Erro ao buscar o curso');
    }

    return await response.json();
  }
}
