import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliverypopComponent } from './deliverypop.component';

describe('DeliverypopComponent', () => {
  let component: DeliverypopComponent;
  let fixture: ComponentFixture<DeliverypopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeliverypopComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeliverypopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
