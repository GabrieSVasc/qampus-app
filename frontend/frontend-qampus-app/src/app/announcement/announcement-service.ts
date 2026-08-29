import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

export interface Announcement {
  id: string;
  title: string;
  description: string;
  type: string;
  publicationDate: string;
  author: any;
}

export interface NewAnnouncement {
  title: string;
  description: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private apiUrl = environment.apiUrl + '/announcements';

  async findAll(): Promise<Announcement[]> {
    const response = await fetch(this.apiUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao buscar anúncios');
    }

    return await response.json();
  }

  async findById(id: string): Promise<Announcement> {
    const response = await fetch(`${this.apiUrl}/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (!response.ok) {
      throw new Error('Erro ao buscar anúncio');
    }

    return await response.json();
  }

  async createAnnouncement(
    announcement: NewAnnouncement
  ): Promise<boolean> {
    try {
      const response = await fetch(this.apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(announcement)
      });

      return response.ok;
    } catch (error) {
      console.error('Erro ao criar anúncio:', error);
      return false;
    }
  }

  async updateAnnouncement(
    id: string,
    announcement: NewAnnouncement
  ): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
        body: JSON.stringify(announcement)
      });

      return response.ok;
    } catch (error) {
      console.error('Erro ao atualizar anúncio:', error);
      return false;
    }
  }

  async deleteAnnouncement(id: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.apiUrl}/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
      });

      return response.ok;
    } catch (error) {
      console.error('Erro ao excluir anúncio:', error);
      return false;
    }
  }
}