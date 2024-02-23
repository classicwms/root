import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompletedLineComponent } from './completed-line.component';

describe('CompletedLineComponent', () => {
  let component: CompletedLineComponent;
  let fixture: ComponentFixture<CompletedLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompletedLineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompletedLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
