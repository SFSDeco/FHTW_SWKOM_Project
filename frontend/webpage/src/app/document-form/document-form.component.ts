import { Component } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { DocumentService } from "../service/document.service";
import { Document } from "../model/document";

@Component({
  selector: 'app-document-form',
  templateUrl: './document-form.component.html',
  styleUrl: './document-form.component.css'
})
export class DocumentFormComponent {

  document! : Document;

  constructor(private route: ActivatedRoute, private router: Router, private documentService: DocumentService) {
    this.document = new Document();
  }

  onFileSelected(event: any): void{
    this.document.selectedFile = event.target.files[0];
  }

  onSubmit() {
    if (this.document.selectedFile){
      const formData = new FormData();
      formData.append('name',this.document.name);
      formData.append('file',this.document.selectedFile);
      this.documentService.addDocument(formData).subscribe(result => this.goToDocumentList());
    }
  }

  goToDocumentList() {
    this.router.navigate(['/documents']).then(r =>{} );
  }

}
