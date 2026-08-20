import { Component } from '@angular/core';
import { MovementTableComponent } from '../../components/movement-table/movement-table.component';
import { AdminsidebarComponent } from '../adminsidebar/adminsidebar.component';
import { RouterModule, Routes } from '@angular/router';
import { AdminheaderComponent } from '../adminheader/adminheader.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MovementTableComponent,
    AdminsidebarComponent,
    RouterModule,
    AdminheaderComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {}
