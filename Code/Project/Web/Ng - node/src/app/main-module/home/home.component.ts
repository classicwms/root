import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { Subscription, timer } from 'rxjs';
import { map, share } from 'rxjs/operators';
import { MenuService } from 'src/app/common-service/menu.service';
import { AuthService } from 'src/app/core/core';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {


  public icon = 'menu';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {

    this.toggle = !this.toggle;

    if (this.icon === 'menu') {
      this.icon = 'arrow_back_ios_new';
    } else {
      this.icon = 'menu'
    }
  }



  username = "User 1";
  currentdate = new Date();
  // default list of  menu items
  menulist: any;

  sub = new Subscription();

  constructor(private router: Router, private ms: MenuService, private auth: AuthService) { }

  showFiller = false;

  ngOnInit(): void {
    // sidenav.toggle();
    // Using RxJS Timer
    this.username = this.auth.username.toUpperCase();
    let menu =
      JSON.parse("[" + localStorage.getItem('menu') + "]");
    this.menulist = this.ms.getMeuList().filter(x => menu.includes(x.id));
    console.log(menu);
    this.sub = timer(0, 10000)
      .pipe(
        map(() => new Date()),
        share()
      )
      .subscribe(time => {
        this.currentdate = time;
      });
  }
  logout() {
    this.auth.logout();
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
  routeto(url: any, id: any) {
    localStorage.setItem('crrentmenu', id);
    console.log(url);
    if (!url) {
      let menu =
        JSON.parse("[" + localStorage.getItem('menu') + "]");
      url = this.ms.getMeuList().filter(x => x.id == Number(id))[0].children?.filter(x => menu.includes(x.id))[0].url;
    }
    this.router.navigate([url]);
  }

///side pannel

@ViewChild('sidenav') sidenav: MatSidenav | undefined;
  isExpanded = false;
  showSubmenu: boolean = false;
  isShowing = false;
  showSubSubMenu: boolean = false;
}
