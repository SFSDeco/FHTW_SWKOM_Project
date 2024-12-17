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
  public getDocumentById(id: number): Observable<Document> {
    return this.http.get<Document>(`${this.docsUrl}/file/${id}`);
  }

  public downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.docsUrl}/download/${id}`, { responseType: 'blob' });
  }

  deleteDocument(id: number): Observable<void> {
    const url = `${this.docsUrl}/delete/${id}`;  // URL für den Löschaufruf
    return this.http.delete<void>(url);
  }

  updateDocument(id: number, formData: FormData): Observable<void> {
    const url = `${this.docsUrl}/update/${id}`;
    return this.http.put<void>(url, formData);
  }

}
