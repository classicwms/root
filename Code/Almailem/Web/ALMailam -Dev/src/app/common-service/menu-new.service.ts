import { Injectable } from '@angular/core';


export interface menuIdList {
  displayName: string,
  id: number,
  route?: string,
  children?: menuIdList[]
  iconName?: string,
  color?: string,
}


@Injectable({
  providedIn: 'root'
})
export class MenuNewService {
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
              children: [
                {
                  displayName: 'General',
                  id: 2101,
                  color: 'whs-green whs-border-green',
                  iconName: "fas fa-business-time sidebar_icon",
                    children: [
                      {
                        displayName: 'Language',
                        id: 3097,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/languageid',
                      },
                      {
                        displayName: 'Country',
                        id: 3133,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/country',
                      },
                      {
                        displayName: 'State',
                        id: 3134,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/state',
                      },
                      {
                        displayName: 'City',
                        id: 3135,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/city',
                      },
                      {
                        displayName: 'Currency',
                        id: 3104,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/currency',
                      },
                      {
                        displayName: 'Date Format',
                        id: 3111,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/dateformatid',
                      },
                      {
                        displayName: 'Decimal Notation',
                        id: 3112,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/decimalnotationid',
                      },
                      {
                        displayName: 'Menu',
                        id: 3109,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/menu',
                      },
                      {
                        displayName: 'Module',
                        id: 3099,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/moduleid',
                      },
                      {
                        displayName: 'Adhoc Module',
                        id: 3100,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/adhocmoduleid',
                      },
                      {
                        displayName: 'Status',
                        id: 3140,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/status',
                      },

                      {
                        displayName: 'Status Messages',
                        id: 3113,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/statussmessagesid',
                      },
                      {
                        displayName: 'Number Range',
                        id: 3162,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/numberrange',
                      },
                     ]
                },
                {
                  displayName: 'Enterprise',
                  id: 2102,
                  color: 'whs-red whs-border-red',
                  iconName: "fas fa-building sidebar_icon ",
                    children: [
                      {
                        displayName: 'Company',
                        id: 3082,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/company',
                      },
                      {
                        displayName: 'Plant',
                        id: 3084,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/plant',
                      },
                      {
                        displayName: 'Warehouse',
                        id: 3083,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/warehouse',
                      },
                      {
                        displayName: 'Floor',
                        id: 3085,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/floor',
                      },
                      {
                        displayName: 'Storage Section',
                        id: 3086,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/storagesection',
                      },
                      {
                        displayName: 'Dock',
                        id: 3122,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/dockid',
                      },
                      {
                        displayName: 'Door',
                        id: 3094,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/doorid',
                      },
                      {
                        displayName: 'Vertical',
                        id: 3094,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/vertical',
                      },
                    ]
                },
                {
                  displayName: 'Storage',
                  id: 2103,
                  color: 'whs-grey whs-border-grey',
                  iconName: "fas fa-boxes sidebar_icon",
                    children: [
                      {
                        displayName: 'Bin Class',
                        id: 3130,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/binclassid',
                      },
                      {
                        displayName: 'Bin Section',
                        id: 3110,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/binsectionid',
                      },
                      {
                        displayName: 'Stock Type',
                        id: 3118,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/stocktype',
                      },
                      {
                        displayName: 'Storage Class',
                        id: 3091,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/storageclass',
                      },
                      {
                        displayName: 'Storage Type',
                        id: 3092,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/storagetype',
                      },
                      {
                        displayName: 'Storage Bin Type',
                        id: 3093,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/storagebintype',
                      },
                      {
                        displayName: 'Stratergy',
                        id: 3108,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/stratergy',
                      },
                      {
                        displayName: 'Aisle',
                        id: 3088,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/aisle',
                      },
                      {
                        displayName: 'Row',
                        id: 3087,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/row',
                      },
                      {
                        displayName: 'Span',
                        id: 3089,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/spanid',
                      },
                      {
                        displayName: 'Shelf',
                        id: 3090,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/shelfid',
                      },
                    ]
                },
                {
                  displayName: 'Warehouse',
                  id: 2104,
                  iconName: "fas fa-warehouse sidebar_icon ",
                  color: 'whs-purple whs-border-purple',
                    children: [
                      {
                        displayName: 'Employee ',
                        id: 3107,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/employeeid',
                      },
                      {
                        displayName: 'Handling Equipment ',
                        id: 3120,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/handlingequipmentid',
                      },
                      {
                        displayName: 'Handling Unit ',
                        id: 3121,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/handlingunitid',
                      },
                      {
                        displayName: 'Palletization',
                        id: 3102,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/palletization-level-id',
                      },
                      {
                        displayName: 'RefDoc Type',
                        id: 3141,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/refdoctypeid',
                      },
                      {
                        displayName: 'Return Type',
                        id: 3125,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/returntypeid',
                      },
                      {
                        displayName: 'Special Stock Indicator',
                        id: 3119,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/specialstockindicator',
                      },
                      {
                        displayName: 'Warehouse Type',
                        id: 3114,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/warehousetype',
                      },
                      {
                        displayName: 'Work Center',
                        id: 3123,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/workcenter',
                      },
                      {
                        displayName: 'User Type',
                        id: 3096,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/usertype',
                      },
                      {
                        displayName: 'Barcode Type',
                        id: 3105,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/barcodetypeid',
                      },
                      {
                        displayName: 'Barcode SubType',
                        id: 3106,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/barcodesubtypeid',
                      },
                    ]
                },
                {
                  displayName: 'Order',
                  id: 2105,
                  iconName: "fas fa-box-open sidebar_icon ",
                  color: 'whs-yellow whs-border-yellow',
                    children: [
                      {
                        displayName: 'Control Process ',
                        id: 3142,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/controlprocessid',
                      },
                      {
                        displayName: 'Control Type',
                        id: 3131,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/controltypeid',
                      },
                      {
                        displayName: 'Process ',
                        id: 3095,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/processid',
                      },
                      {
                        displayName: 'Process Sequence',
                        id: 3144,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/processsequence',
                      },
                      {
                        displayName: 'Level',
                        id: 3101,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/level',
                      },
                      {
                        displayName: 'Sub Level',
                        id: 3143,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/sublevelid',
                      },
                      {
                        displayName: 'Inbound Order Status',
                        id: 3129,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/inboundorderstatusid',
                      },
                      {
                        displayName: 'Inbound Order Type',
                        id: 3128,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/inboundordertypeid',
                      },
                      {
                        displayName: 'Outbound Order Status',
                        id: 3127,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/outboundorderstatusid',
                      },
                      {
                        displayName: 'Outbound Order Type',
                        id: 3128,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/outboundordertypeid',
                      },


                    ]
                },
                {
                  displayName: 'Product',
                  id: 2106,
                  iconName: "fas fa-box sidebar_icon ",
                  color: 'whs-green whs-border-green',
                    children: [
                      {
                        displayName: 'Item Type ',
                        id: 3137,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/itemtype',
                      },
                      {
                        displayName: 'Item Group',
                        id: 3138,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/itemgroup',
                      },
                      {
                        displayName: 'Sub Item Group ',
                        id: 3139,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/subitemgroup',
                      },
                      {
                        displayName: 'Variant',
                        id: 3136,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/variantid',
                      },
                      {
                        displayName: 'UOM',
                        id: 3098,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/uom',
                      },






                    ]
                },
                {
                  displayName: 'Make and Change',
                  id: 2107,
                  color: 'whs-red whs-border-red',
                  iconName: "fas fa-exchange-alt sidebar_icon",
                    children: [
                      {
                        displayName: 'Cycle Count Type ',
                        id: 3124,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/cyclecounttypeid',
                      },
                      {
                        displayName: 'Approval Process',
                        id: 3103,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/approvalprocessid',
                      },
                      {
                        displayName: 'Approval ',
                        id: 3132,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/approvalid',
                      },
                      {
                        displayName: 'Movement Type',
                        id: 3115,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/movementtypeid',
                      },
                      {
                        displayName: 'Sub Movement Type',
                        id: 3116,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/submovementtypeid',
                      },
                      {
                        displayName: 'Transfer Type',
                        id: 3117,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/otherSetup/transfertypeid',
                      },






                    ]
                },

              ]
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
              // {
              //   displayName: 'HHT',
              //   id: 3160,
              //   iconName: "fas fa-warehouse  fa-2x me-2",
              //   route: '/main/userman/hhtuser/',
              // },
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
        children: [


                {
                  displayName: 'Container Reciept',
                  id: 3042,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/container-receipt',
                },
                {
                  displayName: 'Preinbound',
                  id: 3044,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/preinbound',
                },
                // {
                //   displayName: 'Case Receipt',
                //   id: 3046,
                //   iconName: "fas fa-warehouse  fa-2x me-2",
                //   route: '/main/inbound/goods-receipt',
                // },
                {
                  displayName: 'GR Release',
                  id: 3049,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/item-main',
                },
                {
                  displayName: 'Putaway',
                  id: 3051,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/putaway',
                },
                {
                  displayName: 'GR Confirmation',
                  id: 3053,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/readytoConfirm',
                },
                {
                  displayName: 'Inbound Summary',
                  id: 3145,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/masternew/imcapacity',
                },
                {
                  displayName: 'Reversal',
                  id: 3055,
                  iconName: "fas fa-warehouse  fa-2x me-2",
                  route: '/main/inbound/reversal-main',
                },

        ]
        },
        {
          displayName: 'Make & Change',
          id: 5000,
          iconName: 'sync_alt',
          route: '/main/make&change/inhouse-transfer',
          children: [
                  {
                    displayName: 'Inhouse Transfer',
                    id: 3057,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    route: '/main/make&change/inhouse-transfer',
                  },
          ]
          },
          {
            displayName: 'Outbound',
            id: 6000,
            iconName: 'logout',
            children: [


                    {
                      displayName: 'Preoutbound',
                      id: 3059,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/preoutbound',
                    },
                    {
                      displayName: 'Order Management',
                      id: 3060,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/order-management',
                    },
                    {
                      displayName: 'Assignment',
                      id: 3061,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/assignment',
                    },
                    {
                      displayName: 'Pickup',
                      id: 3063,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/pickup-main',
                    },
                    {
                      displayName: 'Quality',
                      id: 3065,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/quality-main',
                    },
                    {
                      displayName: 'Ready for Delivery',
                      id: 3067,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/delivery-main1',
                    },
                    {
                      displayName: 'Delivered',
                      id: 3146,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/delivery-main',
                    },
                    {
                      displayName: 'Reversal',
                      id: 3069,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/reversal',
                    },
                    {
                      displayName: 'Picklist Cancellation',
                      id: 3222,
                      iconName: "fas fa-warehouse  fa-2x me-2",
                      route: '/main/outbound/cancellation',
                    },

            ]
            },
            {
              displayName: 'Delivery',
              id: 9000,
              iconName: 'login',
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
                        route: '/main/cycle-count/varianceConfirm',
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
                children: [
                  {
                    displayName: 'Stock',
                    id: 8100,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                       {
                        displayName: 'Stock Report - By Item',
                        id: 3163,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/stock',
                       },
                      {
                        displayName: 'Inventory Report - By Bin Location',
                        id: 3164,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/inventory',
                      },
                
                    ],},
                    {
                    displayName: 'Inbound',
                    id: 8101,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                      {
                        displayName: 'Container Status',
                        id: 3165,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/container',
                      },
                      {
                        displayName: 'Binning Report',
                        id: 3166,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/binner',
                      },
                      {
                        displayName: 'Order Summary',
                        id: 3172,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/inbound-status',
                      },
                      {
                        displayName: 'Order Details',
                        id: 3185,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/preinbound',
                      },
                      {
                        displayName: 'GR Release ',
                        id: 3221,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/grline',
                      },
                    ],

                  },
                  {
                    displayName: 'Outbound',
                    id: 8102,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                      {
                        displayName: 'Picking',
                        id: 3167,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/picking-Users',
                      },
                      {
                        displayName: 'Picking Productivity',
                        id: 3217,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/pickingprod',
                      },
                      {
                        displayName: 'Shipping Report',
                        id: 3219,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/shippingreport',
                      },
                      {
                        displayName: 'Delivery',
                        id: 3184,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/shipmentTotal',
                      },
                      {
                        displayName: 'Order Summary',
                        id: 3186,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/preoutbound',
                      },
                      {
                        displayName: 'Order Details',
                        id: 3168,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/orderstatus',
                      },
                      {
                        displayName: 'HHT Order Status',
                        id: 3215,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/picking-report',
                      },
                      {
                        displayName: 'Order Management',
                        id: 3223,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/ordermanagement',
                      },
                    ],

                  },
                  {
                    displayName: 'Make & Change',
                    id: 8103,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                      {
                        displayName: 'Transfer',
                        id: 3169,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/transfer',
                      },



                    ],

                  },
                  {
                    displayName: 'Stock Movement',
                    id: 8104,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                     
                      {
                        displayName: 'Stock Movement',
                        id: 3170,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/new-stock-movement',
                      },
                      {
                        displayName: 'Total Stock Movement',
                        id: 3173,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/inventorymovement',
                      },
                      {
                        displayName: 'Transaction History Report',
                        id: 3208,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/transactionHistory',
                      },


                    ],

                  },
                  {
                    displayName: 'Stock Count',
                    id: 8105,
                    iconName: "fas fa-warehouse  fa-2x me-2",
                    children: [
                     
                      {
                        displayName: 'Periodic Report',
                        id: 3213,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/perodic',
                      },
                      {
                        displayName: 'Perpetual Report',
                        id: 3214,
                        iconName: "fas fa-warehouse  fa-2x me-2",
                        route: '/main/reports/perpertual',
                      },
                   


                    ],

                  },
                ]
              },

  ]
  constructor() { }




  getMeuList() {
    return this.menu;
  }

}
