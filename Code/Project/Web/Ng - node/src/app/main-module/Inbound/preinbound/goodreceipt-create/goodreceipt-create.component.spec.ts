import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GoodreceiptCreateComponent } from './goodreceipt-create.component';

describe('GoodreceiptCreateComponent', () => {
  let component: GoodreceiptCreateComponent;
  let fixture: ComponentFixture<GoodreceiptCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GoodreceiptCreateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GoodreceiptCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
