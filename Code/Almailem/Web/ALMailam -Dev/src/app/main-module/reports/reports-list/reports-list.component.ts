import { Component, OnInit } from '@angular/core';
import { MenuNewService } from 'src/app/common-service/menu-new.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-reports-list',
  templateUrl: './reports-list.component.html',
  styleUrls: ['./reports-list.component.scss']
})
export class ReportsListComponent implements OnInit {
//   isChecked = true;
//   isShowDiv = false;
//   table = false;
//   userType = this.auth.userTypeId;
//   div1Function() {
//     this.table = !this.table;
//   }
//   showFloatingButtons: any;
//   toggle = true;
//   public icon = 'expand_more';
//   toggleFloat() {

//     this.isShowDiv = !this.isShowDiv;
//     this.toggle = !this.toggle;

//     if (this.icon === 'expand_more') {
//       this.icon = 'chevron_left';
//     } else {
//       this.icon = 'expand_more'
//     }
//     this.showFloatingButtons = !this.showFloatingButtons;

//   }
//   showFiller = false;
//   constructor(public auth: AuthService) { }

//   ngOnInit(): void {
//   }
//   disabled = false;
//   step = 0;

//   setStep(index: number) {
//     this.step = index;
//   }

//   nextStep() {
//     this.step++;
//   }

//   prevStep() {
//     this.step--;
//   }




// }


constructor(private menu:  MenuNewService, private auth: AuthService) {}
navItems: any[] = [];
reportList: any[] = [];
ngOnInit(): void {
  this.navItems = this.menu.getMeuList();
   this.navItems.filter(x => x.id == 8000 ? this.reportList = (x.children) : '');
   console.log(this.reportList)  
}



isdiabled(id: any) {
  this.auth.isMenudata();
  let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));

  if (fileterdata.length > 0) {
    return false;
  }

  return true;
}


checkMenu(id){
  let fileterdata = this.auth.MenuData.filter((x: any) => x.menuId == id  && (x.view || x.delete || x.createUpdate));

  if (fileterdata.length > 0) {
    return false;
  }

  return true;
}
}
