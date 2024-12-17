import { Component, OnInit } from '@angular/core';
import { DocumentService } from "../service/document.service";
import { Document } from "../model/document";
import { Router } from '@angular/router';
import {saveAs} from "file-saver";

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.component.html',
  styleUrls: ['./document-list.component.css']
})
export class DocumentListComponent implements OnInit {
  documents: Document[] = [];
  filteredDocuments: Document[] = [];
  searchQuery: string = '';
  noDocumentFound: boolean = false;

  constructor(private documentService: DocumentService, private router: Router) {}

  ngOnInit(): void {
    this.loadDocuments(); // Initiale Dokumente laden
  }

  // Methode zum Laden der Dokumente
  loadDocuments(): void {
    this.documentService.findAll().subscribe(
      (data) => {
        this.documents = data;
        this.filteredDocuments = data;
      },
      (error) => {
        console.error('Error loading documents', error);
      }
    );
  }

  onSearch(): void {
    if (this.searchQuery.trim()) {
      this.filteredDocuments = this.documents.filter(doc =>
        doc.name.toLowerCase().includes(this.searchQuery.toLowerCase())
      );
      this.noDocumentFound = this.filteredDocuments.length === 0;
    } else {
      this.filteredDocuments = this.documents;
      this.noDocumentFound = false;
    }
  }



  deleteDocument(id: number): void {
    this.documentService.deleteDocument(id).subscribe(
      response => {
        console.log('Document deleted successfully');
        this.loadDocuments(); // Neue Liste laden
      },
      error => {
        console.error('Error deleting document', error);
      }
    );
  }
  downloadFile(id: number): void {
    this.documentService.downloadFile(id).subscribe(
      (pdfBlob: Blob) => {
        saveAs(pdfBlob, `document-${id}.pdf`);
      },
      (error) => {
        console.error('Error downloading file', error);
      }
    );
  }

  openFile(id: number): void {

    this.documentService.downloadFile(id).subscribe(
      (pdfBlob: Blob) => {
        const fileUrl = URL.createObjectURL(pdfBlob);
        window.open(fileUrl, '_blank');
      },
      (error) => {
        console.error('Error opening file', error);
      }
    );
  }

  navigateToUpdate(documentId: number): void {
    this.router.navigate([`/update/${documentId}`]); // Navigiert zur Route /update/:id`
  }

}
