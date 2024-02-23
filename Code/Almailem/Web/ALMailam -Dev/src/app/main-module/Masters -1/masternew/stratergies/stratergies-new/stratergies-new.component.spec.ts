import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StratergiesNewComponent } from './stratergies-new.component';

describe('StratergiesNewComponent', () => {
  let component: StratergiesNewComponent;
  let fixture: ComponentFixture<StratergiesNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StratergiesNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StratergiesNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
