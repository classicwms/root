import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FloorNewComponent } from './floor-new.component';

describe('FloorNewComponent', () => {
  let component: FloorNewComponent;
  let fixture: ComponentFixture<FloorNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FloorNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FloorNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
