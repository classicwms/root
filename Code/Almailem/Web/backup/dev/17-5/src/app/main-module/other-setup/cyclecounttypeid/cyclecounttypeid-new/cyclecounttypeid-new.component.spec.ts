import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CyclecounttypeidNewComponent } from './cyclecounttypeid-new.component';

describe('CyclecounttypeidNewComponent', () => {
  let component: CyclecounttypeidNewComponent;
  let fixture: ComponentFixture<CyclecounttypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CyclecounttypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CyclecounttypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
