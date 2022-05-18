import { Component, OnInit } from '@angular/core';
import { imenulist, MenuService } from 'src/app/common-service/menu.service';
import { AuthService } from 'src/app/core/core';

@Component({
  selector: 'app-subsidenav',
  templateUrl: './subsidenav.component.html',
  styleUrls: ['./subsidenav.component.scss']
})
export class SubsidenavComponent implements OnInit {

  constructor(private ms: MenuService, private auth: AuthService) { }
  url = '/main/setup/company';
  listmenu!: any;
  menu: number[] = [];
  ngOnInit(): void {
    if (localStorage.length > 0) {
      var d = localStorage.getItem('crrentmenu') || 2000;
      if (localStorage.getItem('menu'))
        this.menu =
          JSON.parse("[" + localStorage.getItem('menu') + "]");

      this.listmenu = this.ms.getMeuList().filter(x => x.id == Number(d))[0].children?.filter(x => this.menu.includes(x.id))

    }
    else
      this.auth.logout();

  }
}
