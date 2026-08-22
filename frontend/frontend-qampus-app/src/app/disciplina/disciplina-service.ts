import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface NewDisciplina{
  nome: string,
  idCurso: number
}

export interface Disciplina{
  codigo: number,
  nome: string,
  nomeCurso: string
}

@Injectable({
  providedIn: 'root',
})
export class DisciplinaService {
  apiUrl = environment.apiUrl +"/disciplinas";

  async create(nova: NewDisciplina){
    const response = await fetch(this.apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
      body: JSON.stringify(nova)
    });
    return response.ok;
  }

  async getAll(): Promise<Disciplina[]>{
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

  async getById(id: number): Promise<Disciplina>{
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

  async remove(id: number){
    const response = await fetch(this.apiUrl+"/"+id, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
    });

    return response.ok;
  }

  async edit(disciplina: NewDisciplina, id: number){
    const response = await fetch(this.apiUrl+"/"+id, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '+localStorage.getItem('token')
      },
      body: JSON.stringify(disciplina)
    });
    return response.ok;
  }
}
