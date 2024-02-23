import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubitemgroupNewComponent } from './subitemgroup-new.component';

describe('SubitemgroupNewComponent', () => {
  let component: SubitemgroupNewComponent;
  let fixture: ComponentFixture<SubitemgroupNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubitemgroupNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubitemgroupNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
