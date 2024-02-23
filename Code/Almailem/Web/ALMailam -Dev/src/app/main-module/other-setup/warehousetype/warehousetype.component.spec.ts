import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehousetypeComponent } from './warehousetype.component';

describe('WarehousetypeComponent', () => {
  let component: WarehousetypeComponent;
  let fixture: ComponentFixture<WarehousetypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehousetypeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehousetypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
