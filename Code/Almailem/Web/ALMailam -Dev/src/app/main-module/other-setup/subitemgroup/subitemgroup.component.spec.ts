import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubitemgroupComponent } from './subitemgroup.component';

describe('SubitemgroupComponent', () => {
  let component: SubitemgroupComponent;
  let fixture: ComponentFixture<SubitemgroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubitemgroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubitemgroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
