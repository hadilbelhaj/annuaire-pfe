import { Injectable } from '@angular/core';
import * as pdfMake from 'pdfmake/build/pdfmake';
import * as pdfFonts from 'pdfmake/build/vfs_fonts';
import { TDocumentDefinitions, Content } from 'pdfmake/interfaces';
import { TopProfessional } from '../models/Stats/healthcare-professional-stats.model';
;



@Injectable({
  providedIn: 'root'
})
export class ProfessionalsPdfExportService {
  
  constructor() { }
  
  /**
   * Generate a PDF from top professionals data
   * @param topByVisits Professionals sorted by visits
   * @param topByTransactions Professionals sorted by transactions
   * @param topByAverage Professionals sorted by average amount
   * @param activeTab The currently active tab
   */
  generateTopProfessionalsPdf(
    topByVisits: TopProfessional[], 
    topByTransactions: TopProfessional[], 
    topByAverage: TopProfessional[],
    activeTab: 'visits' | 'transactions' | 'average'
  ): void {
    const contentArray: Content[] = [];
    
    contentArray.push(this.createHeader(activeTab));
    
    // Add summary
    contentArray.push(this.createSummary(topByVisits, topByTransactions, topByAverage, activeTab));
    
    // Add tables
    contentArray.push(this.createActiveTableSection(
      topByVisits, 
      topByTransactions, 
      topByAverage, 
      activeTab
    ));
    
    // Optionally add all tables
    if (activeTab !== 'visits' && topByVisits.length > 0) {
      contentArray.push(this.createTableSection('Top Professionals by Visits', topByVisits, 'visitCount'));
    }
    
    if (activeTab !== 'transactions' && topByTransactions.length > 0) {
      contentArray.push(this.createTableSection('Top Professionals by Transactions', topByTransactions, 'transactionCount'));
    }
    
    if (activeTab !== 'average' && topByAverage.length > 0) {
      contentArray.push(this.createTableSection('Top Professionals by Average Amount', topByAverage, 'averageAmount'));
    }
    
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
        title: 'Top Professionals Report',
        author: 'System',
        subject: 'Healthcare Professionals Statistics',
        keywords: 'professionals, healthcare, statistics'
      }
    };

    pdfMake.createPdf(documentDefinition).open();
  }

  private createHeader(activeTab: 'visits' | 'transactions' | 'average'): Content {
    let title = 'Top Professionals Report';
    
    switch (activeTab) {
      case 'visits':
        title = 'Top Professionals by Visits Report';
        break;
      case 'transactions':
        title = 'Top Professionals by Transaction Volume Report';
        break;
      case 'average':
        title = 'Top Professionals by Average Amount Report';
        break;
    }
    
    return {
      stack: [
        { text: title, style: 'header' },
        { text: `Generated on ${new Date().toLocaleString()}`, style: 'note' }
      ],
      margin: [0, 0, 0, 20]
    };
  }

  private createSummary(
    topByVisits: TopProfessional[], 
    topByTransactions: TopProfessional[], 
    topByAverage: TopProfessional[],
    activeTab: 'visits' | 'transactions' | 'average'
  ): Content {
    let summaryText = '';
    let professionals: TopProfessional[] = [];
    
    switch (activeTab) {
      case 'visits':
        professionals = topByVisits;
        if (professionals.length > 0) {
          const totalVisits = professionals.reduce((sum, pro) => sum + pro.visitCount, 0);
          summaryText = `This report shows the top professionals by visit count. The professionals listed account for a total of ${totalVisits.toLocaleString()} visits.`;
        } else {
          summaryText = 'This report shows the top professionals by visit count. No data is currently available.';
        }
        break;
      case 'transactions':
        professionals = topByTransactions;
        if (professionals.length > 0) {
          const totalTransactions = professionals.reduce((sum, pro) => sum + pro.transactionCount, 0);
          const totalAmount = professionals.reduce((sum, pro) => sum + pro.totalAmount, 0);
          summaryText = `This report shows the top professionals by transaction volume. The professionals listed account for a total of ${totalTransactions.toLocaleString()} transactions, with a total value of ${this.formatAmount(totalAmount)}.`;
        } else {
          summaryText = 'This report shows the top professionals by transaction volume. No data is currently available.';
        }
        break;
      case 'average':
        professionals = topByAverage;
        if (professionals.length > 0) {
          const highestAvg = professionals[0].averageAmount;
          summaryText = `This report shows the top professionals by average transaction amount. The highest average transaction amount is ${this.formatAmount(highestAvg)}.`;
        } else {
          summaryText = 'This report shows the top professionals by average transaction amount. No data is currently available.';
        }
        break;
    }
    
    return {
      stack: [
        { text: 'Summary', style: 'subheader' },
        { text: summaryText }
      ],
      style: 'summary'
    };
  }

  private createActiveTableSection(
    topByVisits: TopProfessional[], 
    topByTransactions: TopProfessional[], 
    topByAverage: TopProfessional[],
    activeTab: 'visits' | 'transactions' | 'average'
  ): Content {
    let title = '';
    let professionals: TopProfessional[] = [];
    let highlightColumn = '';
    
    switch (activeTab) {
      case 'visits':
        title = 'Top Professionals by Visits';
        professionals = topByVisits;
        highlightColumn = 'visitCount';
        break;
      case 'transactions':
        title = 'Top Professionals by Transaction Volume';
        professionals = topByTransactions;
        highlightColumn = 'transactionCount';
        break;
      case 'average':
        title = 'Top Professionals by Average Amount';
        professionals = topByAverage;
        highlightColumn = 'averageAmount';
        break;
    }
    
    return this.createTableSection(title, professionals, highlightColumn);
  }

  private createTableSection(title: string, professionals: TopProfessional[], highlightColumn: string): Content {
    if (professionals.length === 0) {
      return {
        stack: [
          { text: title, style: 'subheader' },
          { text: 'No data available', italics: true, color: '#6b7280' }
        ],
        margin: [0, 0, 0, 20]
      };
    }
    
    // Define the columns and their widths
    const headers = [
      { text: 'Name', style: 'tableHeader' },
      { text: 'Specialty', style: 'tableHeader' },
      { text: 'Region', style: 'tableHeader' },
      { text: 'Visits', style: 'tableHeader' },
      { text: 'Transactions', style: 'tableHeader' },
      { text: 'Total Amount', style: 'tableHeader' },
      { text: 'Average Amount', style: 'tableHeader' }
    ];
    
    // Create table body rows from the data
    const body = professionals.map((pro, index) => {
      const row = [
        { text: pro.name },
        { text: pro.medicalSpecialty },
        { text: pro.region },
        { text: pro.visitCount.toLocaleString() },
        { text: pro.transactionCount.toLocaleString() },
        { text: this.formatAmount(pro.totalAmount) },
        { text: this.formatAmount(pro.averageAmount) }
      ];
      
      // Highlight the appropriate column based on the tab
      let highlightIndex = -1;
      switch (highlightColumn) {
        case 'visitCount':
          highlightIndex = 3;
          break;
        case 'transactionCount':
          highlightIndex = 4;
          break;
        case 'totalAmount':
          highlightIndex = 5;
          break;
        case 'averageAmount':
          highlightIndex = 6;
          break;
      }
      
      if (highlightIndex >= 0) {
        row[highlightIndex] = { 
          text: row[highlightIndex].text, 
          
        };
      }
      
      // Highlight the first row (top performer)
      if (index === 0) {
        return row.map(cell => {
          if (typeof cell === 'object') {
            return { ...cell, fillColor: '#EBF5FF' }; // Light blue background
          } else {
            return { text: cell, fillColor: '#EBF5FF' };
          }
        });
      }
      
      return row;
    });
    
    return {
      stack: [
        { text: title, style: 'subheader', pageBreak: 'before' },
        {
          table: {
            headerRows: 1,
            widths: ['auto', 'auto', 'auto', 'auto', 'auto', 'auto', 'auto'],
            body: [headers, ...body]
          },
          layout: {
            fillColor: function(rowIndex) {
              return (rowIndex % 2 === 0 && rowIndex > 0) ? '#f9fafb' : null;
            }
          }
        }
      ],
      margin: [0, 0, 0, 20]
    };
  }

  // Méthode pour formater les montants en TND
  private formatAmount(amount: number): string {
    return new Intl.NumberFormat('fr-FR', { 
      style: 'currency', 
      currency: 'TND',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(amount);
  }
}