import { Injectable } from '@angular/core';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import { Content, TDocumentDefinitions, TableCell } from 'pdfmake/interfaces';
(pdfMake as any).vfs = pdfFonts.vfs;

@Injectable({
  providedIn: 'root'
})
export class PdfExportService {
  
  
  constructor() { }
  
  /**
   * Generate a PDF from prestation statistics data
   * @param prestations The prestation data array
   * @param chartBase64 Base64 representation of the chart image
   */
  generatePrestationPdf(prestations: any[], chartBase64?: string): void {
    // Create content array with non-null elements only
    const contentArray: Content[] = [];
    
    // Add header
    contentArray.push(this.createHeader());
    
    // Add summary
    contentArray.push(this.createSummary(prestations));
    
    // Add chart section if available
    if (chartBase64) {
      contentArray.push(this.createChartSection(chartBase64));
    }
    
    // Add table section
    contentArray.push(this.createTableSection(prestations));
    
    const documentDefinition: TDocumentDefinitions = {
      content: contentArray,
      styles: {
        header: {
          fontSize: 18,
          bold: true,
          margin: [0, 0, 0, 10]
        },
        subheader: {
          fontSize: 14,
          bold: true,
          margin: [0, 10, 0, 5]
        },
        tableHeader: {
          bold: true,
          fillColor: '#f3f4f6'
        },
        summary: {
          margin: [0, 0, 0, 20]
        },
        note: {
          fontSize: 10,
          italics: true,
          color: '#6b7280'
        }
      },
      defaultStyle: {
        fontSize: 10
      },
      footer: (currentPage, pageCount) => {
        return {
          text: `Page ${currentPage} of ${pageCount}`,
          alignment: 'center',
          fontSize: 8,
          margin: [0, 10, 0, 0]
        };
      },
      info: {
        title: 'Prestation Analysis Report',
        author: 'System',
        subject: 'Prestation Statistics',
        keywords: 'prestations, analysis, statistics'
      }
    };

    pdfMake.createPdf(documentDefinition).open();
  }

  private createHeader(): Content {
    return {
      stack: [
        { text: 'Prestation Analysis Report', style: 'header' },
        { text: `Generated on ${new Date().toLocaleString()}`, style: 'note' }
      ],
      margin: [0, 0, 0, 20]
    };
  }

  private createSummary(prestations: any[]): Content {
    const totalCount = prestations.reduce((sum, item) => sum + item.count, 0);
    
    return {
      stack: [
        { text: 'Summary', style: 'subheader' },
        { 
          text: [
            `This report shows the `,
            { text: `top ${prestations.length} `, bold: true },
            `most frequent prestations. `,
            `Total number of prestations analyzed: `,
            { text: `${totalCount}`, bold: true },
            '.'
          ] 
        }
      ],
      style: 'summary'
    };
  }

  private createChartSection(chartBase64: string): Content {
    return {
      stack: [
        { text: 'Prestation Distribution Chart', style: 'subheader' },
        {
          image: chartBase64,
          width: 500,
          alignment: 'center',
          margin: [0, 10, 0, 10]
        }
      ],
      margin: [0, 0, 0, 20]
    };
  }

  private createTableSection(prestations: any[]): Content {
    // Extract all possible keys from the prestations array
    const allKeys = new Set<string>();
    prestations.forEach(prestation => {
      Object.keys(prestation).forEach(key => allKeys.add(key));
    });
    
    // Remove any internal or unnecessary fields
    const excludedKeys = ['_id', 'id', '__v'];
    const keys = [...allKeys].filter(key => !excludedKeys.includes(key));
    
    // Format keys for header (capitalize first letter)
    const formattedHeaders = keys.map(key => ({
      text: key.charAt(0).toUpperCase() + key.slice(1),
      style: 'tableHeader'
    }));
    
    // Create table body rows from the data
    const body = prestations.map(prestation => {
      return keys.map(key => {
        const value = prestation[key];
        
        // Format value based on type
        if (typeof value === 'number') {
          return value.toString();
        } else if (typeof value === 'boolean') {
          return value ? 'Yes' : 'No';
        } else if (value instanceof Date) {
          return value.toLocaleString();
        } else if (value === null || value === undefined) {
          return '-';
        } else {
          return value.toString();
        }
      });
    });
    
    return {
      stack: [
        { text: 'Prestation Details', style: 'subheader' },
        {
          table: {
            headerRows: 1,
            widths: Array(keys.length).fill('*'),
            body: [formattedHeaders, ...body]
          },
          layout: {
            fillColor: function(rowIndex) {
              return (rowIndex % 2 === 0) ? '#f9fafb' : null;
            }
          }
        }
      ]
    };
  }
}