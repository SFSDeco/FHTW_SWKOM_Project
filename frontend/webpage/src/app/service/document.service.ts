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

  public addDocument(formData : FormData){
    const documentName = formData.get('name');
    if (typeof documentName === 'string') {
      const url = `${this.docsUrl}/${documentName}`;
      return this.http.post(url, formData);
    } else {
      throw new Error("Document name is missing or invalid in FormData.");
    }
  }
}
