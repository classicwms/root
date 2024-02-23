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
    if (sessionStorage.length > 0) {
      var d = sessionStorage.getItem('crrentmenu') || 2000;
      if (sessionStorage.getItem('menu'))
        this.menu =
          JSON.parse("[" + sessionStorage.getItem('menu') + "]");

          if(sessionStorage.getItem('masterProduct') != null && sessionStorage.getItem('masterProduct') != undefined)
          {
            this.listmenu = this.ms.getMeuList('edit').filter(x => x.id == Number(d))[0].children?.filter(x => this.menu.includes(x.id))
    }
    else
    {
      this.listmenu = this.ms.getMeuList('save').filter(x => x.id == Number(d))[0].children?.filter(x => this.menu.includes(x.id))
    }

    }
    else
      this.auth.logout();

  }
}
