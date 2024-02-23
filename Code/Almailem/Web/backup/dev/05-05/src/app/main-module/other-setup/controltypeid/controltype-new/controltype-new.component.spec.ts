import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControltypeNewComponent } from './controltype-new.component';

describe('ControltypeNewComponent', () => {
  let component: ControltypeNewComponent;
  let fixture: ComponentFixture<ControltypeNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ControltypeNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ControltypeNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
