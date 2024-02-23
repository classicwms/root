import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MaterialModule } from "./material/material.module";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { PrimeModule } from "./prime/prime.module";
// import { UppercaseDirective } from "../common/directives/uppercase.directive";
// import { MatSelectSearchModule } from '../mat-select-search/mat-select-search.module';
// import { SignaturePadModule } from 'angular2-signaturepad';

@NgModule({
  declarations: [
    // UppercaseDirective
  ],
  imports: [CommonModule, MaterialModule, ],
  exports: [
    MaterialModule,
    ReactiveFormsModule,
    FormsModule,
    PrimeModule
    
    // UppercaseDirective,
    // AgmDirectionModule,
    // MatSelectSearchModule,
    // SignaturePadModule
  ]
})
export class SharedModule { }
