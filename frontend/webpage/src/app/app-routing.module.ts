import { Routes, RouterModule } from '@angular/router';
import { NgModule } from "@angular/core";
import { DocumentListComponent} from "./document-list/document-list.component";
import { DocumentFormComponent} from "./document-form/document-form.component";
import {DocumentUpdateComponent} from "./document-update/document-update.component";

export const routes: Routes = [
  { path: 'documents', component: DocumentListComponent },
  { path: 'addDocument', component: DocumentFormComponent },
  { path: 'update/:id', component: DocumentUpdateComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
