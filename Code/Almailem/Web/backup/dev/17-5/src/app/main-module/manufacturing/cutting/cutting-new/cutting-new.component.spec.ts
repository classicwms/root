import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuttingNewComponent } from './cutting-new.component';

describe('CuttingNewComponent', () => {
  let component: CuttingNewComponent;
  let fixture: ComponentFixture<CuttingNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CuttingNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CuttingNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
