import { Component, OnInit } from '@angular/core';
import {Announcement, AnnouncementService} from '../announcement-service';
import { Navbar } from '../../navbar/navbar';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-announcement-list',
  imports: [Navbar],
  templateUrl: './announcement-list.html',
  styleUrl: './announcement-list.css',
})
export class AnnouncementList implements OnInit {

  announcements: Announcement[] = [];

  constructor(
    private announcementService: AnnouncementService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    try {
      this.announcements = await this.announcementService.findAll();
      this.cdr.detectChanges();
    } catch (error) {
      console.error('Erro ao carregar anúncios:', error);
    }
  }
}