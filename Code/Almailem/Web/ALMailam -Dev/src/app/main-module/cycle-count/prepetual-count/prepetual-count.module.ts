import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { PrepetualConfirmComponent } from "./prepetual-confirm/prepetual-confirm.component";
import { PrepetualCountRoutingModule } from "./prepetual-count-routing.module";
import { PrepetualMainComponent } from "./prepetual-main/prepetual-main.component";
import { CreatePopupComponent } from './prepetual-main/create-popup/create-popup.component';
import { AssignuserPopupComponent } from './prepetual-confirm/assignuser-popup/assignuser-popup.component';
import { PerpetualVarianceConfirmComponent } from './perpetual-variance-confirm/perpetual-variance-confirm.component';
import { PerpetualVarianceComponent } from './perpetual-variance/perpetual-variance.component';
import { PerpeatualTabComponent } from './perpeatual-tab/perpeatual-tab.component';



@NgModule({
  declarations: [
    PrepetualMainComponent,
    PrepetualConfirmComponent,
    CreatePopupComponent,
    AssignuserPopupComponent,
    PerpetualVarianceConfirmComponent,
    PerpetualVarianceComponent,
    PerpeatualTabComponent,
  ],
  imports: [
    CommonModule,
    PrepetualCountRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    PrepetualMainComponent,
    PrepetualConfirmComponent,
    CreatePopupComponent,
  ]
})
export class PrepetualCountModule { }
