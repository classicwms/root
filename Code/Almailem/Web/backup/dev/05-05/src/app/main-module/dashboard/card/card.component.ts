import { ArrayDataSource } from '@angular/cdk/collections';
import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Food data with nested structure.
 * Each node has a name and an optiona list of children.
 */

/** Flat node with expandable and status:'Completed',value:5,url:'/',level information */
interface treelading {
  expandable: boolean;
  name: string;
  status: string, value: number, url: string, level: number;
  isExpanded?: boolean;
}


/** Flat node with expandable and status:'Completed',value:5,url:'/',level information */
interface ExampleFlatNode {
  expandable: boolean;
  name: string;
  status: string, value: number, url: string, level: number;

  isExpanded?: boolean;
}


@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {

  datatree: ExampleFlatNode[] = [
    {
      name: 'Setup',
      expandable: true,
      status: 'Completed', value: 5, url: '/', level: 0,
    }, {
      name: 'Company',
      expandable: false,
      status: 'Completed', value: 5, url: '/main/1/company', level: 1,
    }, {
      name: 'Plant',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 1,
    }, {
      name: 'Fruit loops',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 1,
    }, {
      name: 'Vegetables',
      expandable: true,
      status: 'Completed', value: 5, url: '/', level: 0,
    }, {
      name: 'Green',
      expandable: true,
      status: 'Completed', value: 5, url: '/', level: 1,
    }, {
      name: 'Broccoli',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 2,
    }, {
      name: 'Brussels sprouts',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 2,
    }, {
      name: 'Orange',
      expandable: true,
      status: 'Completed', value: 5, url: '/', level: 1,
    }, {
      name: 'Pumpkins',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 2,
    }, {
      name: 'Carrots',
      expandable: false,
      status: 'Completed', value: 5, url: '/', level: 2,
    }
  ];;
  // treeControl = new FlatTreeControl<treelading>(
  //   node => node.status:'Completed',value:5,url:'/',level, node => node.expandable);

  dataSource = new ArrayDataSource(this.datatree);

  // hasChild = (_: number, node: treelading) => node.expandable;
  treeControl = new FlatTreeControl<ExampleFlatNode>(
    node => node.level, node => node.expandable);

  // dataSource = new ArrayDataSource(TREE_DATA);

  hasChild = (_: number, node: ExampleFlatNode) => node.expandable;

  @Input() menulist: any;

  constructor(private router: Router) { }

  ngOnInit(): void {
    var json = [
      {
        name: 'Fruit',
        expandable: true,
        status: 'Completed', value: 5, url: '/', level: 0,
      }, {
        name: 'Apple',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 1,
      }, {
        name: 'Banana',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 1,
      }, {
        name: 'Fruit loops',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 1,
      }, {
        name: 'Vegetables',
        expandable: true,
        status: 'Completed', value: 5, url: '/', level: 0,
      }, {
        name: 'Green',
        expandable: true,
        status: 'Completed', value: 5, url: '/', level: 1,
      }, {
        name: 'Broccoli',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 2,
      }, {
        name: 'Brussels sprouts',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 2,
      }, {
        name: 'Orange',
        expandable: true,
        status: 'Completed', value: 5, url: '/', level: 1,
      }, {
        name: 'Pumpkins',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 2,
      }, {
        name: 'Carrots',
        expandable: false,
        status: 'Completed', value: 5, url: '/', level: 2,
      }
    ];
    var r: treelading[] = json
    // this.dataSource = new ArrayDataSource(r);
  }
  getParentNode(node: ExampleFlatNode) {
    const nodeIndex = this.datatree.indexOf(node);
    for (let i = nodeIndex - 1; i >= 0; i--) {
      if (this.datatree[i].level === node.level - 1) {
        return this.datatree[i];
      }
    }

    return null;
  }

  shouldRender(node: ExampleFlatNode) {
    let parent = this.getParentNode(node);
    while (parent) {
      if (!parent.isExpanded) {
        return false;
      }
      parent = this.getParentNode(parent);
    }
    return true;
  }

  // getParentNode(node: treelading) {
  //   const nodeIndex = this.datatree.indexOf(node);

  //   for (let i = nodeIndex - 1; i >= 0; i--) {
  //     if (this.datatree[i].status:'Completed',value:5,url:'/',level === node.status:'Completed',value:5,url:'/',level - 1) {
  //       return this.datatree[i];
  //     }
  //   }

  //   return null;
  // }

  // shouldRender(node: treelading) {
  //   let parent = this.getParentNode(node);
  //   while (parent) {
  //     if (!parent.isExpanded) {
  //       return false;
  //     }
  //     parent = this.getParentNode(parent);
  //   }
  //   return true;
  // }
  btnClick = (url: any) => {
    alert(url);
    if (url)
      this.router.navigate([url]);
  }
}
