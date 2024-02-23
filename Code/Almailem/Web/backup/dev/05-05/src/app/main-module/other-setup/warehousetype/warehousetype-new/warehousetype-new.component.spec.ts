import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehousetypeNewComponent } from './warehousetype-new.component';

describe('WarehousetypeNewComponent', () => {
  let component: WarehousetypeNewComponent;
  let fixture: ComponentFixture<WarehousetypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WarehousetypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WarehousetypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
