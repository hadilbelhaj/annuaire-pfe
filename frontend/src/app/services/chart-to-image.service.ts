import { Injectable } from '@angular/core';
import html2canvas from 'html2canvas';

@Injectable({
  providedIn: 'root'
})
export class ChartToImageService {
  
  /**
   * Convert a chart element to a base64 image
   * @param chartElement The chart element reference
   * @returns Promise with base64 string
   */
  convertChartToImage(chartElement: HTMLElement): Promise<string> {
    return new Promise((resolve, reject) => {
      try {
        // Use html2canvas if available
        if (typeof html2canvas !== 'undefined') {
          this.useHtml2Canvas(chartElement, resolve);
        } else {
          // Fallback to SVG approach if the chart is an SVG
          const svgElement = chartElement.querySelector('svg');
          if (svgElement) {
            this.convertSvgToImage(svgElement, resolve);
          } else {
            reject(new Error('No suitable chart element found and html2canvas not available'));
          }
        }
      } catch (error) {
        reject(error);
      }
    });
  }

  /**
   * Use html2canvas to convert any HTML element to an image
   * @param element HTML element to convert
   * @param callback Function to call with the result
   */
  private useHtml2Canvas(element: HTMLElement, callback: (base64: string) => void): void {
    // This assumes html2canvas is loaded as a global
    (window as any).html2canvas(element, {
      scale: 2, // Higher quality
      useCORS: true,
      logging: false
    }).then((canvas: HTMLCanvasElement) => {
      callback(canvas.toDataURL('image/png'));
    });
  }

  /**
   * Convert an SVG element to a base64 image
   * @param svgElement The SVG element
   * @param callback Function to call with the result
   */
  private convertSvgToImage(svgElement: SVGElement, callback: (base64: string) => void): void {
    // Get SVG data
    const svgData = new XMLSerializer().serializeToString(svgElement);
    const svgBlob = new Blob([svgData], { type: 'image/svg+xml;charset=utf-8' });
    const url = URL.createObjectURL(svgBlob);
    
    // Create image to convert the SVG
    const img = new Image();
    img.onload = () => {
      // Create canvas to draw the image
      const canvas = document.createElement('canvas');
      canvas.width = svgElement.getBoundingClientRect().width * 2; // Double for quality
      canvas.height = svgElement.getBoundingClientRect().height * 2; // Double for quality
      
      const context = canvas.getContext('2d');
      if (context) {
        context.drawImage(img, 0, 0, canvas.width, canvas.height);
        callback(canvas.toDataURL('image/png'));
      }
      
      // Clean up
      URL.revokeObjectURL(url);
    };
    
    img.onerror = () => {
      URL.revokeObjectURL(url);
      callback(''); // Return empty if there's an error
    };
    
    img.src = url;
  }
}