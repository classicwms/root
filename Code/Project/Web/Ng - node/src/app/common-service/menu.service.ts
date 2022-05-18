import { Injectable } from '@angular/core';


export interface imenulist {
  title: string,
  id: number,
  url?: string,
  children?: imenulist[]
  icon?: string,
}


@Injectable({
  providedIn: 'root'
})
export class MenuService {
  menu: imenulist[] = [
    {
      title: 'Home',
      id: 1000,
      url: '/main/dashboard',
      children: [
        {
          title: 'Dashoboard',
          id: 2000,
          url: '/main/dashboard',
        }
      ]
    },
    {
      title: 'Setup',
      id: 1001,
      children: [
        {
          title: 'Company',
          id: 2101,
          url: '/main/setup/company',
        },
        {
          title: 'Plant',
          id: 2102,
          url: '/main/setup/plant',
        }
         ,
         {
           title: 'Warehouse',
           id: 2103,
            url: '/main/setup/warehouse',
         },
         {
           title: 'Floor',
           id: 2104,
            url: '/main/setup/floor',
         }
         , {
           title: 'Storage',
           id: 2105,
            url: '/main/setup/storage',
         },
        

      ]
    },
    {
      title: 'Selection',
      id: 2106,
       url: '/main/setup/selection',
    },

    //setup- storage

    {
      title: 'storage',
      id: 1009,
      children: [
        {
          title: 'Storage Class',
          id: 2903,
           url: '/main/storage/storage-class',
        },
        {
          title: 'Storage Type',
          id: 2904,
           url: '/main/storage/storage-type',
        },
        {
          title: 'Storage Bin Type',
          id: 2902,
          url: '/main/storage/storage-bin',
        },
        {
          title: 'Strategies',
          id: 2901,
          url: '/main/storage/strategies',
        },
       
      ]
    },

    //setup-product

    
    {
      title: 'product',
      id: 1010,
      children: [
        {
          title: 'Item Type',
          id: 3001,
           url: '/main/product/itemtype',
        },
        {
          title: 'Item Group',
          id: 3002,
           url: '/main/product/itemgroup',
        },
        {
          title: 'Batch/Serial',
          id: 3003,
          url: '/main/product/batchserial',
        },
        {
          title: 'Variant',
          id: 3004,
          url: '/main/product/variant',
        },
        {
          title: 'Barcode',
          id: 3005,
          url: '/main/product/barcode',
        },
       
       
       
      ]
    },
    {
      title: 'Selection',
      id: 3006,
      url: '/main/product/selection',
    },


    //master -product
    {
      title: 'Master Data',
      id: 1002,
      children: [
      
        {
          title: 'Basic Data 1',
          id: 2201,
          url: '/main/masters/basic-data1',
        },
        {
          title: 'Alternate UOM',
          id: 2203,
          url: '/main/masters/Alternate-uom',
        },
        {
          title: 'Batch/Serial',
          id: 2204,
          url: '/main/masters/Batch/Serial',
        },
        {
          title: 'Strategies',
          id: 2206,
          url: '/main/masters/Strategies',
        },
        {
          title: 'Partner',
          id: 2207,
          url: '/main/masters/Partner',
        },
        {
          title: 'Packing',
          id: 2209,
          url: '/main/masters/Packing',
        },
       
      ],
      
    },
    {
      title: 'selection',
      id: 2210,
      url: '/main/masters/selection',
    },

    //master-storage
    {
      title: 'Master Data Storage',
      id: 1008,
      children: [
        {
          title: 'Basic Data',
          id: 2801,
          url: '/main/masters-storage/basic-data',
        },
        {
          title: 'Inventory',
          id: 2803,
          url: '/main/masters-storage/Inventory',
        },
       
      ]
    },
    {
      title: 'Selection',
      id: 2805,
      url: '/main/masters-storage/selection',
    },
    //other master
    {
      title: 'Other Masters',
      id: 1011,
      children: [
        {
          title: 'Handling Equipment',
          id: 1101,
           url: '/main/other-masters/handling-equipment',
        },
        {
          title: 'Handling Unit',
          id: 1102,
           url: '/main/other-masters/handling-unit',
        },
        {
          title: 'BOM',
          id: 1103,
          url: '/main/other-masters/bom',
        },
        {
          title: 'Business Partner',
          id: 1104,
          url: '/main/other-masters/business-partner',
        },
        {
          title: 'Packing Material',
          id: 1105,
          url: '/main/other-masters/packing-material',
        },
       
      ]
    },

    {
      title: 'Inbound',
      id: 1003,
      children: [
        {
          title: 'Container Receipt',
          id: 2301,
          url: '/main/inbound/container-receipt',
        },
        {
          title: 'Preinbound',
          id: 2302,
          url: '/main/inbound/preinbound',
        },
      
      ]
    },
    {
      title: 'Make & Change',
      id: 1004,
      children: [
        {
          title: 'Inhouse Transfer',
          id: 2401,
          url: '/main/make&change/inhouse-transfer',
        },
        {
          title: 'Warehouse  Transfer',
          id: 2402,
          url: '/main/make&change/warehouse-transfer',
        },
        {
          title: 'Bin to Bin',
          id: 2403,
          url: '/main/make&change/bintobin',
        },
        {
          title: 'Sku to Sku',
          id: 2404,
          url: '/main/make&change/skutosku',
        }
      ]
    },
    {
      title: 'Outbound',
      id: 1005,
      children: [
        {
          title: 'Preoutbound',
          id: 2502,
          url: '/main/outbound/preoutbound',
        },
        {
          title: 'Order Management',
          id: 2501,
          url: '/main/outbound/order-management',
        },
        {
          title: 'Preoutbound',
          id: 2502,
          url: '/main/outbound/preoutbound',
        },
        {
          title: 'Pickup create',
          id: 2503,
          url: '/main/outbound/pickup-create',
        }
      ]
    }
    ,
    {
      title: 'Cycle Count',
      id: 1006,
      children: [
        {
          title: 'Perpetual Count',
          id: 2601,
          url: '/main/cycle-count/Prepetual-main',
        },
        {
          title: 'Perpetual Count- Confirm',
          id: 2603,
          url: '/main/cycle-count/Prepetual-confirm',
        },
        {
          title: 'Annual Count- Confirm',
          id: 2604,
          url: '/main/cycle-count/physical-confirm',
        },
        {
          title: 'Annual Count- main',
          id: 2605,
          url: '/main/cycle-count/physical-main',
        }
      ]
    }
    ,
    {
      title: 'Reports',
      id: 1007,
      children: [
       

      ]
    }



  ]
  constructor() { }
  getMeuList() {
    return this.menu;

  }
}
