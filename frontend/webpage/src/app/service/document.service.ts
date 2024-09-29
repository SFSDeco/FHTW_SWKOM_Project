import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Document } from "../model/document";
import { Observable } from "rxjs";

@Injectable(
  {providedIn : 'any'}
)
export class DocumentService {

  private readonly docsUrl: string;

  constructor(private http: HttpClient) {
    this.docsUrl = "http://localhost:8081/document";
  }

  public findAll() : Observable<Document[]> {
    const url: string = `${this.docsUrl}/all`;
    return this.http.get<Document[]>(url);
  }

  public addDocument(document : Document){
    const url : string = `${this.docsUrl}/${document.name}`
    return this.http.post<Document>(url, document);
  }
}
