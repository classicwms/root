import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AltpartNewComponent } from './altpart-new.component';

describe('AltpartNewComponent', () => {
  let component: AltpartNewComponent;
  let fixture: ComponentFixture<AltpartNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AltpartNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AltpartNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
