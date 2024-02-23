import { ComponentFixture, TestBed } from '@angular/core/testing';

import { B2btransferorderComponent } from './b2btransferorder.component';

describe('B2btransferorderComponent', () => {
  let component: B2btransferorderComponent;
  let fixture: ComponentFixture<B2btransferorderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ B2btransferorderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(B2btransferorderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
