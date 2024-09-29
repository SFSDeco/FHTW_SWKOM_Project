import { Component, OnInit } from '@angular/core';
import { Document } from "../model/document";
import { DocumentService } from "../service/document.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-document-list',
  templateUrl: './document-list.component.html',
  styleUrl: './document-list.component.css'
})
export class DocumentListComponent implements OnInit {

    documents!: Document[];

    constructor(private documentService : DocumentService) {
    }

    ngOnInit(){
      this.documentService.findAll().subscribe(data => {
        this.documents = data;
      })
    }
}
