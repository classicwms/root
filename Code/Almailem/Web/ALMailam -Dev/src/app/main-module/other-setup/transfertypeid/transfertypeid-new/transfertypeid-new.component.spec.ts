import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransfertypeidNewComponent } from './transfertypeid-new.component';

describe('TransfertypeidNewComponent', () => {
  let component: TransfertypeidNewComponent;
  let fixture: ComponentFixture<TransfertypeidNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransfertypeidNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransfertypeidNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
