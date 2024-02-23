import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasicdataNewComponent } from './basicdata-new.component';

describe('BasicdataNewComponent', () => {
  let component: BasicdataNewComponent;
  let fixture: ComponentFixture<BasicdataNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BasicdataNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BasicdataNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
