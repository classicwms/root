import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateLineComponent } from './update-line.component';

describe('UpdateLineComponent', () => {
  let component: UpdateLineComponent;
  let fixture: ComponentFixture<UpdateLineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateLineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
