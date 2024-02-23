import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GoodsreceiptMainComponent } from './goodsreceipt-main.component';

describe('GoodsreceiptMainComponent', () => {
  let component: GoodsreceiptMainComponent;
  let fixture: ComponentFixture<GoodsreceiptMainComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GoodsreceiptMainComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GoodsreceiptMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
