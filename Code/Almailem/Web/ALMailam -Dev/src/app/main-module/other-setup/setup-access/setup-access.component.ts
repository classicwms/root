import { Component, OnInit } from '@angular/core';
import { MenuNewService } from 'src/app/common-service/menu-new.service';
import { MenuNew1Service } from 'src/app/common-service/menu-new1.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-setup-access',
  templateUrl: './setup-access.component.html',
  styleUrls: ['./setup-access.component.scss']
})
export class SetupAccessComponent implements OnInit {

  constructor(private menu:  MenuNewService, private auth: AuthService) {}
  navItems: any[] = [];
  menulist: any[] = [];
  menulist1: any[] = [];
  setupList: any[] = [];
  ngOnInit(): void {
    this.navItems = this.menu.getMeuList();
     this.navItems.filter(x => x.id == 2000 ? this.menulist = (x.children) : '');
    this.menulist.filter(x=> x.id == 2001 ? this.setupList = x.children : '')
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
