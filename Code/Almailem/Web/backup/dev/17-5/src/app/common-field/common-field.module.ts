import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomdialogComponent } from './customdialog/customdialog.component';
import { SharedModule } from '../shared/shared.module';
import { DatepickerComponent } from './datepicker/datepicker.component';
import { MainSectionTabbarComponent } from './main-section-tabbar/main-section-tabbar.component';
import { StylePaginationDirective } from '../common-directive/style-pagination.directive';
import { SubsidenavComponent } from './subsidenav/subsidenav.component';
import { InnerheaderComponent } from './innerheader/innerheader.component';
import { RouterModule } from '@angular/router';
import { BarcodeComponent } from './barcode/barcode.component';
import { DeleteComponent } from './delete/delete.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';



@NgModule({
  declarations: [
    CustomdialogComponent,
    DatepickerComponent,
    MainSectionTabbarComponent,
    StylePaginationDirective,
    SubsidenavComponent,
    InnerheaderComponent,
    BarcodeComponent,
    DeleteComponent
  ],
  imports: [
    CommonModule, SharedModule, RouterModule,   NgMultiSelectDropDownModule.forRoot(),    AngularMultiSelectModule,
  ], 
  exports: [CustomdialogComponent, MainSectionTabbarComponent, SubsidenavComponent, InnerheaderComponent, BarcodeComponent]
})
export class CommonFieldModule { }
