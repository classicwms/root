import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoldingNewComponent } from './molding-new.component';

describe('MoldingNewComponent', () => {
  let component: MoldingNewComponent;
  let fixture: ComponentFixture<MoldingNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MoldingNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MoldingNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
