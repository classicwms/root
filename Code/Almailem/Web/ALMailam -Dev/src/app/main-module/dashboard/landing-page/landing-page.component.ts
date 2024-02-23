import { Component, OnInit } from '@angular/core';
import { landingPageService } from 'src/app/common-service/landingPage.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent implements OnInit {

  constructor(private menu:  landingPageService, private auth: AuthService) {}


  navItems: any[] = [];
  menulist: any[] = [];
  ngOnInit(): void {
    this.navItems = this.menu.getMeuList();
    this.navItems.filter(x => x.id != 1000 ? this.menulist.push(x) : '');
  }

  checkModule(id){
    let fileterdata = this.auth.MenuData.filter((x: any) => x.moduleId == id  && (x.view || x.delete || x.createUpdate));

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

  isdiabled(id: any) {
    if(id == 2001){
      return false
    }
    this.auth.isMenudata();
    let fileterdata = this.auth.MenuData.filter((x: any) => x.subMenuId == id && (x.view || x.delete || x.createUpdate));
  
    if (fileterdata.length > 0) {
      return false;
    }
  
    return true;
  }
}
