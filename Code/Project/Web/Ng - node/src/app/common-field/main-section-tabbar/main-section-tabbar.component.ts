import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { CommonService } from 'src/app/common-service/common-service.service';
import { CustomdialogComponent } from '../customdialog/customdialog.component';

@Component({
  selector: 'app-main-section-tabbar',
  templateUrl: './main-section-tabbar.component.html',
  styleUrls: ['./main-section-tabbar.component.scss']
})
export class MainSectionTabbarComponent implements OnInit {
  @Input() pageSize = 2;
  @Input() section = [];
  @Input() infovalue: string | undefined;
  editbtn: boolean = false;
  higlightPage: number[] = [];
  @Output() pagechangeevent = new EventEmitter<any>();
  @Output() saveevent = new EventEmitter<any>();
  @Output() editevent = new EventEmitter<any>();
  @Output() addevent = new EventEmitter<any>();
  @Output() searchevent = new EventEmitter<any>();
  @Output() deleteevent = new EventEmitter<any>();
  pagerequired: boolean = false;
  sub = new Subscription();
  constructor(private cs: CommonService, public dialog: MatDialog) { }

  ngOnInit(): void {
    debugger;
    this.pagechangeevent.emit(this.section.slice(0 * this.pageSize, 0 * this.pageSize + this.pageSize));
    this.sub = this.cs.notifyObservable$.subscribe((res) => {
      this.higlightPage = [2];

      console.log(res);
      this.pagerequired = res;
      setTimeout(() => {
        this.pagerequired = false;
        this.higlightPage = [];
      }, 6000);

    });
  }
  onPageChange($event: any) {
    this.pagechangeevent.emit(this.section.slice($event.pageIndex * this.pageSize, $event.pageIndex * this.pageSize + this.pageSize));
  }
  save() {
    const dialogRef = this.dialog.open(CustomdialogComponent, {
      // width: '60%', height: '70%',
      position: { right: '45%', top: '10%' },
      data: { title: "Save", body: "Save Company" },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.saveevent.emit('save');
    });

  }
  edit() {
    debugger;

    this.editbtn = true;
    this.editevent.emit('edit');
  }
  add() {
    this.editbtn = true;
    this.addevent.emit('add');
  }
  search() {
    this.searchevent.emit('search');
  }
  delete() {
    this.deleteevent.emit('delete');
  }
  info() {

  }


  ngOnDestroy() {
    if (this.sub != null) {
      this.sub.unsubscribe();
    }

  }
}
