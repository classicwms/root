import { ComponentFixture, TestBed } from '@angular/core/testing';

import { B2btransferorderLinesComponent } from './b2btransferorder-lines.component';

describe('B2btransferorderLinesComponent', () => {
  let component: B2btransferorderLinesComponent;
  let fixture: ComponentFixture<B2btransferorderLinesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ B2btransferorderLinesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(B2btransferorderLinesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
