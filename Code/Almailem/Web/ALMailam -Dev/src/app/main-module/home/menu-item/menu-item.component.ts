import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { menuIdList } from 'src/app/common-service/menu-new.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent implements OnInit {

  @Input() items: menuIdList[];
 @ViewChild('childMenu', { static: true }) public childMenu: any;

  constructor(public router: Router, private auth : AuthService) {
  }

  ngOnInit() {
    let menu = [1000, 1001, 2101, 2102, 2103, 2104, 2105, 2106];
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



  checkMenu(id){
    let fileterdata = this.auth.MenuData.filter((x: any) => x.menuId == id  && (x.view || x.delete || x.createUpdate));
    
    if (fileterdata.length > 0) {
      return false;
    }
  
    return true;
  }
}
