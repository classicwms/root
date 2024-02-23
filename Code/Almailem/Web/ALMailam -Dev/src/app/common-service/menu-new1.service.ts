import { Injectable } from '@angular/core';


export interface menuIdList {
  displayName: string,
  id: number,
  route?: string,
  children?: menuIdList[]
  iconName?: string,
}


@Injectable({
  providedIn: 'root'
})
export class MenuNew1Service {
  menu: menuIdList[] = [
    {
      displayName: 'Home',
      id: 1000,
      route: '/main/dashboard/landingPage',
      iconName: 'home',
    },
    {
      displayName: 'Setup',
      id: 2000,
      iconName: 'settings',
      children: [
        {
          displayName: 'Basic Setup',
          id: 2001,
          iconName: 'fas fa-cogs sidebar_icon',
          route: '/main/otherSetup/setupAccess',
        },
        {
          displayName: 'User Management',
          id: 2002,
          iconName: 'fas fa-user sidebar_icon',
          children: [
            {
              displayName: 'User Role',
              id: 3159,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/userman/userrole',
            },
            {
              displayName: 'User Profile',
              id: 3160,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/userman/user-profile',
            },
            {
              displayName: 'HHT User',
              id: 3161,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/userman/hhtuser',
            },
          ]

        },
        {
          displayName: 'Organisation',
          id: 2003,
          iconName: 'fas fa-users sidebar_icon',
          children: [
            {
              displayName: 'Company',
              id: 3002,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/organisationsetup/company',
            },
            {
              displayName: 'Plant',
              id: 3003,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/organisationsetup/plant',
            },
            {
              displayName: 'Warehouse',
              id: 3004,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/organisationsetup/warehouse',
            },
            {
              displayName: 'Floor',
              id: 3005,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/organisationsetup/floor',
            },
            {
              displayName: 'Storage Section',
              id: 3006,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/organisationsetup/storage',
            },
          ]

        },
        {
          displayName: 'Product',
          id: 2004,
          iconName: 'fas fa-box-open sidebar_icon',
          children: [
            {
              displayName: 'Item Type',
              id: 3008,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productsetup/itemtype',
            },
            {
              displayName: 'Item Group',
              id: 3009,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productsetup/itemgroup',
            },
            {
              displayName: 'Batch',
              id: 3010,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productsetup/batch',
            },
            {
              displayName: 'Variant',
              id: 3011,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productsetup/variant',
            },

          ]

        },
        {
          displayName: 'Storage',
          id: 2005,
          iconName: 'fas fa-boxes sidebar_icon',
          children: [
            {
              displayName: 'Storage Class',
              id: 3014,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productstorage/storageclass',
            },
            {
              displayName: 'Storage Type',
              id: 3015,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productstorage/storagetype',
            },
            {
              displayName: 'Storage Bin Type',
              id: 3016,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productstorage/storagebintype',
            },
            {
              displayName: 'Stratergy',
              id: 3017,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/productstorage/stratergy',
            },

          ]

        },
      ]
    },
    {
      displayName: 'Master',
      id: 3000,
      iconName: 'key',
      children: [
        {
          displayName: 'Product',
          id: 3001,
          iconName: "fas fa-box-open sidebar_icon",
          children: [
            {
              displayName: 'Basic Data 1',
              id: 3020,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/basicdata',
            },
            {
              displayName: 'Basic Data 2',
              id: 3021,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/basicdata2',
            },

            {
              displayName: 'Alternate UOM',
              id: 3022,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/altuom',
            },
            {
              displayName: 'Partner',
              id: 3025,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/partner',
            },
            {
              displayName: 'Packing',
              id: 3026,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/impacking',
            },
            {
              displayName: 'Capacity',
              id: 3148,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/imcapacity',
            },
            {
              displayName: 'Batch/Serial',
              id: 3023,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/batchserial',
            },
            {
              displayName: 'Variant',
              id: 3028,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/imvariant',
            },
            {
              displayName: 'Palletization',
              id: 3027,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/palletization',
            },
            {
              displayName: 'Alternate Parts',
              id: 3147,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/altpart',
            },
            {
              displayName: 'Stratergy',
              id: 3024,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/masternew/startegy',
            },
          ]
        },
        {
          displayName: 'Bin Location',
          id: 3002,
          iconName: "fas fa-boxes sidebar_icon",
          children: [
            {
              displayName: 'Storage Bin',
              id: 3030,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/mastersStorageNew/binLocation',
            }
          ]
        },
        {
          displayName: 'Others',
          id: 3003,
          iconName: "fas fa-users-cog sidebar_icon",
          children: [
            {
              displayName: 'Handling Unit',
              id: 3032,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/handling-unit',
            },
            {
              displayName: 'Handling Equipment',
              id: 3034,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/handling-equipment',
            },
            {
              displayName: 'Bill of Material',
              id: 3036,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/bom',
            },
            {
              displayName: 'Business Partner',
              id: 3038,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/business-partner',
            },
            {
              displayName: 'Packing Material',
              id: 3040,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/packing-material',
            },
            {
              displayName: 'HHT',
              id: 3160,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/userman/hhtuser/',
            },
            {
              displayName: 'Dock',
              id: 3149,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/dock',
            },
            {
              displayName: 'Work Center',
              id: 3151,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/workcenter',
            },
            {
              displayName: 'Cycle Count Scheduler',
              id: 3153,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/cyclecountschedular',
            },
            {
              displayName: 'Number Range Item',
              id: 3155,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/numberrange',
            },
            {
              displayName: 'Number Range Storage  Bin',
              id: 3157,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/other-masters/numberrangestoragebin',
            },
          ]
        },
        {
          displayName: 'Delivery',
          id: 3004,
          iconName: "fas fa-truck sidebar_icon",
          children: [
            {
              displayName: 'Driver',
              id: 3177,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/delivery/driver',
            },
            {
              displayName: 'Vehicle',
              id: 3178,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/delivery/vehicle',
            },
            {
              displayName: 'Route',
              id: 3179,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/delivery/route',
            },
            {
              displayName: 'Driver Vehicle Assignment',
              id: 3180,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/delivery/drivervehicleassign',
            }
          ]
        },
      ]
    },
    {
      displayName: 'Inbound',
      id: 4000,
      iconName: 'login',
      route: '/main/inbound/preinbound',
      // children: [


      //         {
      //           displayName: 'Container Reciept',
      //           id: 3042,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/container-receipt',
      //         },
      //         {
      //           displayName: 'Preinbound',
      //           id: 3044,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/preinbound',
      //         },
      //         // {
      //         //   displayName: 'Case Receipt',
      //         //   id: 3046,
      //         //   iconName: "fas fa-warehouse  fa-2x me-2",
      //         //   route: '/main/inbound/goods-receipt',
      //         // },
      //         {
      //           displayName: 'GR Release',
      //           id: 3049,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/item-main',
      //         },
      //         {
      //           displayName: 'Putaway',
      //           id: 3051,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/putaway',
      //         },
      //         {
      //           displayName: 'GR Confirmation',
      //           id: 3053,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/readytoConfirm',
      //         },
      //         {
      //           displayName: 'Inbound Summary',
      //           id: 3145,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/inbound-confirm',
      //         },
      //         {
      //           displayName: 'Reversal',
      //           id: 3055,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/inbound/reversal-main',
      //         },

      // ]
    },
    {
      displayName: 'Make & Change',
      id: 5000,
      iconName: 'sync_alt',
      route: '/main/make&change/inhouse-transfer',
      // children: [


      //         {
      //           displayName: 'Inhouse Transfer',
      //           id: 3057,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/make&change/inhouse-transfer',
      //         },


      // ]
    },
    {
      displayName: 'Outbound',
      id: 6000,
      iconName: 'logout',
      route: '/main/outbound/preoutbound',
      // children: [


      //         {
      //           displayName: 'Preoutbound',
      //           id: 3059,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/preoutbound',
      //         },
      //         {
      //           displayName: 'Order Management',
      //           id: 3060,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/order-management',
      //         },
      //         {
      //           displayName: 'Assignment',
      //           id: 3061,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/assignment',
      //         },
      //         {
      //           displayName: 'Pickup',
      //           id: 3063,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/pickup-main',
      //         },
      //         {
      //           displayName: 'Quality',
      //           id: 3065,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/quality-main',
      //         },
      //         {
      //           displayName: 'Ready for Delivery',
      //           id: 3067,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/delivery-main1',
      //         },
      //         {
      //           displayName: 'Delivered',
      //           id: 3146,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/delivery-main',
      //         },
      //         {
      //           displayName: 'Reversal',
      //           id: 3069,
      //           iconName: "fas fa-warehouse  fa-2x me-2",
      //           route: '/main/outbound/reversal',
      //         },

      // ]
    },
    {
      displayName: 'Delivery',
      id: 9000,
      iconName: 'local_shipping',
      route: '/main/delivery/consignment',
      children: [
        {
          displayName: 'Consignments',
          id: 3209,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/consignment',
        },
        {
          displayName: 'Manifest',
          id: 3210,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/manifest',
        },
        {
          displayName: 'Pickup',
          id: 3211,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/pickup',
        },
        {
          displayName: 'Delivery',
          id: 3212,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/delivery',
        }, 
        {
          displayName: 'Redelivery',
          id: 3218,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/redelivery',
        },
        {
          displayName: 'Returned',
          id: 3220,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/returned',
        },
        {
          displayName: 'Delivered',
          id: 3216,
          iconName: "fas fa-warehouse  fa-2x me-2",
          route: '/main/delivery/delivered',
        },
]
    },
    {
      displayName: 'Cycle Count',
      id: 7000,
      iconName: 'autorenew',
      children: [
        {
          displayName: 'Perpetual',
          id: 7001,
          iconName: "fas fa-warehouse  fa-2x me-2",
          children: [
            {
              displayName: 'Assign & Count',
              id: 3071,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/cycle-count/Prepetual-main/count',
            },
            {
              displayName: 'Stock Analysis',
              id: 3074,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/cycle-count/varianceConfirm',
            },
          ]
        },
        {
          displayName: 'Periodic Count',
          id: 7002,
          iconName: "fas fa-warehouse  fa-2x me-2",
          children: [
            {
              displayName: 'Assign & Count',
              id: 3076,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/cycle-count/physical-main',
            },
            {
              displayName: 'Stock Analysis',
              id: 3079,
              iconName: "fas fa-warehouse  fa-2x me-2",
              route: '/main/cycle-count/variant-analysis-annual',
            },
          ]
        },
        {
          displayName: 'Stock Adjustment',
          id: 3207,
          iconName: "fas fa-warehouse fa-2x me-2",
          route: '/main/cycle-count/stockAdjustment',
        },
      ]
    },

    {
      displayName: 'Reports',
      id: 8000,
      route: '/main/reports/report-list',
      iconName: 'description',
    },



  ]
  constructor() { }

  getMeuList() {
    return this.menu;
  }
}
