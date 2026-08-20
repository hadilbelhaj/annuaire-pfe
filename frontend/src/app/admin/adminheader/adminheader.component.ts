import { NgClass } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';

@Component({
  selector: 'app-adminheader',
  standalone: true,
  imports: [NgClass],
  templateUrl: './adminheader.component.html',
  styleUrl: './adminheader.component.css'
})
export class AdminheaderComponent implements OnInit {
  isScrolled = false;
  private lastScrollPosition = 0;
  private scrollThreshold = 20; // Reduced threshold for better responsiveness

  constructor() { }

  ngOnInit(): void {
    // Check initial scroll position
    this.checkScroll();
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.checkScroll();
  }

  private checkScroll() {
    const currentScrollPosition = window.scrollY;
    
    // Show header when scrolling up, hide when scrolling down
    if (currentScrollPosition > this.lastScrollPosition && currentScrollPosition > this.scrollThreshold) {
      this.isScrolled = true;
    } else {
      this.isScrolled = false;
    }
    
    this.lastScrollPosition = currentScrollPosition;
  }
}