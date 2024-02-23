import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BarcodetypeidNewComponent } from './barcodetypeid-new.component';

describe('BarcodetypeidNewComponent', () => {
  let component: BarcodetypeidNewComponent;
  let fixture: ComponentFixture<BarcodetypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BarcodetypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodetypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
