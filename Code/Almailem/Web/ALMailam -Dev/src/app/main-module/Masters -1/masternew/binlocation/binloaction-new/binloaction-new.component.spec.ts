import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BinloactionNewComponent } from './binloaction-new.component';

describe('BinloactionNewComponent', () => {
  let component: BinloactionNewComponent;
  let fixture: ComponentFixture<BinloactionNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BinloactionNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BinloactionNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
