import { SelectionModel } from "@angular/cdk/collections";
import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { SetupStorageService } from "../setup-storage.service";
import { StrategiesNewComponent } from "./strategies-new/strategies-new.component";
import { StrategyPopupComponent } from "./strategy-popup/strategy-popup.component";

export interface clientcategory {
  no: string;
  type: string;
  seq: string;
  one: string;
  two: string;
  three: string;
  four: string;
  five: string;
  six: string;
  seven: string;
  eight: string;
  nine: string;
  ten: string;
}

export interface ItemGroup {
  priority?: number;
  sequenceIndicator?: number;
  strategyNo?: string;
  strategyTypeId?: number;
  createdBy?: string;
  createdOn?: any;
}


const ELEMENT_DATA: ItemGroup[] = [
  { priority: 1, strategyNo: '12', strategyTypeId: 2,  createdBy: 'Fixed Bin', createdOn: 'Next Avl Bin',},
];
@Component({
  selector: 'app-strategies',
  templateUrl: './strategies.component.html',
  styleUrls: ['./strategies.component.scss']
})
export class StrategiesComponent implements OnInit {
  title1 = "Storage Setup";
  title2 = "Strategies";
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


  constructor(
    public dialog: MatDialog, private setupStorageService: SetupStorageService, private router: Router) { }
  ngOnInit(): void {
   // this.getStrategies();
  }

  displayedColumns: string[] = ['select', 'type', 'seq', 'three', 'four',];
  dataSource = new MatTableDataSource<ItemGroup>();
  selection = new SelectionModel<ItemGroup>(true, []);

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  new(): void {

    const dialogRef = this.dialog.open(StrategiesNewComponent, {
      disableClose: true,
     // width: '100%',
      minWidth: '90%',
      //position: { top: '12.5%', },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /** The label for the checkbox on the passed row */
  // checkboxLabel(row?: clientcategory): string {
  //   if (!row) {
  //     return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
  //   }
  //   return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.no + 1}`;
  // }
  clearselection(row: any) {
    this.selection.clear();
    this.selection.toggle(row);
  }

  getStrategies() {
    this.setupStorageService.getStorageSetupList('strategy').subscribe(
      result => {
        console.log(result);
        this.dataSource = new MatTableDataSource(result);
      },
      error => {
        console.log(error);
      }
    );
  }

  onEditClick()
  {
    const dialogRef = this.dialog.open(StrategyPopupComponent, {
      disableClose: true,
      width: '100%',
      maxWidth: '65%',
      position: { top: '12.5%', },
      data: this.selection.selected[0].strategyNo
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
}
