
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AngularMultiSelectModule } from 'angular2-multiselect-dropdown';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { CommonFieldModule } from 'src/app/common-field/common-field.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { BinlocationComponent } from './binlocation/binlocation.component';
import { MasterStoragetabComponent } from './master-storagetab/master-storagetab.component';
import { MastersStorageNewRoutingModule } from './masters-storage-new-routing.module';
import { BinloactionNewComponent } from './binlocation/binloaction-new/binloaction-new.component';


@NgModule({
  declarations: [
    BinlocationComponent,
    MasterStoragetabComponent,
    BinloactionNewComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    CommonFieldModule,
    MastersStorageNewRoutingModule,
    NgMultiSelectDropDownModule.forRoot(),
    AngularMultiSelectModule,
  ],
  exports:[
    BinlocationComponent,
    MasterStoragetabComponent
  ]
})
export class MastersStorageNewModule { }
