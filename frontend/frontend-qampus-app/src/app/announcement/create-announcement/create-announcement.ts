import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {AnnouncementService, NewAnnouncement} from '../announcement-service';
import { Navbar } from '../../navbar/navbar';

@Component({
  selector: 'app-create-announcement',
  imports: [Navbar, FormsModule],
  templateUrl: './create-announcement.html',
  styleUrl: './create-announcement.css',
})
export class CreateAnnouncement {

  announcement: NewAnnouncement = {
    title: '',
    description: '',
    type: ''
  };

  constructor(
    private announcementService: AnnouncementService,
    private router: Router
  ) {}

  async publish(): Promise<void> {

    if (
      !this.announcement.title.trim() ||
      !this.announcement.description.trim() ||
      !this.announcement.type
    ) {
      alert('Todos os campos são obrigatórios.');
      return;
    }

    const response =
      await this.announcementService.createAnnouncement({
        title: this.announcement.title.trim(),
        description: this.announcement.description.trim(),
        type: this.announcement.type
      });

    if (response) {
      alert('Anúncio publicado com sucesso!');
      this.router.navigate(['/anuncios']);
    } else {
      alert('Não foi possível publicar o anúncio.');
    }
  }

  cancel(): void {
    this.router.navigate(['/anuncios']);
  }
}