import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkuListComponent } from './sku-list.component';

describe('SkuListComponent', () => {
  let component: SkuListComponent;
  let fixture: ComponentFixture<SkuListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkuListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkuListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
