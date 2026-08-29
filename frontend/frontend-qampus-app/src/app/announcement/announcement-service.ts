import { Injectable } from '@angular/core';

export interface Announcement {
  id: string;
  title: string;
  description: string;
  type: string;
  publishedAt: string;
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

  private announcements: Announcement[] = [
    {
      id: '1',
      title: 'Matrícula do próximo período',
      description:
        'As matrículas para o próximo período estarão disponíveis a partir da próxima semana.',
      type: 'AVISO',
      publishedAt: '2026-08-27T10:00:00'
    },
    {
      id: '2',
      title: 'Semana Acadêmica',
      description:
        'A Semana Acadêmica acontecerá no próximo mês com palestras e atividades para os estudantes.',
      type: 'EVENTO',
      publishedAt: '2026-08-25T14:30:00'
    },
    {
      id: '3',
      title: 'Atualização no calendário acadêmico',
      description:
        'O calendário acadêmico foi atualizado. Consulte as novas datas disponíveis.',
      type: 'INFORMATIVO',
      publishedAt: '2026-08-20T09:00:00'
    }
  ];

  async findAll(): Promise<Announcement[]> {
    return [...this.announcements].sort((a, b) =>
      new Date(b.publishedAt).getTime() -
      new Date(a.publishedAt).getTime()
    );
  }

  async createAnnouncement(
    newAnnouncement: NewAnnouncement
  ): Promise<boolean> {

    const announcement: Announcement = {
      id: crypto.randomUUID(),
      title: newAnnouncement.title,
      description: newAnnouncement.description,
      type: newAnnouncement.type,
      publishedAt: new Date().toISOString()
    };

    this.announcements.push(announcement);

    return true;
  }
}