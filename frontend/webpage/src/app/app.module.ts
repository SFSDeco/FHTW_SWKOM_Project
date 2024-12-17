import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule} from "./app-routing.module";
import { AppComponent } from './app.component';
import { DocumentService } from "./service/document.service";
import { DocumentFormComponent} from "./document-form/document-form.component";
import { DocumentListComponent} from "./document-list/document-list.component";
import {DocumentUpdateComponent} from "./document-update/document-update.component";


@NgModule({
  declarations: [
    AppComponent,
    DocumentFormComponent,
    DocumentListComponent,
    DocumentUpdateComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
