import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLinkActive, RouterModule } from '@angular/router';
import { AuthserviceService } from '../../services/authservice.service';
import { Router } from '@angular/router';



@Component({
  selector: 'app-adminsidebar',
  standalone: true,
  imports: [CommonModule,RouterLinkActive,RouterModule],
  templateUrl: './adminsidebar.component.html',
  styleUrl: './adminsidebar.component.css'
})
export class AdminsidebarComponent {

constructor(public authService: AuthserviceService,public router:Router) {}
isSuperAdmin():boolean{
  return this.authService.isSuperAdmin();
}
goBack() {
  this.router.navigate(['/']);
}
 
}
