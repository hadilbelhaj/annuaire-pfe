import {
  Component,
  OnInit,
  AfterViewInit,
  OnDestroy,
  PLATFORM_ID,
  Inject,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Subscription } from 'rxjs';
import { ProfessionalService } from '../../services/professional.service';
import { Professional } from '../../models/professional.modal';
import { Page } from '../../models/page.modal';
import e from 'express';
declare let L: any;
@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private map: any;
  private markers: any[] = [];
  private subscription: Subscription | undefined;
  isLoading = true;
  error: string | null = null;
  isBrowser: boolean;

  constructor(
    private professionalService: ProfessionalService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);

    // Only subscribe if we're in the browser
    if (this.isBrowser) {
      this.subscription = this.professionalService.professionals$.subscribe({
        next: (page: Page<Professional>) => {
          if (this.map) {
            this.updateMarkers(page.content);
            this.isLoading = false;
          }
        },
        error: (err) => {
          this.error = 'Error loading map data';
          this.isLoading = false;
        },
      });
    }
  }

  ngAfterViewInit() {
    // Only initialize map if we're in the browser
    if (this.isBrowser) {
      this.initMap();
    }
  }

  ngOnDestroy() {
    if (this.isBrowser) {
      if (this.map) {
        this.map.remove();
      }
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
    }
  }

  private initMap(): void {
    import('leaflet')
      .then((L) => {
        const mapElement = document.getElementById('map');
        if (!mapElement) {
          console.error('Map element not found in the DOM!');
          return;
        }

        this.map = L.map('map').setView([34.0, 9.0], 6);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 19,
          attribution: '© OpenStreetMap contributors',
        }).addTo(this.map);
      })
      .catch((err) => {
        console.error('Error loading Leaflet:', err);
      });
  }

  private updateMarkers(professionals: Professional[]): void {
    if (!this.isBrowser) return;

    
    this.markers.forEach((marker) => marker.remove());
    this.markers = [];

    
    professionals.forEach((pro) => {
      this.geocodeAddress(pro.address, pro)
    });
  }

  private geocodeAddress(address: string, professional: Professional): void {
    if (!this.isBrowser) return;
  
    const encodedAddress = encodeURIComponent(address);
    const photonUrl = `https://photon.komoot.io/api/?q=${encodedAddress}`;
  
    fetch(photonUrl)
      .then((response) => response.json())
      .then((data) => {
        if (data.features && data.features.length > 0) {
          import('leaflet').then((L) => {
            const coordinates = data.features[0].geometry.coordinates;
            const marker = L.marker([coordinates[1], coordinates[0]])
              .addTo(this.map)
              .bindPopup(`
                <div class="font-bold">${professional.name}</div>
                <div>${professional.address}</div>
              `);
  
            this.markers.push(marker);
          });
        }
      })
      .catch((error) => console.error('Error geocoding address:', error));
  }
  
}
