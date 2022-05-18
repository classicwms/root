import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-item-group',
  templateUrl: './item-group.component.html',
  styleUrls: ['./item-group.component.scss']
})
export class ItemGroupComponent implements OnInit {
  isShowDiv = false;
  public icon = 'expand_more';
  showFloatingButtons: any;
  toggle = true;
  toggleFloat() {
    this.isShowDiv = !this.isShowDiv;  
    this.toggle = !this.toggle;

    if (this.icon === 'expand_more') {
      this.icon = 'chevron_left';
    } else {
      this.icon = 'expand_more'
    }
    this.showFloatingButtons = !this.showFloatingButtons;
    console.log('show:' + this.showFloatingButtons);
  }
  constructor() { }

  ngOnInit(): void {
  }

}
