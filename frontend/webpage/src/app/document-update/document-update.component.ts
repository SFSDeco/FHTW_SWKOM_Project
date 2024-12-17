import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DocumentService } from '../service/document.service';
import { Document } from '../model/document';

@Component({
  selector: 'app-document-update',
  templateUrl: './document-update.component.html',
  styleUrls: ['./document-update.component.css']
})
export class DocumentUpdateComponent implements OnInit {
  document! : Document;
  selectedFile!: File;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private documentService: DocumentService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.documentService.getDocumentById(id).subscribe(
      (data) => this.document = data,
      (error) => console.error('Error fetching document because no idea why', error)
    );
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
    }
  }

  updateDocument(): void {
    const formData = new FormData();
    formData.append('name', this.document.name);
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    this.documentService.updateDocument(this.document.id, formData).subscribe(
      () => this.router.navigate(['/documents']),
      (error: any) => console.error('Error updating document', error)
    );
  }
}
