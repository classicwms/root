import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonFieldModule } from "src/app/common-field/common-field.module";
import { SharedModule } from "src/app/shared/shared.module";
import { PrepetualAssignComponent } from "./prepetual-assign/prepetual-assign.component";
import { PrepetualConfirmComponent } from "./prepetual-confirm/prepetual-confirm.component";
import { PrepetualCountRoutingModule } from "./prepetual-count-routing.module";
import { PrepetualCoutingComponent } from "./prepetual-couting/prepetual-couting.component";
import { AssignUserComponent } from "./prepetual-create/assign-user/assign-user.component";
import { PrepetualCreateComponent } from "./prepetual-create/prepetual-create.component";
import { PrepetualMainComponent } from "./prepetual-main/prepetual-main.component";
import { CreatePopupComponent } from './prepetual-main/create-popup/create-popup.component';
import { AssignuserPopupComponent } from './prepetual-confirm/assignuser-popup/assignuser-popup.component';



@NgModule({
  declarations: [
    PrepetualMainComponent,
    PrepetualCreateComponent,
    PrepetualAssignComponent,
    PrepetualCoutingComponent,
    AssignUserComponent,
    PrepetualConfirmComponent,
    CreatePopupComponent,
    AssignuserPopupComponent,
  ],
  imports: [
    CommonModule,
    PrepetualCountRoutingModule,
    SharedModule,
    CommonFieldModule
  ],
  exports: [
    PrepetualMainComponent,
    PrepetualCreateComponent,
    PrepetualAssignComponent,
    PrepetualCoutingComponent,
    AssignUserComponent,
    PrepetualConfirmComponent,
    CreatePopupComponent,
  ]
})
export class PrepetualCountModule { }
