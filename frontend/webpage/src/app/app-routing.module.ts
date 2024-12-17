import { Routes, RouterModule } from '@angular/router';
import { NgModule } from "@angular/core";
import { DocumentListComponent} from "./document-list/document-list.component";
import { DocumentFormComponent} from "./document-form/document-form.component";
import {DownloadDocumentComponent} from "./download-document/download-document.component";

export const routes: Routes = [
  { path: 'documents', component: DocumentListComponent },
  { path: 'addDocument', component: DocumentFormComponent },
  { path: 'download/:id', component: DownloadDocumentComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
