import { Component, OnInit } from '@angular/core';
import { DocumentService } from '../service/document.service';
import { Document } from '../model/document';
import {saveAs} from "file-saver";

@Component({
  selector: 'app-download-document',
  templateUrl: './download-document.component.html',
  styleUrls: ['./download-document.component.css']
})
export class DownloadDocumentComponent implements OnInit {
  documents: Document[] = []; // Hier definierst du das Array für die Dokumente.

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    // Hier rufst du alle Dokumente vom Service ab
    this.documentService.findAll().subscribe(
      (data: Document[]) => {
        this.documents = data;
      },
      (error) => {
        console.error('Error fetching documents', error);
      }
    );
  }

  // Methode für den Download
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

  // Methode für das Öffnen einer Datei (optional, je nach Bedarf)
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
}
