import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PutawayAddLinesComponent } from './putaway-add-lines.component';

describe('PutawayAddLinesComponent', () => {
  let component: PutawayAddLinesComponent;
  let fixture: ComponentFixture<PutawayAddLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PutawayAddLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PutawayAddLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
