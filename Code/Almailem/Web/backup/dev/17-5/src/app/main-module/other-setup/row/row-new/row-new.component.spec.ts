import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RowNewComponent } from './row-new.component';

describe('RowNewComponent', () => {
  let component: RowNewComponent;
  let fixture: ComponentFixture<RowNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RowNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RowNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
