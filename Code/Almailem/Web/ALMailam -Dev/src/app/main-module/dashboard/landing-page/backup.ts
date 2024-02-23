import { Component, OnInit } from '@angular/core';
import { MenuNewService } from 'src/app/common-service/menu-new.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent implements OnInit {

  constructor(private menu:  MenuNewService, private auth: AuthService) {}


  navItems: any[] = [];
  menulist: any[] = [];
  ngOnInit(): void {
    this.navItems = this.menu.getMeuList();
    this.navItems.filter(x => x.id != 1000 ? this.menulist.push(x) : '');
    console.log(this.menulist)
  }

  checkModule(id){
    let fileterdata = this.auth.MenuData.filter((x: any) => x.moduleId == id  && (x.view || x.delete || x.createUpdate));

    if (fileterdata.length > 0) {
      return false;
    }
  
    return true;
  }

  
  checkMenu(id){
    console.log(id)
    let fileterdata = this.auth.MenuData.filter((x: any) => x.menuId == id  && (x.view || x.delete || x.createUpdate));
    if (fileterdata.length > 0) {
      return false;
    }
    return true;
  }
}
