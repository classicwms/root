import { Component, OnInit } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-reportstab',
  templateUrl: './reportstab.component.html',
  styleUrls: ['./reportstab.component.scss']
})
export class ReportstabComponent implements OnInit {

  listmenu = [
    // { url: "/main/setting/business/activity", title: "Activity" },

    { url: "/main/reports/stock", title: "Stock Report",  icon: "font16   mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
    { url: "/main/reports/inventory", title: "Inventory Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
    { url: "/main/reports/stockmovement", title: "Stock Movement Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
  { url: "/main/reports/orderstatus", title: "Order Status Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
 { url: "/main/reports/shipment", title: "Shipment Delivery Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
 //{ url: "/main/reports/shipment-summary", title: "Shipment Delivery Summary Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
 //{ url: "/main/reports/shipment-dispatch", title: "Shipment Dispatch Summary Report",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },
 //{ url: "/main/reports/receipt-confirmation", title: "Report Confirmation",  icon: "font16    mb-0", class: "bg-white blue-text nav-border fw-bold  mb-0 px-4  my-auto" },

  ]
  constructor(private router: Router) {

    this.router.events.pipe(
      filter((event: any) => event instanceof NavigationEnd),
    ).subscribe(x => {
      this.activeLink = this.router.url;
    });
  }
  activeLink = this.listmenu[0].url;
  background: ThemePalette = undefined;
  // menu = [1105, 1023, 1049]
  toggleBackground() {
    this.background = this.background ? undefined : 'primary';
  }
  ngOnInit(): void {
    // this.listmenu = this.listmenu.filter(x => this.menu.includes(x.screenid))
    this.activeLink = this.router.url;
    if (this.router.url == '/#/main/reports/shipment-dispatch') {
      this.router.navigate([this.listmenu[0].url]);
      this.activeLink = this.listmenu[0].url;

    }
  }

}
