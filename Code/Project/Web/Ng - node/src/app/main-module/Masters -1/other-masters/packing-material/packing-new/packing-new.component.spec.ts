import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PackingNewComponent } from './packing-new.component';

describe('PackingNewComponent', () => {
  let component: PackingNewComponent;
  let fixture: ComponentFixture<PackingNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PackingNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PackingNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
