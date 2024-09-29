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

  document!: Document;

  constructor(private route: ActivatedRoute, private router: Router, private documentService: DocumentService) {
    this.document = new Document();
  }

  onSubmit() {
    this.documentService.addDocument(this.document).subscribe(result => this.goToDocumentList());
  }

  goToDocumentList() {
    this.router.navigate(['/documents']).then(r =>{} );
  }

}
